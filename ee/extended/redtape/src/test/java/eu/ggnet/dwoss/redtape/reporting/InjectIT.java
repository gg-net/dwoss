package eu.ggnet.dwoss.redtape.reporting;

/**
 *
 * @author pascal.perau
 */
import java.util.*;

import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.*;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.CustomerPu;

import eu.ggnet.dwoss.mandator.api.service.WarrantyService;

import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class InjectIT {

    private EJBContainer container;

    // Hint: Is used in another test here.
    @Inject
    private Instance<WarrantyService> wsi;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(ReportPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testInject() {
        assertThat(wsi.isUnsatisfied()).isFalse();
        assertThat(wsi.get()).isNotNull();
    }
}
