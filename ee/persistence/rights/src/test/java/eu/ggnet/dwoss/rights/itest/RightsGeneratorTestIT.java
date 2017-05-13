/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.rights.itest;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rights.RightsAgent;
import eu.ggnet.dwoss.rights.assist.gen.RightsGeneratorOperation;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Bastian Venz
 */
@RunWith(Arquillian.class)
public class RightsGeneratorTestIT extends ArquillianProjectArchive {

    @Inject
    private RightsGeneratorOperation generator;

    @EJB
    private RightsAgent agent;

    /**
     * Test of make method, of class RightsGeneratorOperation.
     */
    @Test
    public void testMake() {
        int countOfOperator = 50;
        int countOfPersona = 5;
        generator.make(countOfOperator, countOfPersona);

        List<Operator> operators = agent.findAll(Operator.class);
        assertEquals("Not all Operators were Persisted", operators.size(), countOfOperator);
        List<Persona> personas = agent.findAll(Persona.class);
        assertEquals("Not all Persona were Persisted", personas.size(), countOfPersona);

    }

}
