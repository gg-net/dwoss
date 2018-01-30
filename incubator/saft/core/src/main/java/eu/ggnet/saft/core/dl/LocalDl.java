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
package eu.ggnet.saft.core.dl;

import java.util.*;

import org.openide.util.Lookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver.guenther
 */
public class LocalDl {

    // Don't use info Logglevel here until the Progress is lookuped in a different way. e.g. keep the instance until the connection fails.
    private final static Logger L = LoggerFactory.getLogger(LocalDl.class);

    private final static Map<String, Object> DIRECT_LOOKUP = new HashMap<>();

    private static LocalDl instance;

    public static LocalDl getInstance() {
        if ( instance == null ) instance = new LocalDl();
        return instance;
    }

    public <T> T lookup(Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        L.debug("Looking Up {}", clazz.getName());
        // The DIRECT_LOOKUP allows the usage of runtime injection direcly via the di light framework. This is normaly used ony for tryout and samples.
        // This could be done better with a injection framework, but through this implementation, we don't need any server at all.
        if ( DIRECT_LOOKUP.containsKey(clazz.getName()) ) return (T)DIRECT_LOOKUP.get(clazz.getName());
        return Lookup.getDefault().lookup(clazz);
    }

    public <T> Optional<T> optional(Class<T> clazz) {
        return Optional.ofNullable(lookup(clazz));
    }

    public <T> void add(Class<T> clazz, T t) {
        DIRECT_LOOKUP.put(clazz.getName(), t);
        L.info("Remote dierct lookup filled with {}.", clazz.getName());
    }

}
