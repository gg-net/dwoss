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
package eu.ggnet.dwoss.redtapext.ee;

import java.util.Arrays;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.CustomerServiceBean;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.redtape.api.UnitAvailability;
import eu.ggnet.dwoss.redtape.ee.RedTapeApiBean;
import eu.ggnet.dwoss.redtape.ee.api.UnitPositionHook;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.ee.interactiveresult.Result;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.LogicTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;

/**
 * A EJB to supply Information about Units bimport eu.ggnet.dwoss.redtape.api.LegacyRemoteBridge;
 * <p>
 * acked up by multiple data sources.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
@AutoLogger
public class UnitOverseerBean implements UnitOverseer {

    private final Logger L = LoggerFactory.getLogger(UnitOverseerBean.class);

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    private CustomerServiceBean customerService;

    @Inject
    private Instance<UnitPositionHook> redTapeHook;

    @Inject
    private PostLedger postLedger;

    @Inject
    private RedTapeApiBean redTapeApi;

    /**
     * Find an available StockUnit and locks it by add to a LogicTransaction via DossierId.
     * <p/>
     * If no unit is found a LayerEightException is thrown.
     * <p/>
     * @param dossierId     The Dossiers ID
     * @param refurbishedId The refurbished id for the Unique Unit search
     * @throws IllegalStateException if the refurbishId is not available
     */
    @Override
    public void lockStockUnit(long dossierId, String refurbishedId) throws UserInfoException {
        if ( !redTapeApi.findUnitByRefurbishIdAndVerifyAviability(refurbishedId).available() )
            throw new UserInfoException("Trying to lock refusbishId " + refurbishedId + ", but it is not available!");
        UniqueUnit uu = new UniqueUnitEao(uuEm).findByIdentifier(Identifier.REFURBISHED_ID, refurbishedId);
        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uu.getId());
        LogicTransaction lt = new LogicTransactionEmo(stockEm).request(dossierId);
        lt.add(stockUnit);
    }

    /**
     * Find a Unit by its refurbished id and returns it.
     * <p/>
     * This method will throw a UserInfoException describing, why the unit is not available.
     * <p/>
     * @param refurbishId The refurbished id of the UniqueUnit
     * @param documentId  the document as reference for tax and more.
     * @return a Unit by its refurbished id or null if nothing is found of the unit is not available.
     * @throws UserInfoException if the refurbishId is not available
     */
    @Override
    public Result<List<Position>> createUnitPosition(String refurbishId, long documentId) throws UserInfoException {
        UnitAvailability ua = redTapeApi.findUnitByRefurbishIdAndVerifyAviability(refurbishId);
        if ( !ua.available() ) throwNotAvailable(refurbishId, ua);

        Document doc = new DocumentEao(redTapeEm).findById(documentId);
        UniqueUnit uu = new UniqueUnitEao(uuEm).findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);

        Position p = Position.builder()
                .amount(1)
                .price(0.)
                .serialNumber(uu.getSerial())
                .refurbishedId(uu.getRefurbishId())
                .bookingAccount(postLedger.get(PositionType.UNIT, doc.getTaxType()).orElse(null))
                .type(PositionType.UNIT)
                .tax(doc.getTaxType().tax())
                .uniqueUnitId(uu.getId())
                .uniqueUnitProductId(uu.getProduct().getId())
                .name(UniqueUnitFormater.toPositionName(uu))
                .description(UniqueUnitFormater.toDetailedDiscriptionLine(uu) + "\n")  // (OG) PDF Hack.
                .build();

        if ( redTapeHook.isUnsatisfied() ) return new Result(Arrays.asList(p)); //return Result
        return redTapeHook.get().elaborateUnitPosition(p, documentId);
    }

    /**
     * Build and throw an exception for a not available unit.
     * <p>
     * @param refurbishId the refurbished id of the unit
     * @param ua          the unit shard
     * @throws UserInfoException
     */
    private void throwNotAvailable(String refurbishId, UnitAvailability ua) throws UserInfoException {
        if ( !ua.exists() ) throw new UserInfoException("SopoNr " + refurbishId + " existiert nicht"); // <- auch in di auslagerung...
        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(ua.uniqueUnitId().get().intValue());
        if ( stockUnit != null && stockUnit.getLogicTransaction() != null ) {
            Dossier dos = new DossierEao(redTapeEm).findById(stockUnit.getLogicTransaction().getDossierId());
            if ( dos == null )
                throw new UserInfoException("SopoNr " + refurbishId + " is on a LogicTransaction, but there is no Dossier, inform Team Software");
            UiCustomer customer = customerService.asUiCustomer(dos.getCustomerId());
            if ( customer == null )
                throw new UserInfoException("SopoNr " + refurbishId + " is on Dossier " + dos.getIdentifier() + ", but Customer " + dos.getCustomerId() + " does not exist.");
            throw new UserInfoException("SopoNr " + refurbishId + " ist schon vergeben"
                    + "\nKID = " + customer.id()
                    + "\nKunde = " + customer.toTitleNameLine()
                    + "\n\nVorgang = " + dos.getIdentifier());
        }
    }

}
