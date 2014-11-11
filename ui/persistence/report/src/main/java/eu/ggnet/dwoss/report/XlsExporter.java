/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.report;

import eu.ggnet.lucidcalc.SUtil;
import eu.ggnet.lucidcalc.SBlock;
import eu.ggnet.lucidcalc.STableModelList;
import eu.ggnet.lucidcalc.CSheet;
import eu.ggnet.lucidcalc.CBorder;
import eu.ggnet.lucidcalc.SCell;
import eu.ggnet.lucidcalc.SFormula;
import eu.ggnet.lucidcalc.STable;
import eu.ggnet.lucidcalc.STableColumn;
import eu.ggnet.lucidcalc.LucidCalcWriter;
import eu.ggnet.lucidcalc.LucidCalc;
import eu.ggnet.lucidcalc.CFormat;
import eu.ggnet.lucidcalc.CCalcDocument;
import eu.ggnet.lucidcalc.TempCalcDocument;

import java.awt.Color;
import java.io.File;
import java.util.*;

import eu.ggnet.dwoss.report.ReportAgent.ReportParameter;
import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult;
import eu.ggnet.dwoss.report.ReportAgent.ViewReportResult.Type;

import eu.ggnet.dwoss.report.entity.ReportLine;

import static eu.ggnet.lucidcalc.CFormat.FontStyle.BOLD;
import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.*;
import static eu.ggnet.lucidcalc.CFormat.Representation.*;
import static eu.ggnet.lucidcalc.CFormat.VerticalAlignment.MIDDLE;
import static eu.ggnet.lucidcalc.SUtil.SR;
import static eu.ggnet.dwoss.util.DateFormats.ISO;

/**
 *
 * @author oliver.guenther
 */
public class XlsExporter {

    /**
     * A Block of Lucidcalc elements that represent a Result Block.
     */
    public static class SResult {

        public SBlock block;

        public SCell sum1;

        public SCell sum2;

        public SCell sum3;

        public SCell sum4;
    }

    public final static CFormat EURO = new CFormat(RIGHT, CURRENCY_EURO);

    public static File toFullXls(ViewReportResult report) {
        STable template = new STable();
        template.setTableFormat(new CFormat("Verdana", 10, new CBorder(Color.BLACK, CBorder.LineStyle.THIN)));
        template.setHeadlineFormat(new CFormat(BOLD, Color.BLACK, Color.YELLOW, CENTER, MIDDLE));
        template.add(new STableColumn("Datum", 10, new CFormat(SHORT_DATE)));
        template.add(new STableColumn("SopoNr", 10));
        template.add(new STableColumn("ArtikelNr", 15));
        template.add(new STableColumn("Bezeichnung", 40));
        template.add(new STableColumn("Seriennummer", 32));
        template.add(new STableColumn("CP", 15, EURO));
        template.add(new STableColumn("VK", 15, EURO));
        template.add(new STableColumn("%CP", 12, new CFormat(RIGHT, PERCENT_FLOAT)).setAction(SUtil.getSelfRow()));
        template.add(new STableColumn("EK", 15, EURO));
        template.add(new STableColumn("Marge", 12, new CFormat(RIGHT, CURRENCY_EURO)).setAction(SUtil.getSelfRow()));
        template.add(new STableColumn("%Marge", 12, new CFormat(RIGHT, PERCENT_FLOAT)));
        template.add(new STableColumn("MFGDate", 10, new CFormat(SHORT_DATE)));
        template.add(new STableColumn("CID", 12));
        template.add(new STableColumn("Firma", 35));
        template.add(new STableColumn("Name", 30));
        template.add(new STableColumn("Rechnungsadresse", 60));
        template.add(new STableColumn("Bemerkung", 50));

        ReportParameter parameter = report.getParameter();

        CSheet sheet = new CSheet(parameter.getReportName());

        for (Type type : report.getLines().keySet()) {
            sheet.addBelow(new SBlock(type.name(), new CFormat(BOLD), true));
            STable table = new STable(template);
            table.setModel(new STableModelList<>(toLucidModel(report.getLines().get(type))));
            sheet.addBelow(table);
            SResult summary = createSummary(table, parameter.getStart(), parameter.getEnd());
            sheet.addBelow(4, 1, summary.block);
        }

// Construct to create a summary for multivple tables.
//
//     SBlock summary = new SBlock();
//        summary.setFormat(new CFormat(BOLD, Color.BLACK, Color.YELLOW, RIGHT, new CBorder(Color.BLACK)));
//        SCell sum1 = new SCell(new SFormula(newSummary.sum1, "+", oldSummary.sum1), EURO);
//        SCell sum2 = new SCell(new SFormula(newSummary.sum2, "+", oldSummary.sum2), EURO);
//        summary.add("Summe", new CFormat(Color.BLUE, Color.WHITE, LEFT),
//                sum1,
//                sum2,
//                new SFormula(sum2, "/", sum1), new CFormat(PERCENT_FLOAT),
//                new SFormula(newSummary.sum3, "+", oldSummary.sum3), EURO,
//                new SFormula(newSummary.sum4, "+", oldSummary.sum4), EURO);
//
        CCalcDocument doc = new TempCalcDocument(parameter.getReportName() + "_");
        doc.add(sheet);
        LucidCalcWriter writer = LucidCalc.createWriter(LucidCalc.Backend.XLS);
        return writer.write(doc);
    }

