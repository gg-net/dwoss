package eu.ggnet.dwoss.receipt.itest;

import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.ProductProcessor;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.entity.*;
import eu.ggnet.dwoss.spec.entity.piece.*;

import static eu.ggnet.dwoss.rules.ProductGroup.NOTEBOOK;
import static eu.ggnet.dwoss.rules.TradeName.ACER;
import static eu.ggnet.dwoss.rules.TradeName.PACKARD_BELL;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReceiptProductLogicProductSpecIT extends ArquillianProjectArchive {

    @EJB
    private ProductProcessor productProcessor;

    @EJB
    private SpecAgent specAgent;

    @Test
    public void testCreateProductSpec() throws Exception {
        //Create a CPU and GPU and persist it.
        Cpu cpu = productProcessor.create(new Cpu(Cpu.Series.AMD_V, "TestCPU", Cpu.Type.MOBILE, 2.0, 5));
        Gpu gpu = productProcessor.create(new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_100, "TestGPU"));

        //Persist Display
        Display display = new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE);
        ProductModel productModel = new ProductModel("M", new ProductFamily("F", new ProductSeries(ACER, NOTEBOOK, "S")));

        Notebook notebook = new Notebook();
        notebook.setDisplay(display);
        notebook.setGpu(gpu);
        notebook.setCpu(cpu);
        notebook.setMemory(2048);
        notebook.setOs(Desktop.Os.LINUX);
        notebook.add(Desktop.Hdd.SSD_0016);
        notebook.add(Desktop.Hdd.ROTATING_2000);
        notebook.add(Desktop.Odd.DVD_ROM);
        notebook.setExtras(ProductSpec.Extra.E_SATA, ProductSpec.Extra.HIGHT_CHANGEABLE);
        notebook.setPartNo("LX.ASDFG.GHJ");
        notebook.setModel(productModel);

        ProductSpec testSpec = productProcessor.create(notebook, productModel);

        assertNotNull(testSpec);

        Notebook notebook2 = new Notebook();
        notebook2.setDisplay(display);
        notebook2.setGpu(gpu);
        notebook2.setCpu(cpu);
        notebook2.setMemory(2048);
        notebook2.setOs(Desktop.Os.LINUX);
        notebook2.add(Desktop.Hdd.SSD_0016);
        notebook2.add(Desktop.Hdd.ROTATING_2000);
        notebook2.add(Desktop.Odd.DVD_ROM);
        notebook2.setExtras(ProductSpec.Extra.E_SATA, ProductSpec.Extra.HIGHT_CHANGEABLE);
        notebook2.setPartNo("LX.ASDFG.GH2");
        notebook2.setModel(productModel);

        ProductSpec testSpec2 = productProcessor.create(notebook2, productModel);
        assertNotNull(testSpec2);
        assertNotSame(testSpec2, testSpec);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateProductSpecException() {

        //Create a CPU and GPU and persist it.
        Cpu cpu = productProcessor.create(new Cpu(Cpu.Series.AMD_V, "TestCPU", Cpu.Type.MOBILE, 2.0, 5));
        Gpu gpu = productProcessor.create(new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_100, "TestGPU"));

        //Persist Display
        Display display = new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE);
        ProductModel productModel = new ProductModel("M", new ProductFamily("F", new ProductSeries(PACKARD_BELL, NOTEBOOK, "S")));

        Notebook notebook = new Notebook();
        notebook.setDisplay(display);
        notebook.setGpu(gpu);
        notebook.setCpu(cpu);
        notebook.setMemory(2048);
        notebook.setOs(Desktop.Os.LINUX);
        notebook.add(Desktop.Hdd.SSD_0016);
        notebook.add(Desktop.Hdd.ROTATING_2000);
        notebook.add(Desktop.Odd.DVD_ROM);
        notebook.setExtras(ProductSpec.Extra.E_SATA, ProductSpec.Extra.HIGHT_CHANGEABLE);
        notebook.setPartNo("LX.ASDFG.GHJ");
        notebook.setModel(productModel);

        productProcessor.create(notebook, productModel);
        productProcessor.create(notebook, productModel);
        fail("Error 040: No Exception Found at: CreateProductSpec");
    }

    @Test
    // Produced Optimistic Lock Exception.
    public void testUpdateProductSpecModelChange() {
        ProductModel productModel = productProcessor.create(ACER, NOTEBOOK, null, null, "TestModel");

        Cpu cpu = productProcessor.create(new Cpu(Cpu.Series.AMD_V, "TestCPU", Cpu.Type.MOBILE, 2.0, 5));
        Gpu gpu = productProcessor.create(new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_100, "TestGPU"));

        Notebook notebook = new Notebook();
        notebook.setDisplay(new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE));
        notebook.setGpu(gpu);
        notebook.setCpu(cpu);
        notebook.setMemory(2048);
        notebook.setOs(Desktop.Os.LINUX);
        notebook.add(Desktop.Hdd.SSD_0016);
        notebook.add(Desktop.Hdd.ROTATING_2000);
        notebook.add(Desktop.Odd.DVD_ROM);
        notebook.setExtras(ProductSpec.Extra.E_SATA, ProductSpec.Extra.HIGHT_CHANGEABLE);
        notebook.setPartNo("LX.ASDFG.GHP");
        notebook.setModel(productModel);

        ProductSpec spec = productProcessor.create(notebook, productModel);
        ProductFamily family = spec.getModel().getFamily();

        ProductModel productModel2 = productProcessor.create(ACER, NOTEBOOK, family.getSeries(), family, "TestModel2");

        spec = productProcessor.refresh(spec, productModel2);

        long model2Id = spec.getModel().getId();

        String comment = "MuhBlub";
        ((Notebook)spec).setComment(comment);

        productProcessor.update(spec);

        List<ProductSeries> series = specAgent.findAllEager(ProductSeries.class);
        assertNotNull(series);
        assertEquals(1, series.size());
        assertNotNull(series.get(0));
        assertNotNull(series.get(0).getFamilys());
        assertEquals(1, series.get(0).getFamilys().size());
        assertNotNull(series.get(0).getFamilys().toArray()[0]);

        List<ProductSpec> specs = specAgent.findAllEager(ProductSpec.class);
        assertNotNull(specs);
        assertEquals(1, specs.size());
        assertEquals(model2Id, specs.get(0).getModel().getId());
        assertEquals(comment, ((Notebook)specs.get(0)).getComment());
    }
}
