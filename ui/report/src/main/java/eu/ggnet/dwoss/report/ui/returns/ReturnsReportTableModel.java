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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import javax.swing.table.AbstractTableModel;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;

//////////////////////////////////////////////////////////////////////////////////////////////////////
//                                                                                                  //
// TODO/FIXME: This class just sucks, do not try to enhance it, just fix small bugs or reimplement. //
//                                                                                                  //
//////////////////////////////////////////////////////////////////////////////////////////////////////
public abstract class ReturnsReportTableModel extends AbstractTableModel {

    private final Object[][] columns;

    private String reportName;

    private TradeName reportType;

    private Date reportStart;

    private Date reportEnd;

    protected final List<TableLine> lines = new ArrayList<>();

    public ReturnsReportTableModel(Object[][] columns) {
        this.columns = columns;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public TradeName getReportType() {
        return reportType;
    }

    public void setReportType(TradeName reportType) {
        this.reportType = reportType;
    }

    public Date getReportStart() {
        return reportStart;
    }

    public void setReportStart(Date reportStart) {
        this.reportStart = reportStart;
    }

    public Date getReportEnd() {
        return reportEnd;
    }

    public void setReportEnd(Date reportEnd) {
        this.reportEnd = reportEnd;
    }

    public List<TableLine> getLines() {
        return lines;
    }

    @Override
    public int getRowCount() {
        return getLines().size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    public void clear() {
        getLines().clear();
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return (String)columns[column][0];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if ( getColumnCount() - 1 == columnIndex ) return Boolean.class;
        return String.class;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if ( getColumnCount() - 1 != columnIndex ) return;
        getLines().get(rowIndex).setShouldReported((boolean)aValue);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // The last column is allways editable.
        // TODO: This should be disabled in case of view mode, but who cares.
        return columnIndex == columns.length - 1;
    }

    public void add(TableLine line) {
        this.lines.add(line);
        fireTableRowsInserted(lines.size() - 2, lines.size() - 1);
    }

    public void addAll(List<TableLine> lines) {
        for (TableLine reportTableLine : lines) {
            add(reportTableLine);
        }
    }

    public List<ReportLine> getSelectedLines() {
        ArrayList<ReportLine> selectedLines = new ArrayList<>();
        for (TableLine reportTableLine : getLines()) {
            if ( reportTableLine.isShouldReported() ) selectedLines.add(reportTableLine.getReportLine());
        }
        return selectedLines;
    }

    /**
     *
     * @param lines the value of lines
     */
    public void setReportLines(Collection<ReportLine> lines) {
        this.clear();
        for (ReportLine reportLine : lines) {
            this.add(new TableLine(reportLine));
        }
        fireTableDataChanged();
    }

    public List<TableLine> getSelectedTableLines() {
        ArrayList<TableLine> selectedLines = new ArrayList<>();
        for (TableLine reportTableLine : getLines()) {
            if ( reportTableLine.isShouldReported() ) selectedLines.add(reportTableLine);
        }
        return selectedLines;
    }

    public static String formatCurrency(double currency) {
        NumberFormat format = new DecimalFormat(",##0.00");
        return format.format(currency) + " €";
    }

    public static String formatPercentage(double percentage) {
        if ( percentage == 0d || Double.isNaN(percentage) ) return "0 %";
        NumberFormat format = new DecimalFormat("#0.00");
        return format.format(percentage * 100) + " %";
    }
}
