package eu.ggnet.dwoss.receipt.itest;

import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.ProductProcessor;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ReceiptProductLogicIT extends ArquillianProjectArchive {

    @EJB
    private ProductProcessor productLogic;

    @EJB
    private SpecAgent specAgent;

    @Test
    public void testCreateCpu() {
        Cpu cpu = productLogic.create(new Cpu(Cpu.Series.CORE, EnumSet.of(Cpu.Type.MOBILE), "Muuuh"));
        assertNotNull(cpu);
        assertTrue(cpu.getId() > 0);
        productLogic.create(new Cpu(Cpu.Series.CORE, EnumSet.of(Cpu.Type.DESKTOP), "Maaah"));
        productLogic.create(new Cpu(Cpu.Series.ATHLON, EnumSet.of(Cpu.Type.MOBILE), "Miiih"));
        List<Cpu> cpus = specAgent.findAll(Cpu.class);
        assertNotNull(cpus);
        assertEquals(3, cpus.size());
    }
}
