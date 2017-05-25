package eu.ggnet.dwoss.receipt.itest;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.priv.SearchSingleton;
import eu.ggnet.dwoss.receipt.gen.ReceiptGeneratorOperation;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

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

    @EJB
    private SearchSingleton search;

    @Test
    public void testGenerate() throws Exception {
        L.info("Start Test");
        // The asserts are mearly nice to have. More importend is that the generators work without an exception.
        assertFalse(receiptGenerator.makeProductSpecs(20, true).isEmpty());
        assertFalse(receiptGenerator.makeUniqueUnits(20, true, true).isEmpty());
        L.info("Test Successful");

        search.reindexSearch();
        while (search.isActive()) {
            L.info("Waiting for Search to reindex");
            Thread.sleep(1000);
        }
        assertThat(search.isActive()).isFalse();

    }
}
