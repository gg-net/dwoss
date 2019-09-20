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
package eu.ggnet.dwoss.report.ee;

import java.io.Serializable;
import java.util.*;

import javax.ejb.Remote;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.util.persistence.RemoteAgent;
import eu.ggnet.saft.api.Reply;


/**
 *
 * @author oliver.guenther
 */
@Remote
public interface ReportAgent extends RemoteAgent {

    public static class SearchParameter implements Serializable {

        private final String refurbishId;

        public SearchParameter(String refurbishId) {
            this.refurbishId = refurbishId;
        }

        public String getRefurbishId() {
            return refurbishId;
        }
        
        public boolean isEmpty() {
            return StringUtils.isBlank(refurbishId);
        }

        @Override
        public String toString() {
            return "SearchParameter{" + "refurbishId=" + refurbishId + '}';
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
     * Updates the comment of a ReportLine
     * If no instance could be found no changes will ba made.
     *
     * @param optLock
     * @param reportId primary key of the ReportLine to be updated.
     * @param comment  string to be set as new comment for the ReportLine
     * @return
     */
    boolean updateReportLineComment(int optLock, long reportId, String comment);

    /**
     * Updates the comment of a Report
     * If no instance could be found no changes will be made.
     *
     * @param key  the optimistic locking key
     * @param name string to be set as new comment for the Report
     * @return a reply, which on success has the name as payload.
     */
    Reply<String> updateReportName(Report.OptimisticKey key, String name);

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
