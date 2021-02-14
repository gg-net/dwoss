package eu.ggnet.dwoss.receipt.itest;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.ee.ProductProcessor.SpecAndModel;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.receipt.itest.support.DatabaseCleaner;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.piece.*;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;

import static eu.ggnet.dwoss.core.common.values.ProductGroup.NOTEBOOK;
import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.ACER;
import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.PACKARD_BELL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ReceiptProductLogicProductSpecIT extends ArquillianProjectArchive {

    @EJB
    private ProductProcessor productProcessor;

    @EJB
    private SpecAgent specAgent;

    @EJB
    private UniqueUnitAgent uuAgent;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    @Test
    public void testCreateProductSpec() throws Exception {
        final long GTIN = 123456782;

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

        ProductSpec testSpec = productProcessor.create(new SpecAndModel(notebook, productModel, 0));

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

        ProductSpec testSpec2 = productProcessor.create(new SpecAndModel(notebook2, productModel, GTIN));
        assertNotNull(testSpec2);
        assertNotSame(testSpec2, testSpec);

        Product product = uuAgent.findById(Product.class, testSpec2.getProductId());
        assertThat(product).isNotNull().returns(GTIN, Product::getGtin);

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

        productProcessor.create(new SpecAndModel(notebook, productModel, 0));
        productProcessor.create(new SpecAndModel(notebook, productModel, 0));
        fail("Error 040: No Exception Found at: CreateProductSpec");
    }

    @Test
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

        ProductSpec spec = productProcessor.create(new SpecAndModel(notebook, productModel, 0));
        ProductFamily family = spec.getModel().getFamily();

        ProductModel productModel2 = productProcessor.create(ACER, NOTEBOOK, family.getSeries(), family, "TestModel2");

        long idOfOldModel = productModel.getId();
        long idOfNewModel = productModel2.getId();

        String comment = "MuhBlub";
        ((Notebook)spec).setComment(comment);

        productProcessor.update(new SpecAndModel(spec, productModel2, 0));

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
        assertThat(specs.get(0).getModel().getId()).as("ID of the Model should match new model, not old " + idOfOldModel).isEqualTo(idOfNewModel);
        assertEquals(comment, ((Notebook)specs.get(0)).getComment());
    }
}
