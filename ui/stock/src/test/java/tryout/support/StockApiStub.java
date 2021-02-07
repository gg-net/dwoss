/*
 * Copyright (C) 2021 GG-Net GmbH
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
package tryout.support;

import java.util.*;
import java.util.stream.Collectors;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.stock.api.*;
import eu.ggnet.dwoss.stock.ee.entity.*;

/**
 *
 * @author oliver.guenther
 */
public class StockApiStub implements StockApi {

    private final List<Stock> stocks;

    private final List<StockUnit> stockUnits;

    private final static Random R = new Random();

    public StockApiStub(List<Stock> stocks, List<StockUnit> stockUnits) {
        this.stocks = stocks;
        this.stockUnits = stockUnits;
    }

    @Override
    public List<Scraped> scrap(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        if ( stockUnitIds == null ) throw new NullPointerException("stockids is null");
        if ( reason == null || reason.isBlank() ) throw new UserInfoException("reason is blank");
        if ( arranger == null || arranger.isBlank() ) throw new UserInfoException("arranger is blank");
        System.out.println("StockApiStub.scrap():" + stockUnitIds);
        return stockUnitIds.stream().map(id -> new Scraped.Builder().description("StockUnit(" + id + ")").comment("Ein Kommentar").successful(R.nextBoolean()).build()).collect(Collectors.toList());
    }

    @Override
    public List<Scraped> delete(List<Long> stockUnitIds, String reason, String arranger) throws NullPointerException, UserInfoException {
        if ( stockUnitIds == null ) throw new NullPointerException("stockids is null");
        if ( reason == null || reason.isBlank() ) throw new UserInfoException("reason is blank");
        if ( arranger == null || arranger.isBlank() ) throw new UserInfoException("arranger is blank");
        System.out.println("StockApiStub.delete():" + stockUnitIds);
        return stockUnitIds.stream().map(id -> new Scraped.Builder().description("StockUnit(" + id + ")").comment("Ein Kommentar").successful(R.nextBoolean()).build()).collect(Collectors.toList());
    }

    @Override
    public List<PicoStock> findAllStocks() {
        return stocks.stream().map(Stock::toPicoStock).collect(Collectors.toList());
    }

    @Override
    public SimpleStockUnit findByUniqueUnitId(long uniqueUnitId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleStockUnit findByRefurbishId(String refurbishId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, SimpleStockUnit> findByRefurbishIds(List<String> refurbishIds) {
        var result = new HashMap<String, SimpleStockUnit>();
        var notFound = new ArrayList<String>(refurbishIds);
        for (StockUnit su : stockUnits) {
            for (String id : refurbishIds) {
                if ( Objects.equals(id, su.getRefurbishId()) ) {
                    result.put(id, toSimple(su, stocks));
                    notFound.remove(id);
                }
            }
        }
        for (String id : notFound) {
            result.put(id, null);
        }
        return result;
    }

    @Override
    public void perpareTransferByUniqueUnitIds(List<Long> uniqueUnitIds, int destinationStockId, String arranger, String comment) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    static SimpleStockUnit toSimple(StockUnit su, List<Stock> stocks) {
        if ( su == null ) return null;
        SimpleStockUnit.Builder ssub = new SimpleStockUnit.Builder()
                .id(su.getId())
                .onLogicTransaction(su.getLogicTransaction() != null)
                .uniqueUnitId(Optional.ofNullable(su.getUniqueUnitId()).orElse(0))
                .shortDescription(su.getName());
        if ( su.isInStock() ) {
            ssub.stock(su.getStock().toPicoStock());
        }
        if ( su.isInTransaction() ) {
            StockTransaction st = su.getTransaction();
            ssub.stockTransaction(new SimpleStockTransaction.Builder()
                    .id(st.getId())
                    .shortDescription(format(st))
                    .source(Optional.ofNullable(st.getSource()).map(Stock::toPicoStock))
                    .destination(Optional.ofNullable(st.getDestination()).map(Stock::toPicoStock))
                    .build());

        }
        if ( su.isInStock() && !su.isInTransaction() ) {
            ssub.addAllPossibleDestinations(stocks.stream().filter(s -> s.getId() != su.getStock().getId()).map(Stock::toPicoStock));
        }
        return ssub.build();
    }

    private static String format(StockTransaction st) {
        return "Transaction(" + st.getId() + "," + st.getType() + ")"
                + (st.getSource() == null ? "" : " von " + st.getSource().getName())
                + (st.getDestination() == null ? "" : " nach " + st.getDestination().getName());
    }

}
