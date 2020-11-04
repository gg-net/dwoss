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
package eu.ggnet.dwoss.rights.ui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import eu.ggnet.dwoss.rights.api.Group;

/**
 * Extension of {@link ListCell}, specified for the usage of {@link Group} elements.
 *
 * @author mirko.schulze
 */
public class GroupListCell extends ListCell<Group> {

    public static class Factory implements Callback<ListView<Group>, ListCell<Group>> {

        @Override
        public ListCell<Group> call(ListView<Group> p) {
            return new GroupListCell();
        }
    }

    @Override
    protected void updateItem(Group item, boolean empty) {
        super.updateItem(item, empty);
        setText("");
        if ( !empty || item != null ) setText(item.getName());
    }

}
