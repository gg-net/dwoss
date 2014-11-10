package eu.ggnet.dwoss.redtape;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeViewTest {

    @Test
    public void testLoadImage() {
        Assert.assertNotNull(RedTapeView.loadImage());
    }
}
