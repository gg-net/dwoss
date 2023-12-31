/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.receipt.ui.tryout.stub;

import eu.ggnet.dwoss.core.common.values.ShipmentStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import jakarta.enterprise.inject.Alternative;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.ReceiptOperation;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers.Key;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;
import eu.ggnet.dwoss.receipt.ee.*;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.assist.SpecConstants;
import eu.ggnet.dwoss.spec.ee.assist.SpecPu;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Hdd;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Odd;
import eu.ggnet.dwoss.spec.ee.entity.Desktop.Os;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec.Extra;
import eu.ggnet.dwoss.spec.ee.entity.*;
import eu.ggnet.dwoss.spec.ee.entity.piece.*;
import eu.ggnet.dwoss.spec.ee.format.SpecFormater;
import eu.ggnet.dwoss.stock.ee.StockAgent;
import eu.ggnet.dwoss.stock.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.ee.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.ee.assist.gen.UniqueUnitGenerator;
import eu.ggnet.dwoss.uniqueunit.ee.entity.*;

import static eu.ggnet.dwoss.core.common.values.ProductGroup.*;
import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.*;

/**
 * Stubclass for offline usage and testing.
 *
 * @author oliver.guenther
 */
@Alternative
public class ProductProcessorStub implements ProductProcessor {

    public static class EditProduct {

        private final String partNo;

        private final TradeName manufacturer;

        private final String description;

        public EditProduct(ProductSpec spec) {
            this.partNo = spec.getPartNo();
            this.manufacturer = spec.getModel().getFamily().getSeries().getBrand().getManufacturer();
            this.description = SpecFormater.toDetailedName(spec);
        }

        public TradeName manufacturer() {
            return manufacturer;
        }

        public String description() {
            return description;
        }

        public String partNo() {
            return partNo;
        }

        @Override
        public String toString() {
            return "EditProduct{" + "partNo=" + partNo + ", manufacturer=" + manufacturer + ", description=" + description + '}';
        }

    }

    private final Set<ProductSeries> serieses;

    private final List<Gpu> gpus;

    private final List<Cpu> cpus;

    private final Map<String, ProductSpec> specs;

    private final SpecOfflineConstructor soc = SpecOfflineConstructor.getInstance();

    private final List<Shipment> shipments;

    public final List<Stock> stocks;

    public Desktop desktop;

    public Notebook notebook;

    public AllInOne allInOne;

    public Monitor monitor;

    private final Random R = new Random();

    private final List<Product> products;

    private final List<UniqueUnit> uniqueUnits;

    private final List<StockUnit> stockUnits;

    private final LocalDateTime now = LocalDateTime.now();

    private final List<Date> dates = new ArrayList<>(Arrays.asList(Date.from(now.minusDays(1).toInstant(ZoneOffset.UTC)), Date.from(now.plusDays(1).toInstant(ZoneOffset.UTC)), Date.from(now.toInstant(ZoneOffset.UTC))));

    private final List<PriceType> priceTypes = new ArrayList<>(Arrays.asList(PriceType.values()));

    private final static List<TradeName> CONTRACTORS = Arrays.asList(AMAZON, EBAY, ACER, APPLE, INGRAM_MICRO);

    private final static Set<TradeName> BRANDS = EnumSet.of(APPLE, ACER, PACKARD_BELL, LENOVO, HP);

    private final Map<Key, Long> RECEIPT_CUSTOMERS;

    public final List<EditProduct> editProducts;

    public String editAbleRefurbishId;

