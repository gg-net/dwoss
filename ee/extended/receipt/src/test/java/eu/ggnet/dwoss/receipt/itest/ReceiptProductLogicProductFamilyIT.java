package eu.ggnet.dwoss.receipt.itest;

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
    public void testCreateProductFamily() throws UserInfoException {
        final String PRODUCT_SERIES_NAME = "Der Name";
        //Create a ProductSeries and persist it.
        ProductSeries series = bean.makeSeries(TradeName.DELL, ProductGroup.MISC, PRODUCT_SERIES_NAME);

        ProductFamily productFamily3 = productProcessor.createFamily(series.getId(), "TestPC3");
        assertThat(productFamily3).as("Created ProductFamily must not be null").isNotNull();
        assertThat(productFamily3.getSeries().getName()).as("Name of ProductSeries must match").isEqualTo(PRODUCT_SERIES_NAME);
    }

    @Test
    public void testCreateProductFamilyExceptionSameNameDifferentSeries() {
        assertThatThrownBy(() -> {
            ProductSeries s1 = bean.makeSeries(TradeName.DELL, ProductGroup.MISC, "Die Exception");
            ProductSeries s2 = bean.makeSeries(TradeName.DELL, ProductGroup.ALL_IN_ONE, "Die Blubla");
            productProcessor.createFamily(s1.getId(), "TestPC");
            productProcessor.createFamily(s2.getId(), "TestPC");
        }).as("Creating two idendical ProductFamily, even with different series, must fail")
                .isInstanceOf(UserInfoException.class);

    }

}
