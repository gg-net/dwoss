package eu.ggnet.dwoss.redtapext.ui.cap;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class CapResTest {

    @Test
    public void testLoadImage() {
        assertThat(CapRes.smallIcon()).isNotNull();
        assertThat(CapRes.largeIcon()).as("loadLargeIcon").isNotNull();
    }
}
