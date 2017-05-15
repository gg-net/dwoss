package eu.ggnet.dwoss.spec.itest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.spec.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.eao.CpuEao;
import eu.ggnet.dwoss.spec.eao.GpuEao;
import eu.ggnet.dwoss.spec.emo.DisplayEmo;
import eu.ggnet.dwoss.spec.emo.ProductModelEmo;
import eu.ggnet.dwoss.spec.entity.*;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;

import static org.junit.Assert.*;

/**
 * Test for correct injection of EntityManagers
 */
@RunWith(Arquillian.class)
public class ContainerIT extends ArquillianProjectArchive {

    @Inject
    private GeneratorBean generatorBean;

    @EJB
    private SpecAgent specAgent;

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
