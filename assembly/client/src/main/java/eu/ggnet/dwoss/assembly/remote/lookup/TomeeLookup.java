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

import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.util.EjbConnectionConfiguration;
import eu.ggnet.saft.core.dl.RemoteLookup;

import static java.util.Objects.requireNonNull;

/**
 * RemoteLookup for Tomee. Untested, but should work.
 *
 * @author oliver.guenther
 */
public class TomeeLookup implements RemoteLookup {

    private final static Logger L = LoggerFactory.getLogger(TomeeLookup.class);

    private boolean initialized = false;

    private Context _context;

    private final String URL;

    // full classname, full lookups
    private static final NavigableMap<String, NavigableSet<String>> CLIENT_JNDI_NAME_CACHE = new TreeMap<>();

    public TomeeLookup(EjbConnectionConfiguration config) {
        requireNonNull(config, "LookupConfig must not be null");
        requireNonNull(config.host(), "Host of LookupConfig must not be null");
        if ( config.port() <= 0 ) throw new IllegalArgumentException("Port of LookupConfig must be greater than 0. " + config);
        this.URL = "http://" + config.host() + ":" + config.port() + "/tomee/ejb";
    }

    public TomeeLookup(String URL) {
        this.URL = requireNonNull(URL, "Remote Host URL is null");
    }

    private void inspectJndiTree(Context context, String suffix) {
        inspectJndiTree(context, suffix, CLIENT_JNDI_NAME_CACHE);
    }

    /**
     * Inspects the JNDI Name Tree and Fills all founded Classes with interfaces into the foundJndiNames Map.
     * <p>
     * @param context the context
     * @param suffix  the suffix
     * @param cache   a mutable list to fill and for recursion
     * @return the inspected result
     */
    // TODO: It would be great to make this completely functional, but for now it's ok.
    private NavigableMap<String, NavigableSet<String>> inspectJndiTree(Context context, String suffix, NavigableMap<String, NavigableSet<String>> cache) {
        try {
            NamingEnumeration<NameClassPair> list = context.list(suffix);
            while (list != null && list.hasMore()) {
                try {
                    String name = list.next().getName();
                    if ( name.contains("EjbModule") || name.contains("com.sun.javafx") ) continue; // Ignoring some defaults in the jndi tree
                    String[] split = name.split("!");
                    if ( split.length > 1 ) { // Only want implementains of Beans, everything else is ignored.
                        String key = split[1];
                        String[] values = {suffix + "/" + split[0], split[0] + "Remote"}; // Second element adding without suffix. @OG whats to know why.
                        if ( cache.get(key) == null ) cache.put(key, new TreeSet<>());
                        for (String value : values) {
                            if ( cache.get(key).add(value) ) L.debug("Storing in cache: key={}, value={}", key, value);
                        }
                    }
                    inspectJndiTree(context, suffix + "/" + name, cache);
                } catch (NamingException ex) {
                    L.warn("Jndi Tree inspection on SubSuffix {} failed: {}", suffix, ex.getMessage());
                }
            }
        } catch (NamingException ex) {
            L.warn("Jndi Tree inspection on Suffix {} failed: {}", suffix, ex.getMessage());
        }
        return cache;
    }

    private synchronized void init() {
        if ( initialized ) return;
        // TODO: Remove later. Or reuse more sensful, wildfly returns nothing.
        // This inspection only works on tomee. wildfly keeps beeing silent.
        L.info("Running Jndi Tree inspection on Suffix: ''");
        inspectJndiTree(context(), ""); // Not existing in Local Environment
        L.info("Running Jndi Tree inspection on Suffix: 'java:global'");
        inspectJndiTree(context(), "java:global");
        L.info("Running Jndi Tree inspection on Suffix: 'java:module'");
        inspectJndiTree(context(), "java:module"); // Olli added, Not existing in Local Environment
        L.info("Running Jndi Tree inspection on Suffix: 'java:app'");
        inspectJndiTree(context(), "java:app"); // Olli added, Not existing in Local Environment
        L.info("Jndi Tree inspection complete, the clientJndiNameCache has now a size of {}", CLIENT_JNDI_NAME_CACHE.size());

        /* Enable only on big problems.
             L.debug("Final CLIENT_JNDI_NAME_CACHE {}", CLIENT_JNDI_NAME_CACHE);
             System.out.println("Final CLIENT_JNDI_NAME_CACHE");
             CLIENT_JNDI_NAME_CACHE.entrySet().forEach(t -> System.out.println(t.getKey() + " - " + t.getValue()));
             L.debug("Final Projects {}", DYNAMIC_JAVA_EE_MODULE_NAMES);
             System.out.println("Final DYNAMIC_JAVA_EE_MODULE_NAMES");
             DYNAMIC_JAVA_EE_MODULE_NAMES.forEach(x -> System.out.println(x));
         */
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
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, URL);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T lookup(Class<T> clazz) {
        initOnce();
        List<String> errors = new ArrayList<>();
        String clazzName = clazz.getName();

        if ( CLIENT_JNDI_NAME_CACHE.containsKey(clazzName) ) {
            for (String name : CLIENT_JNDI_NAME_CACHE.get(clazzName)) {
                try {
                    T result = (T)context().lookup(name);
                    L.debug("Succesful look up via Cache(key={},value={}) class {}", clazzName, name, result.getClass().getName());
                    return result;
                } catch (NamingException ne) {
                    errors.add("NamingException(jndiName=" + name + ", message=" + ne.getMessage() + ")");
                }
            }
            L.warn("No Lookup for cached key={} possible, very unussual. Encounterd the following errors. {}", clazzName, errors);
        }
        return null;
    }

}
