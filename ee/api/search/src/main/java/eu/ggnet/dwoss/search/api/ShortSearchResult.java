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
package eu.ggnet.dwoss.search.api;

import java.io.Serializable;

import lombok.Value;

/**
 * The short result of a search.
 *
 * @author oliver.guenther
 */
@Value
public class ShortSearchResult implements Serializable {

    /**
     * Global Key of the result.
     */
    private final GlobalKey key;

    /**
     * A short description of the result.
     */
    private final String shortDescription;

}