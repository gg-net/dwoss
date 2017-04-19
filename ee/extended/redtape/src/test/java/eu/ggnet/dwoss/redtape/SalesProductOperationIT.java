package eu.ggnet.dwoss.redtape;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.entity.SalesProduct;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.spec.format.SpecFormater;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.util.UserInfoException;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author bastian.venz
 */
public class SalesProductOperationIT {

    private EJBContainer container;

    @EJB
    private RedTapeAgent redTapeAgent;

    @EJB
    private RedTapeWorker redTapeWorker;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    /**
     * Test of getSalesProducts method, of class SalesProductOperation.
     */
    @Test
    public void testGetSalesProducts() {
        SalesProduct product1 = new SalesProduct("Part1", "Name1", 1D, 1, "Descritpt1");
        SalesProduct product2 = new SalesProduct("Part2", "Name2", 2D, 2, "Descritpt1");
        SalesProduct product3 = new SalesProduct("Part3", "Name3", 3D, 3, "Descritpt1");
        SalesProduct product4 = new SalesProduct("Part4", "Name4", 4D, 4, "Descritpt1");

        redTapeAgent.persist(product1);
        redTapeAgent.persist(product2);
        redTapeAgent.persist(product3);
        redTapeAgent.persist(product4);

        assertTrue(redTapeAgent.findAll(SalesProduct.class).size() == 4);
    }

    /**
     * Test of createSalesProduct method, of class SalesProductOperation.
     * <p>
     * @throws de.dw.util.UserInfoException
     */
    @Ignore // Fails under Linux, Enable with Arquilian
    @Test
    public void testCreateSalesProduct() throws UserInfoException {
        ProductSpec ps = receiptGenerator.makeProductSpec();
        redTapeWorker.createSalesProduct(ps.getPartNo());
        List<SalesProduct> salesProducts = redTapeAgent.findAll(SalesProduct.class);

        SalesProduct salesProduct = new SalesProduct(ps.getPartNo(), ps.getModel().getName(), 0., ps.getProductId(), SpecFormater.toSingleLine(ps));
        assertTrue(salesProducts.contains(salesProduct));
    }

    /**
     * Test of updatePrice method, of class SalesProductOperation.
     * <p>
     * @throws de.dw.util.UserInfoException
     */
    @Ignore // Fails under Linux, Enable with Arquilian
    @Test
    public void testUpdateSalesProdukt() throws UserInfoException {
        ProductSpec ps = receiptGenerator.makeProductSpec();

        SalesProduct createSalesProduct = redTapeWorker.createSalesProduct(ps.getPartNo());

        createSalesProduct.setPrice(1337.37);
        SalesProduct updateSalesProdukt = redTapeAgent.merge(createSalesProduct);
        assertTrue(updateSalesProdukt.getPrice() == 1337.37);
    }

    /**
     * Test of removeSalesProdukt method, of class SalesProductOperation.
     */
    @Test
    public void testRemoveSalesProdukt() {
        SalesProduct product1 = new SalesProduct("Part1", "Name1", 1D, 1, "Descritpt1");
        SalesProduct product2 = new SalesProduct("Part2", "Name2", 2D, 2, "Descritpt1");
        SalesProduct product3 = new SalesProduct("Part3", "Name3", 3D, 3, "Descritpt1");
        SalesProduct product4 = new SalesProduct("Part4", "Name4", 4D, 4, "Descritpt1");

        redTapeAgent.persist(product1);
        redTapeAgent.persist(product2);
        redTapeAgent.persist(product3);
        redTapeAgent.persist(product4);

        redTapeAgent.remove(product1);
        redTapeAgent.remove(product2);

        assertTrue(redTapeAgent.findAll(SalesProduct.class).size() == 2);
    }
}
