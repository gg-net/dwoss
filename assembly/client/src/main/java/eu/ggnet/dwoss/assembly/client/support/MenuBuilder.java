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
package eu.ggnet.dwoss.assembly.client.support;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.swing.Action;

import javafx.scene.control.MenuItem;

/**
 * Util to build a menu.
 *
 * @author oliver.guenther
 */
public class MenuBuilder {

    @Inject
    private Instance<Object> instance;

    public MenuItem item(Class<? extends Action> clazz) {
        Action action = instance.select(clazz).get();
        MenuItem item = new MenuItem(action.getValue(Action.NAME).toString());
        item.setOnAction((e) -> action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, action.getValue(Action.NAME).toString())));
        return item;
    }

    public List<MenuItem> items(Class<? extends Action>... classes) {
        return Arrays.stream(classes).map(c -> item(c)).collect(Collectors.toList());
    }

}
