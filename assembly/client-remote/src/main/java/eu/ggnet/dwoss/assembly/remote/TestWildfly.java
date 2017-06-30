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
package eu.ggnet.dwoss.assembly.remote;

import java.util.*;

import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

/**
 *
 * @author olive
 */
public class TestWildfly {

    public static Logger L = LoggerFactory.getLogger(TestWildfly.class);

    public static void main(String[] args) throws Exception {
        tryClassicJndi();
    }

    public static void tryClassicJndi() throws Exception {
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        env.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

// the property below is required ONLY if there is no ejb client configuration loaded (such as a
// jboss-ejb-client.properties in the class path) and the context will be used to lookup EJBs
        env.put("jboss.naming.client.ejb.context", true);
        InitialContext remoteContext = new InitialContext(env);

        NavigableSet<String> inspector = inspectJndiTreeForModuleNames("java:module", remoteContext);
        System.out.println("Inspecting Result: ");
        for (String string : inspector) {
            System.out.println(string);
        }

        Object lookupObject = remoteContext.lookup("MandatorSupporter!eu.ggnet.dwoss.mandator.MandatorSupporterBean");

        MandatorSupporter supporter = (MandatorSupporter)lookupObject;
        Mandator mandator = supporter.loadMandator();

        System.out.println(mandator);

    }

    public static NavigableSet<String> inspectJndiTreeForModuleNames(String prefix, Context context) {
        NavigableSet<String> result = new TreeSet<>();
        try {
            NamingEnumeration<NameClassPair> list = context.list(prefix);
            while (list != null && list.hasMore()) {
                try {
                    String name = list.next().getName();
                    if ( name.contains("EjbModule") || name.contains("com.sun.javafx") ) continue; // Ignoring some values
                    String[] split = name.split("!");
                    if ( split.length == 1 ) {
                        L.debug("Storing in projects {}", prefix + "/" + name);
                        result.add(prefix + "/" + name);
                    }
                } catch (NamingException ex) {
                    L.warn("Jndi Tree Module Name inspection on suffix {} failed: {}", prefix, ex.getMessage());
                }
            }
        } catch (NamingException ex) {
            L.warn("Jndi Tree Module Name inspection on Suffix {} failed: {}", prefix, ex.getMessage());
        }
        return result;
    }

}
