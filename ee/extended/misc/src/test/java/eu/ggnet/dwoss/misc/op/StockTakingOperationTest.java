package eu.ggnet.dwoss.misc.op;

import java.io.File;

import org.junit.Test;

import eu.ggnet.dwoss.misc.op.StockTakingOperation.ReaderResult;
import eu.ggnet.dwoss.util.FileJacket;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author oliver.guenther
 */
public class StockTakingOperationTest {

    @Test
    public void testXlsToList() {
        FileJacket fj = new FileJacket("in", ".xls", new File("target/test-classes/eu/ggnet/dwoss/misc/op/StockTaking.xls"));
        @SuppressWarnings("UseInjectionInsteadOfInstantion")
        StockTakingOperation sto = new StockTakingOperation();
        ReaderResult result = sto.xlsToList(fj);
        assertArrayEquals(new String[]{"1", "2", "3", "4", "5"}, result.getRefurbisIds().toArray());
        assertTrue(result.getErrors().isEmpty());
    }
}
