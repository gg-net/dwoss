package eu.ggnet.dwoss.spec.itest;

import eu.ggnet.dwoss.spec.itest.support.ContainerITGeneratorHelper;
import eu.ggnet.dwoss.spec.itest.support.ArquillianProjectArchive;

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
    private ContainerITGeneratorHelper generatorBean;

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

}
