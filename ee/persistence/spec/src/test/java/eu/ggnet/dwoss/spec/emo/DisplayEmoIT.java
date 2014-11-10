package eu.ggnet.dwoss.spec.emo;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.piece.Display;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class DisplayEmoIT {

    private EntityManagerFactory emf;

    private EntityManager em;

    private Display d;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(SpecPu.NAME, SpecPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();
        d = new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.SIXTEEN_TO_NINE);
        em.persist(d);
        em.persist(new Display(Display.Size._15, Display.Resolution.VGA, Display.Type.CRYSTAL_BRIGHT, Display.Ration.SIXTEEN_TO_NINE));
        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testFind() {
        em.getTransaction().begin();
        DisplayEmo displayEao = new DisplayEmo(em);
        Display display = displayEao.weakRequest(d.getSize(), d.getResolution(), d.getType(), d.getRation());
        assertNotNull(display);
        assertEquals(d.getId(), display.getId());
        display = displayEao.weakRequest(Display.Size._11_6, Display.Resolution.HD, Display.Type.MATT, Display.Ration.SIXTEEN_TO_TEN);
        assertNotNull(display);
        assertEquals(0, display.getId());
        em.getTransaction().commit();
    }
}
