/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ee;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.autolog.AutoLogger;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.stock.api.*;
import eu.ggnet.dwoss.stock.api.event.DeleteEvent;
import eu.ggnet.dwoss.stock.api.event.ScrapEvent;
import eu.ggnet.dwoss.stock.ee.eao.StockEao;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.*;

import static eu.ggnet.dwoss.stock.ee.StockApiLocalBean.toSimple;

/*
 * Implementation of the remote stock api.
 *
 * @author oliver.guenther
 */
@Stateless
public class StockApiBean implements StockApi {

    @Inject
    private StockTransactionProcessorOperation stp;

    @Inject
    private StockEao stockEao;

    @Inject
    private StockUnitEao stockUnitEao;

    @Inject
    private StockTransactionEmo stockTransactionEmo;

    @Inject
    private MonitorFactory mf;

    @Inject
    private Event<ScrapEvent> scrapObservers;

    @Inject
    private Event<DeleteEvent> deleteObservers;

    @Override
    public List<PicoStock> findAllStocks() {
        return stockEao.findAll().stream().map(Stock::toPicoStock).collect(Collectors.toList());
    }

    @Override
    public void perpareTransferByUniqueUnitIds(List<Long> uniqueUnitIds, int destinationStockId, String arranger, String comment) throws UserInfoException {
        List<StockUnit> stockUnits = stockUnitEao.findByUniqueUnitIds(uniqueUnitIds.stream().map(Long::intValue).collect(Collectors.toList()));
        stp.perpareTransfer(stockUnits, destinationStockId, arranger, comment);
    }

    @Override
    public SimpleStockUnit findByUniqueUnitId(long uniqueUnitId) {
        return toSimple(stockUnitEao.findByUniqueUnitId((int)uniqueUnitId), stockEao.findAll());
    }

    @Override
    public SimpleStockUnit findByRefurbishId(String refurbishId) {
        return toSimple(stockUnitEao.findByRefurbishId(refurbishId), stockEao.findAll());
    }

    @Override
    public Map<String, SimpleStockUnit> findByRefurbishIds(List<String> refurbishIds) {
        if ( refurbishIds == null ) return Collections.emptyMap();
        SubMonitor m = mf.newSubMonitor("Lade Geräte via RefurbishId", refurbishIds.size() + 1);
        m.start();
        List<Stock> stocks = stockEao.findAll();
        m.worked(1, "loaded Stocks");
        var result = new HashMap<String, SimpleStockUnit>();
        for (String refurbishId : refurbishIds) {
            result.put(refurbishId, toSimple(stockUnitEao.findByRefurbishId(refurbishId), stocks));
            m.worked(1, "loaded " + refurbishId);
        }
        m.finish();
        return result;
    }

    @AutoLogger
    @Override
    public List<Scraped> scrap(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        if ( StringUtils.isBlank(reason) ) throw new UserInfoException("Keine Grund angeben");
        List<Scraped> result = scrapDelete(stockUnitIds, "Verschrottung : " + reason, arranger);
        scrapObservers.fire(new ScrapEvent(result.stream().filter(Scraped::successful).map(Scraped::uniqueUnitId).collect(Collectors.toList()), arranger, reason));
        return result;
    }

    @AutoLogger
    @Override
    public List<Scraped> delete(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        if ( StringUtils.isBlank(reason) ) throw new UserInfoException("Keine Grund angeben");
        List<Scraped> result = scrapDelete(stockUnitIds, "Löschung : " + reason, arranger);
        deleteObservers.fire(new DeleteEvent(result.stream().filter(Scraped::successful).map(Scraped::uniqueUnitId).collect(Collectors.toList()), arranger, reason));
        return result;
    }