    public ProductProcessorStub() {
        cpus = new ArrayList<>();
        cpus.add(new Cpu(Cpu.Series.ATHLON, EnumSet.of(Cpu.Type.MOBILE), "A123"));
        cpus.add(new Cpu(Cpu.Series.ATHLON, EnumSet.of(Cpu.Type.MOBILE), "B22"));
        cpus.add(new Cpu(Cpu.Series.ATHLON, EnumSet.of(Cpu.Type.MOBILE), "B22"));
        cpus.add(new Cpu(Cpu.Series.CORE, "Quad Q9000", Cpu.Type.MOBILE, 2.26, 4));
        cpus.add(new Cpu(Cpu.Series.CORE, "Quad Q9100", Cpu.Type.MOBILE, 2.00, 4));
        cpus.add(new Cpu(Cpu.Series.CORE, "Duo X6800", Cpu.Type.DESKTOP, 2.93, 2));
        cpus.add(new Cpu(Cpu.Series.CORE, "Duo E6850", Cpu.Type.DESKTOP, 3.00, 2));
        cpus.add(new Cpu(Cpu.Series.XEON, "X5670", Cpu.Type.DESKTOP, 2.93, 6));
        cpus.add(new Cpu(Cpu.Series.XEON, "L5530", Cpu.Type.DESKTOP, 2.4, 4));
        cpus.add(new Cpu(Cpu.Series.ATHLON, "X2 Dual-Core TK-53", Cpu.Type.MOBILE, 1.70, 2));
        cpus.add(new Cpu(Cpu.Series.TURION, "II Ultra Dual-Core m660", Cpu.Type.MOBILE, 2.70, 2));
        cpus.add(new Cpu(Cpu.Series.PHENOM, "II X4 805", Cpu.Type.DESKTOP, 2.50, 4));
        cpus.add(new Cpu(Cpu.Series.CORE_I7, "860", Cpu.Type.DESKTOP, 2.8, 4));
        cpus.add(new Cpu(Cpu.Series.CORE_I7, "930", Cpu.Type.DESKTOP, 2.8, 4));
        cpus.add(new Cpu(Cpu.Series.CORE_I5, "750", Cpu.Type.DESKTOP, 2.66, 4));
        cpus.add(new Cpu(Cpu.Series.CORE_I5, "650", Cpu.Type.DESKTOP, 3.2, 2));
        cpus.add(new Cpu(Cpu.Series.CORE_I3, "2130", Cpu.Type.DESKTOP, 3.4, 2));

        gpus = new ArrayList<>();
        gpus.add(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_5000, "Mobility Radeon™ HD 540v Series"));
        gpus.add(new Gpu(Gpu.Type.MOBILE, Gpu.Series.RADEON_HD_4000, "Mobility Radeon™ HD 4000"));
        gpus.add(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.RADEON_HD_5000, "5850 Graphics"));
        gpus.add(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.RADEON_HD_6000, "6990 Graphics"));
        gpus.add(new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_500, "570M"));
        gpus.add(new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_500, "525M"));
        gpus.add(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.GEFORCE_500, "590"));
        gpus.add(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.GEFORCE_200, "250"));
        gpus.add(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.INTEL_GRAPHICS, "Graphics Media Accelerator"));
        gpus.add(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.INTEL_GRAPHICS, "HD Graphics"));
        gpus.add(new Gpu(Gpu.Type.MOBILE, Gpu.Series.INTEL_GRAPHICS, "Graphics Media Accelerator"));
        gpus.add(new Gpu(Gpu.Type.MOBILE, Gpu.Series.INTEL_GRAPHICS, "HD Graphics"));
        gpus.add(new Gpu(Gpu.Type.DESKTOP, Gpu.Series.GEFORCE_400, "460"));

        serieses = new HashSet<>();

        // ACER Series
        ProductSeries veriton = soc.newProductSeries(ACER, DESKTOP, "Veriton");
        ProductSeries aspire = soc.newProductSeries(ACER, DESKTOP, "Aspire");
        ProductSeries aspire_n = soc.newProductSeries(ACER, NOTEBOOK, "Aspire");
        ProductSeries travelmate = soc.newProductSeries(ACER, NOTEBOOK, "TravelMate");
        ProductSeries tablet = soc.newProductSeries(ACER, MISC, "Tablet");
        ProductSeries monitor_a = soc.newProductSeries(ACER, MONITOR, "A Series");
        ProductSeries allInOneS = soc.newProductSeries(ACER, ALL_IN_ONE, "All In One");
        ProductSeries bundleSeries = soc.newProductSeries(ACER, DESKTOP_BUNDLE, "DB Series");

        // PB Series
        ProductSeries imedia = soc.newProductSeries(PACKARD_BELL, DESKTOP, "iMedia");
        ProductSeries ixtreme = soc.newProductSeries(PACKARD_BELL, DESKTOP, "iXtreme");
        ProductSeries ipower = soc.newProductSeries(PACKARD_BELL, DESKTOP, "iPower");
        ProductSeries easynote = soc.newProductSeries(PACKARD_BELL, NOTEBOOK, "EasyNote");
        ProductSeries dot = soc.newProductSeries(PACKARD_BELL, NOTEBOOK, "Dot");

        serieses.add(veriton);
        serieses.add(aspire);
        serieses.add(aspire_n);
        serieses.add(travelmate);
        serieses.add(imedia);
        serieses.add(ipower);
        serieses.add(ixtreme);
        serieses.add(easynote);
        serieses.add(dot);
        serieses.add(tablet);
        serieses.add(monitor_a);
        serieses.add(allInOneS);
        serieses.add(bundleSeries);

        //ACER families
        ProductFamily veriton_n = soc.newProductFamily();
        ProductFamily veriton_m = soc.newProductFamily();
        ProductFamily veriton_l = soc.newProductFamily();
        ProductFamily aspiren_family = soc.newProductFamily();
        ProductFamily aspiren_timelinex = soc.newProductFamily();
        ProductFamily aspiren_ethos = soc.newProductFamily();
        ProductFamily travelmate_family = soc.newProductFamily();
        ProductFamily travelmate_timelinex = soc.newProductFamily();
        ProductFamily aspire_m = soc.newProductFamily();
        ProductFamily aspire_x = soc.newProductFamily();
        ProductFamily predator = soc.newProductFamily();
        ProductFamily tablet_A = soc.newProductFamily();
        ProductFamily monitor_A23 = soc.newProductFamily();
        ProductFamily allInOneF = soc.newProductFamily();
        ProductFamily bundleFamiliy = soc.newProductFamily();

        veriton_m.setName("Veriton M");
        veriton_n.setName("Veriton N");
        veriton_l.setName("Veriton L");
        aspiren_family.setName("Aspire");
        aspire_m.setName("Aspire M");
        aspire_x.setName("Aspire X");
        predator.setName("Predator");
        travelmate_family.setName("TravelMate");
        aspiren_ethos.setName("Aspire Ethos");
        aspiren_timelinex.setName("Aspire TimelineX");
        travelmate_timelinex.setName("TravelMate TimelineX");
        tablet_A.setName("Tablet Iconia A");
        monitor_A23.setName("A230");
        allInOneF.setName("All In One");
        bundleFamiliy.setName("Th DB Familiy");

        veriton.addFamily(veriton_m);
        veriton.addFamily(veriton_n);
        veriton.addFamily(veriton_l);
        aspire.addFamily(aspire_x);
        aspire.addFamily(aspire_m);
        aspire.addFamily(predator);
        aspire_n.addFamily(aspiren_ethos);
        aspire_n.addFamily(aspiren_family);
        aspire_n.addFamily(aspiren_timelinex);
        travelmate.addFamily(travelmate_family);
        travelmate.addFamily(travelmate_timelinex);
        tablet.addFamily(tablet_A);
        monitor_a.addFamily(monitor_A23);
        allInOneS.addFamily(allInOneF);
        bundleSeries.addFamily(bundleFamiliy);

        ProductModel g7700 = soc.newProductModel();
        ProductModel m1 = soc.newProductModel();
        ProductModel m2 = soc.newProductModel();
        ProductModel m3 = soc.newProductModel();
        ProductModel l1 = soc.newProductModel();
        ProductModel l2 = soc.newProductModel();
        ProductModel a500 = soc.newProductModel();
        ProductModel monitor_a231Bwsx = soc.newProductModel();
        ProductModel p6 = soc.newProductModel();
        ProductModel all = soc.newProductModel();
        ProductModel bundleModel = soc.newProductModel();

        g7700.setName("Aspire Predator G7700");
        m1.setName("Veriton M480G");
        m2.setName("Veriton M460");
        m3.setName("Veriton M420");
        l1.setName("Veriton L480G");
        l2.setName("Veriton L670");
        a500.setName("Iconia A500");
        monitor_a231Bwsx.setName("A231Bwsx");
        p6.setName("TravelMate P648");
        all.setName("All-In-One 1000");
        bundleModel.setName("The DB Model");

        predator.addModel(g7700);
        veriton_m.addModel(m1);
        veriton_m.addModel(m2);
        veriton_m.addModel(m3);
        veriton_l.addModel(l1);
        veriton_l.addModel(l2);
        tablet_A.addModel(a500);
        monitor_A23.addModel(monitor_a231Bwsx);
        travelmate_family.addModel(p6);
        allInOneF.addModel(all);
        bundleFamiliy.addModel(bundleModel);

        specs = new HashMap<>();

        BasicSpec one = soc.newBasicSpec();
        one.setModel(a500);
        one.setPartNo("AX.12345.999");
        one.setProductId(5L);
        one.setExtras(EnumSet.of(ProductSpec.Extra.BLUETOOTH, ProductSpec.Extra.SPEAKERS));
        one.setComment("Ein toller Kommentar");

        specs.put(one.getPartNo(), one);
        monitor = soc.newMonitor();
        monitor.setModel(monitor_a231Bwsx);
        monitor.setPartNo("ET.99999.111");
        monitor.setProductId(10L);
        monitor.setExtras(EnumSet.of(ProductSpec.Extra.BLUETOOTH, ProductSpec.Extra.SPEAKERS));
        monitor.setComment("Ein toller Kommentar");
        monitor.setDisplay(new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.SIXTEEN_TO_TEN));
        specs.put(monitor.getPartNo(), monitor);

        desktop = new Desktop();
        desktop.setModel(m3);
        desktop.setPartNo("PP.XXXXX.AAA");
        desktop.setOs(Desktop.Os.WINDOWS_7_HOME_PREMIUM_64);
        desktop.setMemory(6144);
        desktop.setCpu(cpus.get(0));
        desktop.setGpu(gpus.get(0));
        desktop.setExtras(ProductSpec.Extra.PS_2, ProductSpec.Extra.E_SATA);
        desktop.setVideoPorts(EnumSet.of(BasicSpec.VideoPort.DISPLAY_PORT, BasicSpec.VideoPort.HDMI, BasicSpec.VideoPort.DVI));
        desktop.setColor(BasicSpec.Color.RED);
        desktop.setComment("Iss mal'n cooler PC dieser supermegacore delacrux");
        desktop.add(Hdd.ROTATING_1000);
        desktop.add(Hdd.SSD_0064);
        desktop.add(Odd.DVD_SUPER_MULTI);
        desktop.add(Odd.BLURAY_COMBO);
        specs.put(desktop.getPartNo(), desktop);

        notebook = new Notebook();
        notebook.setModel(p6);
        notebook.setPartNo("LX.12345.AAB");
        notebook.setDisplay(new Display(Display.Size._18_4, Display.Resolution.FULL_HD, Display.Type.CRYSTAL_BRIGHT, Display.Ration.SIXTEEN_TO_NINE));
        notebook.setOs(Os.WINDOWS_7_HOME_PREMIUM_64);
        notebook.setCpu(cpus.get(2));
        notebook.setGpu(gpus.get(3));
        notebook.setMemory(1024);
        notebook.setExtras(Extra.BLUETOOTH, Extra.WLAN_TO_N, Extra.KAMERA, Extra.CARD_READER);
        notebook.add(Hdd.ROTATING_0500);
        notebook.add(Odd.DVD_SUPER_MULTI);
        notebook.setColor(BasicSpec.Color.RED);
        specs.put(notebook.getPartNo(), notebook);

        allInOne = new AllInOne();
        allInOne.setModel(all);
        allInOne.setPartNo("PB.XXXXA.123");
        allInOne.setDisplay(new Display(Display.Size._18_4, Display.Resolution.FULL_HD, Display.Type.CRYSTAL_BRIGHT, Display.Ration.SIXTEEN_TO_NINE));
        allInOne.setOs(Os.WINDOWS_7_HOME_PREMIUM_64);
        allInOne.setCpu(cpus.get(2));
        allInOne.setGpu(gpus.get(3));
        allInOne.setMemory(1024);
        allInOne.setExtras(Extra.CARD_READER, Extra.FINGER_SCANNER, Extra.SPEAKERS);
        allInOne.add(Hdd.SSD_0016);
        allInOne.add(Odd.BLURAY_SUPER_MULTI);
        allInOne.setColor(BasicSpec.Color.ORANGE);

        specs.put(allInOne.getPartNo(), allInOne);

        editProducts = Arrays.asList(
                new EditProduct(one),
                new EditProduct(monitor),
                new EditProduct(desktop),
                new EditProduct(notebook),
                new EditProduct(allInOne)
        );

        // Stock
        shipments = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            shipments.add(new Shipment("Test-SH-" + i,
                    TradeName.values()[R.nextInt(TradeName.values().length)],
                    TradeName.getManufacturers().toArray(new TradeName[0])[R.nextInt(TradeName.getManufacturers().size())],
                    ShipmentStatus.values()[R.nextInt(ShipmentStatus.values().length)]));
        }
        stocks = Arrays.asList(new Stock(1, "Hamburg"), new Stock(2, "Lübeck"));

        //Mandators
        RECEIPT_CUSTOMERS = new HashMap<>();
        long i = 0;
        for (TradeName contractor : CONTRACTORS) {
            for (ReceiptOperation ro : ReceiptOperation.values()) {
                RECEIPT_CUSTOMERS.put(Key.of(contractor, ro), i);
                i++;
            }
        }

        // UniqueUnit
        UniqueUnitGenerator uug = new UniqueUnitGenerator();
        products = new ArrayList<>();
        uniqueUnits = new ArrayList<>();
        stockUnits = new ArrayList<>();

        for (ProductSpec spec : specs.values()) {
            Product p = new Product(
                    spec.getModel().getFamily().getSeries().getGroup(),
                    spec.getModel().getFamily().getSeries().getBrand(),
                    spec.getPartNo(),
                    spec.getModel().getName()
            );
            p.setGtin(R.nextInt(15000));
            p.setEol(dates.get(R.nextInt(dates.size())));
            p.setPrice(priceTypes.get(R.nextInt(priceTypes.size())), R.nextInt(9999), "");
            products.add(p);
            for (int j = 0; j < 10; j++) {
                UniqueUnit uu = uug.makeUniqueUnit(CONTRACTORS.get(R.nextInt(CONTRACTORS.size())), p);
                uu.setProduct(p);
                uniqueUnits.add(uu);
                if ( R.nextInt(10) <= 7 ) {
                    StockUnit su = new StockUnit(uu.getRefurbishId(), uu.getProduct().getName(), uu.getId());
                    su.setStock(stocks.get(0));
                    editAbleRefurbishId = su.getRefurbishId();
                    stockUnits.add(su);
                }
            }
        }

    }

    public SpecAgent specAgent() {
        return new SpecAgentStub(serieses, gpus, cpus, specs);
    }

    public StockAgent stockAgent() {
        return new StockAgentStub(shipments, stocks, stockUnits);
    }

    public CachedMandators cachedMandators() {
        return new MandatorsStub(CONTRACTORS, BRANDS, RECEIPT_CUSTOMERS);
    }

    public UnitSupporter unitSupporter() {
        return new UnitSupporterStub(uniqueUnits);
    }

    public UnitProcessor unitProcessor() {
        return new UnitProcessorStub(uniqueUnits, stockUnits);
    }

    public UniqueUnitAgent uniqueUnitAgent() {
        return new UniqueUnitAgentStub(uniqueUnits, products);
    }

    @Override
    public ProductSpec create(SpecAndModel sam) {
        specs.put(sam.spec().getPartNo(), sam.spec());
        System.out.println(this.getClass().getSimpleName() + ".create(" + sam + ")");
        return sam.spec();
    }

    @Override
    public ProductSpec update(SpecAndModel sam) throws IllegalArgumentException {
        System.out.println(this.getClass().getSimpleName() + ".update(" + sam + ")");
        return sam.spec();
    }

    @Override
    public Cpu create(Cpu newCpu) {
        System.out.println("create " + newCpu);
        cpus.add(newCpu);
        return newCpu;
    }

    @Override
    public Cpu update(Cpu newCpu) {
        System.out.println("update " + newCpu);
        // nothing to do, should be the same instance.
        return newCpu;
    }

    @Override
    public Gpu create(Gpu newGpu) {
        System.out.println("create " + newGpu);
        return newGpu;
    }

    @Override
    public Gpu update(Gpu newGpu) {
        System.out.println("update " + newGpu);
        // nothing to do, should be the same instance.
        return newGpu;
    }

    /**
     * Creates a new ProductModel and Persists it.
     * <p>
     * How this works: If series is null, family is also as null asumed. - so a default series and a default family is selected. If family is null, a default
     * one is selecte at the series. In both cases, if no default exists, createOrUpdate on. Now createOrUpdate a ProductModel with the family.
     *
     * @param brand     may not be null
     * @param group     may not be null
     * @param series    if null, default is used
     * @param family    if null, default is used
     * @param modelName the name of the model
     * @return
     */
    @Override
    public ProductModel create(final TradeName brand, final ProductGroup group, ProductSeries series, ProductFamily family, final String modelName) {
        if ( series == null ) { // implies, that family is also null
            for (ProductSeries s : serieses) {
                if ( s.getBrand().equals(brand) && s.getGroup().equals(group) && s.getName().equals(SpecConstants.DEFAULT_NAME) ) {
                    series = s;
                }
            }
            if ( series == null ) {
                series = soc.newProductSeries(brand, group, SpecConstants.DEFAULT_NAME);
                serieses.add(series);
            }
        }
        if ( family == null ) {
            for (ProductFamily f : series.getFamilys()) {
                if ( f.getName().equals(SpecConstants.DEFAULT_NAME) ) {
                    family = f;
                }
            }
            if ( family == null ) {
                family = soc.newProductFamily();
                family.setName(SpecConstants.DEFAULT_NAME);
                series.addFamily(family);
            }
        }
        // TODO: Check if name exists, just to be sure
        ProductModel model = soc.newProductModel();
        model.setName(modelName);
        model.setFamily(family);
        return model;
    }

    @Override
    public ProductFamily create(TradeName brand, ProductGroup group, ProductSeries series, String familyName) {
        if ( series == null ) {
            for (ProductSeries s : serieses) {
                if ( s.getBrand().equals(brand) && s.getGroup().equals(group) && s.getName().equals(SpecConstants.DEFAULT_NAME) ) {
                    series = s;
                }
            }
            if ( series == null ) {
                series = soc.newProductSeries(brand, group, SpecConstants.DEFAULT_NAME);
                serieses.add(series);
            }
        }
        // TODO: Check if name exists, just to be sure
        ProductFamily family = soc.newProductFamily();
        family.setName(familyName);
        family.setSeries(series);
        return family;
    }

    @Override
    public ProductSeries create(TradeName brand, ProductGroup group, String seriesName) {
        ProductSeries series = soc.newProductSeries(brand, group, SpecConstants.DEFAULT_NAME);
        serieses.add(series);
        return series;
    }
}
