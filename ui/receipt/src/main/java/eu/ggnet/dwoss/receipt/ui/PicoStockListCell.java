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
package eu.ggnet.dwoss.receipt.ui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import eu.ggnet.dwoss.stock.api.PicoStock;

/**
 *
 * @author mirko.schulze
 */
public class PicoStockListCell extends ListCell<PicoStock> {

    public static class Factory implements Callback<ListView<PicoStock>, ListCell<PicoStock>> {

        @Override
        public ListCell<PicoStock> call(ListView<PicoStock> p) {
            return new PicoStockListCell();
        }
    }

    @Override
    protected void updateItem(PicoStock item, boolean empty) {
        super.updateItem(item, empty);
        setText("");
        if ( !empty || item != null ) setText(item.shortDescription);
    }
    
}
