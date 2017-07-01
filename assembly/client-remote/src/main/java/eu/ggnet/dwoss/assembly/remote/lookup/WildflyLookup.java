/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.remote.lookup;

import java.util.*;
import java.util.stream.Collectors;

import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.discover.Discovery;
import eu.ggnet.saft.core.RemoteLookup;

/**
 * Remotelookup with Wildfly as Server.
 *
 * @author oliver.guenther
 */
public class WildflyLookup implements RemoteLookup {

    private final static Logger L = LoggerFactory.getLogger(WildflyLookup.class);

    private final static String APP = "dwoss-server";

    private boolean initialized = false;

    private Context _context;

    // full classname, full lookup
    private Map<String, String> namesAndLookup;

    private synchronized void init() {
        if ( initialized ) return;
        Object instance = null;
        String discoveryName = "ejb:/" + APP + "//" + Discovery.NAME;
        try {
            instance = context().lookup(discoveryName);
        } catch (NamingException ex) {
            throw new RuntimeException("Error on frist lookup", ex);
        }
        L.debug("Lookup of {} sucessfull", discoveryName);
        Discovery discovery = (Discovery)instance;
        List<String> names = discovery.allJndiNames("java:app/" + APP);
        L.debug("Discovery returned {} raw entries", names.size());

        namesAndLookup = names.stream()
                .filter(n -> n.contains("!"))
                .collect(Collectors.toMap(n -> n.split("!")[1], n -> "ejb:/" + APP + "//" + n));
        if ( L.isDebugEnabled() ) namesAndLookup.forEach((k, v) -> L.debug("Lookup cache key={}, value={}", k, v));

//        namesAndLookup = new HashMap<>();
//        for (String name : names) {
//            if ( !name.contains("!") ) continue;
//            String key = name.split("!")[1];
//            // INFO: There is no solution for ?statefull yet.
//            String value = "ejb:/" + APP + "//" + name;
//            L.debug("Storing lookup key={}, value={}", key, value);
//            namesAndLookup.put(key, value);
//        }
        L.debug("RemoteLookup initilaized");
        initialized = true;
    }

    private void initOnce() {
        if ( initialized ) return;
        init();
    }

    private Context context() {
        // TODO: Experiment, with new and reused context.
        if ( _context != null ) return _context;
        final Properties properties = new Properties();
        properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");

        try {
            _context = new InitialContext(properties);
            L.debug("New Context for " + this.getClass().getName() + " created");
            return _context;
        } catch (NamingException ex) {
            throw new RuntimeException("Error on Context init", ex);
        }
    }

    @Override
    public <T> boolean contains(Class<T> clazz) {
        initOnce();
        return namesAndLookup.containsKey(Objects.requireNonNull(clazz, "Class must not be null").getName());
    }

    @Override
    public <T> T lookup(Class<T> clazz) {
        initOnce();
        String namespace = namesAndLookup.get(Objects.requireNonNull(clazz, "Class must not be null").getName());
        if ( namespace == null ) {
            L.info("No remote candidate in namespace discovery found for {}", clazz.getName());
            return null;
        }
        L.debug("Trying to lookup {}", namespace);
        try {
            T t = (T)context().lookup(namespace);
            L.debug("Successfull lookup {}", namespace);
            return t;
        } catch (NamingException ex) {
            throw new RuntimeException("Error on Lookup", ex);
        }
    }

}
