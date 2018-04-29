/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.saft.experimental.ops;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handles the selections.
 * <p>
 * @author oliver.guenther
 */
public class Selector<T> {

    private final Logger L;

    private final Class<T> clazz;

    private final Map<Class, List<Consumer>> allListeners;

    private final SelectionEnhancer<T> selectionEnhancer;

    public Selector(Class<T> clazz, Map<Class, List<Consumer>> listeners, SelectionEnhancer<T> selectionEnhancer) {
        L = LoggerFactory.getLogger(this.getClass().getName() + "<" + clazz.getName() + ">");
        this.clazz = clazz;
        this.allListeners = listeners;
        this.selectionEnhancer = selectionEnhancer;
    }

    public void selected(T t) {
        List<Object> instances = new ArrayList<>();
        instances.add(t);
        if ( selectionEnhancer != null ) instances.addAll(selectionEnhancer.enhance(t));
        L.debug("Selected {}, after enhacement {}", t, instances);

        for (Entry<Class, List<Consumer>> entrySet : allListeners.entrySet()) {
            L.debug("Inspecting {}", entrySet);
            Class k = entrySet.getKey();
            List<Consumer> v = entrySet.getValue();
            if ( instances.stream().anyMatch((i) -> (i != null && i.getClass().equals(k))) ) {
                L.debug("Got a direct selection match {} ", entrySet);
                instances.stream()
                        .filter((i) -> (i != null && i.getClass().equals(k)))
                        .forEach((i) -> v.forEach(c -> c.accept(i)));
            } else if ( clazz.isAssignableFrom(k) ) {
                v.forEach(c -> c.accept(null)); // Same interface, but different consumer. Deselect them.
            }
        }
    }

    public void clear() {
        selected(null);
    }
}
