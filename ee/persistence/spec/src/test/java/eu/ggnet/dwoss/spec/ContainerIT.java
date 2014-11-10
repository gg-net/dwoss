package eu.ggnet.dwoss.spec;

import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;

import java.util.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.eao.CpuEao;
import eu.ggnet.dwoss.spec.eao.GpuEao;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.emo.DisplayEmo;
import eu.ggnet.dwoss.spec.emo.ProductModelEmo;
import eu.ggnet.dwoss.spec.entity.*;

import static org.junit.Assert.*;

/**
 * Test for correct injection of EntityManagers
 */
public class ContainerIT {

    private EJBContainer container;

    @Inject
    @Specs
    private EntityManagerFactory emf;

    @Inject
    private GeneratorBean generatorBean;

    @EJB
    private SpecAgent specAgent;

    @Produces
    public static ReceiptCustomers p = new ReceiptCustomers(new HashMap<>());

    @Produces
    SpecialSystemCustomers sc = new SpecialSystemCustomers(new HashMap<>());

    @Produces
    PostLedger pl = new PostLedger(new HashMap<>());

    @Produces
    ShippingTerms st = new ShippingTerms(new HashMap<>());

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testInjected() {
        assertNotNull("Container is null", container);
        assertNotNull("EntityManagerFactory is null", emf);
    }

    @Test
    public void testAgent() {
        ProductSpec spec = generatorBean.makeOne();
        assertNotNull(spec);
        assertNotNull(specAgent);
        List<ProductSpec> specs = specAgent.findAll(ProductSpec.class);
        assertFalse(specs.isEmpty());
        assertEquals(1, specs.size());
        assertEquals(spec, specs.get(0));
    }

    @Stateless
    public static class GeneratorBean {

        @Inject
        @Specs
        private EntityManager specEm;

        public ProductSpec makeOne() {
            SpecGenerator g = new SpecGenerator();
            ProductSpec spec = g.makeSpec();
            ProductModel model = spec.getModel();
            ProductModelEmo productModelEmo = new ProductModelEmo(specEm);
            model = productModelEmo.request(
                    model.getFamily().getSeries().getBrand(),
                    model.getFamily().getSeries().getGroup(),
                    model.getFamily().getSeries().getName(),
                    model.getFamily().getName(),
                    model.getName());
            spec.setModel(model);
            if ( spec instanceof DisplayAble ) {
                DisplayAble da = (DisplayAble)spec;
                da.setDisplay(new DisplayEmo(specEm).weakRequest(
                        da.getDisplay().getSize(),
                        da.getDisplay().getResolution(),
                        da.getDisplay().getType(),
                        da.getDisplay().getRation()));
            }
            if ( spec instanceof Desktop ) {
                Desktop desktop = (Desktop)spec;
                if ( desktop.getCpu() == null || desktop.getGpu() == null ) throw new IllegalArgumentException("Cpu or Gpu of a Desktop are null. " + desktop);
                Cpu cpu = new CpuEao(specEm).findById(desktop.getCpu().getId());
                Gpu gpu = new GpuEao(specEm).findById(desktop.getGpu().getId());
                if ( cpu != null ) desktop.setCpu(cpu);
                if ( gpu != null ) desktop.setGpu(gpu);
            }
            specEm.persist(spec);
            return spec;

        }
    }
}
