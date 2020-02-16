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
package eu.ggnet.dwoss.uniqueunit.api;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Simple Unit representation for other api usages.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface SimpleUniqueUnit extends Serializable {

    class Builder extends SimpleUniqueUnit_Builder {
    };

    /**
     * Database id.
     * Info: The database representation uses int, but all new elements use long for a later possible switch.
     *
     * @return
     */
    long id();

    String refurbishedId();

    String shortDescription();

    /**
     * Is set if the unit had another refurbishId before.
     * If the unit was in our hands more than once, the refurbishId will change.
     *
     * @return an optional older refurbishId.
     */
    Optional<String> lastRefurbishId();

}
