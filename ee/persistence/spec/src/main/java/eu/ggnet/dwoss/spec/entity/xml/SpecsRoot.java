/* 
 * Copyright (C) 2014 pascal.perau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.spec.entity.xml;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.*;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.entity.*;
import eu.ggnet.dwoss.spec.entity.piece.*;

/**
 *
 * @author oliver.guenther
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SpecsRoot implements Serializable {

    @XmlElements({
        @XmlElement(name = "notebook", type = Notebook.class),
        @XmlElement(name = "basic", type = BasicSpec.class),
        @XmlElement(name = "desktop", type = Desktop.class),
        @XmlElement(name = "allInOne", type = AllInOne.class),
        @XmlElement(name = "desktopBundle", type = DesktopBundle.class),
        @XmlElement(name = "monitor", type = Monitor.class),
        @XmlElement(name = "tablet", type = Tablet.class)
    })
    private List<? extends ProductSpec> productSpecs;

    public SpecsRoot() {
        productSpecs = new ArrayList<>();
    }

    public SpecsRoot(List<? extends ProductSpec> productSpecs) {
        this.productSpecs = productSpecs;
    }

    public List<? extends ProductSpec> getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(List<? extends ProductSpec> productSpecs) {
        this.productSpecs = productSpecs;
    }

    public static void main(String[] args) {

        List<ProductSpec> specs = new ArrayList<>();
        // A Notebook
        ProductSeries ps = new ProductSeries(TradeName.ACER, ProductGroup.NOTEBOOK, "TravelMate");
        ProductFamily pf = new ProductFamily("TravelMate 8700");
        pf.setSeries(ps);
        ProductModel pm = new ProductModel("TravelMate 8741-81222132");
        pm.setFamily(pf);
        Notebook notebook = new Notebook();
        notebook.setPartNo("LX.AAAAA.BBB");
        notebook.setModel(pm);
        notebook.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        notebook.setComment("Ein Kommentar");
        notebook.setCpu(new Cpu(Cpu.Series.CORE, "Eine CPU", Cpu.Type.MOBILE, 123.1, 2));
        notebook.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        notebook.setOs(Desktop.Os.LINUX);
        notebook.setMemory(12345);
        notebook.add(Desktop.Hdd.ROTATING_0500);
        notebook.add(Desktop.Odd.BLURAY_COMBO);
        notebook.setExtras(Desktop.Extra.KAMERA);
        notebook.setDisplay(new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE));

        specs.add(notebook);
        // An AllInOne
        ps = new ProductSeries(TradeName.ACER, ProductGroup.ALL_IN_ONE, "AllInOne");

        pf = new ProductFamily("Z5600");
        pf.setSeries(ps);
        pm = new ProductModel("Z6523");
        pm.setFamily(pf);
        AllInOne allInOne = new AllInOne();
        allInOne.setPartNo("PX.AASAA.BBB");
        allInOne.setModel(pm);
        allInOne.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        allInOne.setComment("Ein Kommentar");
        allInOne.setCpu(new Cpu(Cpu.Series.CELERON, "Eine CPU", Cpu.Type.MOBILE, 123.1, 2));
        allInOne.setGpu(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte"));
        allInOne.setOs(Desktop.Os.LINUX);
        allInOne.setMemory(12345);
        allInOne.add(Desktop.Hdd.ROTATING_0500);
        allInOne.add(Desktop.Odd.BLURAY_COMBO);
        allInOne.setExtras(Desktop.Extra.KAMERA);
        allInOne.setDisplay(new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE));

        specs.add(allInOne);
        // A Desktop
        ProductSeries veriton = new ProductSeries(TradeName.ACER, ProductGroup.DESKTOP, "Veriton");

        ProductFamily m400 = new ProductFamily("M400");
        m400.setSeries(veriton);
        ProductModel M480G = new ProductModel("M480G");
        M480G.setFamily(m400);
        Gpu gpu = new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Eine Graphiccarte");
        Cpu cpu = new Cpu(Cpu.Series.CORE, "Eine CPU", Cpu.Type.MOBILE, 123.1, 2);
        cpu.setEmbeddedGpu(gpu);

        Desktop M480G_1 = new Desktop("PX.99999.321", 2L);
        M480G_1.setModel(M480G);
        M480G_1.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        M480G_1.setComment("Ein Kommentar");
        M480G_1.setCpu(cpu);
        M480G_1.setGpu(gpu);
        M480G_1.setOs(Desktop.Os.LINUX);
        M480G_1.setMemory(12345);
        M480G_1.add(Desktop.Hdd.ROTATING_0500);
        M480G_1.add(Desktop.Odd.BLURAY_COMBO);
        M480G_1.setExtras(Desktop.Extra.KAMERA);
        specs.add(M480G_1);

        // A Monitor
        ProductSeries a = new ProductSeries(TradeName.ACER, ProductGroup.MONITOR, "A");
        ProductFamily a230 = new ProductFamily("A230");
        a230.setSeries(a);
        ProductModel a231Hbmd = new ProductModel("A231Hbmd");
        a231Hbmd.setFamily(a230);
        Monitor A231spec = new Monitor(new Display(Display.Size._11_6, Display.Resolution.VGA,
                Display.Type.CRYSTAL_BRIGHT, Display.Ration.SIXTEEN_TO_NINE));
        A231spec.setModel(a231Hbmd);
        A231spec.setPartNo("ET.VA1HE.008");
        A231spec.setProductId(3L);
        A231spec.setVideoPorts(EnumSet.allOf(BasicSpec.VideoPort.class));
        A231spec.setComment("Ein Kommentar");
        specs.add(A231spec);

        // A Bundle
        ProductSeries box = new ProductSeries(TradeName.ACER, ProductGroup.DESKTOP_BUNDLE, "Veriton");
        ProductFamily boxf = new ProductFamily("M480");
        boxf.setSeries(box);
        ProductModel boxm = new ProductModel("M480G + A231MuhMäh");
        boxm.setFamily(boxf);

        DesktopBundle bundle = new DesktopBundle();
        bundle.setPartNo("BL.32199.321");
        bundle.setProductId(1L);
        bundle.setDesktop(M480G_1);
        bundle.setMonitor(A231spec);
        bundle.setModel(boxm);
        specs.add(bundle);

        SpecsRoot root = new SpecsRoot(specs);

        StringWriter sw = new StringWriter();
        JAXB.marshal(root, sw);
        System.out.println(sw.toString());
    }
}
