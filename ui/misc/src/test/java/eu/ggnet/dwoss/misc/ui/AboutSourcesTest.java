package eu.ggnet.dwoss.misc.ui;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class AboutSourcesTest {

    @Test
    public void testLoadChangelog() {
        assertThat(AboutSources.loadProperties()).as("load Properties").isNotNull();
    }

}
