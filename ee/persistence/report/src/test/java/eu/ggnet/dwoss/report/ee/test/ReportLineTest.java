package eu.ggnet.dwoss.report.ee.test;

import java.util.Date;

import org.junit.Test;

import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;

import static eu.ggnet.dwoss.rules.TradeName.ONESELF;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReportLineTest {

    private final static Date NOW = new Date();

    @Test
    public void testRepayment() {
        Report report = new Report("TestReport", ONESELF, NOW, NOW);

        ReportLine line1 = ReportLine.builder()
                .documentType(DocumentType.INVOICE).documentId(1).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(100).tax(0.19)
                .build();

        report.add(line1);

        assertFalse(line1.isPartialRepayed());
        // Creditmemo unitAnnex.
        ReportLine line2 = ReportLine.builder()
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(2).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT_ANNEX).name("Unit-123").refurbishId("123").amount(1).price(-10).tax(0.19)
                .build();

        line1.add(line2);
        report.add(line2);

        assertTrue(line2.isPartialRepayment());

        assertFalse(line1.isFullRepayed());
        assertTrue(line1.isPartialRepayed());

        // Now add A Unit.
        ReportLine line3 = ReportLine.builder()
                .documentType(DocumentType.ANNULATION_INVOICE).documentId(3).dossierId(1).customerId(1)
                .positionType(PositionType.UNIT).name("Unit-123").refurbishId("123").amount(1).price(-90).tax(0.19)
                .build();

        line1.add(line3);
        line2.add(line3);
        report.add(line3);

        assertTrue(line3.isFullRepayment());

        assertTrue(line2.isFullRepayed());
        assertFalse(line2.isPartialRepayed());

        assertTrue(line1.isFullRepayed());
        assertFalse(line1.isPartialRepayed());
    }

}
