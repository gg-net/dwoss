package eu.ggnet.dwoss.redtape.op.itest;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.RedTapeAgent;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.entity.SalesProduct;
import eu.ggnet.dwoss.redtape.op.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.redtape.op.itest.support.DatabaseCleaner;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.spec.format.SpecFormater;
import eu.ggnet.dwoss.util.UserInfoException;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author bastian.venz
 */
@RunWith(Arquillian.class)
public class SalesProductOperationIT extends ArquillianProjectArchive {

    @EJB
    private RedTapeAgent redTapeAgent;

    @EJB
    private RedTapeWorker redTapeWorker;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
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
