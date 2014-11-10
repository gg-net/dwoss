package eu.ggnet.dwoss.misc.help;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class AboutDialogTest {

    @Test
    public void testLoadChangelog() {
        assertNotNull(AboutDialog.loadProperties());
    }

    @Test
    public void testLoadImage() {
        assertNotNull(AboutDialog.LOGO_IMAGE);
    }
}
