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
