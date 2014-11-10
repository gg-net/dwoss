package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.receipt.ProductProcessor;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class ReceiptProductLogicIT {

    //<editor-fold defaultstate="collapsed" desc=" SetUp ">
    private EJBContainer container;

    @EJB
    private ProductProcessor productLogic;

    @EJB
    private SpecAgent specAgent;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" createCpu Testing ">
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
    //</editor-fold>
}
