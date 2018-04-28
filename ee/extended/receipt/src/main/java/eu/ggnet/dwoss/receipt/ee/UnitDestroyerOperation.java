/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.receipt.ee;

import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.ScrapCustomers;
import eu.ggnet.dwoss.mandator.api.value.DeleteCustomers;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.Position;

import java.util.Arrays;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.emo.DossierEmo;
import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;
import eu.ggnet.dwoss.util.UserInfoException;

import static eu.ggnet.dwoss.common.api.values.SalesChannel.UNKNOWN;

/**
 * Allows Scraping of Units.
 *
 * @a
 * import static eu.ggnet.dwoss.rules.SalesChannel.UNKNOWN;
 * uthor oliver.guenther
 */
@Stateless

public class UnitDestroyerOperation implements UnitDestroyer {

    private final static Logger L = LoggerFactory.getLogger(UnitDestroyerOperation.class);

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private DeleteCustomers deleteCustomers;

    @Inject
    private ScrapCustomers scrapCustomers;

    @Inject
    private PostLedger postLedger;

    /**
     * Validates if a unit identified by refurbishedId is scrapable.
     * Throws Exception if:
     * <ul>
     * <li>No UniqueUnit,SopoUnit or StockUnit exists.</li>
     * <li>StockUnit is inTransaction</li>
     * <li>SopoUnit is in Auftrag or Balanced.</li>
     * </ul>
     *
     * @param refurbishId the refurbishedId
     * @return
     * @throws UserInfoException if not scrapable.
     */
    @Override
    public UniqueUnit verifyScarpOrDeleteAble(String refurbishId) throws UserInfoException {
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        UniqueUnit uniqueUnit = uniqueUnitEao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbishId);
        if ( uniqueUnit == null ) throw new UserInfoException("Keine Gerät mit SopoNr " + refurbishId + " gefunden");
        StockUnitEao stockUnitEao = new StockUnitEao(stockEm);
        StockUnit stockUnit = stockUnitEao.findByUniqueUnitId(uniqueUnit.getId());
        if ( stockUnit == null ) throw new UserInfoException("Keine Lagergerät für SopoNr " + refurbishId + " gefunden, Verschrottung/Löschung unnötig");
        if ( stockUnit.isInTransaction() ) throw new UserInfoException("StockUnit ist auf einer Transaktion, Verschrottung/Löschung unzulässig");
        uniqueUnit.fetchEager();
        return uniqueUnit;
    }

    /**
     * Delete the Unit.
     * Finds the StockUnit, destroys it via a Destroy Transaction.
     * Updates the UniqueUnit and SopoUnit in the internal comments, that it is destroyed.
     * Hint: For simplicity this method assumes, that verifyScarpOrDeleteAble was called, so no extra validation is done.
     * <p/>
     * @param uniqueUnit the unit to scrap
     * @param arranger   the arranger
     * @param reason     the reason
     */
    @Override
    public void delete(UniqueUnit uniqueUnit, String reason, String arranger) {
        long cid = deleteCustomers.get(uniqueUnit.getContractor()).orElseThrow(() -> {
            return new IllegalArgumentException("No DeleteCustomer for " + uniqueUnit);
        });
        scrapDelete(cid, "Löschen", uniqueUnit, reason, arranger);
    }

    /**
     * Scraps the Unit.
     * Finds the StockUnit, destroys it via a Destroy Transaction.
     * Updates the UniqueUnit and SopoUnit in the internal comments, that it is destroyed.
     * Hint: For simplicity this method assumes, that verifyScarpOrDeleteAble was called, so no extra validation is done.
     * <p/>
     * @param uniqueUnit the unit to scrap
     * @param arranger   the arranger
     * @param reason     the reason
     */
    @Override
    public void scrap(final UniqueUnit uniqueUnit, final String reason, final String arranger) {
        long cid = scrapCustomers.get(uniqueUnit.getContractor()).orElseThrow(() -> {
            return new IllegalArgumentException("No ScrapCustomer for " + uniqueUnit);
        });
        scrapDelete(cid, "Verschrottung", uniqueUnit, reason, arranger);
    }

    private void scrapDelete(final long targetCustomerId, final String operation, final UniqueUnit uniqueUnit, final String reason, final String arranger) {
        UniqueUnit uu = new UniqueUnitEao(uuEm).findById(uniqueUnit.getId());
        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(stockEm);
        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uu.getId());
        Document doc = new DossierEmo(redTapeEm)
                .requestActiveDocumentBlock((int)targetCustomerId, "Blockaddresse KundenId " + targetCustomerId, "Erzeugung durch " + operation, arranger);
        Dossier dos = doc.getDossier();
        doc.append(Position.builder().type(PositionType.UNIT).amount(1)
                .bookingAccount(postLedger.get(PositionType.UNIT, doc.getTaxType()).orElse(null))
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(uu))
                .name(UniqueUnitFormater.toPositionName(uu))
                .uniqueUnitId(uu.getId())
                .uniqueUnitProductId(uu.getProduct().getId()).build());
        doc.append(Position.builder().type(PositionType.COMMENT).amount(1)
                .name(operation).description(reason + " by " + arranger).build());
        LogicTransaction lt = new LogicTransactionEmo(stockEm).request(dos.getId());
        lt.add(stockUnit); // Implicit removes it from an existing LogicTransaction
        StockTransaction st = stockTransactionEmo.requestDestroyPrepared(stockUnit.getStock().getId(), arranger, reason);
        st.addUnit(stockUnit);
        stockTransactionEmo.completeDestroy(arranger, Arrays.asList(st));
        uu.addHistory(operation + " of Unit via " + st);
        uu.setInternalComment(uu.getInternalComment() + ", " + operation + " of Unit.");
        uu.setSalesChannel(UNKNOWN);
        L.info("Executed Operation {} for uniqueUnit(id={},refurbishId={}), added to LogicTransaction({}) and Dossier({})",
                operation, uniqueUnit.getId(), uniqueUnit.getRefurbishId(), lt.getId(), dos.getIdentifier());
    }
}
