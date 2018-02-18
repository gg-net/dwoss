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
package eu.ggnet.dwoss.price;

import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

import java.util.Map.Entry;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.price.engine.PriceEngine;
import eu.ggnet.dwoss.price.engine.PriceEngineResult;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.rules.Warranty;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.ProductSpecEao;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.saft.api.progress.IMonitor;

import lombok.Data;

import static eu.ggnet.dwoss.price.engine.PriceEngineResult.Change.*;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;

/**
 * Contains all business methods for price generaimport static de.dw.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
 * tion, import and export
 *
 * @author oliver.guenther
 */
@Stateless
public class PriceCoreOperation {

    @Data
    private static class SuperProduct {

        private final Product product;

        private ProductSpec spec;
    }

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @Specs
    private EntityManager specEm;

    @Inject
    private PriceEngine priceEngine;

    @Inject
    private Mandator mandator;

    private final Logger L = LoggerFactory.getLogger(PriceCoreOperation.class);

    /**
     * Loads all AVAILABLE SopoUnits from the Sopodb an puts them trough the PriceEngine
     *
     * @param monitor
     * @return
     */
    public List<PriceEngineResult> loadAndCalculate(IMonitor monitor) {
        L.info("Starting loadAndCalculate()");
        final SubMonitor m = SubMonitor.convert(monitor, 100);
        m.start();
        final StockUnitEao stockUnitEao = new StockUnitEao(stockEm);
        final UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        final ProductSpecEao productSpecEao = new ProductSpecEao(specEm);

        m.message("loading Units");
        List<Integer> uuids = stockUnitEao.findByNoLogicTransactionAsUniqueUnitId();
        List<UniqueUnit> uus = uniqueUnitEao.findByIds(uuids);

        m.worked(10, "updating Eols");
        updateEols(uus);

        m.worked(5, "loading ProductSpecs");
        Set<Product> products = toProducts(uus);
        List<ProductSpec> productSpecs = productSpecEao.findByProductIds(toProductIds(products));
        Map<Product, ProductSpec> productToSpecs = toProductProductSpec(products, productSpecs);

        m.worked(10);
        final List<PriceEngineResult> pers = new ArrayList<>();
        m.setWorkRemaining(uus.size() + 5);
        for (UniqueUnit uu : uus) {
            m.worked(1, "Calculating RefurbishId(" + uu.getRefurbishId() + ")");
            StockUnit su = stockUnitEao.findByUniqueUnitId(uu.getId());
            pers.add(priceEngine.estimate(uu, productToSpecs.get(uu.getProduct()), su.getStock() != null ? su.getStock().getName() : "kein Lager"));
        }
        m.finish();
        L.info("Finished loadAndCalculate(), estimated {} Units", pers.size());
        return pers;
    }

    /**
     * Stores the supplied Prices to units and the manufacturerPartNoPriceFixeds
     *
     * @param pers     results to store
     * @param comment  a comment for the price history
     * @param arranger a arranger for the price history
     * @param monitor  an optional monitor
     */
    public void store(final List<PriceEngineResult> pers, String comment, String arranger, IMonitor monitor) {
        final SubMonitor m = SubMonitor.convert(monitor, pers.size() + 27);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        ProductEao productEao = new ProductEao(uuEm);
        // preload sopo and unique units
        m.message("Preloading UniqueUnits");
        NavigableMap<String, UniqueUnit> uniqueUnits = UniqueUnit.asMapByRefurbishId(uniqueUnitEao.findByIdentifiers(REFURBISHED_ID, PriceEngineResult.toRefurbishIds(pers)));
        m.worked(5);

        for (PriceEngineResult per : pers) {
            String msg = "Storing Unit " + per.getRefurbishedId() + " HP:" + per.getRetailerPrice()
                    + " EP:" + per.getCustomerPrice() + " UnitFix:" + per.getUnitPriceFixed() + " ProductFix:" + per.getManufacturerPartPriceFixed();
            L.info(msg);
            m.worked(1, msg);
            update(uniqueUnits.get(per.getRefurbishedId()), per, arranger, comment);
        }
        //Inferenced filtering for fixprices
        Map<String, PriceEngineResult> fixPriceImports = new HashMap<>();
        for (PriceEngineResult per : pers) {
            if ( per.getManufacturerPartPriceFixed() == NO_CHANGE ) continue;
            fixPriceImports.put(per.getManufacturerPartNo(), per);
        }
        m.worked(1, "Perloading Products");
        NavigableMap<String, Product> products = Product.asMapByPartNos(productEao.findByPartNos(PriceEngineResult.toPartNos(pers)));
        m.worked(3);
        m.setWorkRemaining(fixPriceImports.size());
        for (PriceEngineResult per : fixPriceImports.values()) {
            update(products.get(per.getManufacturerPartNo()), per, arranger, comment);
            String msg = "Storing ProductDescription Fixed Price " + per.getProductName() + " Retailer:" + per.getRetailerPrice() + " Customer:" + per.getCustomerPrice() + " Manual:" + per.getManufacturerPartPriceFixed();
            L.info(msg);
            m.worked(1, msg);
        }
        m.finish();
    }

