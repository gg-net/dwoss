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
 * All connection arguments needed for the connection.
 *
 * @author oliver.guenther
 */
@Parameters(separators = "=")
public class ConnectionParameter {

    @Parameter(names = "--host", description = "Hostname of the Wildfly server", required = true)
    private String host;

    @Parameter(names = "--port", description = "Port of the Wildfly server")
    private int port = 8080;

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

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String app() {
        return app;
    }

    public String user() {
        return user;
    }

    public String pass() {
        return pass;
    }

    public boolean disableRemote() {
        return disableRemote;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
