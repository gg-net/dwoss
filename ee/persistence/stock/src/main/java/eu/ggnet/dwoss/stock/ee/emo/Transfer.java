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
package eu.ggnet.dwoss.stock.ee.emo;

import java.util.List;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Parameter Class for {@link StockTransactionEmo#prepare(eu.ggnet.dwoss.stock.emo.Transfer, eu.ggnet.saft.api.progress.IMonitor) }.
 * <p>
 * @author oliver.guenther
 */
@FreeBuilder
public interface Transfer {

    class Builder extends Transfer_Builder {

        @Override
        public Transfer build() {

            if ( stockUnitIds().isEmpty() ) throw new IllegalArgumentException("stockUnitIds must not be empty");
            if ( maxTransactionSize() <= 0 ) throw new IllegalArgumentException("maxTransactionSize must be > 0");
            return super.build();
        }

    }

    /**
     * The stockIds to transfer.
     *
     * @return stockunit ids
     */
    List<Integer> stockUnitIds();

    /**
     * The id of the destination stock.
     *
     * @return destination stock id
     */
    int destinationStockId();

    /**
     * A optional arranger.
     *
     * @return arranger
     */
    String arranger();

    /**
     * A optional comment.
     *
     * @return comment
     */
    String comment();

    /**
     * The maximum amount of units a trasaction should hold.
     *
     * @return the max transcation size
     */
    int maxTransactionSize();

}
