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

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.stock.api.*;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.entity.*;

/**
 * Stock API implementation.
 *
 * @author oliver.guenther
 */
@Stateless
public class StockApiBean implements StockApi {

    @Inject
    private StockUnitEao eao;

    private final static Logger L = LoggerFactory.getLogger(StockApiBean.class);

    @Override
    public SimpleStockUnit find(long id) {
        L.debug("find({})", id);
        return toSimple(eao.findById((int)id));
    }

    @Override
    public SimpleStockUnit findByUniqueUnitId(long uniqueUnitId) {
        L.debug("findByUniqueUnitId({})", uniqueUnitId);
        return toSimple(eao.findByUniqueUnitId((int)uniqueUnitId));
    }

    private static SimpleStockUnit toSimple(StockUnit su) {
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
        return ssub.build();
    }

    private static String format(StockTransaction st) {
        return "Transaction(" + st.getId() + "," + st.getType() + ")"
                + (st.getSource() == null ? "" : " von " + st.getSource().getName())
                + (st.getDestination() == null ? "" : " nach " + st.getDestination().getName());
    }

}
