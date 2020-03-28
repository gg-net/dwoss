/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.core.widget.ops;

import java.util.*;
import java.util.function.Consumer;

import javax.swing.Action;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;


/**
 * A Factory for Actions.
 * Implementations should be discoverable thought the lookup.
 * Normally, what this class does, should be done via class inspection. But this is much simpler for now.
 * <p>
 * @author oliver.guenther
 */
public interface ActionFactory {

// TODO: Verify, if this was ever used.   @EqualsAndHashCode 
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
        
        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }

        public Action getAction() {
            return action;
        }

        public List<String> getMenuNames() {
            return menuNames;
        }

        public boolean isToolbar() {
            return toolbar;
        }
        
    }

    default List<MetaAction> createMetaActions() {
        return Collections.EMPTY_LIST;
    }

    default List<Consumer<?>> createDependentActions() {
        return Collections.EMPTY_LIST;
    }

    default List<DescriptiveConsumerFactory<?>> createDependentActionFactories() {
        return Collections.EMPTY_LIST;
    }
}
