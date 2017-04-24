package eu.ggnet.dwoss.customer;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.mandator.api.value.*;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author pascal.perau
 */
public class ContainerIT {

    private EJBContainer container;

    @Inject
    @Customers
    private EntityManager em;

    @Produces
    ReceiptCustomers p = new ReceiptCustomers(new HashMap<>());

    @Produces
    SpecialSystemCustomers sc = new SpecialSystemCustomers(new HashMap<>());

    @Produces
    RepaymentCustomers rc = new RepaymentCustomers(new HashMap<>());

    @Produces
    PostLedger pl = new PostLedger(new HashMap<>());

    @Produces
    ShippingTerms st = new ShippingTerms(new HashMap<>());

    @Produces
    ScrapCustomers scrap = new ScrapCustomers(new HashMap<>());

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testContainerAndEntityLayer() {
        assertNotNull("Container is null", container);
        assertNotNull("EntityManager is null", em);
    }
}
