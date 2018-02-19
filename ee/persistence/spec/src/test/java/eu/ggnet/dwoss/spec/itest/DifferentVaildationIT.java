package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.ee.entity.Notebook;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.Desktop;
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;
import javax.validation.Validation;
import javax.validation.Validator;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class DifferentVaildationIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testDifference() throws Exception {
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
            utx.begin();
            em.joinTransaction();
            em.persist(notebook);
            utx.commit();
            fail("Notebook should not be persitable");
        } catch (Exception ex) {
            // This is correct
            try {
                utx.rollback();
            } catch (Exception e) {
                // Ignore
            }
        }

        // Now it's persitable
        utx.begin();
        em.joinTransaction();
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
        utx.commit();
    }
}
