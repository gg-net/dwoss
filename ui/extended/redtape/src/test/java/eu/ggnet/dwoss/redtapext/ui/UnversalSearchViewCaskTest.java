package eu.ggnet.dwoss.redtapext.ui;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class UnversalSearchViewCaskTest {

    @Test
    public void testLoadIcon() {
        assertThat(UniversalSearchViewCask.loadIcon()).isNotNull();
    }
}
