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
package eu.ggnet.dwoss.report;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Remote;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.report.assist.ReportUtil;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.Report.ViewMode;
import eu.ggnet.dwoss.report.entity.Report.YearSplit;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.ReportLine.SingleReferenceType;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.persistence.RemoteAgent;
import eu.ggnet.dwoss.util.validation.ValidationUtil;

import lombok.experimental.Builder;

import static eu.ggnet.dwoss.report.assist.ReportUtil.*;

import lombok.*;

import static eu.ggnet.dwoss.report.ReportAgent.ViewReportResult.Type.*;
import static eu.ggnet.dwoss.report.entity.Report.ViewMode.DEFAULT;
import static eu.ggnet.dwoss.report.entity.Report.ViewMode.YEARSPLITT_AND_WARRANTIES;
import static eu.ggnet.dwoss.rules.PositionType.*;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface ReportAgent extends RemoteAgent {

    @Value
    public static class ReportParameter implements Serializable {

        public ReportParameter(Report report) {
            this(report.getType(), report.getViewMode(), report.getName(), report.getStartingDate(), report.getEndingDate());
        }

        @Builder
        public ReportParameter(TradeName contractor, ViewMode viewMode, String reportName, Date start, Date end) {
            this.contractor = contractor;
            this.viewMode = (viewMode == null ? DEFAULT : viewMode);
            this.reportName = reportName;
            this.start = start;
            this.end = end;
            ValidationUtil.validate(this);
        }

        @NotNull
        private final TradeName contractor;

        @NotNull
        private final Report.ViewMode viewMode;

        @NotNull
        @Size(min = 1)
        private final String reportName;

        @NotNull
        private final Date start;

        @NotNull
        private final Date end;

        /**
         * Creates a new Report Instance based on the Parameter.
         * <p>
         * @return a new report.
         */
        public Report toNewReport() {
            return new Report(reportName, contractor, start, end, viewMode);
        }

    }

    /**
     * Kill that name as soon as Possible (old ReportResult is gone)
     */
    @Value
    public static class ViewReportResult implements Serializable {

        /**
         * Type of the result, each type is intended for a different table.
         */
        public enum Type {

            /**
             * Lines which should not be stored in the report, as future lines have an implaced here. e.g. a open complaint.
             */
            ACTIVE_INFO,
            /**
             * Lines which represent repayents.
             */
            REPAYMENTS,
            /**
             * Invoice Units with a MFG Date between report date and report date - 1 year.
             */
            UNDER_ONE_YEAR,
            /**
             * Invoice Units with a MFG Date older than report date - 1 year.
             */
            PAST_ONE_YEAR,
            /**
             * Lines which in sum result into zero.
             */
            REPORT_INFO,
            /**
             * Warrenty lines.
             */
            WARRENTY,
            /**
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

        public NavigableSet<ReportLine> getAllLines() {
            return lines.values().stream().flatMap(x -> x.stream()).collect(Collectors.toCollection(() -> new TreeSet<>()));
        }

        /**
         * Returns a copy of all relevant report lines.
         * A ReportLine is relevant if {@link ReportLine#addedToReportProperty} isd true.
         * <p>
         * @return a copy of all relevant report lines.
         */
        public EnumMap<Type, NavigableSet<ReportLine>> getRelevantLines() {
            EnumMap<Type, NavigableSet<ReportLine>> copy = new EnumMap<>(Type.class);
            for (Type keySet : Type.allReportable()) {
                if ( this.getLines().get(keySet) == null ) continue;
                copy.put(keySet, this.getLines().get(keySet).stream().filter((t) -> t.isAddedToReport()).collect(Collectors.toCollection(() -> new TreeSet<>())));
            }
            return copy;
        }

        public static ViewReportResult fromReport(Report report) {
            Set<ReportLine> warranties = report.getLines().stream()
                    // This collects all warranties.
                    .filter(line -> line.getPositionType() == PRODUCT_BATCH && line.getReference(SingleReferenceType.WARRANTY) != null)
                    .collect(Collectors.toCollection(() -> new TreeSet<>()));
            Set<ReportLine> units = report.getLines();
            units.removeAll(warranties);

            EnumMap<ViewReportResult.Type, NavigableSet<ReportLine>> lines = new EnumMap<>(ViewReportResult.Type.class);
            lines.put(REPAYMENTS, ReportUtil.filterRepayed(units));
            lines.put(REPORT_INFO, ReportUtil.filterReportInfo(units));
            if ( report.getViewMode() == YEARSPLITT_AND_WARRANTIES ) {
                YearSplit split = ReportUtil.filterInvoicedSplit(units, report.getStartingDate());
                lines.put(UNDER_ONE_YEAR, split.getBefore());
                lines.put(PAST_ONE_YEAR, split.getAfter());
                lines.put(WARRENTY, filterInvoiced(warranties));
                lines.get(REPAYMENTS).addAll(filterRepayed(warranties));
                lines.get(REPORT_INFO).addAll(filterReportInfo(warranties));
            } else {
                lines.put(INVOICED, ReportUtil.filterInvoiced(units));
            }
            return new ViewReportResult(lines, new ReportParameter(report));
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchParameter implements Serializable {

        private String refurbishId;

        public boolean isEmpty() {
            return StringUtils.isBlank(refurbishId);
        }
    }

    public List<SimpleReportLine> findSimple(SearchParameter search, int firstResult, int maxResults);

    public List<ReportLine> find(SearchParameter search, int firstResult, int maxResults);

    public long count(SearchParameter search);

    /**
     * Prepares a report for view and storage.
     * <p>
     * @param p              the parameter of the report
     * @param loadUnreported should unreported values earlier than start be included.
     * @return a result based on the parameters.
     */
    ViewReportResult prepareReport(ReportParameter p, boolean loadUnreported);

    /**
     * Returns all ReportLines, which have the given document type id from to till the given Dates
     * <p/>
     * @param type the document type
     * @param till the date as upper border
     * @param from the date as lower border
     * @return the matching report lines.
     */
    List<ReportLine> findReportLinesByDocumentType(DocumentType type, Date from, Date till);

    /**
     * Stores a new report, persisting the report and merging the lines.
     * <p/>
     * @param report     the report to persist.
     * @param storeables the lines to merge.
     * @return the persisted report.
     */
    Report store(Report report, Collection<ReportLine.Storeable> storeables);

    /**
     * Returns a ReportResult build from an existing Report.
     * <p>
     * @param reportId the id of the exisitng report
     * @return a ReportResult build from an existing Report or null if nothing is found.
     */
    ViewReportResult findReportResult(long reportId);

    /**
     * Returns all ReportLines, limited by first and max ordered by reportDate descending.
     * <p>
     * @param firstResult the first result to return
     * @param maxResults  the maximum results to return
     * @return all ReportLines, limitied by first and max ordered by reportDate descending.
     */
    public List<ReportLine> findAllReportLinesReverse(int firstResult, int maxResults);

    /**
     * Attaches "dangling" {@link ReportLine}<code>s</code> of {@link DocumentType#COMPLAINT} to existing Reports of contractor. A ReportLine is considered
     * "dangling" if:
     * <ul>
     * <li>It is of {@link PositionType#UNIT}</li>
     * <li>It is not in a report of the contractor</li>
     * <li>It references a {@link ReportLine} of {@link DocumentType#ANNULATION_INVOICE} or {@link DocumentType#CREDIT_MEMO} which is already in a report of the
     * contractor.</li>
     * <li></li>
     * </ul>
     * These "dangling" {@link ReportLine}<code>s</code> are added to the Report that contains the referencing {@link ReportLine} of
     * {@link DocumentType#ANNULATION_INVOICE} or {@link DocumentType#CREDIT_MEMO}.
     * <p>
     * @param type the type as report identifier.
     * @param till the upper limit.
     * @return the discovered and attached ReportLines.
     */
    public Set<ReportLine> attachDanglingComplaints(TradeName type, Date till);

}
