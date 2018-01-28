package eu.ggnet.dwoss.redtapext.op.itest;

/**
 *
 * @author pascal.perau
 */
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.mandator.api.service.WarrantyService;
import eu.ggnet.dwoss.redtapext.op.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class InjectIT extends ArquillianProjectArchive {

    // Added in ArqProjectArchive
    @Inject
    private Instance<WarrantyService> wsi;

    @Test
    public void testInject() {
        assertThat(wsi.isUnsatisfied()).isFalse();
        assertThat(wsi.get()).isNotNull();
    }
}
