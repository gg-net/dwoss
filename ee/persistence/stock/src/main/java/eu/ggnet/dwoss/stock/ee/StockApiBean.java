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

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.stock.api.*;
import eu.ggnet.dwoss.stock.ee.eao.StockEao;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;

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
    private MonitorFactory mf;

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

    @Override
    public List<Scraped> scrap(List<Long> stockIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Scraped> delete(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
