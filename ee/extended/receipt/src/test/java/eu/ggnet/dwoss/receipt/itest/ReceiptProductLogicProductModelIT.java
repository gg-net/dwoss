package eu.ggnet.dwoss.receipt.itest;

import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.itest.support.*;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.SpecPu;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReceiptProductLogicProductModelIT extends ArquillianProjectArchive {

    @EJB
    private ProductProcessor productProcessor;

    @Inject
    private SpecStore specStore;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    @Test
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

    @Test(expected = RuntimeException.class)
    public void testCreateProductModellExceptionSameName() {

        //Test if two Products where created with the same name that will be throw a exception
        productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, null, null, "ModelException");
        productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, null, null, "ModelException");
        failBecauseExceptionWasNotThrown(RuntimeException.class);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateProductModellExceptionSameNameDifferentSeries() {

        //Create a ProductSeries and persist it.
        ProductSeries series = specStore.makeSeries(TradeName.HP, ProductGroup.MISC, "Die Exception2");
        ProductFamily family = specStore.makeFamily("Family Exception", series);

        //Test if two Products where created with the same name but different ProductSeries that will be throw a exception
        productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, series, family, "Model1");
        productProcessor.create(TradeName.HP, ProductGroup.DESKTOP, series, family, "Model1");

        failBecauseExceptionWasNotThrown(RuntimeException.class);

    }

}
