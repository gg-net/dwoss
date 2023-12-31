package eu.ggnet.dwoss.spec.itest;

import java.util.EnumSet;

import eu.ggnet.dwoss.spec.ee.emo.ProductModelEmo;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;


/**
 * This is not really a test, but it creates a join query with over 60 joins.
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ProductModelJoinVerificationIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void verifyJoin() throws Exception {

        SpecGenerator g = new SpecGenerator();

        final int SIZE = 100;

        utx.begin();
        em.joinTransaction();
        for (int i = 0; i < SIZE; i++) {
            g.makeRandom(em);
        }
        utx.commit();

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
        utx.commit();

        utx.begin();
        em.joinTransaction();
        Desktop M480G_1 = new Desktop("PX.99999.321", 2L);
        M480G_1.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        M480G_1.setComment("Ein Kommentar");
        M480G_1.setCpu(new Cpu(Cpu.Series.CORE, "Eine CPU", Cpu.Type.MOBILE, 123.0, 2));
        M480G_1.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        M480G_1.setOs(Desktop.Os.LINUX);
        M480G_1.setMemory(12345);
        M480G_1.add(Desktop.Hdd.ROTATING_1000);
        M480G_1.add(Desktop.Odd.BLURAY_COMBO);
        M480G_1.setExtras(Desktop.Extra.KAMERA);
        
        ProductModelEmo productModelEmo = new ProductModelEmo(em);
        ProductModel model = productModelEmo.request(
                M480G.getFamily().getSeries().getBrand(),
                M480G.getFamily().getSeries().getGroup(),
                M480G.getFamily().getSeries().getName(),
                M480G.getFamily().getName(),
                M480G.getName());
        M480G_1.setModel(model);
        em.persist(M480G_1);
        utx.commit();
    }

}
