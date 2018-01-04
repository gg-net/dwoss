/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.saft;

import eu.ggnet.saft.core.dl.LocalDl;
import eu.ggnet.saft.core.dl.RemoteDl;


/**
 * Content Dependencie Injection Light.
 * Will be the replacement for the client.
 *
 * @author oliver.guenther
 */
public class Dl {

    /**
     * Returns local di connection, for service lookup.
     * Usage: Dl.local().lookup(X.class); <br />
     * It's like:
     * <ul>
     * <li>java std way ServiceLoader.load(X.class).iterator().next() for the first.</li>
     * <li>cdi: CDI.current().select(X.class).get()</li>
     * </ul>
     *
     * @return Local Di conection.
     */
    public static LocalDl local() {
        return LocalDl.getInstance();
    }

    /**
     * Returns remote di connection, for remote service lookup.
     * For discovering services on the remote side. In a lokal cdi environment this would look like:
     * CDI.current().select(RemoteLookup.class).get().lookup(X.class).
     *
     * @return remote di connection
     */
    public static RemoteDl remote() {
        return RemoteDl.getInstance();
    }

}
