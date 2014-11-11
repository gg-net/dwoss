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
package eu.ggnet.dwoss.price.engine;

import eu.ggnet.dwoss.rules.Warranty;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import java.util.*;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.slf4j.*;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.price.api.Estimator.Result;


import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.price.api.EngineTracer;
import eu.ggnet.dwoss.price.api.Estimator;
import eu.ggnet.dwoss.price.engine.support.TraceCollector;

import static eu.ggnet.dwoss.price.engine.PriceEngineResult.Change.SET;

/**
 * This is the PriceEngine 2 of GG-Net.
 * Workflow simplified:
 * - For every SopoUnit
 * - Validate Unit
 * - Validate Part
 * - If Unit or Part has FixedPrice, use it
 * - Else find Estimator by CommodityGroup and use it (Part)
 * - If no Estimator is found or if Estimation is not usable use the FallBack (By Costprice)
 * - Regulate the Result (Part and Unit)
 *
 * @author oliver.guenther
 */
public class PriceEngine {

    private final static Logger L = LoggerFactory.getLogger(PriceEngine.class);

    @Inject
    private Instance<Estimator> estimator;

    /**
     * Executes the Estimation for one SopoUnit.
     * This Method never fails an always returns a usable not null PriceEngineResult
     *
     * @param uu   the UniqueUnit
     * @param spec the ProductSpec
     * @return a estimated PriceEngineResult.
     */
    public PriceEngineResult estimate(UniqueUnit uu, ProductSpec spec) {
        Objects.requireNonNull(uu, "UniqueUnit is null");
        Product p = Objects.requireNonNull(uu.getProduct(), "Product of " + uu + " is null");
        Objects.requireNonNull(spec, "ProductSpec is null");

        PriceEngineResult per = new PriceEngineResult(uu);
        TraceCollector log = new TraceCollector();
        per.setTax(GlobalConfig.TAX);
        per.setWarrantyId(uu.getWarranty().ordinal());
        if ( uu.getWarranty().equals(Warranty.WARRANTY_TILL_DATE) ) {
            per.setWarrentyValid(uu.getWarrentyValid());
        }
        L.debug("Starting estimation for refuribisId:{}", uu.getRefurbishId());
        if ( uu.getFlags().contains(UniqueUnit.Flag.PRICE_FIXED) || p.getFlags().contains(Product.Flag.PRICE_FIXED) ) {
            EngineTracer et = new EngineTracer(PriceEngine.class.getSimpleName(), "estimate");
            if ( uu.getFlags().contains(UniqueUnit.Flag.PRICE_FIXED) ) {
                L.debug("refurbishId:{} has UnitPriceFixed", uu.getRefurbishId());
                per.setCustomerPrice(uu.getPrice(PriceType.CUSTOMER));
                per.setRetailerPrice(uu.getPrice(PriceType.RETAILER));
                per.setUnitPriceFixed(SET);
                per.setSpecial("FIX UNIT");
                et.info("UnitFixPrice");
            } else {
                L.debug("refurbishId:{} has PartPriceFixed", uu.getRefurbishId());
                per.setCustomerPrice(p.getPrice(PriceType.CUSTOMER));
                per.setRetailerPrice(p.getPrice(PriceType.RETAILER));
                per.setManufacturerPartPriceFixed(SET);
                per.setSpecial("FIX PRODUCT");
                et.info("ProductFixPrice");
            }
            log.add(et);
        } else if ( !estimator.isUnsatisfied() ) {
            Result result = estimator.get().estimate(spec, uu);
            L.debug("{}", result);
            log.add(result.getTracer());
            per.setSpecial(result.getTag());
            per.setRetailerPrice(result.getRetailerPrice());
            per.setCustomerPrice(result.getCustomerPrice());
            per.setRetailerToCustomerPricePercentage(result.getRetailerToCustomerPricePercentage());
        } else {
            L.warn("No Estimator found");
            per.setSpecial("NO ESTIMATION");
        }
        per.consumeLog(log);

        return per;
    }
}
