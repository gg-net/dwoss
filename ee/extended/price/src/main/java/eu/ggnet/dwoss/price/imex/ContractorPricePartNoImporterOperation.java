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
package eu.ggnet.dwoss.price.imex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.rules.partno.PartNoSupport;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.format.ProductFormater;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.TwoDigits;
import eu.ggnet.lucidcalc.LucidCalcReader;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;
import eu.ggnet.saft.api.Reply;

import lombok.Value;

import static eu.ggnet.dwoss.rules.TradeName.OTTO;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.CONTRACTOR_REFERENCE;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.MANUFACTURER_COST;

/**
 * Importer implementation.
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class ContractorPricePartNoImporterOperation implements ContractorPricePartNoImporter {

    // Must be Public for JExcelReader
    @Value
    public static class ManufacturerImport implements Serializable {

        private final String partNo;

        private final Double costPrice;

        public double getCostPrice() {
            if ( costPrice == null ) return 0;
            return costPrice;
        }

    }

    // Must be Public for JExcelReader
    @Value
    public static class ContractorImport implements Serializable {

        private final String manufacturerPartNo;

        private final String gtin;

        private final String name;

        private final Double referencePrice;

        private final String contractorPartNo;

        public double getReferencePrice() {
            if ( referencePrice == null ) return 0;
            return referencePrice;
        }

        public boolean hasValidGtin() {
            if ( !StringUtils.isNumeric(gtin) ) return false;
            try {
                Long.parseLong(gtin);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public boolean hasValidContractorPartNo(TradeName contractor) {
            if ( contractor == null ) return false;
            if ( contractor.getPartNoSupport() == null ) return !StringUtils.isBlank(contractorPartNo);
            return contractor.getPartNoSupport().isValid(toNormalizeContractorPart(contractor));
        }

        public String violationMessagesOfContractorPartNo(TradeName contractor) {
            if ( contractor == null ) return "No contracor supplied";
            if ( contractor.getPartNoSupport() == null ) return StringUtils.isBlank(contractorPartNo) ? "ContractorPartNo is blank" : null;
            return contractor.getPartNoSupport().violationMessages(toNormalizeContractorPart(contractor));
        }

        // TODO: optimize me
        public String toNormalizeContractorPart(TradeName contractor) {
            if ( contractor != OTTO ) return StringUtils.trim(contractorPartNo);
            if ( contractorPartNo != null && Pattern.matches("[0-9]{6}", contractorPartNo) ) {
                return StringUtils.trim(contractorPartNo.substring(0, 3) + "." + contractorPartNo.substring(3));
            }
            if ( contractorPartNo != null && Pattern.matches("[0-9]{8}", contractorPartNo) ) {
                return StringUtils.trim(contractorPartNo.substring(0, 2) + "." + contractorPartNo.substring(3, 6) + "." + contractorPartNo.substring(6));
            }
            return StringUtils.trim(contractorPartNo);
        }

        public boolean hasManufacturerPartNoOrGtin() {
            return !(StringUtils.isBlank(manufacturerPartNo) && StringUtils.isBlank(gtin));
        }

    }

    private final static Logger L = LoggerFactory.getLogger(ContractorPricePartNoImporterOperation.class);

    @Inject
    @Reports
    private EntityManager reportEm;

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    private MonitorFactory monitorFactory;

    @Override
    public Reply<Void> fromManufacturerXls(TradeName contractorManufacturer, FileJacket inFile, String arranger) {
        if ( !contractorManufacturer.isManufacturer() ) throw new RuntimeException(contractorManufacturer + " is not a Manufacturer");
        final SubMonitor m = monitorFactory.newSubMonitor(contractorManufacturer.getName() + " Costpreise importieren", 100);
        m.start().message("Reading File");
        ProductEao productEao = new ProductEao(uuEm);
        LucidCalcReader reader = new JExcelLucidCalcReader();
        reader.addColumn(0, String.class).addColumn(1, Double.class);
        List<ManufacturerImport> imports = reader.read(inFile.toTemporaryFile(), ManufacturerImport.class);
        List<String> errors = reader.getErrors();
        m.worked(5);
        m.setWorkRemaining(imports.size());
        L.info("Imports: {}", imports);
        PartNoSupport partNoSupport = contractorManufacturer.getPartNoSupport();

        int databaseLines = 0;
        int newPrices = 0;
        int updatedPrices = 0;

        for (ManufacturerImport mi : imports) {
            m.worked(1);
            if ( partNoSupport != null && !partNoSupport.isValid(mi.partNo) ) {
                errors.add(partNoSupport.violationMessages(mi.partNo));
                continue;
            }
            if ( mi.costPrice == null || mi.costPrice <= 0.01 ) {
                errors.add("PartNo " + mi.costPrice + " hat keinen Preis");
                continue;
            }
            m.message("Importing (" + mi.partNo + ")");
            Product p = productEao.findByPartNo(mi.partNo);
            if ( p == null ) {
                errors.add("No UniqueUnit Entity for PartNo " + mi.partNo);
                continue;
            }
            databaseLines++;
            if ( mi.getCostPrice() > 0.01 && !TwoDigits.equals(mi.getCostPrice(), p.getPrice(MANUFACTURER_COST)) ) {
                if ( p.hasPrice(MANUFACTURER_COST) ) updatedPrices++;
                else newPrices++;
                p.setPrice(PriceType.MANUFACTURER_COST, mi.costPrice, "Import by " + arranger);

            }
        }
        m.finish();

        String summary = "Zeilen, mit gefunden (db)Artikeln: " + databaseLines + " \n"
                + "Neue Preise hinterlegt: " + newPrices + "\n"
                + "Preise aktualisiert: " + updatedPrices;
        StringBuilder details = new StringBuilder();
        for (Object error : errors) {
            details.append(error.toString()).append("\n");
        }
        m.finish();
        if ( newPrices + updatedPrices == 0 ) return Reply.failure(summary, details.toString());
        else return Reply.success(null, summary, details.toString());
    }

    // Manufacturer PartNo | EAN | Name | Contractor Reference Price | ContractorPartNo <br />
    /**
     * See {@link ContractorPricePartNoImporter#fromContractorXls(de.dw.rules.TradeName, de.dw.util.FileJacket, java.lang.String) }.
     * <p/>
     * @param inFile   the inFile
     * @param arranger the arranger
     * @return
     */
    @Override
    public Reply<Void> fromContractorXls(TradeName contractor, FileJacket inFile, String arranger) {
        final SubMonitor m = monitorFactory.newSubMonitor(contractor + " Preise und Artikelnummern importieren", 100);
        m.start();
        m.message("Reading File");
        ProductEao productEao = new ProductEao(uuEm);
        ReportLineEao reportLineEao = new ReportLineEao(reportEm);
        LucidCalcReader reader = new JExcelLucidCalcReader();
        reader.addColumn(0, String.class).addColumn(1, String.class).addColumn(2, String.class).addColumn(3, Double.class).addColumn(4, String.class);
        List<ContractorImport> imports = reader.read(inFile.toTemporaryFile(), ContractorImport.class);
        List<String> errors = reader.getErrors();
        List<String> info = new ArrayList<>();
        m.worked(5);
        m.setWorkRemaining(imports.size() + 100);

        int databaseLines = 0;
        int updatedGtin = 0;
        int newPrices = 0;
        int updatedPrices = 0;
        int updatedContractorPartNo = 0;

        for (ContractorImport ci : imports) {
            m.worked(1, "Importing (" + ci.manufacturerPartNo + ")");
            if ( !ci.hasManufacturerPartNoOrGtin() ) {
                errors.add("No ManufacturerPartNo or EAN found for " + ci);
                continue;
            }
            Product p = null;
            if ( !StringUtils.isBlank(ci.manufacturerPartNo) ) p = productEao.findByPartNo(ci.manufacturerPartNo); // First try finding it via the partNo
            if ( p != null && p.getGtin() == 0 && ci.hasValidGtin() ) { // Optional set of gtin, if it is missing.
                p.setGtin(Long.parseLong(ci.gtin));
                updatedGtin++;
            }
            if ( p == null && ci.hasValidGtin() ) p = productEao.findByGtin(Long.parseLong(ci.gtin)); // Second, try finding it via gtin
            if ( p == null ) {
                errors.add("No UniqueUnit.Product Entity found for PartNo " + ci.manufacturerPartNo + " bzw. Gtin " + ci.gtin + ", Ignoring");
                continue;
            }
            databaseLines++;

            if ( ci.getReferencePrice() > 0.01 && !TwoDigits.equals(p.getPrice(CONTRACTOR_REFERENCE), ci.getReferencePrice()) ) { // If price is valid and not equal, set it.
                if ( p.hasPrice(CONTRACTOR_REFERENCE) ) updatedPrices++;
                else newPrices++;
                p.setPrice(CONTRACTOR_REFERENCE, ci.getReferencePrice(), "Import by " + arranger);
                info.add(ProductFormater.toDetailedName(p) + " added/updated contractor reference price to " + ci.getReferencePrice());
            } else {
                errors.add(ci + " hat keinen Preis");
            }
            if ( ci.hasValidContractorPartNo(contractor) ) { // If partNo is valid, set it.
                String contractorPartNo = ci.toNormalizeContractorPart(contractor);
                if ( !contractorPartNo.equals(p.getAdditionalPartNo(contractor)) ) {
                    p.setAdditionalPartNo(contractor, contractorPartNo);
                    updatedContractorPartNo++;
                    info.add(ProductFormater.toDetailedName(p) + " added/updated contractor part no to " + contractorPartNo);
                }
            } else {
                errors.add(ci.violationMessagesOfContractorPartNo(contractor));
            }
        }
        uuEm.flush();
        // Also update existing report lines, which have unset values.
        // TODO: This should happen from the report component on needed basis or as event call. not here.
        List<ReportLine> missingContractorPartNo = reportLineEao.findMissingContractorPartNo(contractor);
        m.setWorkRemaining(missingContractorPartNo.size());
        m.message("Updateing existing Reportlines");
        for (ReportLine line : missingContractorPartNo) {
            Product product = uuEm.find(Product.class, line.getProductId());
            m.worked(1, "Updating ReportLine:" + line.getId());
            if ( product.getAdditionalPartNo(contractor) != null ) line.setContractorPartNo(product.getAdditionalPartNo(contractor));
            if ( product.hasPrice(CONTRACTOR_REFERENCE) && line.getContractorReferencePrice() == 0 )
                line.setContractorReferencePrice(product.getPrice(CONTRACTOR_REFERENCE));
            if ( product.getGtin() > 0 ) line.setGtin(product.getGtin()); // Overwrite, to be sure.
        }

        String summary = "Zeilen, mit gefunden (db)Artikeln: " + databaseLines + " (Entweder über PartNo oder Gtin)\n"
                + "GTIN/EAN aktuallisiert: " + updatedGtin + "\n"
                + "Neue Preise hinterlegt: " + newPrices + "\n"
                + "Preise aktualisiert: " + updatedPrices + "\n"
                + "Lieferantenartikelnummer aktualisiert: " + updatedContractorPartNo;
        StringBuilder details = new StringBuilder();
        if ( !info.isEmpty() ) {
            details.append("Infos\n-----\n");
            info.forEach((i) -> details.append(i).append("\n"));
        }
        details.append("-----\nFehler\n-----\n");
        errors.forEach((error) -> details.append(error).append("\n"));
        m.finish();

        if ( updatedGtin + newPrices + updatedPrices + updatedContractorPartNo == 0 ) return Reply.failure(summary, details.toString());
        else return Reply.success(null, summary, details.toString());
    }
}
