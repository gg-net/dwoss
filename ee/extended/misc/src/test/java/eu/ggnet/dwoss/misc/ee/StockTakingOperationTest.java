package eu.ggnet.dwoss.misc.ee;

import java.io.File;

import org.junit.Test;

import eu.ggnet.dwoss.misc.ee.StockTakingOperation.ReaderResult;
import eu.ggnet.dwoss.core.common.FileJacket;

import static org.assertj.core.api.Assertions.assertThat;

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
        
        assertThat(result.refurbisIds).hasSize(5).contains("1", "2", "3", "4", "5");
        assertThat(result.errors).isEmpty();
    }
}
