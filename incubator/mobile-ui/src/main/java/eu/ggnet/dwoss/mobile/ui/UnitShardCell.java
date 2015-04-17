/*
 * Copyright (C) 2015 GG-Net GmbH
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
package eu.ggnet.dwoss.mobile.ui;

import javafx.scene.control.*;
import javafx.util.Callback;

import eu.ggnet.dwoss.uniqueunit.api.UnitShard;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 *
 * @author oliver.guenther
 */
public class UnitShardCell extends ListCell<UnitShard> {

    public static class Factory implements Callback<ListView<UnitShard>, ListCell<UnitShard>> {

        @Override
        public ListCell<UnitShard> call(ListView<UnitShard> listView) {
            return new UnitShardCell();
        }
    }

    @Override
    public void updateItem(UnitShard item, boolean empty) {
        super.updateItem(item, empty);
        if ( empty || item == null ) {
            setGraphic(null);
            return;
        }
        String availableText;
        if ( item.getAvailable() == null ) availableText = "Exestiert Nicht";
        else if ( item.getAvailable() ) availableText = "ist Verfügbar";
        else availableText = "ist nicht Verfügbar";

        setText("SopoNr. " + item.getRefurbishedId() + " " + availableText);

    }
}
