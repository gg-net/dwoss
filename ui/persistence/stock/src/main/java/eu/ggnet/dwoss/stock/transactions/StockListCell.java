/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.transactions;

import javafx.scene.control.*;
import javafx.util.Callback;

import eu.ggnet.dwoss.stock.entity.Stock;

/**
 *
 * @author oliver.guenther
 */
public class StockListCell extends ListCell<Stock> {

    public static class Factory implements Callback<ListView<Stock>, ListCell<Stock>> {

        @Override
        public ListCell<Stock> call(ListView<Stock> listView) {
            return new StockListCell();
        }
    }

    @Override
    protected void updateItem(Stock item, boolean empty) {
        super.updateItem(item, empty);
        if ( item != null ) {
            setText(item.getName());
        }
    }

}
