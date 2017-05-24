package eu.ggnet.dwoss.receipt.itest;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.util.UserInfoException;

import static org.junit.Assert.assertFalse;

/**
 * Test for the RedTapeGeneratorOperation.
 * <p/>
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ReceiptGeneratorOperationIT extends ArquillianProjectArchive {

    @Inject
    private ReceiptGeneratorOperation receiptGenerator;

    @Test
    public void testGenerate() throws UserInfoException {
        // The asserts are mearly nice to have. More importend is that the generators work without an exception.
        assertFalse(receiptGenerator.makeProductSpecs(20, true).isEmpty());
        assertFalse(receiptGenerator.makeUniqueUnits(20, true, true).isEmpty());
        LoggerFactory.getLogger(ReceiptGeneratorOperationIT.class).info("testGenerate: successful");
    }
}
