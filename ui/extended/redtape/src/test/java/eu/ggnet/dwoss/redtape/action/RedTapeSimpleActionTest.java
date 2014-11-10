package eu.ggnet.dwoss.redtape.action;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author oliver.guenther
 */
public class RedTapeSimpleActionTest {

    @Test
    public void testLoadImage() {
        Assert.assertNotNull(RedTapeSimpleAction.loadLargeIcon());
        Assert.assertNotNull(RedTapeSimpleAction.loadSmallIcon());
    }
}
