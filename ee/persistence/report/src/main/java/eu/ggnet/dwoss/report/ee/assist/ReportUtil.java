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
package eu.ggnet.dwoss.report.ee.assist;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.TradeName;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.*;

import eu.ggnet.dwoss.report.ee.entity.Report.YearSplit;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType;
import eu.ggnet.dwoss.report.ee.entity.ReportLine.WorkflowStatus;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;

import lombok.Value;

import static eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType.WARRANTY;
import static eu.ggnet.dwoss.rules.DocumentType.*;

/**
 *
 * @author bastian.venz
 */
public class ReportUtil {

    private final static Logger L = LoggerFactory.getLogger(ReportUtil.class);

    /**
     * Returns all Lines of the Report for Category Invoiced.
     * This consists of:
     * <ul>
     * <li>Position of Type Capital Asset</li>
     * <li>Position of Type Invoice, with no References</li>
     * <li>Position of Type UNIT_ANNEX in DocumentType CREDIT_MEMO/ANNULATIION_INVOICE and a Referencing Invoice in the same report.</li>
     * </ul>
     * <p>
     * It's not allowed to have a null value in the collection.
     * <p>
     * @param lines
     * @return all Lines of the Report for Category Invoiced.
     */
    //TODO: We could also substract the value of a unitannex from the invoice and not return the unit annex at all.
    //But consider the impact in the ui, especially if we allow selection of such a "combined" line.
    public static NavigableSet<ReportLine> filterInvoiced(Collection<ReportLine> lines) {
        NavigableSet<ReportLine> result = new TreeSet<>();
        for (ReportLine line : lines) {
            if ( line.getDocumentType() == CAPITAL_ASSET ) result.add(line);
            if ( line.getDocumentType() == INVOICE && !line.isFullRepayedIn(lines) ) result.add(line);
            if ( line.isPartialRepayment() && !line.isFullRepayedIn(lines) ) {
                ReportLine invoiceRef = line.getSingleReference(INVOICE);
                if ( invoiceRef == null ) /*  No Invoice exists, probably before 2014 */ result.add(line);
                else if ( lines.contains(invoiceRef) ) result.add(line);
            }
        }
        return result;
    }

    /**
     * Returns all Lines of the Report for Category Invoiced, split by mfgDate - startOfReport &lt; 1 year and the rest.
     * This consists of:
     * <ul>
     * <li>Position of Type Invoice, with no References</li>
     * <li>Position of Type UNIT_ANNEX in DocumentType CREDIT_MEMO/ANNULATIION_INVOICE and a Referencing Invoice in the same report.</li>
     * </ul>
     * <p>
     * It's not allowed to have a null value in the collection.
     * <p>
     * @param lines
     * @param startingDate
     * @return all Lines of the Report for Category Invoiced.
     */
    public static YearSplit filterInvoicedSplit(Collection<ReportLine> lines, Date startingDate) {
        NavigableSet<ReportLine> pastSplit = new TreeSet<>();
        NavigableSet<ReportLine> preSplit = new TreeSet<>();
        Date splitter = DateUtils.addYears(startingDate, -1);
        for (ReportLine line : filterInvoiced(lines)) {
            if ( splitter.before(line.getMfgDate()) ) {
                preSplit.add(line);
            } else {
                pastSplit.add(line);
            }
        }
        return new YearSplit(startingDate, preSplit, pastSplit);
    }

    /**
     * Returns all Lines of the Report which represent Positions of CreditMemos and Annulation Invoices but the associated Invoices have been reported before.
     * This consists of:
     * <ul>
     * <li>Position of Type UNIT in DocumentType CREDIT_MEMO/ANNULATIION_INVOICE and a Referencing Invoice is not in same report.</li>
     * <li>Position of Type UNIT_ANNEX in DocumentType CREDIT_MEMO/ANNULATIION_INVOICE and a Referencing Invoice is not in same report and no UNIT also in this
     * report</li>
     * </ul>
     * <p>
     * It's not allowed to have a null value in the collection.
     * <p>
     * @param lines
     * @return all Lines of the Report which represent Positions of CreditMemos and Annulation Invoices but the associated Invoices have been reported before.
     */
    public static NavigableSet<ReportLine> filterRepayed(Collection<ReportLine> lines) {
        NavigableSet<ReportLine> result = new TreeSet<>();
        for (ReportLine line : lines) {
            if ( line.isFullRepayment() ) {
                ReportLine invoiceRef = line.getSingleReference(DocumentType.INVOICE);
                if ( invoiceRef == null ) /*  No Invoice exists, probably before 2014 */ result.add(line);
                else if ( !lines.contains(invoiceRef) ) result.add(line);
            }
            if ( line.isPartialRepayment() && !line.isFullRepayedIn(lines) ) {
                ReportLine invoiceRef = line.getSingleReference(INVOICE);
                if ( invoiceRef == null ) /*  No Invoice exists, probably before 2014 */ result.add(line);
                else if ( !lines.contains(invoiceRef) ) result.add(line);
            }
        }
        return result;
    }

