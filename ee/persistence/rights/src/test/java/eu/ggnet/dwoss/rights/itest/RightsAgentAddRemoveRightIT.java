/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.rights.itest;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.ee.RightsAgent;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.rights.ee.entity.Operator;
import eu.ggnet.dwoss.rights.itest.support.ArquillianProjectArchive;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class RightsAgentAddRemoveRightIT extends ArquillianProjectArchive {

    @Inject
    @Rights
    private EntityManager em;

    @EJB
    private RightsAgent agent;

    @Inject
    private UserTransaction utx;

    private final static String NAME = "Test";

    private final static AtomicRight R = AtomicRight.CHANGE_TAX;

    @Test
    public void addAndRemoveRightToFromOperator() throws Exception {
        utx.begin();
        em.joinTransaction();
        Operator o = new Operator(NAME);
        em.persist(o);
        em.persist(new Operator("Max"));
        utx.commit();

        Operator op = agent.findOperatorByUsername(NAME);
        assertThat(op).as("Existing Operator").isNotNull();
        assertThat(op.getRights()).as("Existing Operator should have no rights").isEmpty();

        agent.addRightToOperator(op.getId(), R);
        op = agent.findOperatorByUsername(NAME);
        assertThat(op).as("Existing Operator").isNotNull();
        assertThat(op.getRights()).as("Existing Operator should have no rights").containsExactly(R);

        agent.removeRightFromOperator(op.getId(), R);

        op = agent.findOperatorByUsername(NAME);
        assertThat(op).as("Existing Operator").isNotNull();
        assertThat(op.getRights()).as("Existing Operator should have no rights").isEmpty();
    }

}
