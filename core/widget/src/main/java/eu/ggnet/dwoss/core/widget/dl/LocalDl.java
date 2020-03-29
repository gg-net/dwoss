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
package eu.ggnet.dwoss.core.widget.dl;

import java.util.*;

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

    /**
     * Tries a local service lookup based on the clazz as identifier.
     * If u don't want null as results us the optional variant below.
     * <ul>
     * <li>try the internal repository, filled with add frontup</li>
     * <li>use {@link  ServiceLoader#load(java.lang.Class) }</li>
     * </ul>
     *
     * @param <T>   the type
     * @param clazz the class as identifier
     * @return the service or null if none found.
     */
    public <T> T lookup(Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        L.debug("lookup({}) in {}, containing DIRECT_LOOKUP {}", clazz.getName(), this, DIRECT_LOOKUP.keySet());
        // The DIRECT_LOOKUP allows the usage of runtime injection direcly via the di light framework. This is normaly used ony for tryout and samples.
        // This could be done better with a injection framework, but through this implementation, we don't need any server at all.
        if ( DIRECT_LOOKUP.containsKey(clazz.getName()) ) return (T)DIRECT_LOOKUP.get(clazz.getName());

        Iterator<T> serviceIterator = ServiceLoader.load(clazz).iterator();
        if ( !serviceIterator.hasNext() ) return null;
        T result = serviceIterator.next();
        if ( result instanceof LocalSingleton ) {
            L.info("lookup({}), LocalSingelton tag detected, adding to DIRECT_LOOKUP", clazz.getName());
            DIRECT_LOOKUP.put(clazz.getName(), result);
        }
        return result;
    }

    public <T> Optional<T> optional(Class<T> clazz) {
        return Optional.ofNullable(lookup(clazz));
    }

    /**
     * Add a service direct to the lookup, no overwrites are possible
     *
     * @param <T>     the type
     * @param clazz   the class as identifierer.
     * @param service the service
     */
    public <T> void add(Class<T> clazz, T service) {
        if ( DIRECT_LOOKUP.containsKey(clazz.getName()) ) {
            L.warn("add({}): there is allready a service with that key, doing nothing", clazz.getName());
            return;
        }
        DIRECT_LOOKUP.put(clazz.getName(), service);
        L.info("add({}):Direct lookup filled.", clazz.getName());
    }

}
