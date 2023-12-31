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

import java.util.List;
import java.util.Optional;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import eu.ggnet.dwoss.stock.api.*;
import eu.ggnet.dwoss.stock.ee.eao.StockEao;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.stock.ee.format.StockUnitFormater;

/**
 * Local implementation of the api.
 *
 * @author oliver.guenther
 */
@Stateless
public class StockApiLocalBean implements StockApiLocal {

    @Inject
    private StockUnitEao eao;

    @Inject
    private StockEao stockEao;

    @Override
    public String findByUniqueUnitIdAsHtml(long uniqueUnitId) {
        StockUnit stockUnit = eao.findByUniqueUnitId((int)uniqueUnitId);
        if ( stockUnit == null ) return "Kein Lagergerät vorhanden<br />";
        return StockUnitFormater.toHtml(stockUnit);
    }

    @Override
    public SimpleStockUnit findByUniqueUnitId(long uniqueUnitId) {
        return toSimple(eao.findByUniqueUnitId((int)uniqueUnitId), stockEao.findAll());
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
