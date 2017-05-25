package eu.ggnet.dwoss.redtape.op.itest;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.redtape.gen.RedTapeGeneratorOperation;
import eu.ggnet.dwoss.redtape.op.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.util.UserInfoException;

import static org.junit.Assert.assertFalse;

/**
 * Test for the RedTapeGeneratorOperation.
 * <p/>
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class RedTapeGeneratorOperationIT extends ArquillianProjectArchive {

    @Inject
    private CustomerGeneratorOperation customerGenerator;

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Inject
    private RedTapeGeneratorOperation redTapeGenerator;

    @Test
    public void testGenerate() throws UserInfoException {
        // The asserts are mearly nice to have. More importend is that the generators work without an exception.
        assertFalse(customerGenerator.makeCustomers(50).isEmpty());
        assertFalse(receiptGenerator.makeUniqueUnits(200, true, true).isEmpty());
        assertFalse(redTapeGenerator.makeSalesDossiers(50).isEmpty());
    }
}
