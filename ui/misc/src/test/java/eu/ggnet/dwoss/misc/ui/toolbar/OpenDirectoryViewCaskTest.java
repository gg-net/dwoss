package eu.ggnet.dwoss.misc.ui.toolbar;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
public class OpenDirectoryViewCaskTest {

    @Test
    public void testLoadImage() {
        JFXPanel p = new JFXPanel();
        assertThat(OpenDirectoryToolbarButton.loadLargeIcon()).as("Open Dir Icon found").isNotNull();
        Platform.exit();
    }

}
