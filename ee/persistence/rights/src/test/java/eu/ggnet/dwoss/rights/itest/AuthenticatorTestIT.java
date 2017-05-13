/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.rights.itest;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rights.RightsAgent;
import eu.ggnet.dwoss.rights.assist.gen.RightsGeneratorOperation;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.op.Authentication;
import eu.ggnet.dwoss.util.UserInfoException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

/**
 *
 * @author Bastian Venz
 */
@RunWith(Arquillian.class)
public class AuthenticatorTestIT extends ArquillianProjectArchive {

    @EJB
    private RightsAgent agent;

    @EJB
    private Authentication authentication;

    @Inject
    private RightsGeneratorOperation rightsGenerator;

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
