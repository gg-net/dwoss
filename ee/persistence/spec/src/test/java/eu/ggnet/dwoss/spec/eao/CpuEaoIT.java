package eu.ggnet.dwoss.spec.eao;

import java.util.List;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */

public class CpuEaoIT {

    private EntityManagerFactory emf;
    private EntityManager em;


    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(SpecPu.NAME,SpecPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(new Cpu(Cpu.Series.CORE, "Quad Q9000", Cpu.Type.MOBILE, 2.26, 4));
        em.persist(new Cpu(Cpu.Series.CORE_I3, "Quad Q9100", Cpu.Type.MOBILE, 2.0, 4));
        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testFindAll() {
        em.getTransaction().begin();
        CpuEao cpuEao = new CpuEao(em);
        List<Cpu> cpus = cpuEao.findAll();
        assertNotNull(cpus);
        assertEquals(2, cpus.size());
        em.getTransaction().commit();
    }

    @Test
    public void testFindSeriesName() {
        em.getTransaction().begin();
        CpuEao cpuEao = new CpuEao(em);
        Cpu cpu = cpuEao.find(Cpu.Series.CORE,"Quad Q9000");
        assertNotNull(cpu);
        cpu = cpuEao.find(Cpu.Series.CORE_I5,"XXXX");
        assertNull(cpu);
        em.getTransaction().commit();
    }
}
