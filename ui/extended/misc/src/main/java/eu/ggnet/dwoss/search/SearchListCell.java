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
package eu.ggnet.dwoss.search;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import eu.ggnet.dwoss.search.api.ShortSearchResult;

/**
 *
 * @author oliver.guenther
 */
public class SearchListCell extends ListCell<ShortSearchResult> {

    public static class Factory implements Callback<ListView<ShortSearchResult>, ListCell<ShortSearchResult>> {

        @Override
        public ListCell<ShortSearchResult> call(ListView<ShortSearchResult> listView) {
            return new SearchListCell();
        }
    }

    @Override
    protected void updateItem(ShortSearchResult item, boolean empty) {
        super.updateItem(item, empty);
        if ( item == null ) setText("");
        else setText(item.getShortDescription() + " [" + item.getKey().getComponent() + ":" + item.getKey().getId() + "]");
    }

}
