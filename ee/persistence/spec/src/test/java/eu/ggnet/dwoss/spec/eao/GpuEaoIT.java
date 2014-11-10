package eu.ggnet.dwoss.spec.eao;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @author oliver.guenther
 */
public class GpuEaoIT {

    EntityManagerFactory emf;

    EntityManager em;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(SpecPu.NAME, SpecPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        em.persist(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.RADEON_HD_5000, "Eine Graphiccarte"));
        em.persist(new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_500, "Eine Graphiccarte"));
        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testFindSeriesName() {
        em.getTransaction().begin();
        GpuEao gpuEao = new GpuEao(em);
        Gpu gpu = gpuEao.find(Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte");
        assertNotNull(gpu);
        gpu = gpuEao.find(Gpu.Series.GEFORCE_100, "Nocheine Grafikkarte");
        assertNull(gpu);
        em.getTransaction().commit();
    }
}
