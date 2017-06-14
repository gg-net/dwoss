package eu.ggnet.dwoss.uniqueunit.itest;

import java.util.List;

import eu.ggnet.dwoss.uniqueunit.itest.support.UniqurUnitItHelper;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class PersistenceIT extends ArquillianProjectArchive {

    @EJB
    private UniqueUnitAgent agent;

    @Inject
    private UniqurUnitItHelper persistenceBean;

    @Test
    public void testPersistence() {
        List<Integer> t = persistenceBean.fillPersistenceSource();
        assertNotNull(agent.findAll(UniqueUnit.class));

        UniqueUnit unit3_1 = agent.findById(UniqueUnit.class, t.get(0));
        assertNotNull(unit3_1);
        assertNull(unit3_1.getComment());
        assertNull(unit3_1.getInternalComment());

        UniqueUnit unit4_1 = agent.findById(UniqueUnit.class, t.get(1));
        assertNotNull(unit4_1);
        assertNotNull(unit4_1.getComment());
        assertNotNull(unit4_1.getInternalComment());
    }
}
