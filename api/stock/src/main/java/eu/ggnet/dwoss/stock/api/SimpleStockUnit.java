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
package eu.ggnet.dwoss.stock.api;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Simple representation of an Stockunit
 *
 * @author oliver.guenther
 */
@FreeBuilder
public interface SimpleStockUnit extends Serializable {

    class Builder extends SimpleStockUnit_Builder {
    };

    /**
     * Database id.
     *
     * @return the database id
     */
    long id();

    /**
     * Referencing unique unit id.
     *
     * @return the referencing uniqueUnit id
     */
    long uniqueUnitId();

    /**
     * A short description
     *
     * @return a short description
     */
    String shortDescription();

    /**
     * Returns true if stock unit is on a logic transaction.
     *
     * @return true if stock unit is on a logic transaction
     */
    boolean onLogicTransaction();

    /**
     * A optional stock, if the unit is on a stock.
     *
     * @return the optional stock
     */
    Optional<PicoStock> stock();

    /**
     * An optional stockTransaction, if the unit is in motion.
     *
     * @return the optional stock transaction.
     */
    Optional<SimpleStockTransaction> stockTransaction();

    /**
     * Returns a list of possible destinations
     *
     * @return
     */
    List<PicoStock> possibleDestinations();

}
