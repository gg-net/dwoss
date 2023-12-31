package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import java.util.List;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.assist.gen.SpecGenerator;

import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import static org.junit.Assert.*;

/**
 * Test for correct injection of EntityManagers
 */
@RunWith(Arquillian.class)
public class AgentIT extends ArquillianProjectArchive {

    @Inject
    private UserTransaction utx;

    @Inject
    @Specs
    private EntityManager em;

    @EJB
    private SpecAgent specAgent;

    @Test
    public void testAgent() throws Exception {
        SpecGenerator g = new SpecGenerator();

        utx.begin();
        em.joinTransaction();
        ProductSpec spec = g.makeRandom(em);
        utx.commit();

        assertNotNull(spec);
        assertNotNull(specAgent);
        List<ProductSpec> specs = specAgent.findAll(ProductSpec.class);
        assertFalse(specs.isEmpty());
        assertEquals(1, specs.size());
        assertEquals(spec, specs.get(0));
    }

}
