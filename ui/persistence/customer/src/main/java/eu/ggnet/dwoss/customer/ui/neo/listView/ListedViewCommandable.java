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
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author jens.papenhagen
 */
public interface ListedViewCommandable<T> {

    /**
     * Fill a VBox with a given ObservableList of any Typ
     *
     * @param list
     */
    public void fillList(ObservableList<?> list);

    /**
     * add a new entry to the VBox
     *
     * @param entry for this List
     * @return
     */
    public EventHandler<? super MouseEvent> add(T entry);

    /**
     * edit the entry in the VBox
     *
     * @param entry
     * @return
     */
    public EventHandler<? super MouseEvent> edit(T entry);

    /**
     * delete the entry in the VBox
     *
     * @param entry
     * @return
     */
    public EventHandler<? super MouseEvent> del(T entry);

}