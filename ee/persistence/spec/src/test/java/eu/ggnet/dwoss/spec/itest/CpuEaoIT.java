package eu.ggnet.dwoss.spec.itest;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.eao.CpuEao;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)

public class CpuEaoIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private boolean firstRun = true;

    @Before
    public void setUp() throws Exception {
        if ( !firstRun ) return;
        firstRun = false;
        utx.begin();
        em.joinTransaction();
        em.persist(new Cpu(Cpu.Series.CORE, "Quad Q9000", Cpu.Type.MOBILE, 2.26, 4));
        em.persist(new Cpu(Cpu.Series.CORE_I3, "Quad Q9100", Cpu.Type.MOBILE, 2.0, 4));
        utx.commit();
    }

    @Test

    public void testFindAll() throws Exception {
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
        utx.begin();
        em.joinTransaction();
        CpuEao cpuEao = new CpuEao(em);
        Cpu cpu = cpuEao.find(Cpu.Series.CORE, "Quad Q9000");
        assertNotNull(cpu);
        cpu = cpuEao.find(Cpu.Series.CORE_I5, "XXXX");
        assertNull(cpu);
        utx.commit();
    }
}
