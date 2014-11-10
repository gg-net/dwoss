package eu.ggnet.dwoss.spec.eao;

import java.util.*;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.entity.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProductSpecEaoIT {

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

    /**
     * Test of findByPartNo method, of class ProductSpecEao.
     */
    @Test
    public void testFindByPartNo() {
        em.getTransaction().begin();
        ProductSeries veriton = new ProductSeries(TradeName.FUJITSU, ProductGroup.DESKTOP, "Veriton");
        em.persist(veriton);

        ProductFamily m400 = new ProductFamily("M400");
        m400.setSeries(veriton);
        em.persist(m400);
        ProductModel M480G = new ProductModel("M480G");
        M480G.setFamily(m400);
        em.persist(M480G);
        Desktop M480G_1 = new Desktop("PX.99999.321", 2L);
        M480G_1.setModel(M480G);
        M480G_1.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        M480G_1.setComment("Ein Kommentar");
        M480G_1.setCpu(new Cpu(Cpu.Series.CORE, "Eine CPU", Cpu.Type.MOBILE, 123.0, 2));
        M480G_1.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        M480G_1.setOs(Desktop.Os.LINUX);
        M480G_1.setMemory(12345);
        M480G_1.add(Desktop.Hdd.ROTATING_1000);
        M480G_1.add(Desktop.Odd.BLURAY_COMBO);
        M480G_1.setExtras(Desktop.Extra.KAMERA);
        em.persist(M480G_1);
        em.getTransaction().commit();

        em.getTransaction().begin();
        ProductSpecEao specEao = new ProductSpecEao(em);
        Desktop spec = (Desktop)specEao.findByPartNo(M480G_1.getPartNo());
        assertNotNull(spec);
        assertEquals(M480G_1.getExtras(), spec.getExtras());
    }

    @Test
    public void testFindByProductId() {
        em.getTransaction().begin();
        ProductSeries veriton = new ProductSeries(TradeName.FUJITSU, ProductGroup.DESKTOP, "Veriton");
        em.persist(veriton);

        ProductFamily m400 = new ProductFamily("M400");
        m400.setSeries(veriton);
        em.persist(m400);
        ProductModel M480G = new ProductModel("M480G");
        M480G.setFamily(m400);
        em.persist(M480G);
        Desktop M480G_1 = new Desktop("PX.99999.321", 2L);
        M480G_1.setModel(M480G);
        M480G_1.setProductId(5L);
        M480G_1.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        M480G_1.setComment("Ein Kommentar");
        M480G_1.setCpu(new Cpu(Cpu.Series.CORE, "Eine CPU", Cpu.Type.MOBILE, 123.0, 2));
        M480G_1.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        M480G_1.setOs(Desktop.Os.LINUX);
        M480G_1.setMemory(12345);
        M480G_1.add(Desktop.Hdd.ROTATING_1000);
        M480G_1.add(Desktop.Odd.BLURAY_COMBO);
        M480G_1.setExtras(Desktop.Extra.KAMERA);
        em.persist(M480G_1);
        Desktop M480G_2 = new Desktop("PX.99999.AAA", 2L);
        M480G_2.setModel(M480G);
        M480G_2.setProductId(6L);
        M480G_2.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        M480G_2.setComment("Ein Kommentar");
        M480G_2.setCpu(new Cpu(Cpu.Series.CORE, "Eine CPU", Cpu.Type.MOBILE, 123.0, 2));
        M480G_2.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        M480G_2.setOs(Desktop.Os.LINUX);
        M480G_2.setMemory(12345);
        M480G_2.add(Desktop.Hdd.ROTATING_1000);
        M480G_2.add(Desktop.Odd.BLURAY_COMBO);
        M480G_2.setExtras(Desktop.Extra.KAMERA);
        em.persist(M480G_2);

        em.getTransaction().commit();

        em.getTransaction().begin();
        ProductSpecEao specEao = new ProductSpecEao(em);
        Desktop spec = (Desktop)specEao.findByProductId(5L);
        assertNotNull(spec);
        List<ProductSpec> productSpecs = specEao.findByProductIds(Arrays.asList(5L, 6L));
        assertEquals(2, productSpecs.size());
        em.getTransaction().commit();
    }
}
