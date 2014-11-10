package eu.ggnet.dwoss.redtape;


import org.junit.Test;

import junit.framework.Assert;

/**
 *
 * @author oliver.guenther
 */
public class UnversalSearchDialogTest {

    @Test
    public void testLoadIcon() {
        Assert.assertNotNull(UniversalSearchViewCask.loadIcon());
    }
}
