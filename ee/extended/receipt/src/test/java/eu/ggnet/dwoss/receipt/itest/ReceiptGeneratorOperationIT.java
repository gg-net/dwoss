package eu.ggnet.dwoss.receipt.itest;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.receipt.ee.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.receipt.itest.support.DatabaseCleaner;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for the RedTapeGeneratorOperation.
 * <p/>
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ReceiptGeneratorOperationIT extends ArquillianProjectArchive {

    private Logger L = LoggerFactory.getLogger(ReceiptGeneratorOperationIT.class);

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private StockUnitEao stockUnitEao;

    @Inject
    ProductEao productEao;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    @Test
    public void testMakeUniqueUnit() {
        UniqueUnit uu = receiptGenerator.makeUniqueUnit();
        assertThat(uu).as("UniqueUnit").isNotNull();
        assertThat(uu.getProduct()).as("Product").isNotNull();
    }

    @Test
    public void testMakeProductSpecsAndUniqueUnits() throws Exception {
        List<ProductSpec> specs = receiptGenerator.makeProductSpecs(20, true);
        assertThat(specs).as("Generated ProductSpecs").isNotEmpty().hasSize(20);
        for (ProductSpec spec : specs) {
            assertThat(spec.getId()).as("ProductSpec.id").isGreaterThan(0);
            assertThat(spec.getProductId()).as("ProductSpec.productId").isGreaterThan(0);
            Product product = productEao.findById(spec.getProductId());
            assertThat(product).as("uniqueunit.Product of spec.ProductSpec").isNotNull();
        }

        List<UniqueUnit> uniqueunits = receiptGenerator.makeUniqueUnits(20, true, true);
        assertThat(uniqueunits).as("Generated UniqueUnits").isNotEmpty().hasSize(20);
        for (UniqueUnit uniqueunit : uniqueunits) {
            assertThat(uniqueunit.getId()).as("UniqueUnit.id").isGreaterThan(0);
            StockUnit stockUnit = stockUnitEao.findByUniqueUnitId(uniqueunit.getId());
            assertThat(stockUnit).describedAs("StockUnit of generated UniqueUnit").isNotNull();
            assertThat(stockUnit.getStock()).describedAs("Stock of StockUnit of generated UniqueUnit").isNotNull();

        }
    }
}
