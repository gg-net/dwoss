package eu.ggnet.dwoss.uniqueunit.itest;

import eu.ggnet.dwoss.uniqueunit.itest.support.UniqurUnitItHelper;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnitHistory;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class UnitHistoryListenerOperationIT extends ArquillianProjectArchive {

    @EJB
    private UniqueUnitAgent uniqueUnitAgent;

    @Inject
    private UniqurUnitItHelper helper;

    @Test
    public void testEvent() throws InterruptedException {
        String MSG = "Eine Nachricht";
        int id1 = helper.createSampleUnit();
        helper.send(id1, MSG, "Junit");
        Thread.sleep(1000);
        UniqueUnit u1 = uniqueUnitAgent.findByIdEager(UniqueUnit.class, id1);
        assertNotNull(u1);
        assertEquals("Should have three history Elements, contains " + u1.getHistory(), 3, u1.getHistory().size());
        assertTrue("Should contain '" + MSG + "', but has '" + u1 + "'", hasMessage(u1, MSG));
    }

    private boolean hasMessage(UniqueUnit uu, String msg) {
        for (UniqueUnitHistory uuh : uu.getHistory()) {
            if ( uuh.getComment().contains(msg) ) return true;
        }
        return false;
    }

}
