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
package eu.ggnet.dwoss.discovery;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver.guenther
 */
@Stateless
@Remote(Discovery.class)
public class DiscoveryBean implements Discovery {

    private static Logger L = LoggerFactory.getLogger(DiscoveryBean.class);

    /**
     * Returns a list of all name mappings in the namespace prefix.
     *
     * @param prefix the namespace
     * @return a list of all name mappings in the namespace java:app, or an empty list.
     */
    @Override
    public List<String> allJndiNames(String prefix) {
        try {
            List<String> result = inspect(new InitialContext(), prefix).stream().map(np -> np.getName()).collect(Collectors.toList());
            L.debug("Found {} elements", result.size());
            return result;
        } catch (NamingException ex) {
            L.warn("Naming Inspection not succesfull, returning empty list", ex);
            return new ArrayList<>();
        }
    }

    /**
     * Inspects a supplied context (Used in the Server, normally it could be private)
     *
     * @param context
     * @param prefixes
     * @return
     */
    private static List<NameClassPair> inspect(Context context, String... prefixes) {
        Objects.requireNonNull(context, "Context must not be null");
        Objects.requireNonNull(prefixes, "At least one prefix must be supplied");

        List<NameClassPair> result = new ArrayList<>();
        for (String prefix : prefixes) {
            try {
                NamingEnumeration<NameClassPair> list = context.list(prefix);
                while (list != null && list.hasMore()) {
                    try {
                        result.add(list.next());
                    } catch (NamingException ex) {
                    }
                }
            } catch (NamingException ex) {
            }
        }
        return result;
    }

}
