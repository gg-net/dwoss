package eu.ggnet.dwoss.report;

import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGeneratorOperation;
import eu.ggnet.dwoss.report.entity.ReportLine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class ContainerIT {

    private EJBContainer container;

    @EJB
    private ReportAgent reportAgent;

    @Inject
    private ReportLineGeneratorOperation generator;

    @Produces
    ReceiptCustomers p = new ReceiptCustomers(new HashMap<>());

    @Produces
    SpecialSystemCustomers sc = new SpecialSystemCustomers(new HashMap<>());

    @Produces
    PostLedger pl = new PostLedger(new HashMap<>());

    @Produces
    ShippingTerms st = new ShippingTerms(new HashMap<>());

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
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
    public void testPersistence() {
        generator.makeReportLines(100);
        List<ReportLine> lines = reportAgent.findAll(ReportLine.class);
        assertNotNull(lines);
        assertFalse(lines.isEmpty());
    }
}
