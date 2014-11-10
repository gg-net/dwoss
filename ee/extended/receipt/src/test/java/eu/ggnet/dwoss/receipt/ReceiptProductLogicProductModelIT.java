package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.receipt.ProductProcessor;
import eu.ggnet.dwoss.spec.entity.ProductModel;
import eu.ggnet.dwoss.spec.entity.ProductSeries;
import eu.ggnet.dwoss.spec.entity.ProductFamily;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import static org.junit.Assert.*;

public class ReceiptProductLogicProductModelIT {

    //<editor-fold defaultstate="collapsed" desc=" SetUp ">
    private EJBContainer container;

    @EJB
    private ProductProcessor productProcessor;

    @Inject
    private SpecStore specStore;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
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
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" createProductModel Testings ">

    @Test(timeout = 5000)
    public void testCreateProductModell() {
        ProductModel productModel = productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, null, null, "ProductModel1");
        assertNotNull(productModel);
        assertTrue(productModel.getId() > 0);
        assertEquals(SpecPu.DEFAULT_NAME, productModel.getFamily().getSeries().getName());

        ProductModel productModel2 = productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, null, null, "ProductModel2");
        assertNotNull(productModel2);
        assertTrue(productModel2.getId() > 0);
        assertEquals(SpecPu.DEFAULT_NAME, productModel2.getFamily().getSeries().getName());

        //Create a ProductSeries and persist it.
        ProductSeries series = specStore.makeSeries(TradeName.HP, ProductGroup.MISC, "Der Name2");
        ProductFamily family = specStore.makeFamily("Family 2", series);

        ProductModel productModel3 = productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, series, family, "ProductModel3");
        assertNotNull(productModel3);
        assertTrue(productModel3.getId() > 0);
        assertEquals("Der Name2", productModel3.getFamily().getSeries().getName());

        ProductModel productModel4 = productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, series, family, "ProductModel4");
        assertNotNull(productModel4);
        assertTrue(productModel4.getId() > 0);
        assertEquals("Der Name2", productModel4.getFamily().getSeries().getName());

    }

    @Test(expected = RuntimeException.class, timeout = 5000)
    public void testCreateProductModellExceptionSameName() {

        //Test if two Products where created with the same name that will be throw a exception
        productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, null, null, "ModelException");
        productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, null, null, "ModelException");
        fail("No Exception Throw at the test \"testCreateProductModellExceptionSameName()\"");
    }

    @Test(expected = RuntimeException.class, timeout = 5000)
    public void testCreateProductModellExceptionSameNameDifferentSeries() {

        //Create a ProductSeries and persist it.
        ProductSeries series = specStore.makeSeries(TradeName.HP, ProductGroup.MISC, "Die Exception2");
        ProductFamily family = specStore.makeFamily("Family Exception", series);

        //Test if two Products where created with the same name but different ProductSeries that will be throw a exception
        productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, series, family, "Model1");
        productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, series, family, "Model1");

        fail("No Exception throw at the test \"testCreateProductModellExceptionSameNameDifferentSeries()\"");

    }
    //</editor-fold>
}
