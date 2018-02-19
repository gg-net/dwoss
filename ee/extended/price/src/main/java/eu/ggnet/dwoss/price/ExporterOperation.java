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
package eu.ggnet.dwoss.price;

import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.TempCalcDocument;
import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.SRowFormater;
import eu.ggnet.lucidcalc.SActionAdapter;
import eu.ggnet.lucidcalc.SFormulaAction;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.LucidCalcReader;
import eu.ggnet.lucidcalc.SFormula;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.CCellReference;
import eu.ggnet.lucidcalc.LucidCalc;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.CCellReferenceAdapter;
import eu.ggnet.lucidcalc.SUtil;

import java.awt.Color;
import java.io.File;
import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;

import eu.ggnet.dwoss.price.engine.PriceEngine;
import eu.ggnet.dwoss.price.engine.PriceEngineResult;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.eao.ProductSpecEao;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ee.format.ProductFormater;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.saft.api.Reply;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static eu.ggnet.lucidcalc.CFormat.Representation.*;
import static eu.ggnet.lucidcalc.SUtil.SR;
import static eu.ggnet.dwoss.price.engine.PriceEngineResult.*;
import static java.awt.Color.*;

/**
 * Export Operation for the Price Engine.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class ExporterOperation implements Exporter {

    private final Logger L = LoggerFactory.getLogger(PriceCoreOperation.class);

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Specs
    private EntityManager specEm;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    private PriceCoreOperation priceCore;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private PriceEngine priceEngine;
    
    /**
     * Export PriceManagement as Xls.
     * <p/>
     * @return PriceManagement as Xls.
     */
    @Override
    public FileJacket toXls() {
        SubMonitor m = monitorFactory.newSubMonitor("Exporting the PriceManagement II", 100);
        List<PriceEngineResult> pers = priceCore.loadAndCalculate(m.newChild(80));
        
        Collections.sort(pers);
        m.setWorkRemaining(pers.size() + 1);
        m.worked(1);

        CFormat euro = new CFormat(RIGHT, CURRENCY_EURO);
        CFormat date = new CFormat(CENTER, SHORT_DATE);
        CFormat percent = new CFormat(ITALIC, BLUE, null, null, null, PERCENT_FLOAT);
        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, BLACK, YELLOW, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("UnitId", 8).setAction(SUtil.getBeanProperty(PROP_REFURBISHED_ID)));
        table.add(new STableColumn("Gruppe", 14).setAction(SUtil.getBeanProperty(PROP_COMMODITY_GROUP)));
        table.add(new STableColumn("Artikelnummer", 15).setAction(SUtil.getBeanProperty(PROP_MANUFACTURER_PART_NO)));
        table.add(new STableColumn("Name", 30).setAction(SUtil.getBeanProperty(PROP_PRODUCT_NAME)));
        table.add(new STableColumn("Hek", 11, euro).setAction(SUtil.getBeanProperty(PROP_RETAILER_PRICE)));
        table.add(new STableColumn("%Cost", 10, percent).setAction(new SFormulaAction(SR(4), "/", SR(17))));
        table.add(new STableColumn("%Reference", 10, percent).setAction(new SFormulaAction(SR(4), "/", SR(18))));
        table.add(new STableColumn("EvP netto", 11, euro).setAction(new SActionAdapter<PriceEngineResult>() {
            @Override
            public Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, PriceEngineResult lineModel) {
                CCellReference x = new CCellReferenceAdapter(absoluteRowIndex, 4);
                // TODO: Change this, in the case of a fixed price. Here should be no formula if we have fixed prices.
                double p = 1 + lineModel.getRetailerToCustomerPricePercentage();
                double t = 1 + lineModel.getTax();
                return new SFormula("RUNDEN", "((", "RUNDEN", "(", x, "*", p, "*", t, ",", 0, ")", ")/", t, ",", 2, ")");
            }
        }));
        table.add(new STableColumn("EvP brutto", 11, euro).setAction(new SActionAdapter<PriceEngineResult>() {
            @Override
            public Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, PriceEngineResult lineModel) {
                return new SFormula(new CCellReferenceAdapter(absoluteRowIndex, 7), "*", 1 + lineModel.getTax());
            }
        }));
        table.add(new STableColumn("UnitFix", 4, new CFormat(CENTER)).setAction(new SActionAdapter<PriceEngineResult>() {
            @Override
            public Object getValue(int relativeColumnIndex, int relativeRowIndex, int absoluteColumnIndex, int absoluteRowIndex, PriceEngineResult lineModel) {
                if ( lineModel.getUnitPriceFixed() == PriceEngineResult.Change.SET ) return 1;
                else if ( lineModel.getUnitPriceFixed() == PriceEngineResult.Change.UNSET ) return -1;
                return 0;
            }
        }));
        table.add(new STableColumn("PartFix", 4, new CFormat(CENTER)).setAction(SUtil.getConstant(0)));
        table.add(new STableColumn("Gar.-Id", 4, new CFormat(CENTER)).setAction(SUtil.getBeanProperty(PROP_WARRANTY_ID)));
        table.add(new STableColumn("Mfg-Date", 12, date).setAction(SUtil.getBeanProperty(PROP_MFG_DATE)));
        table.add(new STableColumn("Input-Date", 12, date).setAction(SUtil.getBeanProperty(PROP_INPUT_DATE)));
        table.add(new STableColumn("Eol-Date", 12, date).setAction(SUtil.getBeanProperty(PROP_EOL)));
        table.add(new STableColumn("First-Priced", 12, date).setAction(SUtil.getBeanProperty(PROP_DATE_FIRST_PRICED)));
        table.add(new STableColumn("Zustand", 16).setAction(SUtil.getBeanProperty(PROP_CONDITION_LEVEL)));
        table.add(new STableColumn("Manufacturer CP", 11, euro).setAction(SUtil.getBeanProperty(PROP_COST_PRICE)));
        table.add(new STableColumn("Contractor Reference CP", 11, euro).setAction(SUtil.getBeanProperty(PROP_CONTRACTOR_REFERENCE_PRICE)));
        table.add(new STableColumn("Ref.-Price", 11, euro).setAction(SUtil.getBeanProperty(PROP_REFERENCE_PRICE)));
        table.add(new STableColumn("Rules", 50).setAction(SUtil.getBeanProperty(PROP_RULES_LOG)));
        table.add(new STableColumn("Beschreibung", 50).setAction(SUtil.getBeanProperty(PROP_PRODUCT_DESCRIPTION)));
        table.add(new STableColumn("Bemerkung", 50).setAction(SUtil.getBeanProperty(PROP_COMMENT)));
        table.add(new STableColumn("Interne Bemerkungen", 50).setAction(SUtil.getBeanProperty(PROP_INTERNAL_COMMENT)));
        table.add(new STableColumn("Verkaufskanal", 18).setAction(SUtil.getBeanProperty(PROP_SALES_CHANNEL)));
        table.add(new STableColumn("Special", 12).setAction(SUtil.getBeanProperty(PROP_SPECIAL)));
        table.add(new STableColumn("Last Retailer", 12, euro).setAction(SUtil.getBeanProperty(PROP_LAST_RETAILER_PRICE)));
        table.add(new STableColumn("Last Customer", 12, euro).setAction(SUtil.getBeanProperty(PROP_LAST_CUSTOMER_PRICE)));
        table.add(new STableColumn("Warrenty Valid", 12, date).setAction(SUtil.getBeanProperty(PROP_WARRENTYVALID)));
        table.add(new STableColumn("Lager", 12).setAction(SUtil.getBeanProperty(PROP_STOCK)));
        table.setRowFormater((SRowFormater<PriceEngineResult>)(i, p) -> {
            if ( p.isError() ) return new CFormat(RED, null);
            else if ( p.isWarning() ) return new CFormat(new Color(0x80, 0x80, 0), null);
            else if ( p.getManufacturerPartPriceFixed() == PriceEngineResult.Change.SET ) return new CFormat(CYAN, null);
            return null;
        });
        table.setModel(new STableModelList<>(pers));

        CCalcDocument cdoc = new TempCalcDocument("PriceManagement_");
        cdoc.add(new CSheet("PriceManagement", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        FileJacket result = new FileJacket("PriceManagement", ".xls", file);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
        }
        m.finish();
        return result;
    }

    /**
     * Creates a price compare sheet, expects an xls file with the first column filed with partNos.
     *
     * @param inFile the infile
     * @return the price compare xls outfile.
     */
    @Override
    public FileJacket toXlsByXls(FileJacket inFile) {
        File f = inFile.toTemporaryFile();
        LucidCalcReader reader = new JExcelLucidCalcReader();
        List<String> partNos = new ArrayList<>();
        List<List<? extends Object>> readXls = reader.read(f);
        for (List<? extends Object> list : readXls) {
            if ( list == null || list.isEmpty() ) continue;
            partNos.add(list.get(0).toString());
        }
        return toXls(partNos.toArray(new String[0]));
    }

    /**
     * Creates a price compare sheet, expects a list of partNos and returns a xls File with last sales and estimated generated price
     *
     * @param partNos the partNos to inspect
     * @return the xls file with informations
     */
    private FileJacket toXls(String... partNos) {
        // Create a Produkt with the part no;
        SubMonitor m = monitorFactory.newSubMonitor("Auswertung über PartNos", partNos.length + 10);
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        ProductSpecEao productSpecEao = new ProductSpecEao(specEm);
        StockUnitEao suEao = new StockUnitEao(stockEm);
        DocumentEao documentEao = new DocumentEao(redTapeEm);
        List<List<Object>> model = new ArrayList<>();
        for (String partNo : partNos) {
            m.worked(1, "loading: " + partNo);
            List<Object> line = new ArrayList<>();
            model.add(line);
            partNo = partNo.trim();
            line.add(partNo);
            List<UniqueUnit> uus = uniqueUnitEao.findByProductPartNo(partNo);

            if ( uus.isEmpty() ) {
                line.add("Keine Geräte oder Produkte im System.");
                for (int i = 0; i < 14; i++) line.add(null);
                continue;
            }
            Product product = uus.get(0).getProduct();
            line.add(ProductFormater.toName(product));
            line.add(uus.size());
            line.add(maxPrice(uus, PriceType.CUSTOMER));
            line.add(minPrice(uus, PriceType.RETAILER));

            List<Document> documents = documentEao.findInvoiceWithProdcutId(product.getId());

            for (int i = 0; i < 3; i++) {
                if ( documents.size() > i ) {
                    line.add(documents.get(i).getActual()); // TODO: Was balancingId
                    line.add(priceByProductId(documents.get(i), product.getId()));
                } else {
                    line.add(null);
                    line.add(null);
                }
            }
            PriceEngineResult per = priceEngine.estimate(
                    uus.get(0),
                    productSpecEao.findByProductId(product.getId()),
                    suEao.findByUniqueUnitId(uus.get(0).getId()).getStock().getName());
            line.add(per.getCostPrice());
            line.add(per.getRetailerPrice());
            line.add(per.getCustomerPrice());
            line.add(per.getRulesLog());
        }

        m.message("creating File");
        STable table = new STable();
        CFormat euro = new CFormat(RIGHT, CURRENCY_EURO);
        CFormat date = new CFormat(CENTER, SHORT_DATE);
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn("PartNo", 15)).add(new STableColumn("Name", 30));
        table.add(new STableColumn("Menge im System", 12));
        table.add(new STableColumn("VP(Min)", 12, euro)).add(new STableColumn("VP(Max)", 12, euro));
        table.add(new STableColumn("Datum", 12, date)).add(new STableColumn("Vk", 12, euro));
        table.add(new STableColumn("Datum", 12, date)).add(new STableColumn("Vk", 12, euro));
        table.add(new STableColumn("Datum", 12, date)).add(new STableColumn("Vk", 12, euro));
        table.add(new STableColumn("Cp", 12, euro)).add(new STableColumn("Hp", 12, euro)).add(new STableColumn("Ep", 12, euro));
        table.add(new STableColumn("Rules", 40));
        table.setModel(new STableModelList(model));

        CCalcDocument cdoc = new TempCalcDocument("PartNoPrice_");
        cdoc.add(new CSheet("PartNoPrice", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        FileJacket result = new FileJacket("PartNoPrice", ".xls", file);
        m.finish();

        return result;
    }

    /**
     * Loads exactly one Unit as PriceEngineResult.
     *
     * @param refurbishId the unitid
     * @return The PriceEngineResult or Null if Id not found
     * @throws UserInfoException if the unitId is not a Number or to not get found
     */
    @Override
    public Reply<PriceEngineResult> load(String refurbishId) throws UserInfoException {
        if(!StringUtils.isNumeric(refurbishId)){
            throw new UserInfoException("refurbishId", "refurbishId is not a number");
        }
        UniqueUnit uu = new UniqueUnitEao(uuEm).findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);
        if(uu == null){
            throw new UserInfoException("UniqueUnit", "UniqueUnit not forund");
        }
        PriceEngineResult per = new PriceEngineResult(uu);
        per.setRetailerPrice(uu.getPrice(PriceType.RETAILER));
        per.setCustomerPrice(uu.getPrice(PriceType.CUSTOMER));
        
        return Reply.success(per);
    }

    /**
     * Calculates a Price for on Unit.
     *
     * @param refurbishId the refurbishId
     * @return The PriceEngineResult or Null if Id not found
     */
    @Override
    public PriceEngineResult onePrice(String refurbishId) {
        final UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        final ProductSpecEao productSpecEao = new ProductSpecEao(specEm);
        final StockUnitEao suEao = new StockUnitEao(stockEm);
        L.info("Loading s.getUnit({})", refurbishId);
        UniqueUnit uu = uniqueUnitEao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbishId);
        if ( uu == null ) return null;
        ProductSpec spec = productSpecEao.findByProductId(uu.getProduct().getId());
        String stock = suEao.findByUniqueUnitId(uu.getId()).getStock().getName();
        return priceEngine.estimate(uu, spec, stock);
    }

    private Double minPrice(List<UniqueUnit> units, PriceType priceType) {
        return maxMinPrice(units, priceType, false);
    }

    private Double maxPrice(List<UniqueUnit> units, PriceType priceType) {
        return maxMinPrice(units, priceType, true);
    }

    private Double maxMinPrice(List<UniqueUnit> units, PriceType priceType, boolean max) {
        SortedSet<Double> prices = new TreeSet<>();
        for (UniqueUnit uniqueUnit : units) {
            prices.add(uniqueUnit.getPrice(priceType));
        }
        prices.remove(0.0);
        if ( prices.isEmpty() ) return null;
        if ( max ) return prices.first();
        return prices.last();
    }

    private Double priceByProductId(Document doc, long productId) {
        for (Position position : doc.getPositions().values()) {
            if ( position.getUniqueUnitProductId() == productId ) return position.getPrice();
        }
        return null;
    }
}
