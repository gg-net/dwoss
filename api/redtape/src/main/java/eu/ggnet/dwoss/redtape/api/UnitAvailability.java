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
package eu.ggnet.dwoss.redtape.api;

import java.io.Serializable;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Unit Availability information.
 * Combinde information if a unit is avialable for sale.
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface UnitAvailability extends Serializable {

    class Builder extends UnitAvailability_Builder {
    };

    /**
     * The refurbishId of the unit.
     *
     * @return refurbishId of the unit.
     */
    String refurbishId();

    /**
     * Returns the avialable status, true meaning the unit is saleable.
     *
     * @return the avialable status.
     */
    boolean available();

    /**
     * Returns true if the unit exists.
     *
     * @return true if the unit exists.
     */
    boolean exists();

    /**
     * The backing unique unit.
     *
     * @return the backing unique unit id.
     */
    Optional<Long> uniqueUnitId();

    /**
     * Last refurbishId, if there is one in the history.
     *
     * @return last refurbishId.
     */
    Optional<String> lastRefurbishId();

    /**
     * Optional conflict description, if there exists one.
     *
     * @return conflict description.
     */
    Optional<String> conflictDescription();

    /**
     * Optional stock information, if unit is in stock.
     *
     * @return stock information, if unit is in stock.
     */
    Optional<String> stockInformation();

    /**
     * Returns optional stock id.
     *
     * @return stock id.
     */
    Optional<Integer> stockId();

}
