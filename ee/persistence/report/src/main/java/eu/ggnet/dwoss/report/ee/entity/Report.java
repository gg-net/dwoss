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
package eu.ggnet.dwoss.report.ee.entity;

import java.io.Serializable;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;

import static eu.ggnet.dwoss.report.ee.entity.Report.ViewMode.DEFAULT;
import static jakarta.persistence.GenerationType.IDENTITY;

//TODO: Name: Zusammenfassung, Comulation. Gruppierung.
/**
 *
 * @author bastian.venz
 * @has 1 - n ReportLine
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class Report extends BaseEntity implements Serializable, EagerAble {

    /**
     * Wrapper for updates containing id and optlock.
     */
    public final static class OptimisticKey implements Serializable {

        public final long id;

        public final int optLock;

        public OptimisticKey(long id, int optLock) {
            this.id = id;
            this.optLock = optLock;
        }

        @Override
        public String toString() {
            return "OptimisticKey{" + "id=" + id + ", optLock=" + optLock + '}';
        }

    }

    /**
     * The ViewModw of a Report.
     * A Viewmode allowed different looks and filterings for the ui.
     * <p>
     */
    public enum ViewMode {

        /**
         * Default.
         */
        DEFAULT,
        /**
         * Splitt the Invoiced Lines by the mfgDate of each unit and creates an extra view for warranties.
         */
        YEARSPLITT_AND_WARRANTIES
    }

    /**
     * Wrapper for a collection of reportlines to be split between a date.
     * Usage only as wrapper result.
     */
    public static class YearSplit implements Serializable {

        public final Date splitter;

        /**
         * Contains lines from splitter till today.
         */
        public final NavigableSet<ReportLine> before;

        /**
         * Contains lines from 1970 till splitter.
         */
        public final NavigableSet<ReportLine> after;

        public YearSplit(Date splitter, NavigableSet<ReportLine> before, NavigableSet<ReportLine> after) {
            this.splitter = Objects.requireNonNull(splitter,"splitter must not be null");
            this.before = Objects.requireNonNull(before,"before must not be null");
            this.after = Objects.requireNonNull(after,"after must not be null");
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
        
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long id;

    @Version
    private int optLock;

    @NotNull
    private String name;

    /**
     * This is the type of report.
     * This value should never be changed afterwards.
     */
    @NotNull
    private TradeName type;

    /**
     * This String is a representation the contractor for who the report was generated.
     * If this is null its represent the AllReport.
     */
    private String typeName;

    /**
     * This date represent a point where the span of the report start.
     */
    @NotNull
    @Temporal(jakarta.persistence.TemporalType.DATE)
    private Date startingDate;

    /**
     * This date represent a point where the span of the report ends.
     */
    @NotNull
    @Temporal(jakarta.persistence.TemporalType.DATE)
    private Date endingDate;

    @Lob
    @Column(length = 65536)
    private String comment;

    @NotNull
    private ViewMode viewMode = DEFAULT;

    /**
     * This set contains the ReportsLines of the Reports, where is the mapping unidirectional.
     */
    @ManyToMany
    private Set<ReportLine> lines = new HashSet<>();

    public Report() {
    }
    
    public Report(String name, TradeName type, Date startingDate, Date endingDate, ViewMode viewMode) {
        this(name, type, startingDate, endingDate);
        this.viewMode = viewMode;
    }

    public Report(String name, TradeName type, Date startingDate, Date endingDate) {
        this.name = name;
        this.type = Objects.requireNonNull(type, "The type must not be null");
        this.typeName = type.name();
        this.startingDate = startingDate;
        this.endingDate = endingDate;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Date getStartingDate() {
        return startingDate;
    }
    
    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }
    
    public Date getEndingDate() {
        return endingDate;
    }
    
    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    public int getOptLock() {
        return optLock;
    }
    
    public TradeName getType() {
        return type;
    }
    
    public String getTypeName() {
        return typeName;
    }
    
    public ViewMode getViewMode() {
        return viewMode;
    }
    //</editor-fold>
    
    /**
     * Add a ReportLine to the Set of ReportLines.
     * <p/>
     * @param reportLine the ReportLine that will be added.
     */
    public void add(ReportLine reportLine) {
        if ( reportLine == null ) return;
        lines.add(reportLine);
        reportLine.reports.add(this);
    }

    /**
     * Remove a ReportLine from the Set of ReportLines
     * <p/>
     * @param reportLine the ReportLine that will be removed.
     */
    public void remove(ReportLine reportLine) {
        if ( reportLine == null ) return;
        lines.remove(reportLine);
        reportLine.reports.remove(this);
    }

    public void addAll(Collection<? extends ReportLine> reportLines) {
        for (ReportLine reportLine : reportLines) {
            add(reportLine);
        }
    }

    public NavigableSet<ReportLine> getLines() {
        return new TreeSet<>(lines);
    }

    /**
     * Returns all Lines of the Report for Category Invoiced, split by mfgDate - startOfReport &lt; 1 year and the rest.
     * This consists of:
     * <ul>
     * <li>Position of Type Invoice, with no References</li>
     * <li>Position of Type UNIT_ANNEX in DocumentType CREDIT_MEMO/ANNULATIION_INVOICE and a Referencing Invoice in the same report.</li>
     * </ul>
     * <p>
     * @return all Lines of the Report for Category Invoiced.
     */
    public YearSplit filterInvoicedSplit() {
        NavigableSet<ReportLine> pastSplit = filterInvoiced();
        NavigableSet<ReportLine> preSplit = new TreeSet<>();
        Date splitter = DateUtils.addYears(startingDate, -1);
        for (ReportLine line : pastSplit) {
            if ( splitter.before(line.getMfgDate()) ) preSplit.add(line);
        }
        pastSplit.removeAll(preSplit);
        return new YearSplit(startingDate, preSplit, pastSplit);
    }

    /**
     * Returns all Lines of the Report for Category Invoiced.
     * This consists of:
     * <ul>
     * <li>Position of Type Capital Asset</li>
     * <li>Position of Type Invoice, with no References</li>
     * <li>Position of Type UNIT_ANNEX in DocumentType CREDIT_MEMO/ANNULATIION_INVOICE and a Referencing Invoice in the same report.</li>
     * </ul>
     * <p>
     * @return all Lines of the Report for Category Invoiced.
     */
    //TODO: We could also substract the value of a unitannex from the invoice and not return the unit annex at all.
    //But consider the impact in the ui, especially if we allow selection of such a "combined" line.
    public NavigableSet<ReportLine> filterInvoiced() {
        NavigableSet<ReportLine> result = new TreeSet<>();
        for (ReportLine line : lines) {
            if ( line.getDocumentType() == DocumentType.CAPITAL_ASSET ) result.add(line); // There is no way a capital Asset can be returned.
            // Only if we are fully repayed in this report, we are not in the invoiced result.
            if ( line.getDocumentType() == DocumentType.INVOICE && !line.isFullRepayedIn(lines) ) result.add(line);
            if ( line.isPartialRepayment() && !line.isFullRepayedIn(lines) && lines.contains(line.getSingleReference(DocumentType.INVOICE)) ) result.add(line);
        }
        return result;
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
     * @return all Lines of the Report which represent Positions of CreditMemos and Annulation Invoices but the associated Invoices have been reported before.
     */
    public NavigableSet<ReportLine> filterRepayed() {
        NavigableSet<ReportLine> result = new TreeSet<>();
        for (ReportLine line : lines) {
            if ( line.isFullRepayment() && !lines.contains(line.getSingleReference(DocumentType.INVOICE)) ) result.add(line);
            // Implies ( !lines.contaions(null) ) == true
            if ( line.isPartialRepayment() && !line.isFullRepayedIn(lines) && !lines.contains(line.getSingleReference(DocumentType.INVOICE)) ) result.add(line);
        }
        return result;
    }

    /**
     * Returns all Reportlines, that don't have an impact on the result, but have only informational character.
     * <p>
     * @return all Reportlines, that don't have an impact on the result, but have only informational character.
     */
    public NavigableSet<ReportLine> filterInfos() {
        NavigableSet<ReportLine> result = new TreeSet<>(lines);
        result.removeAll(filterInvoiced());
        result.removeAll(filterRepayed());
        return result;
    }

    @Override
    public void fetchEager() {
        ReportLine.EagerHelper eagerHelper = new ReportLine.EagerHelper();
        eagerHelper.fetch(this);
    }

    /**
     * Returns a tuple of the id and the optLock value
     *
     * @return a tuple of the id and the optLock value
     */
    public OptimisticKey toKey() {
        return new OptimisticKey(id, optLock);
    }

    /**
     * Returns a String representation of the report, seperated by all filteroperations.
     * <p>
     * @param showSplit if true the invoiced part is also splitt by mfgDate and one year before the start
     * @return a String representation of the report, seperated by all filteroperations.
     */
    public String toMultiLine(boolean showSplit) {
        String result = this.name + "\n";
        if ( showSplit ) {
            YearSplit splitresult = filterInvoicedSplit();

            result += buildFilter("Invoiced Before " + Utils.ISO_DATE.format(splitresult.splitter), splitresult.before);
            result += buildFilter("Invoiced After " + Utils.ISO_DATE.format(splitresult.splitter), splitresult.after);
        } else {
            result += buildFilter("Invoiced", filterInvoiced());

        }
        result += buildFilter("Repayed", filterRepayed());
        result += buildFilter("Infos", filterInfos());
        return result;
    }

    private String buildFilter(String head, Collection<ReportLine> lines) {
        StringBuilder sb = new StringBuilder(" " + head);
        if ( lines.isEmpty() ) return "";
        sb.append("\n");
        for (ReportLine line : lines) {
            sb.append("   - ")
                    .append(line.getRefurbishId()).append("|")
                    .append(line.getDocumentTypeName()).append("|")
                    .append(line.getPositionTypeName()).append("|")
                    .append(line.getPrice()).append("|")
                    .append(line.getMfgDate() == null ? "no mfgDate" : Utils.ISO_DATE.format(line.getMfgDate())).append("\n");
        }
        sb.append("-----\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Report{" + "id=" + id + ", name=" + name + ", type=" + type + ", typeName=" + typeName + ", startingDate=" + startingDate + ", endingDate=" + endingDate + ", comment=" + comment + '}';
    }

    /**
     * ToString HTML representation.
     *
     * @return HTML view of the Report.
     */
    public String toHtml() {
        StringBuilder sb = new StringBuilder("<table style='width: 100%; background-color: #e8e8e8; font-family: Sans-Serif;' border='0'><tbody>");
        sb.append("<tr>");
        sb.append("<td><b>Report</b>");
        sb.append("<b>id: </b>");
        sb.append(id);
        sb.append("</td>");
        sb.append("<td><b>Name</b>");
        sb.append(name);
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td><b>Type:</b>");
        sb.append(type.getDescription());
        sb.append("<td>&nbsp;</td>");
        sb.append("</td></tr>");

        sb.append("<tr>");
        sb.append("<td><b>Starting Date:</b><br>");
        sb.append(startingDate);
        sb.append("<td><p><b>Ending Date</b><br>");
        sb.append(endingDate);
        sb.append("</td>");
        sb.append("</td></tr>");

        sb.append("<tr>");
        sb.append("<td colspan='2'><b>Comment:</b><br>");
        sb.append("<textarea  rows='5' cols='70' disabled>");
        sb.append(comment);
        sb.append("</textarea>");
        sb.append("</td></tr>");

        sb.append("</tbody></table>");

        return sb.toString();

    }

    /**
     * toHtmlSingleLine HTML representation.
     *
     * @return HTML on a Singleline view of the Report.
     */
    public String toHtmlSingleLine() {
        return "Report{" + "id:" + id + ", Name:" + name + ", Type:" + type + ", Date:" + startingDate + " - " + endingDate + '}';

    }

}
