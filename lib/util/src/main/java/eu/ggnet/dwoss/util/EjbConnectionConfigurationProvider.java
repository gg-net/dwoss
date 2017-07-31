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

import java.util.Map;

/**
 * Provides more possible Configuration for an Ejb Connection.
 * This is a way to keep the client configuration in the internal project, while the open source project only has the sample configuations.
 *
 * @author oliver.guenther
 */
public interface EjbConnectionConfigurationProvider {

    public Map<String, EjbConnectionConfiguration> getConfigurations();

}
