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
import java.util.*;
import java.util.stream.Collectors;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import javax.swing.Action;

import javafx.application.Platform;
import javafx.scene.control.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.widget.AccessableMenuItem;
import eu.ggnet.dwoss.core.widget.auth.Accessable;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.rights.api.AtomicRight;

import jakarta.enterprise.context.Dependent;

/**
 * Util to build javafx menuitems via CDI wrapping swing actions.
 *
 * @author oliver.guenther
 */
@Dependent
public class MenuBuilder {

    private static class MenuActionAccessable implements Accessable {

        private final MenuItem item;

        private final Accessable accessable;

        public MenuActionAccessable(MenuItem item, Accessable accessable) {
            this.item = Objects.requireNonNull(item, "item must not be null");
            this.accessable = Objects.requireNonNull(accessable, "accessable must not be null");
        }

        @Override
        public void setEnabled(boolean enable) {
            Platform.runLater(() -> item.setDisable(!enable));
        }

        @Override
        public AtomicRight getNeededRight() {
            return accessable.getNeededRight();
        }
    };

    private final Logger L = LoggerFactory.getLogger(MenuBuilder.class);

    @Inject
    private Instance<Object> instance;

    @Inject
    private Guardian guardian;

    /**
     * Selects the class in the running CDI context.#
     * It the class is an {@link Action} it is wrapped into a {@link MenuItem}
     *
     * @param clazz the class to lookup
     * @return the resulting Menuitem.
     */
    public MenuItem item(Class<?> clazz) {
        L.debug("item(clazz={},qualifiers={}) starting", clazz);
        if ( MenuItem.class.isAssignableFrom(clazz) ) {
            MenuItem item = (MenuItem)instance.select(clazz).get();
            if ( item instanceof AccessableMenuItem ) guardian.add((AccessableMenuItem)item);
            return item;
        } else if ( Action.class.isAssignableFrom(clazz) ) {
            Action action = (Action)instance.select(clazz).get();
            MenuItem item;
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
            // Remapping of old access rules.
            if ( action instanceof Accessable ) guardian.add(new MenuActionAccessable(item, (Accessable)action));
            return item;
        }
        throw new RuntimeException("Class " + clazz + " not supported");
    }

    /**
     * Shortcut for multiple lookups at once.
     *
     * @see #item(java.lang.Class)
     * @param classes
     * @return
     */
    public List<MenuItem> items(Class<?>... classes) {
        return Arrays.stream(classes).map(c -> item(c)).collect(Collectors.toList());
    }

}
