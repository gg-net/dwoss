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
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.auth.client.*;

import eu.ggnet.dwoss.common.api.IsStateful;
import eu.ggnet.dwoss.discovery.Discovery;
import eu.ggnet.dwoss.util.EjbConnectionConfiguration;
import eu.ggnet.saft.core.dl.RemoteLookup;

import lombok.*;

import static java.util.Objects.requireNonNull;

/**
 * Remotelookup with Wildfly as Server.
 *
 * @author oliver.guenther
 */
public class WildflyLookup implements RemoteLookup {

    @RequiredArgsConstructor
    @Getter
    @EqualsAndHashCode(of = "key")
    private static class KeyEquals {

        private final String key;

        private final String value;

    }

    private final static Logger L = LoggerFactory.getLogger(WildflyLookup.class);

    private boolean initialized = false;

    private Context _context;

    private AuthenticationContext context;

    // full classname, full lookup
    private Map<String, String> namesAndLookup;

    private final EjbConnectionConfiguration CONFIG;

    public WildflyLookup(EjbConnectionConfiguration config) {
        requireNonNull(config, "LookupConfig must not be null");
        requireNonNull(config.getHost(), "Host of LookupConfig must not be null");
        requireNonNull(config.getUsername(), "Username of LookupConfig must not be null");
        requireNonNull(config.getPassword(), "Password of LookupConfig must not be null");
        requireNonNull(config.getApp(), "App of LookupConfig must not be null");
        if ( config.getPort() <= 0 ) throw new IllegalArgumentException("Port of LookupConfig must be greater than 0. " + config);

        this.CONFIG = config;
    }

    private synchronized void init() {
        if ( initialized ) return;

        AuthenticationConfiguration ejbConfig = AuthenticationConfiguration.empty().useName(CONFIG.getUsername()).usePassword(CONFIG.getPassword());
        context = AuthenticationContext.empty().with(MatchRule.ALL.matchHost(CONFIG.getHost()), ejbConfig);

        Callable<List<String>> callable = () -> {

            // create an InitialContext
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            properties.put(Context.PROVIDER_URL, "remote+http://" + CONFIG.getHost() + ":" + CONFIG.getPort());
            InitialContext c = new InitialContext(properties);

            final String APP = CONFIG.getApp();
            Object instance = null;
            String discoveryName = "ejb:/" + APP + "//" + Discovery.NAME;
            try {
                instance = c.lookup(discoveryName);
            } catch (NamingException ex) {
                throw new RuntimeException("Error on frist lookup", ex);
            }
            L.info("Lookup of {} sucessfull", discoveryName);
            Discovery discovery = (Discovery)instance;
            List<String> result = discovery.allJndiNames("java:app/" + APP);
            L.debug("Discovery returned {} raw entries", result.size());
            return result;
        };

        try {
            List<String> names = context.runCallable(callable);
            namesAndLookup = names.stream()
                    .filter(n -> n.contains("!"))
                    .map(n -> new KeyEquals(n.split("!")[1], "ejb:/" + CONFIG.getApp() + "//" + n))
                    .distinct() // Removes posible multiple implementations. If these exist in the JNDI tree, we can ignore them, as we discover via Interface.
                    .collect(Collectors.toMap(KeyEquals::getKey, KeyEquals::getValue));
            if ( L.isDebugEnabled() ) namesAndLookup.forEach((k, v) -> L.debug("Lookup cache key={}, value={}", k, v));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        L.debug("RemoteLookup initilaized");
        initialized = true;
    }

    private void initOnce() {
        if ( initialized ) return;
        init();
    }

//    private Context context() {
//        // TODO: Experiment, with new and reused context.
//        if ( _context != null ) return _context;
//        final Properties properties = new Properties();
//        properties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
//
//        try {
//            _context = new InitialContext(properties);
//            L.debug("New Context for " + this.getClass().getName() + " created");
//            return _context;
//        } catch (NamingException ex) {
//            throw new RuntimeException("Error on Context init", ex);
//        }
//    }
    @Override
    public <T> boolean contains(Class<T> clazz) {
        initOnce();
        return namesAndLookup.containsKey(Objects.requireNonNull(clazz, "Class must not be null").getName());
    }

    @Override
    public <T> T lookup(Class<T> clazz) {
        initOnce();

        Callable<T> callable = () -> {
            String namespace = namesAndLookup.get(Objects.requireNonNull(clazz, "Class must not be null").getName());
            if ( namespace == null ) {
                L.info("No remote candidate in namespace discovery found for {}", clazz.getName());
                return null;
            }
            if ( clazz.isAnnotationPresent(IsStateful.class) ) {
                namespace += "?stateful";
            }

            L.debug("Trying to lookup {}", namespace);

            // create an InitialContext
            Properties properties = new Properties();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
            properties.put(Context.PROVIDER_URL, "remote+http://" + CONFIG.getHost() + ":" + CONFIG.getPort());
            InitialContext c = new InitialContext(properties);

            T t = (T)c.lookup(namespace);
            L.debug("Successfull lookup {}", namespace);
            return t;
        };

        try {
            return context.runCallable(callable);
        } catch (Exception ex) {
            throw new RuntimeException("Error on Lookup", ex);
        }
    }

}
