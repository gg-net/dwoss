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
package eu.ggnet.saft.core;

/**
 * LookupTool for the Client to supply some form of remote lookup.
 * This lookup concept assumes, that interfaces are used as key to discover a possible remote instance and also represents the implementation.
 * Therefore there can only be one interface per implementation.
 *
 * @author oliver.guenther
 */
public interface RemoteLookup {

    /**
     * Verifies if a supplied class can be discoverd with this lookup.
     *
     * @param <T>   type of the clazz
     * @param clazz interface class token as key
     * @return true if the supplied interface has a remote implementation available
     */
    <T> boolean contains(Class<T> clazz);

    /**
     * Returns the local endpoint for a remote connection.
     *
     * @param <T>   type of the clazz
     * @param clazz interface class token as key
     * @return the local endpoint for a remote connection or null if none found.
     */
    <T> T lookup(Class<T> clazz);
}
