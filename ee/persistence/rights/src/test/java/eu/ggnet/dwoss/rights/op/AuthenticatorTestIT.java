/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.rights.op;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.rights.RightsAgent;
import eu.ggnet.dwoss.rights.assist.RightsPu;
import eu.ggnet.dwoss.rights.assist.gen.RightsGeneratorOperation;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.util.UserInfoException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

/**
 *
 * @author Bastian Venz
 */
public class AuthenticatorTestIT {

    private EJBContainer container;

    @Inject
    private RightsAgent agent;

    @EJB
    private Authentication authentication;

    @Inject
    private RightsGeneratorOperation rightsGenerator;

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
    public void testMakeOperatorAndAuthenticate() {
        String password = "xxx123yyy";
        String username = "user";
        Operator o = rightsGenerator.make(username, password, 0, new ArrayList<>());
        List<Operator> operators = agent.findAll(Operator.class);

        assertThat(operators).describedAs("Operators of database").hasSize(1);
        assertThat(operators.get(0).getId()).describedAs("OperatorId").isEqualTo(o.getId());

        try {
            authentication.login(username, password.toCharArray());
        } catch (UserInfoException ex) {
            fail("Authentication failed:" + ex.getMessage());
        }
    }

}
