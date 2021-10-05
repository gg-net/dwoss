/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.misc.ee.tryout;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.JFrame;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.swing.JRViewer;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.Warranty;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.system.util.TwoDigits;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.mandator.api.service.ListingConfiguration;
import eu.ggnet.dwoss.misc.ee.listings.*;
import eu.ggnet.dwoss.uniqueunit.ee.assist.gen.ProductGenerator;
import eu.ggnet.dwoss.uniqueunit.ee.assist.gen.UniqueUnitGenerator;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;

import static eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType.CUSTOMER;

/**
 *
 * @author oliver.guenther
 */
public class SalesListingTryout {

    public static void main(String[] args) {

        TradeName CONTRACTOR = TradeName.ONESELF;
        ProductGenerator pg = new ProductGenerator();
        UniqueUnitGenerator uug = new UniqueUnitGenerator();

        List<StackedLine> stackedLines = new ArrayList<>();
        DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance(Locale.GERMAN);
        df.applyPattern("#,###,##0.00");
        for (Product p : pg.generateProduct(20)) {
            StackedLine line = new StackedLine();
            line.setBrand(p.getTradeName());
            line.setGroup(p.getGroup());
            line.setCommodityGroupName(p.getGroup().getNote());
            line.setDescription(p.getDescription());
            line.setManufacturerName(p.getTradeName().getName());
            line.setManufacturerPartNo(p.getPartNo());
            line.setName(p.getName());
//            line.setImageUrl(imageFinder.findImageUrl(p.getImageId()));
            boolean priceChanged = false;
            double customerPrice = 0;

            for (int i = 0; i < 10; i++) {
                UniqueUnit uu = uug.makeUniqueUnit(CONTRACTOR, p);
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

                double uuPrice = uu.getPrice(CUSTOMER);
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

        var config = new ListingConfiguration.Builder()
                .filePrefix("Geräteliste ")
                .name(TradeName.ACER.getDescription())
                .brand(TradeName.ACER)
                .addAllGroups(EnumSet.allOf(ProductGroup.class))
                .addAllSupplementBrands(EnumSet.allOf(TradeName.class))
                .headLeft("Beispieltext Links\nZeile 2")
                .headCenter("Beispieltext Mitte\nZeile 2")
                .headRight("Beispieltext Rechts\nZeile 2")
                .footer("Fusszeilentext")
                .build();

        try {

            ListingConfiguration.Builder finalConfigBuilder = new ListingConfiguration.Builder().mergeFrom(config);

            if ( !config.jasperTemplateFile().isPresent() )
                finalConfigBuilder.jasperTemplateFile(compileReportToTempFile("CustomerSalesListing"));

            if ( !config.jasperTempleteUnitsFile().isPresent() )
                finalConfigBuilder.jasperTempleteUnitsFile(compileReportToTempFile("CustomerSalesListingUnits"));

            var finalConfig = finalConfigBuilder.build();

            SortedSet<StackedLine> filtered = stackedLines.stream()
                    .filter(line -> (finalConfig.getAllBrands().contains(line.getBrand()) && finalConfig.groups().contains(line.getGroup())))
                    .collect(Collectors.toCollection(TreeSet::new));
            JasperPrint result = JasperFillManager.fillReport(finalConfig.jasperTemplateFile().get(),
                    finalConfig.toReportParamters(), new JRBeanCollectionDataSource(filtered));

            JasperExportManager.exportReportToPdfFile(result, "target/Endkundenliste.pdf");
            Desktop.getDesktop().open(new File("target/Endkundenliste.pdf"));

            JRViewer viewer = new JRViewer(result);
            JFrame frame = new JFrame("Viewer");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(viewer, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } catch (IOException | JRException e) {
            e.printStackTrace();
        }
    }

    private static String compileReportToTempFile(final String name) {
        // Optimize, only do it on updates.
        String reportFile = Utils.getTempDirectory("jasper") + "/" + name + ".jasper";
        URL url = Objects.requireNonNull(SalesListingProducerOperation.class.getResource(name + ".jrxml"));
        try (InputStream is = url.openStream()) {
            JRSaver.saveObject(JasperCompileManager.compileReport(is), reportFile);
            return reportFile;
        } catch (IOException | JRException ex) {
            throw new RuntimeException(ex);
        }
    }

}
