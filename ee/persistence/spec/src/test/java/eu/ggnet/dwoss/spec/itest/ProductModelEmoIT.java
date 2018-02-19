package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.ee.emo.ProductModelEmo;
import eu.ggnet.dwoss.spec.ee.emo.ProductSeriesEmo;
import eu.ggnet.dwoss.spec.ee.emo.ProductFamilyEmo;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ProductModelEmoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testFullRequestBrandGroupName() throws Exception {
        TradeName sb = TradeName.HP;
        ProductGroup sg = ProductGroup.PROJECTOR;
        String sn = "SERIES";
        String fn = "FAMILY";
        String mn = "MODEL";

        utx.begin();
        em.joinTransaction();
        ProductModelEmo modelEmo = new ProductModelEmo(em);
        ProductModel model = modelEmo.request(sb, sg, sn, fn, mn);
        assertNotNull(model);
        assertEquals(mn, model.getName());
        assertEquals(fn, model.getFamily().getName());
        assertEquals(sb, model.getFamily().getSeries().getBrand());
        assertEquals(sg, model.getFamily().getSeries().getGroup());
        assertEquals(sn, model.getFamily().getSeries().getName());
        long fid = model.getFamily().getId();
        long mid = model.getId();
        long sid = model.getFamily().getSeries().getId();
        utx.commit();

        utx.begin();
        em.joinTransaction();
        model = modelEmo.request(sb, sg, sn, fn, mn);
        assertNotNull(model);
        assertEquals(mid, model.getId());
        assertEquals(fid, model.getFamily().getId());
        assertEquals(sid, model.getFamily().getSeries().getId());
        utx.commit();
    }

    @Test
    public void testHalfRequestBrandGroupName() throws Exception {
        TradeName sb = TradeName.HP;
        ProductGroup sg = ProductGroup.PROJECTOR;
        String sn = "SERIES";
        String fn = "FAMILY";
        String mn = "MODEL";

        utx.begin();
        em.joinTransaction();
        new ProductFamilyEmo(em).request(sb, sg, sn, fn);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ProductModelEmo modelEmo = new ProductModelEmo(em);
        ProductModel model = modelEmo.request(sb, sg, sn, fn, mn);
        assertNotNull(model);
        assertEquals(mn, model.getName());
        assertEquals(fn, model.getFamily().getName());
        assertEquals(sb, model.getFamily().getSeries().getBrand());
        assertEquals(sg, model.getFamily().getSeries().getGroup());
        assertEquals(sn, model.getFamily().getSeries().getName());
        long fid = model.getFamily().getId();
        long mid = model.getId();
        long sid = model.getFamily().getSeries().getId();
        utx.commit();

        utx.begin();
        em.joinTransaction();
        model = modelEmo.request(sb, sg, sn, fn, mn);
        assertNotNull(model);
        assertEquals(mid, model.getId());
        assertEquals(fid, model.getFamily().getId());
        assertEquals(sid, model.getFamily().getSeries().getId());
        utx.commit();
    }

    @Test
    public void testRequestBrandGroupNameByHand() throws Exception {
        TradeName sb = TradeName.HP;
        ProductGroup sg = ProductGroup.PROJECTOR;
        String sn = "SERIES";
        String fn = "FAMILY";
        String mn = "MODEL";

        utx.begin();
        em.joinTransaction();
        new ProductSeriesEmo(em).request(sb, sg, sn);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        new ProductFamilyEmo(em).request(sb, sg, sn, fn);
        utx.commit();

        utx.begin();
        em.joinTransaction();
        ProductModel model = new ProductModelEmo(em).request(sb, sg, sn, fn, mn);
        assertNotNull(model);
        utx.commit();

    }
}
