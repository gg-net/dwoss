package eu.ggnet.dwoss.report.ee.test;

import java.text.ParseException;
import java.util.*;

import org.junit.Test;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.report.ee.entity.*;
import eu.ggnet.dwoss.core.system.util.Utils;

import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.ALSO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class ReportTest {

    private final static Date NOW = new Date();

    private static Date _2009_01_01;

    private static Date _2011_09_01;

    private static Date _2011_10_01;

    private static Date _2011_10_07;

    static {
        try {
            _2009_01_01 = Utils.ISO_DATE.parse("2009-01-01");
            _2011_09_01 = Utils.ISO_DATE.parse("2011-09-01");
            _2011_10_01 = Utils.ISO_DATE.parse("2011-10-01");
            _2011_10_07 = Utils.ISO_DATE.parse("2011-10-07");
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testSplitterResult() {
        Report report = new Report("TestReport", ALSO, _2011_10_01, _2011_10_07);

        ReportLine unitAfter = ReportLine.builder().id(1)
                .documentType(DocumentType.INVOICE).documentId(1).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1)
                .price(100).tax(0.19).mfgDate(_2009_01_01)
                .build();

        ReportLine unitBefore = ReportLine.builder().id(2)
                .documentType(DocumentType.INVOICE).documentId(1).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("124").amount(1)
                .price(100).tax(0.19).mfgDate(_2011_09_01)
                .build();

        report.add(unitBefore);
        report.add(unitAfter);

        Report.YearSplit result = report.filterInvoicedSplit();
        assertThat(result.after)
                .describedAs("Report.after : Split at " + result.splitter)
                .isNotEmpty()
                .hasSize(1);
        assertFalse("Before should not be empty, splitting at " + result.splitter, result.before.isEmpty());
        assertEquals("Before should be exactly one, splitting at " + result.splitter, 1, result.before.size());
        assertEquals(unitAfter, result.after.first());
        assertEquals(unitBefore, result.before.first());
    }

    @Test
    public void testFilterInvoice() {
        Report report = new Report("TestReport", ALSO,
                new Date(Calendar.getInstance().getTimeInMillis() - 100000), new Date());

        ReportLine line1 = ReportLineBuilder.create(1,"PersName1", "This is a TestDescription1", 137, "DW0037", 3, "RE0008", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");

        report.add(line1);

        assertTrue(report.filterInvoiced().contains(line1));
        assertTrue(report.filterRepayed().isEmpty());
        assertTrue(report.filterInfos().isEmpty());

        ReportLine line2 = ReportLineBuilder.create(2,"PersName1", "This is a TestDescription1", 137, "DW0037", 3, "RE0008", PositionType.UNIT,
                DocumentType.COMPLAINT, 2, 1, 0.19, 0, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line2.setWorkflowStatus(ReportLine.WorkflowStatus.UNDER_PROGRESS);

        line1.add(line2);
        report.add(line2);

        ReportLine line3 = ReportLineBuilder.create(3,"PersName1", "This is a TestDescription1", 137, "DW0037", 3, "RE0008", PositionType.UNIT,
                DocumentType.COMPLAINT, 2, 1, 0.19, 0, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");
        line3.setWorkflowStatus(ReportLine.WorkflowStatus.DISCHARGED);

        line1.add(line3);
        line2.add(line3);
        report.add(line3);

        assertTrue("Complaint was discharged, so line1 should be visible in invoiced.", report.filterInvoiced().contains(line1));
        assertTrue(report.filterRepayed().isEmpty());
        assertTrue("Complaints are only infos, so line2 should be visible in infos.", report.filterInfos().contains(line2));
        assertTrue("Complaints are only infos, so line3 should be visible in infos.", report.filterInfos().contains(line3));
    }

    @Test
    public void testRepaymentOneReport() {
        Report report = new Report("TestReport", ALSO, NOW, NOW);

        ReportLine line1 = ReportLine.builder().id(1)
                .documentType(DocumentType.INVOICE).documentId(1).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(100).tax(0.19)
                .build();

        report.add(line1);

        // Creditmemo unitAnnex.
        ReportLine line2 = ReportLine.builder().id(2)
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(2).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT_ANNEX).name("Unit-123").refurbishId("123").amount(1).price(-10).tax(0.19)
                .build();

        line1.add(line2);
        report.add(line2);

        assertTrue("Only Unit Annex - Annulation Invoice in same Report, so line1 should be invoiced\n" + report.toMultiLine(false),
                report.filterInvoiced().contains(line1));
        assertTrue("Only Unit Annex - Annulation Invoice in same Report, so line2 should be invoiced" + report.toMultiLine(false),
                report.filterInvoiced().contains(line2));

        // Now add A Unit.
        ReportLine line3 = ReportLine.builder().id(3)
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(3).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(-90).tax(0.19)
                .build();

        line1.add(line3);
        line2.add(line3);
        report.add(line3);

        assertTrue("Case Full Repayment in one Report, all are info\n" + report.toMultiLine(false),
                report.filterInvoiced().isEmpty());
        assertTrue("Case Full Repayment in one Report, all are info\n" + report.toMultiLine(false),
                report.filterRepayed().isEmpty());
        assertTrue("Case Full Repayment in one Report, all are info\n" + report.toMultiLine(false),
                report.filterInfos().contains(line1));
        assertTrue("Case Full Repayment in one Report, all are info\n" + report.toMultiLine(false),
                report.filterInfos().contains(line2));
        assertTrue("Case Full Repayment in one Report, all are info\n" + report.toMultiLine(false),
                report.filterInfos().contains(line3));

    }

    @Test
    public void testRepaymentTwoReport() {
        Report report1 = new Report("TestReport 1", ALSO, NOW, NOW);
        Report report2 = new Report("TestReport 2", ALSO, NOW, NOW);

        ReportLine line1 = ReportLine.builder().id(1)
                .documentType(DocumentType.INVOICE).documentId(1).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(100).tax(0.19)
                .build();

        report1.add(line1);

        // Creditmemo unitAnnex.
        ReportLine line2 = ReportLine.builder().id(2)
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(2).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT_ANNEX).name("Unit-123").refurbishId("123").amount(1).price(-10).tax(0.19)
                .build();

        line1.add(line2);
        report2.add(line2);

        assertTrue("Only Unit Annex Repayment, Different Reports, so no invoices in report2\n" + report2.toMultiLine(false),
                report2.filterInvoiced().isEmpty());
        assertTrue("Only Unit Annex Repayment, Different Reports, so line1 should be repayment\n" + report2.toMultiLine(false),
                report2.filterRepayed().contains(line2));

        // Now add A Unit.
        ReportLine line3 = ReportLine.builder().id(3)
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(3).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(-90).tax(0.19)
                .build();

        line1.add(line3);
        line2.add(line3);
        report2.add(line3);

        assertTrue("Case Full Repayment in two Reports, so no invoices in report2\n" + report2.toMultiLine(false),
                report2.filterInvoiced().isEmpty());
        assertTrue("Case Full Repayment in two Reports, so unit annex is only info\n" + report2.toMultiLine(false),
                report2.filterInfos().contains(line2));
        assertTrue("Case Full Repayment in two Reports, so unit is repayment\n" + report2.toMultiLine(false),
                report2.filterRepayed().contains(line3));

    }

    @Test
    public void testRepaymentThreeReports() {
        Report report1 = new Report("TestReport 1", ALSO, NOW, NOW);
        Report report2 = new Report("TestReport 2", ALSO, NOW, NOW);
        Report report3 = new Report("TestReport 3", ALSO, NOW, NOW);

        ReportLine line1 = ReportLine.builder().id(1)
                .documentType(DocumentType.INVOICE).documentId(1).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(100).tax(0.19)
                .build();

        report1.add(line1);

        // Creditmemo unitAnnex.
        ReportLine line2 = ReportLine.builder().id(2)
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(2).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT_ANNEX).name("Unit-123").refurbishId("123").amount(1).price(-10).tax(0.19)
                .build();

        line1.add(line2);
        report2.add(line2);

        // Now add A Unit.
        ReportLine line3 = ReportLine.builder().id(3)
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(3).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(-90).tax(0.19)
                .build();

        line1.add(line3);
        line2.add(line3);
        report3.add(line3);

        assertTrue("Report1 should only contain line1 of invoice\n"
                + report1.toMultiLine(false)
                + report2.toMultiLine(false)
                + report3.toMultiLine(false),
                report1.filterInvoiced().size() == 1
                && report1.filterInvoiced().contains(line1)
                && report1.filterInfos().isEmpty()
                && report1.filterRepayed().isEmpty());

        assertTrue("Report2 should only contaion line2 of partial repayment\n"
                + report1.toMultiLine(false)
                + report2.toMultiLine(false)
                + report3.toMultiLine(false),
                report2.filterInvoiced().isEmpty()
                && report2.filterInfos().isEmpty()
                && report2.filterRepayed().size() == 1
                && report2.filterRepayed().contains(line2));

        assertTrue("Report3 should only contaion line3 of full repayment\n"
                + report1.toMultiLine(false)
                + report2.toMultiLine(false)
                + report3.toMultiLine(false),
                report3.filterInvoiced().isEmpty()
                && report3.filterInfos().isEmpty()
                && report3.filterRepayed().size() == 1
                && report3.filterRepayed().contains(line3));
    }

    // TODO: Report 1 :Invoice + Partiall Repayment, Report 2: Full Repayment
    @Test
    public void testRepaymentTwoReports() {
        Report report1 = new Report("TestReport 1", ALSO, NOW, NOW);
        Report report2 = new Report("TestReport 2", ALSO, NOW, NOW);

        ReportLine line1 = ReportLine.builder().id(1)
                .documentType(DocumentType.INVOICE).documentId(1).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(100).tax(0.19)
                .build();

        report1.add(line1);

        // Creditmemo unitAnnex.
        ReportLine line2 = ReportLine.builder().id(2)
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(2).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT_ANNEX).name("Unit-123").refurbishId("123").amount(1).price(-10).tax(0.19)
                .build();

        line1.add(line2);
        report1.add(line2);

        // Now add A Unit.
        ReportLine line3 = ReportLine.builder().id(3)
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(3).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(-90).tax(0.19)
                .build();

        line1.add(line3);
        line2.add(line3);
        report2.add(line3);

        assertTrue("Report1 should only contain line1 of invoice and line2 of partial repayment\n"
                + report1.toMultiLine(false)
                + report2.toMultiLine(false),
                report1.filterInvoiced().size() == 2
                && report1.filterInvoiced().containsAll(Arrays.asList(line1, line2))
                && report1.filterInfos().isEmpty()
                && report1.filterRepayed().isEmpty());

        assertTrue("Report2 should only contaion line3 of full repayment\n"
                + report1.toMultiLine(false)
                + report2.toMultiLine(false),
                report2.filterInvoiced().isEmpty()
                && report2.filterInfos().isEmpty()
                && report2.filterRepayed().size() == 1
                && report2.filterRepayed().contains(line3));
    }

}
