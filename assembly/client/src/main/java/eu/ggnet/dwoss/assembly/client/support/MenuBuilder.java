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
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.swing.Action;

import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util to build a menu.
 *
 * @author oliver.guenther
 */
public class MenuBuilder {

    private Logger L = LoggerFactory.getLogger(MenuBuilder.class);

    @Inject
    private Instance<Object> instance;

    public MenuItem item(Class<?> clazz) {
        L.debug("item(clazz={},qualifiers={}) starting", clazz);
        if ( clazz.isNestmateOf(MenuItem.class) ) {
            return (MenuItem)instance.select(clazz).get();
        } else if ( clazz.isNestmateOf(Action.class) ) {
            Action action = (Action)instance.select(clazz).get();
            MenuItem item = new MenuItem(action.getValue(Action.NAME).toString());
            L.debug("item() found action {}", action);
            if ( Objects.nonNull(action.getValue(Action.SHORT_DESCRIPTION)) ) {
                Label l = new Label(action.getValue(Action.NAME).toString());
                Tooltip t = new Tooltip(action.getValue(Action.SHORT_DESCRIPTION).toString());
                Tooltip.install(l, t);
                item = new CustomMenuItem(l);
            } else {
                item = new MenuItem(action.getValue(Action.NAME).toString());
            }
            item.setOnAction((e) -> action.actionPerformed(new ActionEvent(action, ActionEvent.ACTION_PERFORMED, action.getValue(Action.NAME).toString())));
            return item;
        }
        throw new RuntimeException("Class " + clazz + " not supported");
    }

    public List<MenuItem> items(Class<? extends Action>... classes) {
        return Arrays.stream(classes).map(c -> item(c)).collect(Collectors.toList());
    }

}
