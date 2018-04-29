package eu.ggnet.dwoss.misc.ui.toolbar;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author pascal.perau
 */
public class OpenDirectoryViewCaskTest {

    @Test
    public void testLoadImage() {
        assertThat(OpenDirectoryViewCask.OPEN_DIRECTORY_ICON).as("Open Dir Icon found").isNotNull();
    }

}
