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

import eu.ggnet.dwoss.remote.spi.EjbConnectionConfiguration;
import eu.ggnet.dwoss.remote.spi.EjbConnectionConfigurationProvider;
import eu.ggnet.dwoss.core.widget.Dl;

/**
 *
 * @author oliver.guenther
 */
public class Configurations {

    private static Map<String, EjbConnectionConfiguration> conf;

    private final static String DEFAULT_KEY = "ggnet";

    private static void init() {
        if ( !Objects.isNull(conf) ) return;
        conf = new HashMap<>();
        conf.put("local", new EjbConnectionConfiguration.Builder()
                .host("localhost")
                .port(8080)
                .username("admin")
                .password("admin")
                .app("dwoss-server-sample")
                .build());

        Dl.local().optional(EjbConnectionConfigurationProvider.class).ifPresent(p -> conf.putAll(p.getConfigurations()));
    }

    public static boolean containsConfig(String key) {
        init();
        return conf.containsKey(key);
    }

    /**
     * Returns the configuration of the key or if not pressend either the default or the first.
     * Exists for the rare case, that the client is called with wrong or old parameters.
     *
     * @param key identifing the configuration
     * @return a configuration
     */
    public static EjbConnectionConfiguration getConfigOrDefault(String key) {
        init();
        if ( !containsConfig(key) && !containsConfig(DEFAULT_KEY) ) return conf.values().iterator().next(); // the first usefull.
        if ( !containsConfig(key) ) return conf.get(DEFAULT_KEY);
        return conf.get(key);
    }

    public static String toInfo() {
        init();
        return "Keys:" + conf.keySet() + " Default: " + DEFAULT_KEY;
    }

}
