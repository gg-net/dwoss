package eu.ggnet.dwoss.spec;

import java.util.EnumSet;

import javax.persistence.*;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.piece.*;
import eu.ggnet.dwoss.spec.entity.*;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class DifferentVaildationIT {

    private EntityManagerFactory emf;

    private EntityManager em;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(SpecPu.NAME, SpecPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testDifference() {
        // A Notebook which is valid, but not persitable
        Notebook notebook = new Notebook();
        notebook.setPartNo("LX.AAAAA.BBB");
        notebook.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        notebook.setComment("Ein Kommentar");
        notebook.setCpu(new Cpu(Cpu.Series.CORE, "Eine CPU", Cpu.Type.MOBILE, 123.1, 2));
        notebook.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        notebook.setOs(Desktop.Os.LINUX);
        notebook.setMemory(12345);
        notebook.add(Desktop.Hdd.ROTATING_0500);
        notebook.add(Desktop.Odd.BLURAY_COMBO);
        notebook.setExtras(Desktop.Extra.KAMERA);
        notebook.setDisplay(new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE));
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        assertTrue(validator.validate(notebook).isEmpty());

        try {
            em.getTransaction().begin();
            em.persist(notebook);
            em.getTransaction().commit();
            fail("Notebook should not be persitable");
        } catch (Exception ex) {
            // This is correct
            try {
                em.getTransaction().rollback();
            } catch (Exception e) {
                // Ignore
            }
        }

        // Now it's persitable
        em.getTransaction().begin();
        ProductSeries ps = new ProductSeries(TradeName.ACER, ProductGroup.NOTEBOOK, "TravelMate");
        em.persist(ps);
        ProductFamily pf = new ProductFamily("TravelMate 8700");
        pf.setSeries(ps);
        em.persist(pf);
        ProductModel pm = new ProductModel("TravelMate 8741-81222132");
        pm.setFamily(pf);
        em.persist(pm);
        notebook.setModel(pm);
        em.persist(notebook);
        em.getTransaction().commit();
    }
}
