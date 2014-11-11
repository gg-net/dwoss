/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.price.api;

import java.io.Serializable;

import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import lombok.Value;

/**
 * Interface to supply your one rules for price estimation of units.
 * <p>
 * @author oliver.guenther
 */
public interface Estimator {

    @Value
    public static class Result implements Serializable {

        /**
         * A one word tag about what happend.
         * Something Like NotebookEstimator or FallbackSystem.
         */
        private final String tag;

        private final EngineTracer tracer;

        private final double retailerPrice;

        private final double customerPrice;

        private final double retailerToCustomerPricePercentage;

    }

    /**
     * Estimates based on spec and unit.
     * <p>
     * @param spec the spec
     * @param unit the unit
     * @return the result
     */
    Result estimate(ProductSpec spec, UniqueUnit unit);
}
