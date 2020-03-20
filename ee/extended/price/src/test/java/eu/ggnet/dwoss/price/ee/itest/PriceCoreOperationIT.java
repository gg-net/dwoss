package eu.ggnet.dwoss.price.ee.itest;

import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceHistory;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.price.ee.PriceCoreOperation;
import eu.ggnet.dwoss.price.ee.engine.PriceEngineResult;
import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class PriceCoreOperationIT extends ArquillianProjectArchive {

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private PriceCoreOperation priceCore;

    @Inject
    private StockGeneratorOperation stockGenerator;

    @EJB
    private UniqueUnitAgent uniqueUnitAgent;

    @Test
    public void testStore() {
        stockGenerator.makeStocksAndLocations(2);
        receiptGenerator.makeUniqueUnits(20, true, false);

        // Estimate all Units
        List<PriceEngineResult> pers = priceCore.loadAndCalculate(null);

        final double fixedUnitPrice = 100;
        final double fixedProductPrice = 50;

        String fixedPriceRefurbishId = pers.get(0).getRefurbishedId();
        pers.get(0).setCustomerPrice(100);
        pers.get(0).setRetailerPrice(100);
        pers.get(0).setUnitPriceFixed(PriceEngineResult.Change.SET);

        String fixedPartNo = pers.get(3).getManufacturerPartNo();
        pers.get(3).setCustomerPrice(50);
        pers.get(3).setRetailerPrice(50);
        pers.get(3).setManufacturerPartPriceFixed(PriceEngineResult.Change.SET);

        priceCore.store(pers, "via test", "testuser", null);

        UniqueUnit uniqueUnit = uniqueUnitAgent.findUnitByIdentifierEager(Identifier.REFURBISHED_ID, fixedPriceRefurbishId);
        assertEquals(fixedUnitPrice, uniqueUnit.getPrice(PriceType.CUSTOMER), 0.001);
        assertEquals(fixedUnitPrice, uniqueUnit.getPrice(PriceType.RETAILER), 0.001);
        assertTrue("Unit should contain Flage PriceFixed" + uniqueUnit.getFlags(), uniqueUnit.getFlags().contains(UniqueUnit.Flag.PRICE_FIXED));
        for (PriceHistory priceHistory : uniqueUnit.getPriceHistory()) {
            assertTrue(priceHistory.getComment().contains("unitfix"));
            assertEquals(fixedUnitPrice, priceHistory.getPrice(), 0.001);
            assertTrue(priceHistory.getType() == PriceType.CUSTOMER || priceHistory.getType() == PriceType.RETAILER);
        }

        Product product = uniqueUnitAgent.findProductByPartNoEager(fixedPartNo);
        assertEquals(fixedProductPrice, product.getPrice(PriceType.CUSTOMER), 0.001);
        assertEquals(fixedProductPrice, product.getPrice(PriceType.RETAILER), 0.001);
        assertTrue(product.getFlags().contains(Product.Flag.PRICE_FIXED));
        for (PriceHistory priceHistory : product.getPriceHistory()) {
            assertTrue(priceHistory.getComment().contains("productfix"));
            assertEquals(fixedProductPrice, priceHistory.getPrice(), 0.001);
            assertTrue(priceHistory.getType() == PriceType.CUSTOMER || priceHistory.getType() == PriceType.RETAILER);
        }
    }

}
