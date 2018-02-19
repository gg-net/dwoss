package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.CpuEao;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // Using Order for now. the Data created in the first test is needed in the second.
public class CpuEaoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testFindAll() throws Exception {
        // Bad solution. Better clean up the Database or else.
        utx.begin();
        em.joinTransaction();
        em.persist(new Cpu(Cpu.Series.CORE, "Quad Q9000", Cpu.Type.MOBILE, 2.26, 4));
        em.persist(new Cpu(Cpu.Series.CORE_I3, "Quad Q9100", Cpu.Type.MOBILE, 2.0, 4));
        utx.commit();

        utx.begin();
        em.joinTransaction();
        CpuEao cpuEao = new CpuEao(em);
        List<Cpu> cpus = cpuEao.findAll();
        assertNotNull(cpus);
        assertEquals(2, cpus.size());
        utx.commit();
    }

    @Test
    public void testFindSeriesName() throws Exception {
        // Test needs Data from first Test.

        utx.begin();
        em.joinTransaction();

        List<Cpu> resultList = em.createNamedQuery("Cpu.bySeriesModel", Cpu.class).setParameter(1, Cpu.Series.CORE).setParameter(2, "Quad Q9000").getResultList();

        LoggerFactory.getLogger(CpuEaoIT.class).info("Found: {}", resultList);

        CpuEao cpuEao = new CpuEao(em);
        Cpu cpu = cpuEao.find(Cpu.Series.CORE, "Quad Q9000");
        assertNotNull(cpu);
        cpu = cpuEao.find(Cpu.Series.CORE_I5, "XXXX");
        assertNull(cpu);
        utx.commit();
    }
}
