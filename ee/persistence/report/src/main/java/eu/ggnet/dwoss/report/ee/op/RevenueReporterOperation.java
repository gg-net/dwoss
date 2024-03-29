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
package eu.ggnet.dwoss.report.ee.op;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.common.values.SalesChannel;

import java.util.Map.Entry;
import java.util.*;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import eu.ggnet.dwoss.core.system.util.Step;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.core.system.progress.MonitorFactory;
import eu.ggnet.dwoss.core.system.progress.SubMonitor;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.eao.Revenue;
import eu.ggnet.dwoss.report.ee.eao.Revenue.Key;
import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.lucidcalc.*;
import eu.ggnet.lucidcalc.jexcel.JExcelLucidCalcWriter;

import static eu.ggnet.dwoss.core.common.values.DocumentType.INVOICE;
import static eu.ggnet.dwoss.core.common.values.PositionType.UNIT;
import static eu.ggnet.dwoss.core.common.values.PositionType.UNIT_ANNEX;
import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.CENTER;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static eu.ggnet.lucidcalc.CFormat.Representation.CURRENCY_EURO;
import static java.awt.Color.*;

/**
 * Class that provides methods used for revenue reports.
 * <p>
 * @author pascal.perau
 */
@Stateless
public class RevenueReporterOperation implements RevenueReporter {

    @Inject
    private ReportLineEao eao;

    @Inject
    private MonitorFactory monitorFactory;

    @Inject
    private Contractors contractors;

    @Override
    public FileJacket toXls(Date start, Date end, Step step, boolean extraReported) {
        String name = "Umsatzreport";
        SubMonitor m = monitorFactory.newSubMonitor(name);
        m.start();

        NavigableMap<Date, Revenue> revenue = eao.revenueByPositionTypesAndDate(Arrays.asList(UNIT, UNIT_ANNEX), start, end, step, extraReported);

        m.worked(10);
        STable template = new STable();
        template.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        template.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        template.add(new STableColumn(step.name(), 12));
        template.add(new STableColumn("Einnahmen Händler", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Einnahmen Endkunde", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Einnahmen Unknown", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Einnahmen Summe", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Storno Händler", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Storno Endkunde", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Storno Summe", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Umsatz Summe", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Ertrag Reported Summe", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        template.add(new STableColumn("Einkauf Reported Summe", 18, new CFormat(RIGHT, CURRENCY_EURO)));

        STable all = new STable(template);
        all.setModel(new STableModelList(buildSumModel(step, revenue)));

        CCalcDocument cdoc = new TempCalcDocument(name);
        cdoc.add(new CSheet("Revenue_All", all));

        for (TradeName contractor : contractors.all()) {
            STable simple = new STable(template);
            simple.setModel(new STableModelList(buildContractorModel(step, contractor, revenue)));
            cdoc.add(new CSheet("Revenue_" + contractor, simple));
        }

        FileJacket result = new FileJacket(name, ".xls", new JExcelLucidCalcWriter().write(cdoc));
        m.finish();
        return result;
    }

    private List<Object[]> buildSumModel(Step step, NavigableMap<Date, Revenue> revenue) {
        List<Object[]> rows = new ArrayList<>();
        for (Entry<Date, Revenue> e : revenue.entrySet()) {
            Revenue r = e.getValue();
            rows.add(new Object[]{
                step.format(e.getKey()),
                r.sumBy(SalesChannel.RETAILER, DocumentType.INVOICE),
                r.sumBy(SalesChannel.CUSTOMER, DocumentType.INVOICE),
                r.sumBy(SalesChannel.UNKNOWN, DocumentType.INVOICE),
                r.sumBy(INVOICE),
                r.sumBy(SalesChannel.RETAILER, DocumentType.ANNULATION_INVOICE),
                r.sumBy(SalesChannel.CUSTOMER, DocumentType.ANNULATION_INVOICE),
                r.sumBy(DocumentType.ANNULATION_INVOICE),
                r.sum(),
                r.sumReportedRevenue(),
                r.sumReportedPurchasePrice()
            });
        }
        return rows;
    }

    private List<Object[]> buildContractorModel(Step step, TradeName contractor, NavigableMap<Date, Revenue> revenue) {
        List<Object[]> rows = new ArrayList<>();
        for (Entry<Date, Revenue> e : revenue.entrySet()) {
            Revenue r = e.getValue();
            rows.add(new Object[]{
                step.format(e.getKey()),
                r.getDetails().get(Key.valueOf(SalesChannel.RETAILER, DocumentType.INVOICE, contractor)).revenue,
                r.getDetails().get(Key.valueOf(SalesChannel.CUSTOMER, DocumentType.INVOICE, contractor)).revenue,
                r.getDetails().get(Key.valueOf(SalesChannel.UNKNOWN, DocumentType.INVOICE, contractor)).revenue,
                r.sumBy(INVOICE, contractor),
                r.getDetails().get(Key.valueOf(SalesChannel.RETAILER, DocumentType.ANNULATION_INVOICE, contractor)).revenue,
                r.getDetails().get(Key.valueOf(SalesChannel.CUSTOMER, DocumentType.ANNULATION_INVOICE, contractor)).revenue,
                r.sumBy(DocumentType.ANNULATION_INVOICE, contractor),
                r.sumBy(contractor),
                r.sumReportedRevenueBy(contractor),
                r.sumReportedPurchasePriceBy(contractor)
            });
        }
        return rows;
    }

}
