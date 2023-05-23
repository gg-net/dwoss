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
package eu.ggnet.dwoss.receipt.ee.gen;

import java.util.*;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.DefaultEao;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.receipt.ee.UnitProcessor;
import eu.ggnet.dwoss.spec.ee.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.stock.ee.StockTransactionProcessor;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.stock.ee.eao.StockTransactionEao;
import eu.ggnet.dwoss.stock.ee.emo.StockTransactionEmo;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApi;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.assist.gen.UniqueUnitGenerator;
import eu.ggnet.dwoss.uniqueunit.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.ee.op.ProductOperation;

import static eu.ggnet.dwoss.core.common.values.ProductGroup.COMMENTARY;
import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType.PREPARED;
import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionType.ROLL_IN;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class ReceiptGeneratorOperation {

    private final static Random R = new Random();

    private final Logger L = LoggerFactory.getLogger(ReceiptGeneratorOperation.class);

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    private StockGeneratorOperation stockGen;

    @EJB
    private ProductProcessor productProcessor;

    @EJB
    private UnitProcessor unitProcessor;

    @EJB
    private StockTransactionProcessor stockTransactionProcessor;

    @Inject
    private ProductOperation productOperation;

    @EJB
    private UniqueUnitAgent uniqueUnitAgent;

    @EJB
    private UniqueUnitApi uniqueUnitApi;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private StockTransactionEmo stockTransactionEmo;

    @Inject
    private StockTransactionEao stockTransactionEao;

    @Inject
    private Contractors contractors;

    private final UniqueUnitGenerator unitGenerator = new UniqueUnitGenerator();

    private final SpecGenerator specGenerator = new SpecGenerator();

    /**
     * Generates an amount of ProductSpecs and receipts them.
     * Persists entities in spec and uniqueunit.
     *
     * @param amount            the amount to generate.
     * @param generateCostPrice
     * @return the generated and receipted specs.
     */
    public List<ProductSpec> makeProductSpecs(int amount, boolean generateCostPrice) {
        L.info("Generating {} Products", amount);
        SubMonitor m = monitorFactory.newSubMonitor("Generating " + amount + " Products", amount);
        m.start();
        List<ProductSpec> specs = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ProductSpec spec = makeProductSpec(generateCostPrice);
            specs.add(spec);
            m.worked(1, spec.getPartNo());
        }
        m.finish();
        return specs;
    }

    /**
     * Generates one ProductSpec and receipts it.
     *
     * @param generateCostPrice if true generates a random costprice between 1 and 1000
     * @return the generated ProductSpec.
     */
    private ProductSpec makeProductSpec(boolean generateCostPrice) {
        ProductSpec spec = specGenerator.makeSpec();
        L.debug("Persisting {}", spec);
        eu.ggnet.dwoss.uniqueunit.api.ShopCategory sc = null;
        if ( R.nextDouble() >= 0.7 ) {
            List<eu.ggnet.dwoss.uniqueunit.api.ShopCategory> scs = uniqueUnitApi.findAllShopCategories();
            if ( !scs.isEmpty() ) {
                sc = scs.get(R.nextInt(scs.size()));
            }
        }
        spec = productProcessor.create(new SpecAndModel(spec, spec.getModel(), 0, sc, false));
        if ( generateCostPrice ) productOperation.updatePrice(spec.getProductId(), PriceType.MANUFACTURER_COST,
                    R.nextInt(1000) + 1, "Generated by ReceiptGeneratorOperation.makeProductSpec()");
        return spec;
    }

    public eu.ggnet.dwoss.uniqueunit.api.ShopCategory makeShopCategory() {
        ShopCategory sc = new ShopCategory();
        int sid = R.nextInt(1000);
        sc.setName("Generierte Category-" + sid);
        sc.setShopId(sid);
        uuEm.persist(sc);
        uuEm.flush();
        return sc.toApi();
    }

    /**
     * Generates one ProductSpec and receipts it.
     *
     * @return the generated ProductSpec.
     */
    public ProductSpec makeProductSpec() {
        return makeProductSpec(false);
    }

    /**
     * Generates an amount of UniquUnits and receipts them.
     *
     * @param amount               the amount to generate.
     * @param generateSalesChannel
     * @param generatePrice        if true a random customer and retailer price is set
     * @return the generated and receipted UniquUnits.
     */
    // TODO: Create more Shipments on multiple contractors.
    public List<UniqueUnit> makeUniqueUnits(int amount, boolean generateSalesChannel, boolean generatePrice) {
        final int minProducts = 5;
        final int maxProducts = 450;
        int amountProducts = (amount / 25);
        if ( amountProducts <= minProducts ) amountProducts = minProducts;
        if ( amountProducts >= maxProducts ) amountProducts = maxProducts;

        L.info("Generating {} Units", amount);
        SubMonitor m = monitorFactory.newSubMonitor("Generating " + amount + " Units", amount);
        m.start();
        List<ProductSpec> productSpecs = makeProductSpecs(amountProducts, generatePrice);

        List<UniqueUnit> units = new ArrayList<>();
        Stock stock = findOrMakeStock();
        TradeName contractor = new ArrayList<>(contractors.all()).get(R.nextInt(contractors.all().size()));

        Shipment shipment = new Shipment("TEST-SHIPMENT-" + R.nextInt(10), contractor, TradeName.ACER, Shipment.Status.OPENED);
        stockEm.persist(shipment);

        for (int i = 0; i < amount; i++) {
            ProductSpec productSpec = productSpecs.get(R.nextInt(productSpecs.size()));
            Product product = uniqueUnitAgent.findById(Product.class, productSpec.getProductId());
            UniqueUnit unit = unitGenerator.makeUniqueUnit(contractor, product);
            m.worked(1, "created SopoNr " + unit.getRefurbishId());
            if ( generatePrice ) {
                int price = R.nextInt(1000) + 1;
                unit.setPrice(PriceType.CUSTOMER, price, "Generated by ReceiptGeneratorOperation.makeUniqueUnits()");
                unit.setPrice(PriceType.RETAILER, price * 1.08, "Generated by ReceiptGeneratorOperation.makeUniqueUnits()");
            }
            if ( generateSalesChannel ) unit.setSalesChannel(R.nextBoolean() ? SalesChannel.CUSTOMER : SalesChannel.RETAILER);
            unitProcessor.receipt(unit, product, shipment, stock.getId(), ReceiptOperation.SALEABLE, "SampleGenerator", "Generator");
            units.add(uniqueUnitAgent.findUnitByIdentifierEager(REFURBISHED_ID, unit.getRefurbishId())); // Ad the now persisted instance.
        }

        // Transactions are created while receipt
        List<StockTransaction> transactions = stockTransactionEao.findByTypeAndStatus(ROLL_IN, PREPARED);
        stockTransactionProcessor.rollIn(transactions, "Generator");
        m.finish();
        return units;
    }

    /**
     * Generates one UniquUnits and receipts it.
     *
     * @return the generated UniquUnits.
     */
    public UniqueUnit makeUniqueUnit() {
        Stock stock = findOrMakeStock();
        TradeName contractor = new ArrayList<>(contractors.all()).get(R.nextInt(contractors.all().size()));
        Shipment shipment = new Shipment("TEST-SHIPMENT-" + R.nextInt(10), contractor, TradeName.ACER, Shipment.Status.OPENED);
        L.info("Shipment {}", shipment.toString());
        stockEm.persist(shipment);

        ProductSpec productSpec = makeProductSpec();
        L.info("ProductSpec {}", productSpec.toString());

        Product product = uniqueUnitAgent.findById(Product.class, productSpec.getProductId());
        L.info("Product {}", product.toString());

        UniqueUnit unit = unitGenerator.makeUniqueUnit(contractor, product);
        L.info("UniqueUnit {}", unit.toString());

        unitProcessor.receipt(unit, product, shipment, stock.getId(), ReceiptOperation.SALEABLE, "SampleGenerator", "Generator");

        List<StockTransaction> transactions = stockTransactionEao.findByTypeAndStatus(ROLL_IN, PREPARED);
        stockTransactionProcessor.rollIn(transactions, "Generator");

        return uniqueUnitAgent.findUnitByIdentifierEager(REFURBISHED_ID, unit.getRefurbishId());
    }

    public void makeWarrantyProduct(String partNumber) {
        Product p = new Product();
        p.setPartNo(partNumber);
        p.setTradeName(TradeName.ACER);
        p.setName("Garantieerweiterung auf 2 Jahre, Seriennummer: ");
        p.setDescription("Mit dem Kauf der Garantieerweiterung wird bestätigt, "
                + "dass die angegebene e-mail Addresse für die Aktivierung ausschließlich an die 123 weitergegeben wird."
                + " Seriennummer des Gerätes: ");
        p.setGroup(COMMENTARY);
        uuEm.persist(p);
    }

    private Stock findOrMakeStock() {
        List<Stock> stocks = new DefaultEao<>(Stock.class, stockEm).findAll();
        if ( !stocks.isEmpty() ) return stocks.get(0);
        return stockGen.makeStocksAndLocations(1).get(0);
    }
}
