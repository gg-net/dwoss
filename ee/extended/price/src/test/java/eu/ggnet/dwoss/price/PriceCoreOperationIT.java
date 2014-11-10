package eu.ggnet.dwoss.price;

import eu.ggnet.dwoss.price.PriceCoreOperation;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.PriceHistory;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.stock.assist.gen.StockGeneratorOperation;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.price.engine.PriceEngineResult;

import static org.junit.Assert.*;

public class PriceCoreOperationIT {

    private EJBContainer container;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private PriceCoreOperation priceCore;

    @Inject
    private StockGeneratorOperation stockGenerator;

    @Inject
    private Mandator mandator;

    @EJB
    private UniqueUnitAgent uniqueUnitAgent;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testStore() {
        stockGenerator.makeStocksAndLocations(2);
        List<UniqueUnit> generated = receiptGenerator.makeUniqueUnits(20, true, false);

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
