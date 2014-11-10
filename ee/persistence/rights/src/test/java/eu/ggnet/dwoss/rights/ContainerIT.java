package eu.ggnet.dwoss.rights;

import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.rights.RightsAgent;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.rights.assist.RightsPu;
import eu.ggnet.dwoss.rights.assist.gen.RightsGeneratorOperation;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;

import static eu.ggnet.dwoss.rights.api.AtomicRight.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bastian Venz
 */
public class ContainerIT {

    private EJBContainer container;

    @EJB
    private RightsAgent agent;

    @Produces
    public static ReceiptCustomers c = new ReceiptCustomers(new HashMap<>());

    @Produces
    SpecialSystemCustomers sc = new SpecialSystemCustomers(new HashMap<>());

    @Produces
    ShippingTerms st = new ShippingTerms(new HashMap<>());

    @Produces
    PostLedger pl = new PostLedger(new HashMap<>());

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(RightsPu.CMP_IN_MEMORY);
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
    public void testPrePersist() {
        Operator op = new Operator("TestUser");
        Persona p = new Persona("Testpersona");
        p.add(CREATE_ANNULATION_INVOICE);
        p = agent.store(p);
        op.add(p);
        op = agent.store(op);
        // Now we have one operator with one persona with one right.
        op.add(CREATE_ANNULATION_INVOICE); // adding the same right to the operator
        op = agent.store(op); // This should clear the duplicated right.
        assertFalse("The Operator should not have any right, cause its duplicate of the persona. Rights=" + op + ",personas=" + op.getPersonas(),
                op.getRights().contains(CREATE_ANNULATION_INVOICE));
    }

    // TODO: Remove me, but show basti before.
    @Stateless
    public static class RightsContainerITBean {

        @Inject
        private RightsAgent agent;

        @Inject
        private RightsGeneratorOperation rightsGenerator;

        /**
         * Make 11 Operators and 10 Personas and return the id of the first Operator.
         * <p>
         * @return
         */
        public Operator make() {

            rightsGenerator.make(10, 10);
            Persona persona = agent.findAll(Persona.class).get(0);
            persona.getPersonaRights().clear();
            agent.store(persona);
            Operator op = agent.findAll(Operator.class).get(0);
            op.getRights().clear();
            op.getPersonas().clear();
            op.getPersonas().add(persona);
            agent.store(op);
            return op;
        }

    }

}
