package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;
import eu.ggnet.dwoss.spec.ee.entity.Desktop;
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.BasicSpec;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import java.util.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.ProductSpecEao;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ProductSpecEaoFindByProductIdIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Inject
    private ProductSpecEao specEao;

    @Test
    public void testFindByProductId() throws Exception {
        utx.begin();
        em.joinTransaction();
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

        utx.commit();

        ProductSpecEao specEao = new ProductSpecEao(em);
        Desktop spec = (Desktop)specEao.findByProductId(5L);
        assertNotNull(spec);
        List<ProductSpec> productSpecs = specEao.findByProductIds(Arrays.asList(5L, 6L));
        assertEquals(2, productSpecs.size());
    }
}
