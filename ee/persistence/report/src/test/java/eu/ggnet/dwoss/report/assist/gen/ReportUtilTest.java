package eu.ggnet.dwoss.report.assist.gen;

import eu.ggnet.dwoss.rules.DocumentType;

import java.text.DecimalFormat;
import java.util.*;

import org.apache.commons.lang3.time.DateUtils;

import static eu.ggnet.dwoss.rules.TradeName.HP;
import static org.junit.Assert.*;

import org.junit.Test;

import eu.ggnet.dwoss.report.assist.ReportUtil;
import eu.ggnet.dwoss.report.entity.Report.YearSplit;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.ReportLine.SingleReferenceType;

import static eu.ggnet.dwoss.rules.DocumentType.*;
import static eu.ggnet.dwoss.rules.PositionType.*;
import static org.hamcrest.CoreMatchers.hasItem;

/**
 *
 * @author pascal.perau
 */
public class ReportUtilTest {

    private final DecimalFormat format = new DecimalFormat("0000");

    private final Date now = new Date();

    @Test
    public void testFilterInvoiceSplit() {
        List<ReportLine> lines = new ArrayList<>();

        //One invoice with mfg date today and one with mfg date two years in the past
        lines.add(makeUnitReportLine(now, 0, INVOICE));
        lines.add(makeUnitReportLine(DateUtils.addYears(now, -2), lines.get(0).getDossierId(), INVOICE));

        // Test if the year split is working correctly
        YearSplit split = ReportUtil.filterInvoicedSplit(lines, now);

        assertThat(split.getBefore(), hasItem(lines.get(0)));
        assertTrue("Expect DW0002 to be present but was: " + out(split.getBefore()), split.getBefore().contains(lines.get(0)));
        assertTrue("Expect DW0001 to be present but was: " + out(split.getAfter()), split.getAfter().contains(lines.get(1)));
    }

    @Test
    public void testFilterRepayed() {
        List<ReportLine> lines = new ArrayList<>();

        //One invoice unit line in the report with a referenced annulation invoice line
        ReportLine invoice1 = makeUnitReportLine(now, 0, INVOICE);
        lines.add(invoice1);
        lines.add(annulationForInvoice(invoice1, false));

        //One invoice unit not in the report with a referenced annulation invoice line
        ReportLine invoice2 = makeUnitReportLine(now, invoice1.getDossierId(), INVOICE);
        lines.add(annulationForInvoice(invoice2, false));

        //One invoice unit in the report with a referenced partial annulation invoice line
        ReportLine invoice3 = makeUnitReportLine(now, invoice2.getDossierId(), INVOICE);
        lines.add(invoice3);
        lines.add(annulationForInvoice(invoice3, true));

        //One invoice unit_annex not in the report with a referenced partial annulation invoice line
        ReportLine invoice4 = makeUnitReportLine(now, invoice3.getDossierId(), INVOICE);
        lines.add(annulationForInvoice(invoice4, true));

        Set filtered = ReportUtil.filterRepayed(lines);
        assertTrue("Expect DW0002 and DW0004 to be present but was: " + out(filtered),
                filtered.containsAll(
                        Arrays.asList(
                                invoice2.getSingleReference(ANNULATION_INVOICE),
                                invoice4.getSingleReference(ANNULATION_INVOICE)
                        )
                )
        );

    }

    @Test
    public void testFilterReportInfo() {
        List<ReportLine> lines = new ArrayList<>();
        lines.add(makeUnitReportLine(now, 0, CAPITAL_ASSET));
        lines.add(makeUnitReportLine(now, 1, INVOICE));
        lines.add(makeUnitReportLine(now, 2, COMPLAINT));

        //annulation line wich references a invoice line in another report
        ReportLine invoice = makeUnitReportLine(now, 3, INVOICE);
        lines.add(annulationForInvoice(invoice, false));

        //annulation line wich references a invoice line in the same report
        ReportLine invoice2 = makeUnitReportLine(now, 4, INVOICE);
        lines.add(invoice2);
        lines.add(annulationForInvoice(invoice2, false));

        Set filtered = ReportUtil.filterReportInfo(lines);
        assertTrue("Expect DW0003 and DW0004 twice to be present but was: " + out(filtered),
                filtered.containsAll(
                        Arrays.asList(
                                lines.get(2),
                                invoice2,
                                invoice2.getSingleReference(ANNULATION_INVOICE)
                        )
                )
        );

    }

