/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ui.neo.listView;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;

/**
 *
 * @author jens.papenhagen
 */
public interface CustomerListViewCommand<T> {

    /**
     * Fill a VBox with a given ObservableList of any Typ
     *
     * @param list
     * @return
     */
    public VBox fillList(ObservableList<?> list);

    /**
     * add a new entry to the VBox
     *
     * @param entry for this List
     * @return
     */
    public ActionEvent add(Object entry);

    /**
     * edit the entry in the VBox
     *
     * @param id of the entry
     * @return
     */
    public ActionEvent edit(long id);

    /**
     * delete the entry in the VBox
     *
     * @param id of the entry
     * @return
     */
    public ActionEvent del(long id);

}
