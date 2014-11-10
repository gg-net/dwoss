package eu.ggnet.dwoss.report.op;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.LucidCalc;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.TempCalcDocument;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.CFormat;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.report.RevenueReportSum;
import eu.ggnet.dwoss.report.eao.ReportLineEao.Step;
import eu.ggnet.dwoss.report.eao.Revenue.Key;


import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.report.eao.*;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD_ITALIC;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.*;
import static eu.ggnet.lucidcalc.CFormat.Representation.CURRENCY_EURO;
import static eu.ggnet.dwoss.rules.DocumentType.INVOICE;
import static eu.ggnet.dwoss.rules.PositionType.*;
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

    /**
     * <p>
     * @param pTypes position types to be included
     * @param start  start date
     * @param end    end date
     * @return Daily seperated {@link RevenueReportSum} containing the aggregated information.
     */
    @Override
    public Set<RevenueReportSum> aggregateDailyRevenue(List<PositionType> pTypes, Date start, Date end) {
        List<Set<DailyRevenue>> reportSets = eao.findRevenueDataByPositionTypesAndDate(pTypes, start, end);
        Set<RevenueReportSum> reportData = new HashSet();
        for (Set<DailyRevenue> set : reportSets) {
            RevenueReportSum sum = new RevenueReportSum();
            for (DailyRevenue rpc : set) {
                if ( rpc.getDocumentTypeName().equals(DocumentType.ANNULATION_INVOICE.getName()) ) {
                    sum.addSumByDocumentType(DocumentType.ANNULATION_INVOICE, rpc.getDailySum());
                } else {
                    if ( rpc.getSalesChannelName() != null && rpc.getSalesChannelName().equals(SalesChannel.CUSTOMER.getName()) ) {
                        sum.addSalesChannelSum(SalesChannel.CUSTOMER, rpc.getDailySum());
                    } else if ( rpc.getSalesChannelName() != null && rpc.getSalesChannelName().equals(SalesChannel.RETAILER.getName()) ) {
                        sum.addSalesChannelSum(SalesChannel.RETAILER, rpc.getDailySum());
                    } else {
                        sum.addSalesChannelSum(SalesChannel.UNKNOWN, rpc.getDailySum());
                    }
                    sum.addSumByDocumentType(DocumentType.INVOICE, rpc.getDailySum());
                }
                sum.setReportingDate(rpc.getReportingDate());
            }
            reportData.add(sum);
        }
        return reportData;
    }

    @Override
    public FileJacket toXls(Date start, Date end, Step step) {
        String name = "Umsatzreport";
        SubMonitor m = monitorFactory.newSubMonitor(name);
        m.start();

        NavigableMap<Date, Revenue> revenue = eao.revenueByPositionTypesAndDate(Arrays.asList(UNIT, UNIT_ANNEX), start, end, step);

        List<Object[]> rows = new ArrayList<>();
        for (Entry<Date, Revenue> e : revenue.entrySet()) {
            Revenue r = e.getValue();
            rows.add(new Object[]{
                step.format(e.getKey()),
                r.getDetails().get(Key.valueOf(SalesChannel.RETAILER, DocumentType.INVOICE)),
                r.getDetails().get(Key.valueOf(SalesChannel.CUSTOMER, DocumentType.INVOICE)),
                r.getDetails().get(Key.valueOf(SalesChannel.UNKNOWN, DocumentType.INVOICE)),
                r.getSum(INVOICE),
                r.getDetails().get(Key.valueOf(SalesChannel.RETAILER, DocumentType.ANNULATION_INVOICE)),
                r.getDetails().get(Key.valueOf(SalesChannel.CUSTOMER, DocumentType.ANNULATION_INVOICE)),
                r.getSum(DocumentType.ANNULATION_INVOICE),
                r.getSum()
            });
        }

        m.worked(10);
        STable table = new STable();
        table.setTableFormat(new CFormat(BLACK, WHITE, new CBorder(BLACK)));
        table.setHeadlineFormat(new CFormat(BOLD_ITALIC, WHITE, BLUE, CENTER, new CBorder(BLACK)));
        table.add(new STableColumn(step.name(), 12));
        table.add(new STableColumn("Einnahmen Händler", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("Einnahmen Endkunde", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("Einnahmen Unknown", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("Einnahmen Summe", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("Storno Händler", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("Storno Endkunde", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("Storno Summe", 18, new CFormat(RIGHT, CURRENCY_EURO)));
        table.add(new STableColumn("Umsatz Summe", 18, new CFormat(RIGHT, CURRENCY_EURO)));

        table.setModel(new STableModelList(rows));

        CCalcDocument cdoc = new TempCalcDocument(name);
        cdoc.add(new CSheet("Revenue", table));

        File file = LucidCalc.createWriter(LucidCalc.Backend.XLS).write(cdoc);
        FileJacket result = new FileJacket(name, ".xls", file);
        m.finish();
        return result;
    }

}
