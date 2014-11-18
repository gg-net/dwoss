/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.assembly.remote.provides;

import java.util.Objects;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.saft.core.Server;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = Server.class)
public class RemoteServer implements Server {

    public static String URL;

    @Override
    public Context getContext() {
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
        properties.setProperty(Context.PROVIDER_URL, Objects.requireNonNull(URL, "Remote Host URL is null"));
        try {
            return new InitialContext(properties);
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initialise() {
    }

    @Override
    public void shutdown() {
    }
}
