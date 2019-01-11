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

import java.util.List;
import java.util.Properties;

import java.util.concurrent.Callable;

import javax.naming.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.security.auth.client.*;

import eu.ggnet.dwoss.util.EjbConnectionConfiguration;
import eu.ggnet.dwoss.assembly.remote.lookup.WildflyLookup;
import eu.ggnet.dwoss.discovery.Discovery;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.mandator.ee.Mandators;

/**
 *
 * @author olive
 */
public class TestWildfly {

    public static Logger L = LoggerFactory.getLogger(TestWildfly.class);

    public static void main(String[] args) throws Exception {

        EjbConnectionConfiguration c = EjbConnectionConfiguration.builder()
                .host("localhost")
                .port(8080)
                .username("admin")
                .password("admin")
                .app("dwoss-server")
                .build();

       // tryEjbJndi(c2);
        tryRemoteLookupImplementation(c);
    }

    public static void tryEjbJndi(EjbConnectionConfiguration config) throws Exception {

        AuthenticationConfiguration ejbConfig = AuthenticationConfiguration.empty().useName(config.getUsername()).usePassword(config.getPassword());
        AuthenticationContext authContext = AuthenticationContext.empty().with(MatchRule.ALL.matchHost(config.getHost()), ejbConfig);
        
        AuthenticationContext.getContextManager().setGlobalDefault(authContext);

        Properties properties = new Properties();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        properties.put(Context.PROVIDER_URL, "remote+http://" + config.getHost() + ":" + config.getPort());

        Callable<List<String>> nameDiscovery = () -> {

            // create an InitialContext
            InitialContext c = new InitialContext(properties);

            final String APP = config.getApp();
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
            return result;
        };

        List<String> names = authContext.runCallable(nameDiscovery);
        System.out.println("Names are working");
        System.out.println(names);

        Callable<Mandators> lookUpMandators = () -> {

            // create an InitialContext
            InitialContext c = new InitialContext(properties);

            final String APP = config.getApp();
            Mandators instance = null;
            String name = "ejb:/" + APP + "//MandatorsBean!eu.ggnet.dwoss.mandator.ee.Mandators";
            L.debug("Trying lookup of {} ", name);
            try {
                instance = (Mandators)c.lookup(name);
            } catch (NamingException ex) {
                throw new RuntimeException("Error on frist lookup", ex);
            }

            L.info("calling Method in Context");
            Mandator mandator = instance.loadMandator();

            System.out.println(mandator);

            return instance;
        };

        L.info("calling Method out of Context");

        Mandators supporter = authContext.runCallable(lookUpMandators);
        Mandator mandator = supporter.loadMandator();

        System.out.println(mandator);
        System.out.println(mandator.getDocumentIntermix().toMultiLine());

//        
//        final Properties env = new Properties();
//        env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
//        InitialContext remoteContext = new InitialContext(env);
//        Object lookupObject = remoteContext.lookup("ejb:/dwoss-server//MandatorSupporterBean!eu.ggnet.dwoss.mandator.MandatorSupporter");
//        Mandators supporter = (Mandators)lookupObject;
//        Mandator mandator = supporter.loadMandator();
//
//        System.out.println(mandator);
//        System.out.println(mandator.getDocumentIntermix().toMultiLine());
    }

    public static void tryRemoteLookupImplementation(EjbConnectionConfiguration c) {
        WildflyLookup l = new WildflyLookup(c);

        Mandators supporter = l.lookup(Mandators.class);
        Mandator mandator = supporter.loadMandator();

        System.out.println(mandator);
        System.out.println(mandator.getDocumentIntermix().toMultiLine());

        DefaultCustomerSalesdata sd = supporter.loadSalesdata();

        System.out.println(sd);

    }

}
