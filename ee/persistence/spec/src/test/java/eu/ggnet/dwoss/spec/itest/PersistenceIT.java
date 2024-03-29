package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.ee.entity.piece.Display;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.ee.entity.Monitor;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.Notebook;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.Desktop;
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import java.util.EnumSet;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)

public class PersistenceIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testPersistence() throws Exception {
        utx.begin();
        em.joinTransaction();
        // A Notebook
        ProductSeries ps = new ProductSeries(TradeName.ACER, ProductGroup.NOTEBOOK, "TravelMate");
        em.persist(ps);

        ProductFamily pf = new ProductFamily("TravelMate 8700");
        pf.setSeries(ps);
        em.persist(pf);
        ProductModel pm = new ProductModel("TravelMate 8741-81222132");
        pm.setFamily(pf);
        em.persist(pm);
        Notebook notebook = new Notebook();
        notebook.setPartNo("LX.AAAAA.BBB");
        notebook.setModel(pm);
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
        em.persist(notebook);

        // An AllInOne
        ps = new ProductSeries(TradeName.ACER, ProductGroup.ALL_IN_ONE, "AllInOne");
        em.persist(ps);

        pf = new ProductFamily("Z5600");
        pf.setSeries(ps);
        em.persist(pf);
        pm = new ProductModel("Z6523");
        pm.setFamily(pf);
        em.persist(pm);
        Notebook allInOne = new Notebook();
        allInOne.setPartNo("PX.AASAA.BBB");
        allInOne.setModel(pm);
        allInOne.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        allInOne.setComment("Ein Kommentar");
        allInOne.setCpu(new Cpu(Cpu.Series.CELERON, "Eine CPU", Cpu.Type.MOBILE, 123.1, 2));
        allInOne.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        allInOne.setOs(Desktop.Os.LINUX);
        allInOne.setMemory(12345);
        allInOne.add(Desktop.Hdd.ROTATING_0500);
        allInOne.add(Desktop.Odd.BLURAY_COMBO);
        allInOne.setExtras(Desktop.Extra.KAMERA);
        allInOne.setDisplay(new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE));
        em.persist(allInOne);

        // A Desktop
        ProductSeries veriton = new ProductSeries(TradeName.ACER, ProductGroup.DESKTOP, "Veriton");
        em.persist(veriton);

        ProductFamily m400 = new ProductFamily("M400");
        m400.setSeries(veriton);
        em.persist(m400);
        ProductModel M480G = new ProductModel("M480G");
        M480G.setFamily(m400);
        em.persist(M480G);
        Gpu gpu = new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte");
        em.persist(gpu);
        Cpu cpu = new Cpu(Cpu.Series.CORE, "Eine CPU", Cpu.Type.MOBILE, 123.1, 2);
        cpu.setEmbeddedGpu(gpu);
        em.persist(cpu);

        Desktop M480G_1 = new Desktop("PX.99999.321", 2L);
        M480G_1.setModel(M480G);
        M480G_1.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        M480G_1.setComment("Ein Kommentar");
        M480G_1.setCpu(cpu);
        M480G_1.setGpu(gpu);
        M480G_1.setOs(Desktop.Os.LINUX);
        M480G_1.setMemory(12345);
        M480G_1.add(Desktop.Hdd.ROTATING_0500);
        M480G_1.add(Desktop.Odd.BLURAY_COMBO);
        M480G_1.setExtras(Desktop.Extra.KAMERA);
        em.persist(M480G_1);

        // A Monitor
        ProductSeries a = new ProductSeries(TradeName.ACER, ProductGroup.MONITOR, "A");
        em.persist(a);
        ProductFamily a230 = new ProductFamily("A230");
        a230.setSeries(a);
        em.persist(a230);
        ProductModel a231Hbmd = new ProductModel("A231Hbmd");
        a231Hbmd.setFamily(a230);
        em.persist(a231Hbmd);
        Monitor A231spec = new Monitor(new Display(Display.Size._11_6, Display.Resolution.VGA,
                Display.Type.CRYSTAL_BRIGHT, Display.Ration.SIXTEEN_TO_NINE));
        A231spec.setModel(a231Hbmd);
        A231spec.setPartNo("ET.VA1HE.008");
        A231spec.setProductId(3L);
        A231spec.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        A231spec.setComment("Ein Kommentar");
        em.persist(A231spec);

        utx.commit();

        utx.begin();
        em.joinTransaction();
        CriteriaQuery<ProductSeries> cq = em.getCriteriaBuilder().createQuery(ProductSeries.class);
        cq.select(cq.from(ProductSeries.class));
        List<ProductSeries> serieses = em.createQuery(cq).getResultList();
        assertFalse(serieses.isEmpty());
        for (ProductSeries series : serieses) {
            assertNotNull(series.getFamilys());
            assertFalse(series.getFamilys().isEmpty());
            for (ProductFamily family : series.getFamilys()) {
                assertNotNull(family.getModels());
                assertFalse(family.getModels().isEmpty());
                for (ProductModel model : family.getModels()) {
                    assertNotNull(model.getSpecs());
                    assertFalse(model.getSpecs().isEmpty());
                    for (ProductSpec spec : model.getSpecs()) {
                        assertNotNull(spec.getPartNo());
                        assertNotNull(spec.getModel());
                        assertNotNull(spec.getModel().getFamily());
                        assertNotNull(spec.getModel().getFamily().getSeries());
                        assertNotNull(spec.getModel().getFamily().getSeries().getBrand());
                        assertNotNull(spec.getModel().getFamily().getSeries().getGroup());
                    }
                }
            }
        }
        utx.commit();
    }
}
