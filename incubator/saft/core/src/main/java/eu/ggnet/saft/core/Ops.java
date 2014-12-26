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
package eu.ggnet.saft.core;

import java.util.*;

import eu.ggnet.saft.api.ui.DefaultAction;
import eu.ggnet.saft.api.ui.DependendAction;
import eu.ggnet.saft.core.all.DependendActionRunner;

/**
 * Operation Central. The point there Actions and Factories are registered.
 * 
 * @author oliver.guenther
 */
public class Ops {
    
    public final static Map<Class,List<DependendAction>> REGISTERED_ACTIONS = new HashMap<>();
        
    private final static Map<Class,DependendAction> REGISTERED_DEFAULT_ACTIONS = new HashMap<>();
    
    public static <T> void register(Class<T> clazz, DependendAction<T> action) {
        if (action.getClass().getAnnotation(DefaultAction.class) != null ) REGISTERED_DEFAULT_ACTIONS.put(clazz, action);        
        if (!REGISTERED_ACTIONS.containsKey(clazz)) REGISTERED_ACTIONS.put(clazz, new ArrayList<>());
        REGISTERED_ACTIONS.get(clazz).add(action);        
    }
    
    /**
     * Returns the default DependendAction wrapped in a runner.
     * 
     * @param <T>
     * @param t
     * @return 
     */
    public static <T> Optional<DependendActionRunner<T>> defaultOf(T t) {
        if (t == null || !REGISTERED_DEFAULT_ACTIONS.containsKey(t.getClass())) return Optional.empty();
        return Optional.of(new DependendActionRunner<>(REGISTERED_DEFAULT_ACTIONS.get(t.getClass()),t));
    }
}
