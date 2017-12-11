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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.format.ProductFormater;
import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.lucidcalc.*;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcWriter;

import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CONTRACTOR_REFERENCE;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.MANUFACTURER_COST;
import static eu.ggnet.dwoss.util.DateFormats.ISO;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.Representation.CURRENCY_EURO;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;

/**
 * Exporter implementation.
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class ContractorPricePartNoExporterOperation implements ContractorPricePartNoExporter {

    @Inject
    private ProductEao productEao;

    @Inject
    private MonitorFactory monitorFactory;

    @Override
    public FileJacket toManufacturerXls(TradeName contractorManufacturer) {
        if ( !contractorManufacturer.isManufacturer() ) throw new RuntimeException(contractorManufacturer + " is not a Manufacturer, wrong exporter");
        SubMonitor m = monitorFactory.newSubMonitor("Lieferanten und Hersteller Exporter");
        m.message("Loading Units").start();
        List<Object[]> rows = productEao.findByTradeNames(contractorManufacturer.getBrands()).stream()
                .filter(p -> p.getPrice(MANUFACTURER_COST) <= 0.01)
                .sorted()
                .map(p -> new Object[]{p.getPartNo(), 0.0, ProductFormater.toName(p)})
                .collect(Collectors.toList());

        m.setWorkRemaining(rows.size() + 10).message("Filtering");
        m.message("Generating Document");
        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC));
        table.add(new STableColumn("Artikelnummer", 18)).add(new STableColumn("Kostpreis", 12, new CFormat(CURRENCY_EURO))).add(new STableColumn("Name", 45));
        table.setModel(new STableModelList(rows));
        CCalcDocument cdoc = new TempCalcDocument();
        cdoc.add(new CSheet("Kostpreise", table));

        FileJacket result = new FileJacket("Fehlende " + contractorManufacturer.getName() + " Kostpreise vom " + ISO.format(new Date()), ".xls",
                new JExcelLucidCalcWriter().write(cdoc));
        m.finish();
        return result;
    }

    @Override
    public FileJacket toContractorXls(TradeName contractor) {
        SubMonitor m = monitorFactory.newSubMonitor("Lieferanten Exporter", 100);
        m.message("Loading Units").start();

        List<Object[]> rows = productEao.findByContractor(contractor)
                .stream()
                .filter(p -> p.getPrice(CONTRACTOR_REFERENCE) <= 0.01)
                .sorted()
                .map(p -> new Object[]{"", p.getPartNo(), p.getTradeName().getName(), p.getName(), 0.0, p.getGtin()})
                .collect(Collectors.toList());

        m.worked(5, "Generating File");
        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC));
        table.add(new STableColumn("Contractor PartNo", 18)).add(new STableColumn("Hersteller PartNo", 20));
        table.add(new STableColumn("Brand", 15)).add(new STableColumn("Bezeichnung", 25))
                .add(new STableColumn("Reference Preis", 12, new CFormat(CURRENCY_EURO))).add(new STableColumn("GTIN/EAN", 14));
        table.setModel(new STableModelList(rows));
        CCalcDocument cdoc = new TempCalcDocument();
        cdoc.add(new CSheet(contractor.getName(), table));

        FileJacket result = new FileJacket("Fehlende " + contractor.getName() + " Preise und Artikelnummern vom " + ISO.format(new Date()), ".xls",
                new JExcelLucidCalcWriter().write(cdoc));
        m.finish();
        return result;
    }

}