    /**
     * Returns all Reportlines, that don't have an impact on the result, but have only informational character.
     * <p>
     * It's not allowed to have a null value in the collection.
     * <p>
     * @param lines
     * @return all Reportlines, that don't have an impact on the result, but have only informational character.
     */
    public static NavigableSet<ReportLine> filterReportInfo(Collection<ReportLine> lines) {
        NavigableSet<ReportLine> result = new TreeSet<>(lines);
        result.removeAll(filterInvoiced(lines));
        result.removeAll(filterRepayed(lines));
        return result;
    }

    /**
     * Removes all Lines, that only represent active Info (open Complaints).
     * <ol>
     * <li>Sammle alle only Invoice Positions raus → Report</li>
     * <li>Sammle alle Repayment Positions raus → Report</li>
     * <li>Sammle alle Complaint Positionen die mit den Repayment Positionen zusammenhängen raus → Report</li>
     * <li>Sammle alle Compleints die DISCHARDED sind → Report</li>
     * <li>Alles was übrig ist, sollten (offene) Complaints sein → Active Info</li>
     * </ol>
     * <p>
     * It's not allowed to have a null value in the collection.
     * <p>
     * @param allLines   all lines.
     * @param reportType the report type
     * @return
     */
    public static PrepareReportPartition partition(Collection<ReportLine> allLines, TradeName reportType) {
        L.debug("filter {}", allLines);
        NavigableSet<ReportLine> reportAble = new TreeSet<>();
        for (ReportLine line : allLines) {
            L.debug("filter processing {}", line.toSimple());
            if ( !(line.getDocumentType() == DocumentType.ANNULATION_INVOICE
                   || line.getDocumentType() == DocumentType.CREDIT_MEMO
                   || line.getDocumentType() == DocumentType.CAPITAL_ASSET
                   || (line.getDocumentType() == DocumentType.COMPLAINT && line.getWorkflowStatus() == WorkflowStatus.DISCHARGED)
                   || (line.getDocumentType() == DocumentType.INVOICE && line.hasNoRepayments()) && line.hasNoOpenComplaints()) )
                continue;
            L.debug("filter processing, add to reportAble {}", line.toSimple());
            reportAble.add(line);
            Date tomorrow = DateUtils.addDays(line.getReportingDate(), 1);
            for (ReportLine ref : line.getRefrences()) {
                if ( ref.getDocumentType() == DocumentType.COMPLAINT && !ref.isInReport(reportType) ) {
                    L.debug("filter processing referencing complaints, add to reportAble {}", ref.toSimple());
                    reportAble.add(ref);
                } else if ( ref.getDocumentType() == DocumentType.INVOICE && !ref.isInReport(reportType) && ref.getReportingDate().before(tomorrow) ) {
                    L.debug("filter processing referencing invoices, add to reportAble {}", ref.toSimple());
                    reportAble.add(ref);
                }
            }
        }
        NavigableSet<ReportLine> activeInfo = new TreeSet<>(allLines);
        activeInfo.removeAll(reportAble);
        return new PrepareReportPartition(reportAble, activeInfo);
    }

    @Value
    public static class PrepareReportPartition {

        private final NavigableSet<ReportLine> reportAble;

        private final NavigableSet<ReportLine> activeInfo;

    }

    /**
     * Returns a set containing only non reportable lines that are not of the RETURNS type.
     * It's not allowed to have a null value in the collection.
     * <p>
     * @param allLines
     * @param reportAble
     * @return
     */
    public static NavigableSet<ReportLine> filterActiveInfo(Collection<ReportLine> allLines, Collection<ReportLine> reportAble) {
        TreeSet<ReportLine> treeSet = new TreeSet<>(allLines);
        treeSet.removeAll(reportAble);
        for (Iterator<ReportLine> it = treeSet.iterator(); it.hasNext();) {
            ReportLine reportLine = it.next();
            if ( reportLine.getDocumentType() == DocumentType.RETURNS ) it.remove();
        }
        return treeSet;

    }

    /**
     * Returns a Set of all Warrenty Positions in the Collection that is given to the method.
     * <p>
     * A Warranty is in the Set of Reportable Warranty if
     * <ul>
     * <li>SingleRefence from Type {@link SingleReferenceType#WARRANTY} is not null</li>
     * <li>SingleReferenced Unit is in the reportable Amount of ReportLines</li>
     * <li>Reporting Date is after the from Parameter and before the till Parameter</li>
     * </ul>
     * <p>
     * @param warrentyLines all unreported Reportlines that represent warrenty.
     * @param unitLines     all ReportLine's that are already in amount of Reportlines which should be reported.
     * @return all Warrentys which can be reported in this report.
     */
    public static NavigableSet<ReportLine> filterWarrenty(Collection<ReportLine> warrentyLines, Collection<ReportLine> unitLines) {
        L.info("Warranties in filter: {}", warrentyLines);
        return warrentyLines.stream()
                .filter((t) -> t != null
                        && t.getReference(WARRANTY) != null
                        && (unitLines.contains(t.getReference(WARRANTY)) || !t.getReference(WARRANTY).getReports().isEmpty()))
                .collect(Collectors.toCollection(() -> new TreeSet<ReportLine>()));
    }

}
