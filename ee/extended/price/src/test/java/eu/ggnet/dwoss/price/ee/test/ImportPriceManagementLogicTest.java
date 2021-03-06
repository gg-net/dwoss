package eu.ggnet.dwoss.price.ee.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.ggnet.dwoss.price.ee.ImporterOperation;
import eu.ggnet.dwoss.price.ee.PriceCoreOperation;
import eu.ggnet.dwoss.price.ee.engine.PriceEngineResult;
import eu.ggnet.dwoss.core.system.progress.ProgressProducerForTests;
import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.system.progress.IMonitor;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ImportPriceManagementLogicTest {

    @Test
    public void testFromXls() throws UserInfoException {
        PriceCoreOperation core = mock(PriceCoreOperation.class);

        ImporterOperation importer = new ImporterOperation(core, new ProgressProducerForTests());
        FileJacket jacket = new FileJacket("Sample", "xls", new File("target/test-classes/ImportPriceManagementLogicSamples.xls"));
        importer.fromXls(jacket, "testuser");

        List<PriceEngineResult> expected = new ArrayList<>();
        expected.add(new PriceEngineResult("1", "A", 1.0, 1.0, 0, 1, 0));
        expected.add(new PriceEngineResult("2", "A", 2.0, 2.0, 1, 0, 0));
        expected.add(new PriceEngineResult("3", "B", 3.0, 3.0, 0, -1, 0));
        expected.add(new PriceEngineResult("4", "B", 4.0, 4.0, 1, 0, 0));
        expected.add(new PriceEngineResult("5", "C", 5.0, 5.0, 0, 0, 1));

        verify(core).store(eq(expected), eq("ImportPriceManagementOperation.fromXls()"), anyString(), any(IMonitor.class));
    }

}
