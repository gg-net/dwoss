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

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.assembly.remote.lookup.LookupConfig;
import eu.ggnet.dwoss.assembly.remote.lookup.WildflyLookup;
import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

/**
 *
 * @author olive
 */
public class TestWildfly {

    public static Logger L = LoggerFactory.getLogger(TestWildfly.class);

    public static void main(String[] args) throws Exception {
        // tryClassicJndi();
        // tryEjbJndi();
        tryRemoteLookupImplementation();
    }

    public static void tryEjbJndi() throws Exception {
        final Properties env = new Properties();
        env.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        InitialContext remoteContext = new InitialContext(env);
        Object lookupObject = remoteContext.lookup("ejb:/dwoss-server//MandatorSupporterBean!eu.ggnet.dwoss.mandator.MandatorSupporter");
        MandatorSupporter supporter = (MandatorSupporter)lookupObject;
        Mandator mandator = supporter.loadMandator();

        System.out.println(mandator);
        System.out.println(mandator.getDocumentIntermix().toMultiLine());
    }

    public static void tryClassicJndi() throws Exception {
        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
        env.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");

// the property below is required ONLY if there is no ejb client configuration loaded (such as a
// jboss-ejb-client.properties in the class path) and the context will be used to lookup EJBs
        env.put("jboss.naming.client.ejb.context", true);
        InitialContext remoteContext = new InitialContext(env);

        Object lookupObject = remoteContext.lookup("dwoss-server/MandatorSupporterBean!eu.ggnet.dwoss.mandator.MandatorSupporter");

        MandatorSupporter supporter = (MandatorSupporter)lookupObject;
        Mandator mandator = supporter.loadMandator();

        System.out.println(mandator);
        System.out.println(mandator.getDocumentIntermix().toMultiLine());
    }

    public static void tryRemoteLookupImplementation() {
        WildflyLookup l = new WildflyLookup(LookupConfig.builder()
                .host("localhost")
                .port(8080)
                .username("admin")
                .password("admin")
                .app("dwoss-server")
                .build());

        MandatorSupporter supporter = l.lookup(MandatorSupporter.class);
        Mandator mandator = supporter.loadMandator();

        System.out.println(mandator);
        System.out.println(mandator.getDocumentIntermix().toMultiLine());
    }

}