    private void update(UniqueUnit uniqueUnit, PriceEngineResult per, String arranger, String comment) {
        if ( uniqueUnit == null ) return;
        if ( per.getUnitPriceFixed() == SET ) {
            uniqueUnit.addFlag(UniqueUnit.Flag.PRICE_FIXED);
        } else if ( per.getUnitPriceFixed() == UNSET ) {
            uniqueUnit.removeFlag(UniqueUnit.Flag.PRICE_FIXED);
        }
        String type = "estimated";
        if ( per.getUnitPriceFixed() == SET ) type = "unitfixed";
        /* TODO: This is incomplete, because I only get new fixed infos.
         * If a product was fixed in a run before, this is type estimated.
         * Whould need to import this information allso. (Need to change the report itself and the export) */
        if ( per.getManufacturerPartPriceFixed() == SET ) type = "productfixed";

        uniqueUnit.setPrice(eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.CUSTOMER, per.getCustomerPrice(), arranger + " - " + type + ", " + comment);
        uniqueUnit.setPrice(eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.RETAILER, per.getRetailerPrice(), arranger + " - " + type + ", " + comment);
        uniqueUnit.setWarranty(Warranty.values()[per.getWarrantyId()]);
    }

    private void update(Product product, PriceEngineResult per, String arranger, String comment) {
        if ( product == null ) return;
        product.setPrice(PriceType.RETAILER, per.getRetailerPrice(), arranger + " - productfix," + comment);
        product.setPrice(PriceType.CUSTOMER, per.getCustomerPrice(), arranger + " - productfix," + comment);
        if ( per.getManufacturerPartPriceFixed() == SET ) product.addFlag(Product.Flag.PRICE_FIXED);
        if ( per.getManufacturerPartPriceFixed() == UNSET ) product.removeFlag(Product.Flag.PRICE_FIXED);
    }

    private Set<Product> toProducts(List<UniqueUnit> uus) {
        Set<Product> products = new HashSet<>();
        for (UniqueUnit uniqueUnit : uus) {
            products.add(uniqueUnit.getProduct());
        }
        return products;
    }

    private Set<Long> toProductIds(Set<Product> products) {
        Set<Long> productIds = new HashSet<>();
        for (Product product : products) {
            productIds.add(product.getId());
        }
        return productIds;
    }

    private Map<Product, ProductSpec> toProductProductSpec(Set<Product> products, List<ProductSpec> productSpecs) {
        Map<Product, ProductSpec> productToSpecs = new HashMap<>();
        for (Product product : products) {
            for (ProductSpec productSpec : productSpecs) {
                if ( product.getId() == productSpec.getProductId() ) {
                    productToSpecs.put(product, productSpec);
                    break;
                }
            }
        }
        return productToSpecs;
    }

    private void updateEols(List<UniqueUnit> uus) {
        Map<Product, Date> productEols = new HashMap<>();
        for (UniqueUnit uu : uus) {
            if ( !productEols.containsKey(uu.getProduct()) || productEols.get(uu.getProduct()).before(uu.getMfgDate()) ) {
                productEols.put(uu.getProduct(), uu.getMfgDate());
            }
        }
        for (Entry<Product, Date> entry : productEols.entrySet()) {
            Date eol = DateUtils.addMonths(entry.getValue(), 3);
            if ( entry.getKey().getEol() == null ) entry.getKey().setEol(eol);
            else if ( entry.getKey().getEol().before(eol) ) entry.getKey().setEol(eol);
        }
    }
}
