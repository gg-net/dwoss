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
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.report.assist.Reports;
import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.rules.partno.PartNoSupport;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.lucidcalc.LucidCalcReader;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcReader;

import lombok.Value;

import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CONTRACTOR_REFERENCE;

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

    }

    // Must be Public for JExcelReader
    @Value
    public static class ContractorImport implements Serializable {

        private final String contractorPartNo;

        private final String manufacturerPartNo;

        private final String name1;

        private final String name2;

        private final Double referencePrice;

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
    public ImportResult fromManufacturerXls(TradeName contractorManufacturer, FileJacket inFile, String arranger) {
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
        int validSize = 0;
        int importedSize = 0;
        for (ManufacturerImport partNoPrice : imports) {
            m.worked(1);
            if ( partNoSupport != null && !partNoSupport.isValid(partNoPrice.partNo) ) {
                errors.add(partNoSupport.violationMessages(partNoPrice.partNo));
                continue;
            }
            if ( partNoPrice.costPrice == null || partNoPrice.costPrice <= 0.01 ) {
                errors.add("PartNo " + partNoPrice.costPrice + " hat keinen Preis");
                continue;
            }
            validSize++;
            m.message("Importing (" + partNoPrice.partNo + ")");
            Product p = productEao.findByPartNo(partNoPrice.partNo);
            if ( p == null ) {
                errors.add("No UniqueUnit Entity for PartNo " + partNoPrice.partNo);
                continue;
            }
            p.setPrice(PriceType.MANUFACTURER_COST, partNoPrice.costPrice, arranger);
            importedSize++;
        }
        m.finish();
        return new ImportResult(imports.size(), validSize, importedSize, importedSize, errors);
    }

    /**
     * See {@link ContractorPricePartNoImporter#fromContractorXls(de.dw.rules.TradeName, de.dw.util.FileJacket, java.lang.String) }.
     * <p/>
     * @param inFile   the inFile
     * @param arranger the arranger
     * @return
     */
    @Override
    public ImportResult fromContractorXls(TradeName contractor, FileJacket inFile, String arranger) {
        final SubMonitor m = monitorFactory.newSubMonitor(contractor + " Preise und Artikelnummern importieren", 100);
        m.start();
        m.message("Reading File");
        ProductEao productEao = new ProductEao(uuEm);
        ReportLineEao reportLineEao = new ReportLineEao(reportEm);
        LucidCalcReader reader = new JExcelLucidCalcReader();
        reader.addColumn(0, String.class).addColumn(1, String.class).addColumn(2, String.class).addColumn(3, String.class).addColumn(4, Double.class);
        List<ContractorImport> imports = reader.read(inFile.toTemporaryFile(), ContractorImport.class);
        List<String> errors = reader.getErrors();
        m.worked(5);
        m.setWorkRemaining(imports.size() + 100);
        int validSize = 0;
        int importAbleSize = 0;
        int importedSize = 0;
        PartNoSupport support = contractor.getPartNoSupport();

        for (ContractorImport contractorImport : imports) {
            m.worked(1, "Importing (" + contractorImport.manufacturerPartNo + ")");
            // HINT: It would be much better to validate the import class and put the validation there, but the support comes afterwards.
            String contractorPartNo = contractorImport.contractorPartNo;
            if ( support != null ) {
                contractorPartNo = support.normalize(contractorPartNo);
                if ( !support.isValid(contractorPartNo) ) {
                    errors.add(support.violationMessages(contractorPartNo));
                    continue;
                }
            }
            if ( contractorImport.manufacturerPartNo == null ) {
                errors.add("ManufacturerPartNo is null of" + contractorImport);
                continue;
            }
            if ( contractorImport.referencePrice == null || contractorImport.referencePrice <= 0.01 ) {
                errors.add("PartNo " + contractorImport + " hat keinen Preis");
                continue;
            }
            // Some validation of the manufacturer part no would also be nice.
            validSize++;
            Product p = productEao.findByPartNo(contractorImport.manufacturerPartNo);
            if ( p == null ) {
                errors.add("No UniqueUnit.Product Entity found for PartNo " + contractorImport.manufacturerPartNo + ", Ignoring");
                continue;
            }
            importAbleSize++;
            // There was a difference before. Now its the same.
            importedSize++;
            p.setAdditionalPartNo(contractor, contractorPartNo);
            p.setPrice(CONTRACTOR_REFERENCE, contractorImport.referencePrice, "Import by " + arranger);
        }
        uuEm.flush();
        // Also update existing report lines, which have unset values.
        List<ReportLine> missingContractorPartNo = reportLineEao.findMissingContractorPartNo(contractor);
        m.setWorkRemaining(missingContractorPartNo.size());
        m.message("Updateing existing Reportlines");
        for (ReportLine line : missingContractorPartNo) {
            Product product = uuEm.find(Product.class, line.getProductId());
            m.worked(1, "Updating ReportLine:" + line.getId());
            if ( product.getAdditionalPartNo(contractor) != null ) line.setContractorPartNo(product.getAdditionalPartNo(contractor));
            if ( product.getPrice(CONTRACTOR_REFERENCE) > 0.01 ) line.setContractorReferencePrice(product.getPrice(CONTRACTOR_REFERENCE));
        }
        m.finish();
        return new ImportResult(imports.size(), validSize, importAbleSize, importedSize, errors);
    }
}
