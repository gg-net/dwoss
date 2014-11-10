package eu.ggnet.dwoss.mandator;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class SampleTest {

    @Test
    public void testLoadMailDocument() {
        assertNotNull(Sample.loadMailDocument());
    }

    @Test
    public void testLoadLogo() {
        assertNotNull(Sample.loadLogo());
    }
}