    @Test
    public void testFilterWarranty() {
        List<ReportLine> lines = new ArrayList<>();

        //Add two invoice unit lines
        lines.add(makeUnitReportLine(now, 0, INVOICE));
        lines.add(makeUnitReportLine(now, 1, INVOICE));

        //Add two warranty lines for the invoice units
        lines.add(warrantyForInvoiceUnit(lines.get(0)));
        lines.add(warrantyForInvoiceUnit(lines.get(1)));

        //Add a annulation invoice line for the first invoice unit
        lines.add(annulationForInvoice(lines.get(0), false));

        Set filtered = ReportUtil.filterWarrenty(lines, lines);
        assertTrue("Expect DW0001 and DW0002 to be present but was: " + out(filtered),
                filtered.containsAll(
                        Arrays.asList(
                                lines.get(2),
                                lines.get(3)
                        )
                )
        );
    }

    private String out(Collection<ReportLine> lines) {
        StringBuilder sb = new StringBuilder();
        for (ReportLine line : lines) {
            sb.append("ReportLine{dossierIdentifier=").append(line.getDossierIdentifier()).append("} ");
        }
        return sb.toString();
    }

    /**
     * Create an annulation invoice reportline referenced to a invoice report line.
     * <p>
     * @param invoiceLine      the referenced report line
     * @param partialRepayment does the annulation represent a partial repayment (UNIT/UNIT_ANNEX)
     * @return an annulation invoice reportline referenced to a invoice report line
     */
    private ReportLine annulationForInvoice(ReportLine invoiceLine, boolean partialRepayment) {
        if ( invoiceLine == null || DocumentType.INVOICE != invoiceLine.getDocumentType() ) return null;
        ReportLine annulationLine = ReportLine
                .builder()
                .mfgDate(invoiceLine.getMfgDate())
                .dossierId(invoiceLine.getDossierId())
                .dossierIdentifier("DW" + format.format(invoiceLine.getDossierId()))
                .documentType(ANNULATION_INVOICE)
                .positionType(partialRepayment ? UNIT_ANNEX : invoiceLine.getPositionType())
                .build();
        invoiceLine.add(annulationLine);
        return annulationLine;
    }

    /**
     * Create an warranty reportline referenced to a invoice report line.
     * <p>
     * @param invoiceLine the referenced report line
     * @return an warranty reportline referenced to a invoice report line
     */
    private ReportLine warrantyForInvoiceUnit(ReportLine invoiceLine) {
        if ( invoiceLine == null || DocumentType.INVOICE != invoiceLine.getDocumentType() ) return null;
        ReportLine warranty = ReportLine.builder()
                .mfgDate(invoiceLine.getMfgDate())
                .dossierId(invoiceLine.getDossierId())
                .dossierIdentifier("DW" + format.format(invoiceLine.getDossierId()))
                .documentType(invoiceLine.getDocumentType())
                .refurbishId(invoiceLine.getRefurbishId())
                .positionType(PRODUCT_BATCH)
                .partNo("XX.YYYYY.ZZZ")
                .build();
        warranty.setContractor(invoiceLine.getContractor());
        warranty.setReportingDate(invoiceLine.getReportingDate());
        invoiceLine.setReference(SingleReferenceType.WARRANTY, warranty);
        return warranty;
    }

    /**
     * Generates a invoice report line for a unit.
     * <p>
     * @param mfgDate       the manufavcturing date from the unit
     * @param lastDossierId the last used dossier id
     * @param dType         the document type of the reportline
     * @return a invoice report line for a unit.
     */
    private ReportLine makeUnitReportLine(Date mfgDate, long lastDossierId, DocumentType dType) {
        ReportLine build = ReportLine
                .builder()
                .mfgDate(mfgDate)
                .dossierId(lastDossierId + 1)
                .dossierIdentifier("DW" + format.format(lastDossierId + 1))
                .documentType(dType)
                .positionType(UNIT)
                .build();
        build.setReportingDate(now);
        build.setContractor(HP);
        return build;
    }
}
