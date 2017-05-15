package eu.ggnet.dwoss.spec.itest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.emo.DisplayEmo;
import eu.ggnet.dwoss.spec.entity.piece.Display;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)

public class DisplayEmoIT extends ArquillianProjectArchive {

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
        DisplayEmo displayEao = new DisplayEmo(em);
        Display display = displayEao.weakRequest(d.getSize(), d.getResolution(), d.getType(), d.getRation());
        assertNotNull(display);
        assertEquals(d.getId(), display.getId());
        display = displayEao.weakRequest(Display.Size._11_6, Display.Resolution.HD, Display.Type.MATT, Display.Ration.SIXTEEN_TO_TEN);
        assertNotNull(display);
        assertEquals(0, display.getId());
        utx.commit();
    }
}
