package eu.ggnet.dwoss.spec.test;

import java.util.EnumSet;
import java.util.Set;

import jakarta.validation.Validator;

import org.junit.Test;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.piece.*;
import eu.ggnet.dwoss.core.system.util.ValidationUtil;

import jakarta.validation.Validation;

import static org.junit.Assert.assertTrue;

//TODO two identic GPUs should not exist.
/**
 *
 * @author bastian.venz
 */
public class SpecTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final BasicSpec basicSpec;

    private final Desktop desktop;

    public SpecTest() {
        basicSpec = new BasicSpec();
        basicSpec.setColor(BasicSpec.Color.RED);
        basicSpec.setComment("TestBasicSpec");
        basicSpec.setExtras(ProductSpec.Extra.BLUETOOTH, ProductSpec.Extra.E_SATA, ProductSpec.Extra.CONVERTABLE);
        Set<BasicSpec.VideoPort> ports = EnumSet.noneOf(BasicSpec.VideoPort.class);
        ports.add(BasicSpec.VideoPort.VGA);
        ports.add(BasicSpec.VideoPort.DISPLAY_PORT);
        ProductModel moppel = new ProductModel("ASDF Moddel");
        ProductFamily fam = new ProductFamily("FUUUUUUU");
        fam.setSeries(new ProductSeries(TradeName.GATEWAY, ProductGroup.MISC, "RageSeries"));
        moppel.setFamily(fam);
        basicSpec.setModel(moppel);
        basicSpec.setPartNo("LX.AAAAA.BBB");
        basicSpec.setVideoPorts(ports);

        desktop = new Desktop();

        //Copy data from tested BasicSpec
        desktop.setColor(basicSpec.getColor());
        desktop.setModel(basicSpec.getModel());
        desktop.setPartNo(basicSpec.getPartNo());
        desktop.setVideoPorts(basicSpec.getVideoPorts());

        //added Desktop elemets
        desktop.add(Desktop.Hdd.SSD_0016);
        desktop.add(Desktop.Odd.DVD_ROM);
        desktop.setComment("TestDesktop");
        desktop.setMemory(2048);
        desktop.setOs(Desktop.Os.WINDOWS_VISTA_ULTIMATE_32);
        desktop.setGpu(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.GEFORCE_300, "TestDesktopGPU"));
        desktop.setCpu(new Cpu(Cpu.Series.CORE_I7, "TestDesktopCPU", Cpu.Type.DESKTOP, Double.valueOf(2.4), Integer.valueOf(4)));
        Set<ProductSpec.Extra> extras = EnumSet.noneOf(ProductSpec.Extra.class);
        extras.add(ProductSpec.Extra.USB_3);
        extras.add(ProductSpec.Extra.UMTS);
        desktop.setExtras(extras);
    }

    /**
     * Test of getExtras method, of class BasicSpec.
     */
    @Test
    public void testBasicSpecs() {
        assertTrue("ViolationException: BasicSpec look like this: " + basicSpec.toString() + "\nViolations: " + ValidationUtil.formatToSingleLine(validator.validate(basicSpec)), validator.validate(basicSpec).isEmpty());
    }

    @Test
    public void testDesktop() {
        assertTrue("ViolationException: Desktop look like this: " + desktop.toString() + "\nViolations: " + ValidationUtil.formatToSingleLine(validator.validate(desktop)), validator.validate(desktop).isEmpty());
    }

    @Test
    public void testMonitor() {

        Display display = new Display(Display.Size._19, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE);
        Monitor monitor = new Monitor(display);
        monitor.setColor(BasicSpec.Color.RED);
        monitor.setModel(basicSpec.getModel());
        monitor.setPartNo(basicSpec.getPartNo());
        monitor.setComment("TestBasicMonitor");
        monitor.setExtras(ProductSpec.Extra.KAMERA, ProductSpec.Extra.CONVERTABLE);

        assertTrue("ViolationException: Monitor look like this: " + monitor.toString() + "\nViolations: " + ValidationUtil.formatToSingleLine(validator.validate(monitor)), validator.validate(monitor).isEmpty());
    }

    @Test
    public void testNotebook() {

        Notebook notebook = new Notebook();
        notebook.setColor(BasicSpec.Color.RED);
        notebook.setModel(basicSpec.getModel());
        notebook.setPartNo(basicSpec.getPartNo());
        notebook.setComment("TestBasicNotebook");
        notebook.setExtras(ProductSpec.Extra.KAMERA, ProductSpec.Extra.CONVERTABLE);

        notebook.setDisplay(new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE));
        desktop.add(Desktop.Hdd.SSD_0016);
        notebook.add(Desktop.Odd.DVD_ROM);
        notebook.setMemory(4096);
        notebook.setOs(Desktop.Os.WINDOWS_7_STARTER_32);
        notebook.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_300, "TestNotebookGPU"));
        notebook.setCpu(new Cpu(Cpu.Series.CORE_I7, "TestDesktopCPU", Cpu.Type.DESKTOP, 2.4, 4));
        Set<ProductSpec.Extra> extras = EnumSet.noneOf(ProductSpec.Extra.class);
        extras.add(ProductSpec.Extra.USB_3);
        extras.add(ProductSpec.Extra.UMTS);
        notebook.setExtras(extras);

        assertTrue("ViolationException: Notebook look like this: " + notebook.toString() + "\nViolations: " + ValidationUtil.formatToSingleLine(validator.validate(notebook)), validator.validate(notebook).isEmpty());
    }
}
