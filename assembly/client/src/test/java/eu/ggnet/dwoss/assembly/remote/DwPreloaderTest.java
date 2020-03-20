package eu.ggnet.dwoss.assembly.remote;

import eu.ggnet.dwoss.assembly.remote.DwPreloader;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author oliver.guenther
 */
public class DwPreloaderTest {

    @Test
    public void testResources() {
        assertNotNull(DwPreloader.loadSplash());
    }

}
