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
package tryout;

import java.util.List;
import java.util.Properties;

import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.auth.client.*;

import eu.ggnet.dwoss.discovery.Discovery;

/**
 *
 * @author olive
 */
public class TryoutWildflyConnection {

    public static Logger L = LoggerFactory.getLogger(TryoutWildflyConnection.class);

    public static void main(String[] args) throws Exception {
        tryEjbJndi("remote+https", "retrax.cybertron.global", 443, "dw-ggnet", "dwapp", "dwuserapp");
    }

    public static void tryEjbJndi(String protocol, String host, int port, String app, String username, String password) throws Exception {

        AuthenticationConfiguration ejbConfig = AuthenticationConfiguration.empty().useName(username).usePassword(password);
        AuthenticationContext authContext = AuthenticationContext.empty().with(MatchRule.ALL.matchHost(host), ejbConfig);

        AuthenticationContext.getContextManager().setGlobalDefault(authContext);

        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        properties.put(Context.PROVIDER_URL, protocol + "://" + host + ":" + port);

        // create an InitialContext
        InitialContext c = new InitialContext(properties);

        final String APP = app;
        Object instance = null;
        String discoveryName = "ejb:/" + APP + "//" + Discovery.NAME;
        L.debug("Trying lookup of {} ", discoveryName);
        try {
            instance = c.lookup(discoveryName);
        } catch (NamingException ex) {
            throw new RuntimeException("Error on frist lookup", ex);
        }
        L.info("Lookup of {} sucessfull", discoveryName);
        Discovery discovery = (Discovery)instance;
        List<String> result = discovery.allJndiNames("java:app/" + APP);
        L.debug("Discovery returned {} raw entries", result.size());

        System.out.println("-----------------");
        System.out.println("Names are working");
        System.out.println("");
        result.forEach(System.out::println);

    }

}
