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
package eu.ggnet.dwoss.stock.ee.emo;

import java.util.Set;

import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.dwoss.stock.ee.entity.LogicTransaction;

/**
 * Result for Equilibration.
 * <p>
 * @author oliver.guenther
 */
@FreeBuilder
public interface EquilibrationResult {

    class Builder extends EquilibrationResult_Builder {};
    
    /**
     * UniqueUnit.ids referencing StockUnits which were actually added.
     * @return 
     */
    Set<Integer> added();

    /**
     * UniqueUnit.ids referencing StockUnits which were actually removed.
     * @return 
     */
    Set<Integer> removed();

    /**
     * The resulting logic transaction.
     * @return 
     */
    LogicTransaction transaction();
}
