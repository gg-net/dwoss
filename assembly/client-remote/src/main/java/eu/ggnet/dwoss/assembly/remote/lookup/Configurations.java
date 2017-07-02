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

/**
 *
 * @author oliver.guenther
 */
public class Configurations {

    private final static Map<String, LookupConfig> CONF;

    private final static LookupConfig DEFAULT;

    // TODO: Move Configs of produktive systems to dw pro an make service Lookup here.
    static {
        Map<String, LookupConfig> c = new HashMap<>();
        c.put("ggnet", LookupConfig.builder()
                .host("retrax.cybertron.global")
                .port(8080)
                .username("admin")
                .password("admin")
                .app("dwoss-server")
                .build());
        c.put("elus", LookupConfig.builder()
                .host("retrax.cybertron.global")
                .port(9080)
                .username("admin")
                .password("admin")
                .app("dwoss-server")
                .build());
        c.put("local", LookupConfig.builder()
                .host("localhost")
                .port(8080)
                .username("admin")
                .password("admin")
                .app("dwoss-server")
                .build()
        );

        CONF = Collections.unmodifiableMap(c);
        DEFAULT = null;
    }

    public static boolean containsConfig(String key) {
        return CONF.keySet().contains(key);
    }

    public static LookupConfig getConfigOrDefault(String key) {
        if ( !containsConfig(key) ) return DEFAULT;
        return CONF.get(key);
    }

    public static String toInfo() {
        return "Keys:" + CONF.keySet() + " Default: ggnet";
    }

}
