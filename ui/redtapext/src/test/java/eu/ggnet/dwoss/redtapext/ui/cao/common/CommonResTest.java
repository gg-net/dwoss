package eu.ggnet.dwoss.redtapext.ui.cao.common;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author oliver.guenther
 */
public class CommonResTest {

    @Test
    public void testResources() {
        assertNotNull(CommonRes.downArrow());
        assertNotNull(CommonRes.leftArrow());
    }

}
