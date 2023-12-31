package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.DisplayEao;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class DisplayEaoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testFind() throws Exception {
        utx.begin();
        em.joinTransaction();
        Display d = new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.SIXTEEN_TO_NINE);
        em.persist(d);
        em.persist(new Display(Display.Size._15, Display.Resolution.VGA, Display.Type.CRYSTAL_BRIGHT, Display.Ration.SIXTEEN_TO_NINE));
        utx.commit();
        utx.begin();
        em.joinTransaction();
        DisplayEao displayEao = new DisplayEao(em);
        Display display = displayEao.find(d.getSize(), d.getResolution(), d.getType(), d.getRation());
        assertNotNull(display);
        assertEquals(d.getId(), display.getId());
        display = displayEao.find(Display.Size._11_6, Display.Resolution.HD, Display.Type.MATT, Display.Ration.SIXTEEN_TO_TEN);
        assertNull(display);
        utx.commit();
    }
}
