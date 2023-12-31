package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.GpuEao;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)

public class GpuEaoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testFindSeriesName() throws Exception {
        utx.begin();
        em.joinTransaction();
        em.persist(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        em.persist(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.RADEON_HD_5000, "Eine Graphiccarte"));
        em.persist(new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_500, "Eine Graphiccarte"));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        GpuEao gpuEao = new GpuEao(em);
        Gpu gpu = gpuEao.find(Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte");
        assertNotNull(gpu);
        gpu = gpuEao.find(Gpu.Series.GEFORCE_100, "Nocheine Grafikkarte");
        assertNull(gpu);
        utx.commit();
    }
}
