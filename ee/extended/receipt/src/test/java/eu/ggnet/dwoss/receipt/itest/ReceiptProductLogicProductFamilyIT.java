package eu.ggnet.dwoss.receipt.itest;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.itest.support.*;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.SpecPu;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReceiptProductLogicProductFamilyIT extends ArquillianProjectArchive {

    @EJB
    private ProductProcessor productProcessor;

    @Inject
    private SpecStore bean;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    @Test
    public void testCreateProductFamily() {
        ProductFamily productFamily = productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC");
        assertThat(productFamily).isNotNull(); //Test if the created ProductFamily is not Null
        assertTrue(productFamily.getId() > 0); // Test if the ProductFamily has a ID
        assertEquals(SpecPu.DEFAULT_NAME, productFamily.getSeries().getName()); // Test if the Name of the created Series the Default Name is
        ProductFamily productFamily2 = productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC2");
        assertNotNull(productFamily2); //Test if the created ProductFamily is not Null
        assertTrue(productFamily2.getId() > 0); // Test if the ProductFamily has a ID
        assertFalse(productFamily.equals(productFamily2)); //Test if the first created ProductFamily != the secound is!
        assertTrue(productFamily2.getSeries().getName().equals(SpecPu.DEFAULT_NAME));// Test if the Name of the created Series the Default Name is
        assertEquals(productFamily.getSeries(), productFamily2.getSeries());//Test if the ID of the Series is the same

        //Create a ProductSeries and persist it.
        ProductSeries series = bean.makeSeries(TradeName.DELL, ProductGroup.MISC, "Der Name");

        ProductFamily productFamily3 = productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, series, "TestPC3");
        assertNotNull(productFamily3);//Test if the created ProductFamily is not Null
        assertTrue(productFamily3.getId() > 0);// Test if the ProductFamily has a ID
        assertEquals("Der Name", productFamily3.getSeries().getName()); //Test if the Name of the Series now the same name is as on the creation
        assertNotSame(productFamily3.getSeries(), productFamily.getSeries());
        // Test if the Series not the Same is as the DefaultSeries

        ProductFamily productFamily4 = productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, series, "TestPC4");
        assertNotNull(productFamily4);//Test if the created ProductFamily is not Null
        assertTrue(productFamily4.getId() > 0);// Test if the ProductFamily has a ID
        assertTrue(productFamily3.getSeries().equals(productFamily4.getSeries())); //Test if the Both ProdcutFamily have the same series

    }

    @Test(expected = RuntimeException.class)
    public void testCreateProductFamilyExceptionSameName() {
        //Test if two Products where created with the same name that will be throw a exception
        productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC");
        productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC");

        failBecauseExceptionWasNotThrown(RuntimeException.class);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateProductFamilyExceptionSameNameDifferentSeries() {

        ProductSeries series = bean.makeSeries(TradeName.DELL, ProductGroup.MISC, "Die Exception");

        //Test if two Products where created with the same name but different ProductSeries that will be throw a exception
        productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, series, "TestPC");
        productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC");

        failBecauseExceptionWasNotThrown(RuntimeException.class);

    }

}
