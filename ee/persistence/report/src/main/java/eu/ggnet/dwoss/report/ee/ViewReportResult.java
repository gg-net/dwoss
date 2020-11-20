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
package eu.ggnet.dwoss.report.ee;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.core.common.values.PositionType;
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
public class ViewReportResult implements Serializable {

    /**
     * Type of the result, each type is intended for a different table.
     */
    public enum Type {
        /**
         * Lines which should not be stored in the report, as future lines have an implaced here. e.g. a open complaint.
         */
        ACTIVE_INFO, /**
         * Lines which represent repayents.
         */
        REPAYMENTS, /**
         * Invoice Units with a MFG Date between report date and report date - 1 year.
         */
        UNDER_ONE_YEAR, /**
         * Invoice Units with a MFG Date older than report date - 1 year.
         */
        PAST_ONE_YEAR, /**
         * Lines which in sum result into zero.
         */
        REPORT_INFO, /**
         * Warrenty lines.
         */
        WARRENTY, /**
         * Invoiced elements.
         */
        INVOICED;

        /**
         * Retruns all Reportable.
         * <p>
         * @return all Reortable.
         */
        public static EnumSet<Type> allReportable() {
            return EnumSet.complementOf(EnumSet.of(ACTIVE_INFO));
        }
    }

    private final EnumMap<Type, NavigableSet<ReportLine>> lines;

    private final ReportParameter parameter;

    public ViewReportResult(EnumMap<Type, NavigableSet<ReportLine>> lines, ReportParameter parameter) {
        this.lines = lines;
        this.parameter = parameter;
    }

    public EnumMap<Type, NavigableSet<ReportLine>> getLines() {
        return lines;
    }

    public ReportParameter getParameter() {
        return parameter;
    }

    public NavigableSet<ReportLine> getAllLines() {
        return lines.values().stream().flatMap((x) -> x.stream()).collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    public static ViewReportResult fromReport(Report report) {
        Set<ReportLine> warranties
                = // This collects all warranties.
                report.getLines().stream().filter((eu.ggnet.dwoss.report.ee.entity.ReportLine line) -> line.getPositionType() == PositionType.PRODUCT_BATCH && line.getReference(SingleReferenceType.WARRANTY) != null).collect(Collectors.toCollection(() -> new TreeSet<>()));
        Set<ReportLine> units = report.getLines();
        units.removeAll(warranties);
        EnumMap<ViewReportResult.Type, NavigableSet<ReportLine>> lines = new EnumMap<>(ViewReportResult.Type.class);
        lines.put(REPAYMENTS, ReportUtil.filterRepayed(units));
        lines.put(REPORT_INFO, ReportUtil.filterReportInfo(units));
        if ( report.getViewMode() == ViewMode.YEARSPLITT_AND_WARRANTIES ) {
            YearSplit split = ReportUtil.filterInvoicedSplit(units, report.getStartingDate());
            lines.put(UNDER_ONE_YEAR, split.before);
            lines.put(PAST_ONE_YEAR, split.after);
            lines.put(WARRENTY, ReportUtil.filterInvoiced(warranties));
            lines.get(REPAYMENTS).addAll(ReportUtil.filterRepayed(warranties));
            lines.get(REPORT_INFO).addAll(ReportUtil.filterReportInfo(warranties));
        } else {
            lines.put(INVOICED, ReportUtil.filterInvoiced(units));
        }
        return new ViewReportResult(lines, new ReportParameter.Builder().contractor(report.getType()).viewMode(report.getViewMode()).reportName(report.getName()).start(report.getStartingDate()).end(report.getEndingDate()).build());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