    /**
     * Create the Summary Block at the End.
     * <p/>
     * @param table        The Stable where all the data exist.
     * @param startingDate the startnig date of the Report.
     * @param endingDate   the ending date of the Report.
     * @return a SResult Block with the Summary.
     */
    private static SResult createSummary(STable table, Date startingDate, Date endingDate) {

        SResult r = new SResult();
        r.block = new SBlock();
        r.block.setFormat(new CFormat(BOLD, Color.BLACK, Color.YELLOW, RIGHT, new CBorder(Color.BLACK)));
        r.sum1 = new SCell(new SFormula("SUMME(", table.getCellFirstRow(5), ":", table.getCellLastRow(5), ")"), EURO);
        r.sum2 = new SCell(new SFormula("SUMME(", table.getCellFirstRow(6), ":", table.getCellLastRow(6), ")"), EURO);
        r.sum3 = new SCell(new SFormula("SUMME(", table.getCellFirstRow(8), ":", table.getCellLastRow(8), ")"), EURO);
        r.sum4 = new SCell(new SFormula("SUMME(", table.getCellFirstRow(9), ":", table.getCellLastRow(9), ")"), EURO);
        r.block.add("Vom " + ISO.format(startingDate) + " bis " + ISO.format(endingDate),
                new CFormat(Color.BLUE, Color.WHITE, LEFT), r.sum1, r.sum2, new SFormula(r.sum2, "/", r.sum1), new CFormat(PERCENT_FLOAT), r.sum3, r.sum4);
        return r;
    }

    private static List<Object[]> toLucidModel(Collection<ReportLine> newLines) {
        TreeSet<ReportLine> resorted = new TreeSet<>(Comparator.comparing(ReportLine::getRefurbishId));
        resorted.addAll(newLines == null ? new ArrayList<>() : newLines);
        List<Object[]> newLinesData = new ArrayList<>();
        for (ReportLine line : resorted) {
            Object[] data = new Object[]{
                line.getActual(),
                line.getRefurbishId(),
                line.getPartNo(),
                line.toName(),
                line.getSerial(),
                line.getManufacturerCostPrice(),
                line.getPrice(),
                new SFormula(SR(6), "/", SR(5)),
                line.getPurchasePrice(),
                new SFormula(SR(6), "-", SR(8)),
                line.getMarginPercentage(),
                line.getMfgDate(),
                line.getCustomerId(),
                line.getCustomerCompany(),
                line.getCustomerName(),
                line.getInvoiceAddress(),
                line.getDocumentTypeName()
                + (line.getWorkflowStatus() == ReportLine.WorkflowStatus.DEFAULT ? "" : ", " + line.getWorkflowStatus())
                + ", Position:" + line.getPositionTypeName()
                + ", " + line.getDescription()
            };
            newLinesData.add(data);
        }
        return newLinesData;
    }

}
