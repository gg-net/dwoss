package eu.ggnet.dwoss.redtapext.ui.cap;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeSimpleActionTest {

    @Test
    public void testLoadImage() {
        Assert.assertNotNull(RedTapeAction.loadLargeIcon());
        Assert.assertNotNull(RedTapeAction.loadSmallIcon());
    }
}
