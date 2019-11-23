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

/**
 *
 * @author Bastian Venz
 */
public class OperatorListCell extends ListCell<UiOperator> {

    public static class Factory implements Callback<ListView<UiOperator>, ListCell<UiOperator>> {

        @Override
        public ListCell<UiOperator> call(ListView<UiOperator> p) {
            return new OperatorListCell(p);
        }
    }

    public OperatorListCell(ListView<UiOperator> listView) {

    }

    @Override
    protected void updateItem(UiOperator item, boolean empty) {
        super.updateItem(item, empty);
        if ( empty || item == null ) {
            setGraphic(null);
            return;
        }
        setText(item.usernameProperty().get());
    }
}
