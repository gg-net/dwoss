package eu.ggnet.dwoss.uniqueunit;

import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;

import static org.junit.Assert.*;

/**
 * Test for correct injection of EntityManagers
 */
public class ContainerIT {

    private EJBContainer container;

    @Inject
    @UniqueUnits
    private EntityManager em;

    @Inject
    @UniqueUnits
    private EntityManagerFactory emf;

    @Produces
    public static ReceiptCustomers p = new ReceiptCustomers(new HashMap<>());

    @Produces
    SpecialSystemCustomers sc = new SpecialSystemCustomers(new HashMap<>());

    @Produces
    PostLedger pl = new PostLedger(new HashMap<>());

    @Produces
    ShippingTerms st = new ShippingTerms(new HashMap<>());

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
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
    public void testInjected() {
        assertNotNull("Container is null", container);
        assertNotNull("EntityManagerFactory is null", emf);
        // TODO: Remove if persistence.xml changed to JTA
        // assertNotNull("EntityManager is null", em);
    }
}
