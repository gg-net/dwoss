package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.eao.ProductSpecEao;
import eu.ggnet.dwoss.spec.entity.*;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ProductSpecEaoFindByPartNoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    /**
     * Test of findByPartNo method, of class ProductSpecEao.
     */
    @Test
    public void testFindByPartNo() throws Exception {
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
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ProductSpecEao specEao = new ProductSpecEao(em);
        Desktop spec = (Desktop)specEao.findByPartNo(M480G_1.getPartNo());
        assertNotNull(spec);
        assertEquals(M480G_1.getExtras(), spec.getExtras());
    }

}
