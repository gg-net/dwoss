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

import java.time.LocalDate;
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
import eu.ggnet.dwoss.stock.ee.eao.*;
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
    private ShipmentEao shipmentEao;
    
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

    @Override
    public List<SimpleShipment> findShipmentsSince(LocalDate since) {
        return shipmentEao.findSince(since).stream().map(Shipment::toSimple).collect(Collectors.toList());
    }
}
