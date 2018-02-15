package eu.ggnet.dwoss.misc.help;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class AboutDialogTest {

    @Test
    public void testLoadChangelog() {
        assertThat(AboutDialog.loadProperties()).as("load Properties").isNotNull();
    }

    @Test
    public void testLoadImage() {
        assertThat(AboutDialog.LOGO_IMAGE).as("load LOGO_IMAGE").isNotNull();
    }
}
