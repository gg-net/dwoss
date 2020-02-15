package eu.ggnet.dwoss.redtapext.ui.cap;

import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 *
 * @author oliver.guenther
 */
public class RedTapeSimpleActionTest {

    @Test
    public void testLoadImage() {
        assertThat(RedTapeAction.loadLargeIcon()).isNotNull();
        assertThat(RedTapeAction.loadSmallIcon()).isNotNull();
        assertThat(RedTapeMenuItem.loadSmallIcon()).isNotNull();
        assertThat(RedTapeToolbarButton.loadLargeIcon()).as("loadLargeIcon").isNotNull();
    }
}
