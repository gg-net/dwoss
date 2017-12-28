/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.product;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.SERIAL;

/**
 *
 * @author lucas.huelsen
 */
public class SimpleUnitListCell extends ListCell<UniqueUnit> {

    public static Callback<ListView<UniqueUnit>, ListCell<UniqueUnit>> factory() {
        return p -> new SimpleUnitListCell();
    }

    @Override
    public void updateItem(UniqueUnit p, boolean empty) {
        super.updateItem(p, empty);
        if ( empty ) {
            setText(null);
        } else {
            String text = p.getIdentifier(REFURBISHED_ID) + " || " + p.getIdentifier(SERIAL) + " || " + p.getCondition().getNote();
            setText(text);
        }
    }
}
