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
package eu.ggnet.dwoss.mandator.api.service;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.*;

/**
 *
 * @author pascal.perau
 */
public class ClientLocation implements Serializable {

    private final Set<InetAddress> inetAdresses;

    public ClientLocation(Set<InetAddress> inetAdresses) {
        this.inetAdresses = new HashSet<>(inetAdresses);
    }

    public Set<InetAddress> getInetAdresses() {
        return Collections.unmodifiableSet(inetAdresses);
    }

    @Override
    public String toString() {
        return "ClientLocation{" + "inetAdresses=" + inetAdresses + '}';
    }

}
