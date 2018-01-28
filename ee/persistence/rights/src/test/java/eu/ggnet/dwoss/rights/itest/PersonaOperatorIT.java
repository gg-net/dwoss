package eu.ggnet.dwoss.rights.itest;

import eu.ggnet.dwoss.rights.itest.support.ArquillianProjectArchive;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rights.*;
import eu.ggnet.dwoss.rights.entity.Operator;
import eu.ggnet.dwoss.rights.entity.Persona;

import static eu.ggnet.dwoss.rights.api.AtomicRight.CREATE_ANNULATION_INVOICE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the core running fuctionality of the rights pu in the container.
 *
 * @author Oliver Guenther
 */
@RunWith(Arquillian.class)
public class PersonaOperatorIT extends ArquillianProjectArchive {

    @Inject
    private RightsAgentBean agent;

    /**
     * Test, that duplicate Rights to Persona and Operator are only keept on the persona.
     */
    @Test
    public void testDuplicateRemoval() {
        assertThat(agent).as("RightsAgent").isNotNull();
        Operator op = new Operator("TestUser");
        Persona p = new Persona("Testpersona");
        p.add(CREATE_ANNULATION_INVOICE);
        p = agent.store(p);
        op.add(p);
        op = agent.store(op);
        // Now we have one operator with one persona with one right.
        op.add(CREATE_ANNULATION_INVOICE); // adding the same right to the operator
        op = agent.store(op); // This should clear the duplicated right.
        assertThat(op.getRights())
                .describedAs("The Operator, that should not have any right, cause its duplicate of the persona.")
                .doesNotContain(CREATE_ANNULATION_INVOICE);

    }

}
