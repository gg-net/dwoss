package eu.ggnet.dwoss.mandator.sample.impl;


import eu.ggnet.dwoss.mandator.sample.impl.Sample;

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
