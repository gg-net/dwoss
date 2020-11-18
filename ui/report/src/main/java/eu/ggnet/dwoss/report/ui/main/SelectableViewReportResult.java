/*
 * Copyright (C) 2019 GG-Net GmbH
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
package eu.ggnet.dwoss.report.ui.main;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.report.ee.ReportParameter;
import eu.ggnet.dwoss.report.ee.ViewReportResult;
import eu.ggnet.dwoss.report.ee.ViewReportResult.Type;
import eu.ggnet.dwoss.report.ee.assist.ReportUtil;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.Report.ViewMode;
import eu.ggnet.dwoss.report.ee.entity.Report.YearSplit;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType;

import static eu.ggnet.dwoss.report.ee.ViewReportResult.Type.*;

/**
 * Kill that name as soon as Possible (old ReportResult is gone)
 */
public class SelectableViewReportResult implements Serializable {

    private final EnumMap<Type, NavigableSet<SelectableReportLine>> lines;

    private final ReportParameter parameter;

    public SelectableViewReportResult(ViewReportResult in) {
        this.parameter = in.getParameter();
        lines = new EnumMap<>(Type.class);
        in.getLines().forEach((Type type, NavigableSet<ReportLine> set) -> lines.put(type, SelectableReportLine.wrap(set)));
    }

    public SelectableViewReportResult(EnumMap<Type, NavigableSet<SelectableReportLine>> lines, ReportParameter parameter) {
        this.lines = lines;
        this.parameter = parameter;
    }

    public EnumMap<Type, NavigableSet<SelectableReportLine>> getLines() {
        return lines;
    }

    public ReportParameter getParameter() {
        return parameter;
    }

    public NavigableSet<SelectableReportLine> getAllLines() {
        return lines.values().stream().flatMap((x) -> x.stream()).collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    public static SelectableViewReportResult fromReport(Report report) {
        Set<ReportLine> warranties
                = // This collects all warranties.
                report.getLines().stream().filter((eu.ggnet.dwoss.report.ee.entity.ReportLine line) -> line.getPositionType() == PositionType.PRODUCT_BATCH && line.getReference(SingleReferenceType.WARRANTY) != null).collect(Collectors.toCollection(() -> new TreeSet<>()));
        Set<ReportLine> units = report.getLines();
        units.removeAll(warranties);
        EnumMap<ViewReportResult.Type, NavigableSet<SelectableReportLine>> lines = new EnumMap<>(ViewReportResult.Type.class);
        lines.put(REPAYMENTS, SelectableReportLine.wrap(ReportUtil.filterRepayed(units)));
        lines.put(REPORT_INFO, SelectableReportLine.wrap(ReportUtil.filterReportInfo(units)));
        if ( report.getViewMode() == ViewMode.YEARSPLITT_AND_WARRANTIES ) {
            YearSplit split = ReportUtil.filterInvoicedSplit(units, report.getStartingDate());
            lines.put(UNDER_ONE_YEAR, SelectableReportLine.wrap(split.before));
            lines.put(PAST_ONE_YEAR, SelectableReportLine.wrap(split.after));
            lines.put(WARRENTY, SelectableReportLine.wrap(ReportUtil.filterInvoiced(warranties)));
            lines.get(REPAYMENTS).addAll(SelectableReportLine.wrap(ReportUtil.filterRepayed(warranties)));
            lines.get(REPORT_INFO).addAll(SelectableReportLine.wrap(ReportUtil.filterReportInfo(warranties)));
        } else {
            lines.put(INVOICED, SelectableReportLine.wrap(ReportUtil.filterInvoiced(units)));
        }
        return new SelectableViewReportResult(lines, new ReportParameter.Builder().contractor(report.getType()).viewMode(report.getViewMode()).reportName(report.getName()).start(report.getStartingDate()).end(report.getEndingDate()).build());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
