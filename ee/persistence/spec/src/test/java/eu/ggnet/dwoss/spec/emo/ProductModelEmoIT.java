package eu.ggnet.dwoss.spec.emo;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.ProductModel;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class ProductModelEmoIT {

    EntityManagerFactory emf;

    EntityManager em;

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
    public void testFullRequestBrandGroupName() {
        TradeName sb = TradeName.HP;
        ProductGroup sg = ProductGroup.PROJECTOR;
        String sn = "SERIES";
        String fn = "FAMILY";
        String mn = "MODEL";

        em.getTransaction().begin();
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
        em.getTransaction().commit();

        em.getTransaction().begin();
        model = modelEmo.request(sb, sg, sn, fn, mn);
        assertNotNull(model);
        assertEquals(mid, model.getId());
        assertEquals(fid, model.getFamily().getId());
        assertEquals(sid, model.getFamily().getSeries().getId());
        em.getTransaction().commit();
    }

    @Test
    public void testHalfRequestBrandGroupName() {
        TradeName sb = TradeName.HP;
        ProductGroup sg = ProductGroup.PROJECTOR;
        String sn = "SERIES";
        String fn = "FAMILY";
        String mn = "MODEL";

        em.getTransaction().begin();
        new ProductFamilyEmo(em).request(sb, sg, sn, fn);
        em.getTransaction().commit();

        em.getTransaction().begin();
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
        em.getTransaction().commit();

        em.getTransaction().begin();
        model = modelEmo.request(sb, sg, sn, fn, mn);
        assertNotNull(model);
        assertEquals(mid, model.getId());
        assertEquals(fid, model.getFamily().getId());
        assertEquals(sid, model.getFamily().getSeries().getId());
        em.getTransaction().commit();
    }

    @Test
    public void testRequestBrandGroupNameByHand() {
        TradeName sb = TradeName.HP;
        ProductGroup sg = ProductGroup.PROJECTOR;
        String sn = "SERIES";
        String fn = "FAMILY";
        String mn = "MODEL";

        em.getTransaction().begin();
        new ProductSeriesEmo(em).request(sb, sg, sn);
        em.getTransaction().commit();

        em.getTransaction().begin();
        new ProductFamilyEmo(em).request(sb, sg, sn, fn);
        em.getTransaction().commit();

        em.getTransaction().begin();
        ProductModel model = new ProductModelEmo(em).request(sb, sg, sn, fn, mn);
        assertNotNull(model);
        em.getTransaction().commit();

    }
}
