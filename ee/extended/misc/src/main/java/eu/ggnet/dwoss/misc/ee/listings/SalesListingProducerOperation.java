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
package eu.ggnet.dwoss.misc.ee.listings;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRSaver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.system.ImageFinder;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.core.system.util.TwoDigits;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.customer.api.ResellerListService;
import eu.ggnet.dwoss.mandator.api.service.*;
import eu.ggnet.dwoss.mandator.api.service.UploadConfiguration.UploadCommand;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.misc.api.SalesListingService;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;
import eu.ggnet.lucidcalc.CFormat.Representation;
import eu.ggnet.lucidcalc.*;

import static eu.ggnet.dwoss.mandator.api.service.UploadConfiguration.Type.SCP;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.LEFT;
import static eu.ggnet.lucidcalc.CFormat.VerticalAlignment.MIDDLE;
import static eu.ggnet.lucidcalc.CFormat.VerticalAlignment.TOP;

/**
 * Operation for all Saleslistings.
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class SalesListingProducerOperation implements SalesListingProducer, SalesListingService {

    public static class UniqueUnitComparator implements Comparator<UniqueUnit> {

        @Override
        public int compare(UniqueUnit o1, UniqueUnit o2) {
            if ( o1 == null && o2 == null ) return 0;
            if ( o1 == null ) return 1;
            if ( o2 == null ) return -1;
            Product p1 = o1.getProduct();
            Product p2 = o2.getProduct();
            // Safetynet till product @NotNull
            if ( p1 == null || p2 == null ) return o1.getRefurbishId().compareTo(o2.getRefurbishId());
            if ( p1.getGroup() != p2.getGroup() ) return p1.getGroup().compareTo(p2.getGroup());
            if ( p1.getTradeName() != p2.getTradeName() ) return p1.getTradeName().compareTo(p2.getTradeName());
            if ( !p1.getName().equals(p2.getName()) ) return p1.getName().compareTo(p2.getName());
            if ( !p1.getPartNo().equals(p2.getPartNo()) ) return p1.getPartNo().compareTo(p2.getPartNo());
            return o1.getRefurbishId().compareTo(o2.getRefurbishId());
        }
    };

    private final static Logger L = LoggerFactory.getLogger(SalesListingProducerOperation.class);

    private final static CFormat LFT = new CFormat(LEFT);

    private final static CFormat EURO = new CFormat(CFormat.Representation.CURRENCY_EURO);

    @Inject
    private ImageFinder imageFinder;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    private Mandator mandator;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private Instance<ListingConfigurationService> listingService;

    @EJB // Is here ok, otherwise we would need a local service. And bevor doing that, we would cleanup misc.
    private ResellerListService rls;

    @Override
    public FileJacket generateAllSalesListing() {
        SubMonitor m = monitorFactory.newSubMonitor("All List", 5);
        m.message("loading Units");
        m.start();

        List<StockUnit> stockUnits = new StockUnitEao(stockEm).findByNoLogicTransaction();
        List<UniqueUnit> uniqueUnits = new UniqueUnitEao(uuEm).findByIds(toUniqueUnitIds(stockUnits));
        m.worked(3, "preparing Units");

        List<Object[]> retailers = new ArrayList<>(stockUnits.size());
        List<Object[]> customers = new ArrayList<>(stockUnits.size());
        for (Map.Entry<UniqueUnit, StockUnit> entry : toSortedMap(uniqueUnits, stockUnits, new UniqueUnitComparator()).entrySet()) {
            UniqueUnit uu = entry.getKey();
            StockUnit su = entry.getValue();
            Product p = uu.getProduct();
            Date firstPriced = null;
            for (PriceHistory priceHistory : uu.getPriceHistory()) {
                if ( firstPriced == null || firstPriced.after(priceHistory.getDate()) ) firstPriced = priceHistory.getDate();
            }
            String source = "Automatisch";
            if ( p != null && p.getFlags().contains(Product.Flag.PRICE_FIXED) ) source = "Manuell (Artikel)";
            else if ( uu.getFlags().contains(UniqueUnit.Flag.PRICE_FIXED) ) source = "Manuell (Gerät)";
            Object[] row = {
                uu.getRefurbishId(),
                (p == null ? null : p.getPartNo()),
                (p == null ? null : p.getGroup().getNote()),
                (p == null ? null : p.getTradeName().getName()),
                (p == null ? null : p.getName()),
                (p == null ? null : p.getDescription()),
                uu.getWarranty().getName(),
                uu.getWarrentyValid(),
                UniqueUnitFormater.toSingleLineAccessories(uu),
                uu.getCondition().getNote(),
                UniqueUnitFormater.toSingleLineComment(uu),
                uu.getPrice(PriceType.RETAILER),
                uu.getPrice(PriceType.CUSTOMER),
                (!uu.hasPrice(PriceType.CUSTOMER) ? null : TwoDigits.roundedApply(uu.getPrice(PriceType.CUSTOMER), GlobalConfig.DEFAULT_TAX.tax(), 0)),
                (su.getStock() == null ? su.getTransaction() : su.getStock().getName()),
                uu.getMfgDate(),
                uu.getInputDate(),
                firstPriced,
                source
            };
            if ( uu.getSalesChannel() == SalesChannel.CUSTOMER && uu.hasPrice(PriceType.CUSTOMER) ) customers.add(row);
            else if ( uu.getSalesChannel() == SalesChannel.RETAILER && (uu.hasPrice(PriceType.CUSTOMER) || uu.hasPrice(PriceType.RETAILER)) )
                retailers.add(row);
        }
        m.worked(1, "creating File, Endkundengeräte: " + customers.size() + ", Händlergeräte: " + retailers.size());

        STable consumerTable = new STable();
        consumerTable.setTableFormat(new CFormat(CENTER, TOP, new CBorder(Color.GRAY, CBorder.LineStyle.THIN), true));
        consumerTable.setHeadlineFormat(new CFormat(CFormat.FontStyle.BOLD, Color.BLACK, Color.LIGHT_GRAY, CENTER, MIDDLE));
        consumerTable.setRowHeight(1000);
        consumerTable.add(new STableColumn("SopoNr", 12));
        consumerTable.add(new STableColumn("ArtikelNr", 15));
        consumerTable.add(new STableColumn("Warengruppe", 18));
        consumerTable.add(new STableColumn("Hersteller", 15));
        consumerTable.add(new STableColumn("Bezeichnung", 30));
        consumerTable.add(new STableColumn("Beschreibung", 60, LFT));
        consumerTable.add(new STableColumn("Garantie", 18, LFT));
        consumerTable.add(new STableColumn("Garantie bis", 18, new CFormat(Representation.SHORT_DATE)));
        consumerTable.add(new STableColumn("Zubehör", 30, LFT));
        consumerTable.add(new STableColumn("optische Bewertung", 25));
        consumerTable.add(new STableColumn("Bemerkung", 50, LFT));
        consumerTable.add(new STableColumn("Händler", 15, EURO));
        consumerTable.add(new STableColumn("Endkunde", 15, EURO));
        consumerTable.add(new STableColumn("E.inc.Mwst", 15, EURO));
        consumerTable.add(new STableColumn("Lager", 18));
        consumerTable.add(new STableColumn("Mfg Datum", 18, new CFormat(Representation.SHORT_DATE)));
        consumerTable.add(new STableColumn("Aufnahme Datum", 18, new CFormat(Representation.SHORT_DATE)));
        consumerTable.add(new STableColumn("Erstmalig Bepreist", 18, new CFormat(Representation.SHORT_DATE)));
        consumerTable.add(new STableColumn("Preis Quelle", 18));

        consumerTable.setModel(new STableModelList(customers));

        STable retailerTable = new STable(consumerTable);
        retailerTable.setModel(new STableModelList(retailers));

        CCalcDocument cdoc = new TempCalcDocument();
        cdoc.add(new CSheet("Endkunde", consumerTable));
        cdoc.add(new CSheet("Händler", retailerTable));
        FileJacket fj = new FileJacket("All", ".xls", LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc));
        m.finish();
        return fj;
    }

    /**
     * Returns the next Image Id.
     * <p/>
     * @return the next Image Id.
     */
    @Override
    public int nextImageId() {
        return imageFinder.nextImageId();
    }

    private List<Integer> toUniqueUnitIds(List<StockUnit> stockUnits) {
        List<Integer> uuids = new ArrayList<>(stockUnits.size());
        for (StockUnit stockUnit : stockUnits) {
            uuids.add(stockUnit.getUniqueUnitId());
        }
        return uuids;
    }

    private SortedMap<UniqueUnit, StockUnit> toSortedMap(List<UniqueUnit> uniqueUnits, List<StockUnit> stockUnits, Comparator<UniqueUnit> comparator) {
        Map<Integer, UniqueUnit> uuIdMs = new HashMap<>(uniqueUnits.size());
        for (UniqueUnit uniqueUnit : uniqueUnits) {
            uuIdMs.put(uniqueUnit.getId(), uniqueUnit);
        }
        SortedMap<UniqueUnit, StockUnit> uusu = new TreeMap<>(comparator);
        for (StockUnit stockUnit : stockUnits) {
            uusu.put(uuIdMs.get(stockUnit.getUniqueUnitId()), stockUnit);
        }
        return uusu;
    }

    /**
     *
     * @param name the fileName in the jar, but without jrxml
     * @return
     */
    private String compileReportToTempFile(final String name) {
        // Optimize, only do it on updates.
        String reportFile = Utils.getTempDirectory("jasper") + "/" + name + ".jasper";
        URL url = Objects.requireNonNull(getClass().getResource(name + ".jrxml"), "The Resource " + getClass().getPackage() + "/" + name + ".jrxml not found.");
        try (InputStream is = url.openStream()) {
            JRSaver.saveObject(JasperCompileManager.compileReport(is), reportFile);
            return reportFile;
        } catch (IOException | JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<FileJacket> generateListings(ListingActionConfiguration config) throws UserInfoException {

        Map<TradeName, Collection<FileJacket>> result = new HashMap<>();

        switch (config.type) {
            case XLS:
                result = generateXlsListings(config.channel);
                break;
            case PDF:
                result = generatePdfListings(config.channel);
                break;
        }

        List<FileJacket> jackets = result.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

        switch (config.location) {
            case LOCAL:
                return jackets;
            case REMOTE:
                prepareAndUpload(result);
                break;
        }
        return Collections.emptyList();
    }

    @Override
    public List<FileJacket> generatePdfs(SalesChannel channel) {
        return generatePdfListings(channel).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public List<FileJacket> generateXlses(SalesChannel channel) {
        return generateXlsListings(channel).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    /**
     * Generates XLS files for units in a specific sales channel.
     * The lists are seperated by brand.
     * <p>
     * @param channel the saleschannel
     * @return XLS files for units in a specific sales channel.
     */
    private Map<TradeName, Collection<FileJacket>> generateXlsListings(SalesChannel channel) {
        SubMonitor m = monitorFactory.newSubMonitor("Listen für " + channel.getName() + " erstellen", 100);
        m.start();
        List<StockUnit> stockUnits = new StockUnitEao(stockEm).findByNoLogicTransactionAndPresentStock();
        List<UniqueUnit> uniqueUnits = new UniqueUnitEao(uuEm).findByIds(toUniqueUnitIds(stockUnits));

        Map<TradeName, List<UniqueUnit>> units = uniqueUnits.stream().collect(Collectors.groupingBy(uu -> uu.getProduct().getTradeName()));

        m.worked(2, "prüfe und filtere Geräte");
        Map<TradeName, Collection<FileJacket>> files = new HashMap<>();

        for (TradeName k : units.keySet()) {
            List<UniqueUnit> uus = units.get(k);
            Collections.sort(uus, new UniqueUnitComparator());
            List<Object[]> rows = new ArrayList<>();
            for (UniqueUnit get : uus) {
                UniqueUnit uu = get;
                Product p = uu.getProduct();

                // Cases to filter out.
                if ( uu.getSalesChannel() != channel ) continue;
                if ( !uu.hasPrice((channel == SalesChannel.CUSTOMER ? PriceType.CUSTOMER : PriceType.RETAILER)) ) continue;

                Object[] row = {
                    uu.getRefurbishId(),
                    p.getPartNo(),
                    p.getGroup().getNote(),
                    p.getTradeName().getName(),
                    p.getName(),
                    p.getDescription(),
                    uu.getWarranty().getName(),
                    uu.getWarrentyValid(),
                    UniqueUnitFormater.toSingleLineAccessories(uu),
                    uu.getCondition().getNote(),
                    UniqueUnitFormater.toSingleLineComment(uu),
                    uu.getPrice(PriceType.RETAILER),
                    uu.getPrice(PriceType.CUSTOMER),
                    (!uu.hasPrice(PriceType.CUSTOMER) ? null : TwoDigits.roundedApply(uu.getPrice(PriceType.CUSTOMER), GlobalConfig.DEFAULT_TAX.tax(), 0)),};
                rows.add(row);
            }
            if ( rows.isEmpty() ) continue;

            m.worked(5, "creating File, Geräte: " + rows.size());

            STable unitTable = new STable();
            unitTable.setTableFormat(new CFormat(CENTER, TOP, new CBorder(Color.GRAY, CBorder.LineStyle.THIN), true));
            unitTable.setHeadlineFormat(new CFormat(CFormat.FontStyle.BOLD, Color.BLACK, Color.LIGHT_GRAY, CENTER, MIDDLE));
            unitTable.setRowHeight(1000);

            unitTable.add(new STableColumn("SopoNr", 12));
            unitTable.add(new STableColumn("ArtikelNr", 15));
            unitTable.add(new STableColumn("Warengruppe", 18));
            unitTable.add(new STableColumn("Hersteller", 15));
            unitTable.add(new STableColumn("Bezeichnung", 30));
            unitTable.add(new STableColumn("Beschreibung", 60, LFT));
            unitTable.add(new STableColumn("Garantie", 18, LFT));
            unitTable.add(new STableColumn("Garantie bis", 18, new CFormat(Representation.SHORT_DATE)));
            unitTable.add(new STableColumn("Zubehör", 30, LFT));
            unitTable.add(new STableColumn("optische Bewertung", 25));
            unitTable.add(new STableColumn("Bemerkung", 50, LFT));
            unitTable.add(new STableColumn("Händler", 15, EURO));
            unitTable.add(new STableColumn("Endkunde", 15, EURO));
            unitTable.add(new STableColumn("E.inc.Mwst", 15, EURO));
            unitTable.setModel(new STableModelList(rows));

            CCalcDocument cdoc = new TempCalcDocument();
            cdoc.add(new CSheet("Sonderposten", unitTable));
            files.put(k, Arrays.asList(new FileJacket(k.getName() + " Liste", ".xls", LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc))));
        }
        m.finish();
        return files;
    }

    /**
     * Generates PDF files for units in a specific sales channel.
     * The lists are seperated by brand.
     * <p>
     * @param channel the saleschannel
     * @return PDF files for units in a specific sales channel.
     */
    private Map<TradeName, Collection<FileJacket>> generatePdfListings(SalesChannel channel) {
        SubMonitor m = monitorFactory.newSubMonitor("Endkundenlisten erstellen", 10);
        m.message("lade Gerätedaten");
        m.start();
        List<StockUnit> stockUnits = new StockUnitEao(stockEm).findByNoLogicTransaction();
        List<UniqueUnit> uniqueUnits = new UniqueUnitEao(uuEm).findByIds(toUniqueUnitIds(stockUnits));

        PriceType priceType = (channel == SalesChannel.CUSTOMER ? PriceType.CUSTOMER : PriceType.RETAILER);

        m.worked(2, "prüfe und filtere Geräte");
        SortedMap<UniqueUnit, StockUnit> uusus = toSortedMap(uniqueUnits, stockUnits, new UniqueUnitComparator());
        for (Iterator<Map.Entry<UniqueUnit, StockUnit>> it = uusus.entrySet().iterator(); it.hasNext();) {
            Map.Entry<UniqueUnit, StockUnit> entry = it.next();
            UniqueUnit uu = entry.getKey();
            StockUnit su = entry.getValue();
            if ( uu == null ) throw new NullPointerException(su + " has no UniqueUnit, Database Error");
            if ( uu.getSalesChannel() != channel || !uu.hasPrice(priceType) || su.isInTransaction() ) {
                it.remove();
            }
        }
        L.info("Selected {} Units for the Lists", uusus.size());

        m.worked(1, "sortiere und bereite Geräte vor");
        Map<Product, Set<UniqueUnit>> stackedUnits = new HashMap<>();
        for (Map.Entry<UniqueUnit, StockUnit> entry : uusus.entrySet()) {
            Product p = entry.getKey().getProduct();
            if ( !stackedUnits.containsKey(p) ) stackedUnits.put(p, new HashSet<>());
            stackedUnits.get(p).add(entry.getKey());
        }

        List<StackedLine> stackedLines = new ArrayList<>(stackedUnits.size());
        DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance(Locale.GERMAN);
        df.applyPattern("#,###,##0.00");
        for (Map.Entry<Product, Set<UniqueUnit>> entry : stackedUnits.entrySet()) {
            Product p = entry.getKey();
            StackedLine line = new StackedLine();
            line.setBrand(p.getTradeName());
            line.setGroup(p.getGroup());
            line.setCommodityGroupName(p.getGroup().getNote());
            line.setDescription(p.getDescription());
            line.setManufacturerName(p.getTradeName().getName());
            line.setManufacturerPartNo(p.getPartNo());
            line.setName(p.getName());
            line.setImageUrl(imageFinder.findImageUrl(p.getImageId()));
            boolean priceChanged = false;
            double customerPrice = 0;
            for (UniqueUnit uu : entry.getValue()) {
                StackedLineUnit elem = new StackedLineUnit();
                elem.setAccessories(UniqueUnitFormater.toSingleLineAccessories(uu));
                elem.setComment(UniqueUnitFormater.toSingleLineComment(uu));
                elem.setConditionLevelDescription(uu.getCondition().getNote());
                elem.setMfgDate(uu.getMfgDate());
                elem.setRefurbishedId(uu.getRefurbishId());
                elem.setSerial(uu.getSerial());
                elem.setWarranty(uu.getWarranty().getName());
                if ( uu.getWarranty().equals(Warranty.WARRANTY_TILL_DATE) )
                    elem.setWarrentyTill(uu.getWarrentyValid());

                double uuPrice = uu.getPrice(priceType);
                elem.setCustomerPrice(uuPrice);
                elem.setRoundedTaxedCustomerPrice(TwoDigits.roundedApply(uuPrice, GlobalConfig.DEFAULT_TAX.tax(), 0.02));

                // For the "ab € XXX" handler
                if ( customerPrice == 0 ) {
                    customerPrice = uuPrice;
                } else if ( customerPrice > uuPrice ) {
                    customerPrice = uuPrice;
                    priceChanged = true;
                } else if ( customerPrice < uuPrice ) {
                    priceChanged = true;
                }
                elem.normaize();
                line.add(elem);
            }
            line.setAmount(line.getUnits().size());
            line.setCustomerPriceLabel((priceChanged ? "ab €" : "€") + df.format(TwoDigits.roundedApply(customerPrice, GlobalConfig.DEFAULT_TAX.tax(), 0.02)));
            line.normaize();
            stackedLines.add(line);
        }
        L.info("Created {} Lines for the Lists", stackedLines.size());

        m.worked(1, "erzeuge listen");

        Set<ListingConfiguration> configs = new HashSet<>();
        if ( listingService.isAmbiguous() || listingService.isUnsatisfied() ) {
            for (TradeName brand : TradeName.values()) {
                for (ProductGroup value : ProductGroup.values()) {
                    configs.add(new ListingConfiguration.Builder()
                            .filePrefix("Geräteliste ")
                            .name(brand.getDescription() + " " + value.description)
                            .logoLeft(mandator.company().logo().toURL())
                            .brand(brand)
                            .addAllGroups(EnumSet.of(value))
                            .addAllSupplementBrands(EnumSet.noneOf(TradeName.class))
                            .headLeft("Beispieltext Links\nZeile 2")
                            .headCenter("Beispieltext Mitte\nZeile 2")
                            .headRight("Beispieltext Rechts\nZeile 2")
                            .footer("Fusszeilentext")
                            .build());
                }
            }
        } else {
            configs.addAll(listingService.get().listingConfigurations());
        }

        m.setWorkRemaining(configs.size() + 1);

        Map<TradeName, Collection<FileJacket>> jackets = new HashMap<>();
        for (ListingConfiguration config : configs) {
            m.worked(1, "erstelle Liste " + config.name());
            ListingConfiguration.Builder finalConfigBuilder = new ListingConfiguration.Builder().mergeFrom(config);

            if ( !config.jasperTemplateFile().isPresent() )
                finalConfigBuilder.jasperTemplateFile(compileReportToTempFile("CustomerSalesListing"));

            if ( !config.jasperTempleteUnitsFile().isPresent() )
                finalConfigBuilder.jasperTempleteUnitsFile(compileReportToTempFile("CustomerSalesListingUnits"));

            FileJacket fj = createListing(finalConfigBuilder.build(), stackedLines);
            if ( fj != null ) {
                if ( !jackets.containsKey(config.brand()) ) jackets.put(config.brand(), new HashSet<>());
                jackets.get(config.brand()).add(fj);
            }
        }
        m.finish();
        return jackets;
    }

    /**
     * Create a filejacket from a collection of lines that are filtered by configuration parameters.
     * Lines are filtered by brand and group.
     * <p>
     * @param config configuration for filtering and file creation
     * @param all    lines to be considered
     * @return a filejacket from a collection of lines that are filtered by configuration parameters.
     */
    private FileJacket createListing(ListingConfiguration config, Collection<StackedLine> all) {
        // Todo. Validate optional
        try {
            SortedSet<StackedLine> filtered = all.stream()
                    .filter(line -> (config.getAllBrands().contains(line.getBrand()) && config.groups().contains(line.getGroup())))
                    .collect(Collectors.toCollection(TreeSet::new));
            if ( filtered.isEmpty() ) return null;
            L.info("Creating listing {} with {} lines", config.name(), filtered.size());
            JasperPrint jasperPrint = JasperFillManager.fillReport(config.jasperTemplateFile().orElseThrow(
                    () -> new NullPointerException("JasperTemplateUnitsFile not set in toReportParameters. Unsing " + this)),
                    config.toReportParamters(),
                    new JRBeanCollectionDataSource(filtered));
            byte[] pdfContend = JasperExportManager.exportReportToPdf(jasperPrint);
            return new FileJacket(config.filePrefix() + config.name(), ".pdf", pdfContend);
        } catch (JRException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Prepares and uploads filejackets to a specified location.
     * <p>
     * @param files files to be uploaded
     * @throws UserInfoException
     */
    private void prepareAndUpload(Map<TradeName, Collection<FileJacket>> files) throws UserInfoException {
        UploadConfiguration uploadConfig = listingService.get().listingFtpConfiguration(files);
        L.info("prepareAndUpload() uploadCommands:{}", uploadConfig.getUploadCommands());
        if ( uploadConfig.getConfig().type == SCP ) {
            try {
                new ScpUpload().upload(uploadConfig.getConfig(), monitorFactory.newSubMonitor("Bereite FTP tranfer vor"), uploadConfig.getUploadCommands().toArray(new UploadCommand[0]));
            } catch (IOException | ClassNotFoundException ex) {
                throw new UserInfoException("Fileupload konnte nicht durchgeführt werden.", "Fileupload configuration = " + uploadConfig);
            }
        } else {
            throw new IllegalArgumentException("Type " + uploadConfig.getConfig().type + "  not supported");
        }
    }
}
