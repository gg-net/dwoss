/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.rights.gen;

import eu.ggnet.dwoss.rights.entity.Persona;
import eu.ggnet.dwoss.rights.entity.Operator;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.rights.RightsAgent;
import eu.ggnet.dwoss.rights.assist.RightsPu;
import eu.ggnet.dwoss.rights.assist.gen.RightsGeneratorOperation;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Bastian Venz
 */
public class RightsGeneratorTestIT {

    private EJBContainer container;

    @Inject
    private RightsGeneratorTestBean bean;

    @Inject
    private RightsAgent agent;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(RightsPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_TESTING);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    /**
     * Test of make method, of class RightsGeneratorOperation.
     */
    @Test
    public void testMake() {
        int countOfOperator = 50;
        int countOfPersona = 5;
        bean.make(countOfOperator, countOfPersona);

        List<Operator> operators = agent.findAll(Operator.class);
        assertEquals("Not all Operators were Persisted", operators.size(), countOfOperator);
        List<Persona> personas = agent.findAll(Persona.class);
        assertEquals("Not all Persona were Persisted", personas.size(), countOfPersona);

    }

    // TODO: Whats that ?
    @Stateless
    public static class RightsGeneratorTestBean {

        @Inject
        private RightsGeneratorOperation rightsGenerator;

        public void make(int countOfOperator, int countOfPersona) {
            rightsGenerator.make(countOfOperator, countOfPersona);
        }
    }

}
