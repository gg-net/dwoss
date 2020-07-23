/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * All connection arguments for startup containing host,port,user,pass and application.
 *
 * @author oliver.guenther
 */
@Parameters(separators = "=")
public class ConnectionParameter {

    @Parameter(names = "--protocol", description = "Protocol to connect to the Wildfly server, either remote+https or remote+http")
    private String protocol = "remote+https";

    @Parameter(names = "--host", description = "Hostname of the Wildfly server", required = true)
    private String host;

    @Parameter(names = "--port", description = "Port of the Wildfly server")
    private int port = 443;

    @Parameter(names = "--app", description = "Applikation name on the Wildfly server, normally the name of the deployed war", required = true)
    private String app;

    @Parameter(names = "--user", description = "Useraccount to connect to the Wildfly server", required = true)
    private String user;

    @Parameter(names = "--pass", description = "Password to connect to the Wildfly server", required = true)
    private String pass;

    /**
     * Special parameter to disable the remote connection entierly.
     * Used in the tryout contruct.
     */
    @Parameter(names = "--disableRemote", hidden = true)
    private boolean disableRemote = false;

    /**
     * Returns the hostname of the wildfly server.
     *
     * @return the hostname of the wildfly server.
     */
    public String host() {
        return host;
    }

    /**
     * Returns the port of the wildfly server.
     *
     * @return the port of the wildfly server.
     */
    public int port() {
        return port;
    }

    /**
     * Returns the app of the wildfly server.
     * This is the root path the deployed application is reachable.
     * By default it's the name of the war.
     *
     * @return the app of the wildfly server.
     */
    public String app() {
        return app;
    }

    /**
     * Returns the username for the connection to the wildfly server.
     *
     * @return the username for the connection to the wildfly server.
     */
    public String user() {
        return user;
    }

    /**
     * Returns the password for the connection to the wildfly server.
     *
     * @return the password for the connection to the wildfly server.
     */
    public String pass() {
        return pass;
    }

    public String protocol() {
        return protocol;
    }

    /**
     * If true, no remote connection should be enabled at all.
     * This is a special hidden parameter only usefull in tryouts. If set to
     * true, no remote connection will be created. See {@link ClientApplication#initRemoteConnection() }.
     * <p>
     * This also implies that before the Remoteconnection must be set. See tryout.ClientTryout.
     *
     * @return
     */
    public boolean disableRemote() {
        return disableRemote;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String toUrl() {
        return protocol + "://" + host + ":" + port + "/" + app;
    }

}
