package eu.ggnet.dwoss.receipt.itest;

import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.itest.support.*;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.SpecConstants;

import static org.assertj.core.api.Assertions.*;
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
    public void testCreateProductModell() throws UserInfoException {
        final String PRODUCT_SERIES_NAME = "Der Name2";

        ProductSeries series = specStore.makeSeries(TradeName.HP, ProductGroup.MISC, PRODUCT_SERIES_NAME);
        ProductFamily family = specStore.makeFamily("Family 2", series);

        ProductModel productModel3 = productProcessor.createModel(family.getId(), "ProductModel3");
        assertThat(productModel3).as("Created Instance must not be null").isNotNull();
        assertThat(productModel3.getId()).as("ProductModel.id should be set be GeneratedValue").isNotEqualTo(0);
        assertThat(productModel3.getFamily().getSeries().getName()).as("Extra name should be set").isEqualTo(PRODUCT_SERIES_NAME);
    }

    @Test
    public void testCreateProductModellExceptionSameNameDifferentSeries() {
                assertThatThrownBy(() -> {
                    //Create a ProductSeries and persist it.
                    ProductSeries series = specStore.makeSeries(TradeName.HP, ProductGroup.MISC, "Die Exception2");
                    ProductFamily family = specStore.makeFamily("Family Exception", series);

                    //Test if two Products where created with the same name but different ProductSeries that will be throw a exception
                    productProcessor.createModel(family.getId(), "Model1");
                    productProcessor.createModel(family.getId(), "Model1");
                }).as("Creating two idendical ProductModells must fail")
                        .isInstanceOf(UserInfoException.class);
    }

}
