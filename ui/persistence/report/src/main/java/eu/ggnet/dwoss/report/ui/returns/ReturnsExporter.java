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
package eu.ggnet.dwoss.report.ui.returns;

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

import eu.ggnet.dwoss.report.ee.entity.ReportLine;

import static eu.ggnet.lucidcalc.CFormat.HorizontalAlignment.RIGHT;
import static eu.ggnet.lucidcalc.CFormat.Representation.CURRENCY_EURO;

public class ReturnsExporter {

    public final static CFormat EURO = new CFormat(RIGHT, CURRENCY_EURO);

    /**
     * A Block of Lucidcalc elements that represent a Result Block.
     */
    public static class SResult {

        public SBlock block;

        public SCell sum1;

        public SCell sum2;
    }

    public static File returnsToXls(List<ReportLine> lines) {
        List<Object[]> linesData = new ArrayList<>();
        for (ReportLine reportLine : lines) {
            Object[] data = new Object[]{
                reportLine.getDossierIdentifier(),
                reportLine.getActual(),
                reportLine.getRefurbishId(),
                reportLine.getPartNo(),
                reportLine.getName(),
                reportLine.getSerial(),
                reportLine.getMfgDate(),
                reportLine.getReportingDate()
            };
            linesData.add(data);
        }
        STable newTable = new STable();
        newTable.setTableFormat(new CFormat("Verdana", 10, new CBorder(Color.BLACK, CBorder.LineStyle.THIN)));
        newTable.setHeadlineFormat(new CFormat(CFormat.FontStyle.BOLD, Color.BLACK, Color.YELLOW, CFormat.HorizontalAlignment.CENTER, CFormat.VerticalAlignment.MIDDLE));
        newTable.add(new STableColumn("Auftrag", 15));
        newTable.add(new STableColumn("Datum", 10, new CFormat(CFormat.Representation.SHORT_DATE)));
        newTable.add(new STableColumn("SopoNr", 10));
        newTable.add(new STableColumn("ArtikelNr", 15));
        newTable.add(new STableColumn("Bezeichnung", 40));
        newTable.add(new STableColumn("Seriennummer", 35));
        newTable.add(new STableColumn("MFGDate", 10, new CFormat(CFormat.Representation.SHORT_DATE)));
        newTable.add(new STableColumn("reported Am", 10, new CFormat(CFormat.Representation.SHORT_DATE)));
        newTable.setModel(new STableModelList<>(linesData));
        STable table = new STable(newTable);
        table.setModel(new STableModelList<>(linesData));
        ReturnsExporter.SResult summary = createReturnsSummary(table, lines.size());
        SBlock block = new SBlock();
        block.setFormat(new CFormat(Color.BLACK, Color.LIGHT_GRAY, CFormat.HorizontalAlignment.LEFT));
        block.add(new SCell("Rückläufer"), "");
        CSheet sheet = new CSheet("Rückläufer Report");
        sheet.setShowGridLines(false);
        sheet.addBelow(block);
        sheet.addBelow(newTable);
        sheet.addBelow(4, 1, summary.block);
        CCalcDocument doc = new TempCalcDocument("Rückläufer");
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
    static ReturnsExporter.SResult createReturnsSummary(STable table, int amount) {
        ReturnsExporter.SResult r = new ReturnsExporter.SResult();
        r.block = new SBlock();
        r.block.setFormat(new CFormat(CFormat.FontStyle.BOLD, Color.BLACK, Color.YELLOW, CFormat.HorizontalAlignment.RIGHT, new CBorder(Color.BLACK)));
        r.sum1 = new SCell(new SFormula("SUMME(", amount, "*", 10, ")"), EURO);
        r.sum2 = new SCell(amount);
        r.block.add(r.sum1, r.sum2);
        return r;
    }
}