    // TODO: We never verify the existence of Scrapcustomers (Which would need the uniqueunit here). Will Fail in the RedTapeObserver
    public List<Scraped> scrapDelete(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        if ( Objects.requireNonNull(stockUnitIds, "stockIds must not be null").isEmpty() )
            throw new UserInfoException("Keine StockUnitIds zum verschrotten/löschen übergeben");
        if ( StringUtils.isBlank(arranger) ) throw new UserInfoException("Kein Arranger angeben");
        var result = new ArrayList<Scraped>();
        var stockUnits = new HashMap<Stock, List<StockUnit>>();
        // Load all supplied stockunits.
        for (long stockUnitId : stockUnitIds) {
            StockUnit su = stockUnitEao.findById((int)stockUnitId);
            if ( su == null ) {
                result.add(new Scraped.Builder()
                        .uniqueUnitId(0)
                        .description("Gerät mit StockUnit.id=" + stockUnitId)
                        .successful(false)
                        .comment("Gerät nicht in Datenbank").build());
            } else if ( su.isInTransaction() ) {
                result.add(new Scraped.Builder()
                        .uniqueUnitId(Optional.ofNullable(su.getUniqueUnitId()).orElse(0))
                        .description(su.getRefurbishId() + " - " + su.getName())
                        .successful(false)
                        .comment("Gerät auf Lager Transaktion").build());
            } else if ( su.getLogicTransaction() != null ) {
                result.add(new Scraped.Builder()
                        .uniqueUnitId(Optional.ofNullable(su.getUniqueUnitId()).orElse(0))
                        .description(su.getRefurbishId() + " - " + su.getName())
                        .successful(false)
                        .comment("Gerät auf einem Kundenauftrag").build());
            } else if ( su.getStock() == null ) {
                result.add(new Scraped.Builder()
                        .uniqueUnitId(Optional.ofNullable(su.getUniqueUnitId()).orElse(0))
                        .description(su.getRefurbishId() + " - " + su.getName())
                        .successful(false)
                        .comment("Gerät ist in keinem Lager").build());
            } else {
                if ( stockUnits.get(su.getStock()) == null ) stockUnits.put(su.getStock(), new ArrayList<>());
                stockUnits.get(su.getStock()).add(su);
            }
        }

        var stockTransactions = new ArrayList<StockTransaction>();
        for (Stock stock : stockUnits.keySet()) {
            StockTransaction st = stockTransactionEmo.requestDestroyPrepared(stock.getId(), arranger, reason);
            for (StockUnit su : stockUnits.get(stock)) {
                st.addUnit(su);
                result.add(new Scraped.Builder()
                        .uniqueUnitId(Optional.ofNullable(su.getUniqueUnitId()).orElse(0))
                        .description(su.getRefurbishId() + " - " + su.getName())
                        .successful(true)
                        .comment("verschrottet/gelösht").build());
            }
            stockTransactions.add(st);

        }
        stockTransactionEmo.completeDestroy(arranger, stockTransactions);
        return result;
    }

//        /**
//     * Delete the Unit.
//     * Finds the StockUnit, destroys it via a Destroy Transaction.
//     * Updates the UniqueUnit and SopoUnit in the internal comments, that it is destroyed.
//     * Hint: For simplicity this method assumes, that verifyScarpOrDeleteAble was called, so no extra validation is done.
//     * <p/>
//     * @param uniqueUnit the unit to scrap
//     * @param arranger   the arranger
//     * @param reason     the reason
//     */
//    @Override
//    public void delete(UniqueUnit uniqueUnit, String reason, String arranger) {
//        long cid = deleteCustomers.get(uniqueUnit.getContractor()).orElseThrow(() -> {
//            return new IllegalArgumentException("No DeleteCustomer for " + uniqueUnit);
//        });
//        scrapDelete(cid, "Löschen", uniqueUnit, reason, arranger);
//    }
//
//    /**
//     * Scraps the Unit.
//     * Finds the StockUnit, destroys it via a Destroy Transaction.
//     * Updates the UniqueUnit and SopoUnit in the internal comments, that it is destroyed.
//     * Hint: For simplicity this method assumes, that verifyScarpOrDeleteAble was called, so no extra validation is done.
//     * <p/>
//     * @param uniqueUnit the unit to scrap
//     * @param arranger   the arranger
//     * @param reason     the reason
//     */
//    @Override
//    public void scrap(final UniqueUnit uniqueUnit, final String reason, final String arranger) {
//        long cid = scrapCustomers.get(uniqueUnit.getContractor()).orElseThrow(() -> {
//            return new IllegalArgumentException("No ScrapCustomer for " + uniqueUnit);
//        });
//        scrapDelete(cid, "Verschrottung", uniqueUnit, reason, arranger);
//    }
//
//    private void scrapDelete(final long targetCustomerId, final String operation, final UniqueUnit uniqueUnit, final String reason, final String arranger) {
//        UniqueUnit uu = new UniqueUnitEao(uuEm).findById(uniqueUnit.getId());
//        StockTransactionEmo stockTransactionEmo = new StockTransactionEmo(stockEm);
//        StockUnit stockUnit = new StockUnitEao(stockEm).findByUniqueUnitId(uu.getId());
//        Document doc = new DossierEmo(redTapeEm)
//                .requestActiveDocumentBlock((int)targetCustomerId, "Blockaddresse KundenId " + targetCustomerId, "Erzeugung durch " + operation, arranger);
//        Dossier dos = doc.getDossier();
//        doc.append(Position.builder().type(PositionType.UNIT).amount(1)
//                .bookingAccount(postLedger.get(PositionType.UNIT, doc.getTaxType()).orElse(null))
//                .description(UniqueUnitFormater.toDetailedDiscriptionLine(uu))
//                .name(UniqueUnitFormater.toPositionName(uu))
//                .uniqueUnitId(uu.getId())
//                .uniqueUnitProductId(uu.getProduct().getId()).build());
//        doc.append(Position.builder().type(PositionType.COMMENT).amount(1)
//                .name(operation).description(reason + " by " + arranger).build());
//        LogicTransaction lt = new LogicTransactionEmo(stockEm).request(dos.getId());
//        lt.add(stockUnit); // Implicit removes it from an existing LogicTransaction
//        StockTransaction st = stockTransactionEmo.requestDestroyPrepared(stockUnit.getStock().getId(), arranger, reason);
//        st.addUnit(stockUnit);
//        stockTransactionEmo.completeDestroy(arranger, Arrays.asList(st));
//        uu.addHistory(operation + " of Unit via " + st);
//        uu.setInternalComment(uu.getInternalComment() + ", " + operation + " of Unit.");
//        uu.setSalesChannel(UNKNOWN);
//        L.info("Executed Operation {} for uniqueUnit(id={},refurbishId={}), added to LogicTransaction({}) and Dossier({})",
//                operation, uniqueUnit.getId(), uniqueUnit.getRefurbishId(), lt.getId(), dos.getIdentifier());
//    }
}
