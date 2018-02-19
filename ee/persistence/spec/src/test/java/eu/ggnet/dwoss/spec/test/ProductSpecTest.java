package eu.ggnet.dwoss.spec.test;

import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author oliver.guenther
 */
public class ProductSpecTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testValidateProductSpec() {
        ProductSeries veriton = new ProductSeries(TradeName.ACER, ProductGroup.DESKTOP, "Veriton");
        ProductFamily lfamiliy = new ProductFamily("Veriton L");
        lfamiliy.setSeries(veriton);
        ProductModel l640 = new ProductModel("Veriton L640");
        l640.setFamily(lfamiliy);
        ProductSpec s = new BasicSpec("LX.AAAAA.BBB", 1L);
        s.setModel(l640);
        assertThat(validator.validate(s)).isEmpty();
        s.setPartNo("LX.AAAAA.OOB");
        assertTrue("The letter O should be allowed", validator.validate(s).isEmpty());
        s.setPartNo("  LLL");
        assertFalse(s.getPartNo() + " should be invalid", validator.validate(s).isEmpty());
        s.setPartNo("RA LL");
        assertFalse(s.getPartNo() + " should be invalid", validator.validate(s).isEmpty());
    }
}
