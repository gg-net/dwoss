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
package eu.ggnet.dwoss.util;

import org.inferred.freebuilder.FreeBuilder;


/**
 * Configuration for an EjbConnection.
 * Conntains enought parameters to be used for a wildfly client or a tomme client.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface EjbConnectionConfiguration {

    int port();

    String host();

    String username();

    String password();

    String app();
    
    class Builder extends EjbConnectionConfiguration_Builder {};

}
