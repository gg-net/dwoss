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
package eu.ggnet.dwoss.rights.ui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import eu.ggnet.dwoss.rights.api.AtomicRight;

/**
 * Extension of {@link ListCell}, specified for the usage of {@link AtomicRight} elements.
 *
 * @author Bastian Venz
 */
public class RightsListCell extends ListCell<AtomicRight> {

    public static class Factory implements Callback<ListView<AtomicRight>, ListCell<AtomicRight>> {

        @Override
        public ListCell<AtomicRight> call(ListView<AtomicRight> p) {
            return new RightsListCell();
        }
    }

    @Override
    protected void updateItem(AtomicRight item, boolean empty) {
        super.updateItem(item, empty);
        if ( empty || item == null ) {
            setText("");
        } else {
            setText(item.description());
        }
    }
}
