package eu.ggnet.dwoss.receipt.itest;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.itest.support.*;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.SpecConstants;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

import static org.assertj.core.api.Assertions.*;

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
        assertThat(productFamily).as("Created Instance must not be null").isNotNull();
        assertThat(productFamily.getId()).as("ProductFamiliy.id should be set be GeneratedValue").isNotEqualTo(0);
        assertThat(productFamily.getSeries().getName()).as("Default name should be set").isEqualTo(SpecConstants.DEFAULT_NAME);
        ProductFamily productFamily2 = productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC2");
        assertThat(productFamily2).as("ProductFamiliy instance should be created").isNotNull();
        assertThat(productFamily2.getId()).as("ProductFamiliy.id should be set be GeneratedValue").isNotEqualTo(0);
        assertThat(productFamily).as("The two created instances should be different").isNotEqualTo(productFamily2);
        assertThat(productFamily2.getSeries().getName()).as("Default name should be set").isEqualTo(SpecConstants.DEFAULT_NAME);
        assertThat(productFamily2.getSeries()).as("The Series of both ProductFamilies should be equal").isEqualTo(productFamily.getSeries());

        final String PRODUCT_SERIES_NAME = "Der Name";
        //Create a ProductSeries and persist it.
        ProductSeries series = bean.makeSeries(TradeName.DELL, ProductGroup.MISC, PRODUCT_SERIES_NAME);

        ProductFamily productFamily3 = productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, series, "TestPC3");
        assertThat(productFamily3).as("Created ProductFamily must not be null").isNotNull();
        assertThat(productFamily3.getId()).as("ProductFamiliy.id should be set be GeneratedValue").isNotEqualTo(0);
        assertThat(productFamily3.getSeries().getName()).as("Name of ProductSeries must match").isEqualTo(PRODUCT_SERIES_NAME);
        
        assertThat(productFamily3.getSeries()).isNotEqualTo(productFamily.getSeries());
    }

    @Test
    public void testCreateProductFamilyExceptionSameName() {
        assertThatExceptionOfType(RuntimeException.class)
                .as("Creating two idendical ProductFamily must fail")
                .isThrownBy(() -> {
                    productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC");
                    productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC");
                });
    }

    @Test
    public void testCreateProductFamilyExceptionSameNameDifferentSeries() {
        assertThatExceptionOfType(RuntimeException.class)
                .as("Creating two idendical ProductFamily, even with different series, must fail")
                .isThrownBy(() -> {
                    ProductSeries series = bean.makeSeries(TradeName.DELL, ProductGroup.MISC, "Die Exception");
                    productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, series, "TestPC");
                    productProcessor.create(TradeName.DELL, ProductGroup.NOTEBOOK, null, "TestPC");
                });

    }

}
