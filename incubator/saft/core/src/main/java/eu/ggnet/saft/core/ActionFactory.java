/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.saft.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.Action;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * A Factory for Actions.
 * Implementations should be discoverable thought the lookup.
 * <p/>
 * @author oliver.guenther
 */
public interface ActionFactory {

    @Getter
    @EqualsAndHashCode
    @ToString
    public static class MetaAction {

        private final Action action;

        private final List<String> menuNames = new ArrayList<>();

        private final boolean toolbar;

        public MetaAction(String menu, Action action) {
            this.action = action;
            menuNames.add(Objects.requireNonNull(menu, "Menu name must not be null"));
            this.toolbar = false;
        }

        public MetaAction(String menu, Action action, boolean showOnToolbar) {
            this.action = action;
            menuNames.add(Objects.requireNonNull(menu, "Menu name must not be null"));
            this.toolbar = showOnToolbar;
        }

        public MetaAction(String menu, String subMenu, Action action) {
            this.action = action;
            menuNames.add(Objects.requireNonNull(menu, "Menu name must not be null"));
            menuNames.add(Objects.requireNonNull(subMenu, "Menu name must not be null"));
            this.toolbar = false;
        }
    }

    List<MetaAction> createMetaActions();
}
