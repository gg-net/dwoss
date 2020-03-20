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
package eu.ggnet.dwoss.discovery;

import java.util.List;

/**
 *
 * @author oliver.guenther
 */
public interface Discovery {

    /**
     * The Lookup Name.
     */
    public final static String NAME = DiscoveryBean.class.getSimpleName() + "!" + Discovery.class.getName();

    /**
     * Returns a list of all name mappings in the namespace prefix.
     *
     * @param prefix the namespace
     * @return a list of all name mappings in the namespace java:app, or an empty list.
     */
    List<String> allJndiNames(String prefix);

}
