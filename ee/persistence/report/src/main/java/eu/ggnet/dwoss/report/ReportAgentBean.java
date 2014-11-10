package eu.ggnet.dwoss.report;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.TradeName;

import java.util.*;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import eu.ggnet.dwoss.common.log.AutoLogger;
import eu.ggnet.dwoss.report.api.MarginCalculator;
import eu.ggnet.dwoss.report.assist.ReportUtil.PrepareReportPartition;
import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.report.entity.Report.YearSplit;


import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.dwoss.report.assist.Reports;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;

import static eu.ggnet.dwoss.report.ReportAgent.ViewReportResult.Type.*;
import static eu.ggnet.dwoss.report.assist.ReportUtil.*;
import static eu.ggnet.dwoss.report.entity.Report.ViewMode.*;
import static eu.ggnet.dwoss.rules.PositionType.*;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class ReportAgentBean extends AbstractAgentBean implements ReportAgent {

    private final static Logger L = LoggerFactory.getLogger(ReportAgentBean.class);

    @Inject
    @Reports
    private EntityManager reportEm;

    @Inject
    private ReportLineEao reportLineEao;

    @Inject
    private Instance<MarginCalculator> marginCalculator;

    @Override
    protected EntityManager getEntityManager() {
        return reportEm;
    }

    /**
     * Returns all ReportLines, which are at the given Customer id from to till the given Dates
     * <p/>
     * @param type the document type
     * @param till the date as upper border
     * @param from the date as lower border
     * @return the matching report lines.
     */
    @Override
    public List<ReportLine> findReportLinesByDocumentType(DocumentType type, Date from, Date till) {
        return reportLineEao.findbyDocumentTypeFromTill(type, from, till);
    }

    /**
     * Returns all ReportLines, limited by first and max ordered by reportDate descending.
     * <p>
     * @param firstResult the first result to return
     * @param maxResults  the maximum results to return
     * @return all ReportLines, limitied by first and max ordered by reportDate descending.
     */
    @Override
    public List<ReportLine> findAllReportLinesReverse(int firstResult, int maxResults) {
        return reportLineEao.findAllReverse(firstResult, maxResults);
    }

    /**
     * Stores a new report, persisting the report and merging the lines.
     * <p/>
     * @param report    the report to persist.
     * @param storables the lines to merge, only the id is considered and a new instance is used from the EntityManager.
     * @return the persisted report.
     */
    @Override
    @AutoLogger
    public Report store(Report report, Collection<ReportLine.Storeable> storables) {
        for (ReportLine.Storeable storable : storables) {
            ReportLine line = reportEm.find(ReportLine.class, storable.getId());
            line.setMarginPercentage(storable.getMarginPercentage());
            line.setPurchasePrice(storable.getPurchasePrice());
            report.add(line);
            L.debug("Report Line {} was anded to report. ", line);
        }
        reportEm.persist(report);
        return optionalFetchEager(report);
    }

    @Override
    public ViewReportResult findReportResult(long reportId) {
        Report r = findById(Report.class, reportId);
        return r == null ? null : ViewReportResult.fromReport(r);
    }

    /**
     * Attaches "dangling" {@link ReportLine}<code>s</code> of {@link DocumentType#COMPLAINT} to existing Reports of contractor. A ReportLine is considered
     * "dangling" if:
     * <ul>
     * <li>It is of {@link PositionType#UNIT}</li>
     * <li>It is not in a report of the type</li>
     * <li>It references a {@link ReportLine} of {@link DocumentType#ANNULATION_INVOICE} or {@link DocumentType#CREDIT_MEMO} which is already in a report of the
     * type.</li>
     * <li></li>
     * </ul>
     * These "dangling" {@link ReportLine}<code>s</code> are added to the Report that contains the referencing {@link ReportLine} of
     * {@link DocumentType#ANNULATION_INVOICE} or {@link DocumentType#CREDIT_MEMO}.
     * <p>
     * @param type the contractor as report identifier.
     * @param till the upper limit.
     * @return the discovered and attached ReportLines.
     */
    @Override
    public Set<ReportLine> attachDanglingComplaints(TradeName type, Date till) {
        L.info("attachDanglingComplaints(contractor={},till={}", type, till);
        //TODO: There might be a case their first a CreditMemo for a Unit_Annex is create and than a complaint for the full unit. For now, this is ignored.
        //TODO: The Allreport will fail here. Should be covered before
        List<ReportLine> unrepored = reportLineEao.findUnreported(type, null, till, UNIT, UNIT_ANNEX, PRODUCT_BATCH);
        L.debug("found unreported: {}", unrepored);
        Map<ReportLine, Report> danglingComplaints = filterDanglingComplaints(unrepored, type);
        L.debug("filted dangling: {}", danglingComplaints);
        for (Map.Entry<ReportLine, Report> entry : danglingComplaints.entrySet()) {
            entry.getValue().add(entry.getKey());
        }
        L.info("attachDanglingComplaints complete");
        return new HashSet<>(danglingComplaints.keySet());
    }

    private Map<ReportLine, Report> filterDanglingComplaints(List<ReportLine> unrepored, TradeName type) {
        Map<ReportLine, Report> filtered2 = new HashMap<>();
        for (ReportLine possibleDangling : unrepored) {
            if ( possibleDangling.getDocumentType() != DocumentType.COMPLAINT ) continue;
            for (ReportLine ref : possibleDangling.getRefrences()) {
                Report report = inReport(ref, type);
                if ( (ref.getDocumentType() == DocumentType.ANNULATION_INVOICE || ref.getDocumentType() == DocumentType.CREDIT_MEMO) && report != null ) {
                    filtered2.put(possibleDangling, report);
                }
            }
        }
        return filtered2;
    }

    private Report inReport(ReportLine line, TradeName type) {
        for (Report report : line.getReports()) {
            if ( report.getType() == type ) return report;
        }
        return null;
    }

    @Override
    public List<SimpleReportLine> findSimple(SearchParameter search, int firstResult, int maxResults) {
        StringBuilder sb = new StringBuilder("Select l from SimpleReportLine l");
        if ( !StringUtils.isBlank(search.getRefurbishId()) ) sb.append(" where l.refurbishId = :refurbishId");
        L.debug("Using created SearchQuery:{}", sb);
        TypedQuery<SimpleReportLine> q = reportEm.createQuery(sb.toString(), SimpleReportLine.class);
        if ( !StringUtils.isBlank(search.getRefurbishId()) ) q.setParameter("refurbishId", search.getRefurbishId().trim());
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q.getResultList();
    }

    @Override
    public List<ReportLine> find(SearchParameter search, int firstResult, int maxResults) {
        StringBuilder sb = new StringBuilder("Select l from ReportLine l");
        if ( !StringUtils.isBlank(search.getRefurbishId()) ) sb.append(" where l.refurbishId = :refurbishId");
        L.debug("Using created SearchQuery:{}", sb);
        TypedQuery<ReportLine> q = reportEm.createQuery(sb.toString(), ReportLine.class);
        if ( !StringUtils.isBlank(search.getRefurbishId()) ) q.setParameter("refurbishId", search.getRefurbishId().trim());
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        return q.getResultList();
    }

    @Override
    public long count(SearchParameter search) {
        StringBuilder sb = new StringBuilder("Select Count(l) from ReportLine l");
        if ( !StringUtils.isBlank(search.getRefurbishId()) ) sb.append(" where l.refurbishId = :refurbishId");
        L.debug("Using created SearchQuery:{}", sb);
        TypedQuery<Long> q = reportEm.createQuery(sb.toString(), Long.class);
        if ( !StringUtils.isBlank(search.getRefurbishId()) ) q.setParameter("refurbishId", search.getRefurbishId().trim());
        return q.getSingleResult();
    }

    @Override
    public ViewReportResult prepareReport(ReportParameter p, boolean loadUnreported) {
        attachDanglingComplaints(p.getContractor(), p.getEnd());
        List<ReportLine> findUnreportedUnits = reportLineEao.findUnreportedUnits(p.getContractor(), (loadUnreported) ? null : p.getStart(), p.getEnd());
        EnumMap<ViewReportResult.Type, NavigableSet<ReportLine>> lines = new EnumMap<>(ViewReportResult.Type.class);

        PrepareReportPartition unitPartition = partition(findUnreportedUnits, p.getContractor());

        lines.put(ACTIVE_INFO, unitPartition.getActiveInfo());
        lines.put(REPORT_INFO, filterReportInfo(unitPartition.getReportAble()));
        lines.put(REPAYMENTS, filterRepayed(unitPartition.getReportAble()));

        switch (p.getViewMode()) {
            case DEFAULT:
                lines.put(INVOICED, filterInvoiced(unitPartition.getReportAble()));
                break;
            case YEARSPLITT_AND_WARRANTIES:
                YearSplit filterInvoicedSplit = filterInvoicedSplit(unitPartition.getReportAble(), p.getStart());
                lines.put(PAST_ONE_YEAR, filterInvoicedSplit.getAfter());
                lines.put(UNDER_ONE_YEAR, filterInvoicedSplit.getBefore());
                PrepareReportPartition warrantyPartition = partition(filterWarrenty(
                        reportLineEao.findUnreportedWarrentys(), unitPartition.getReportAble()), p.getContractor());
                lines.put(WARRENTY, filterInvoiced(warrantyPartition.getReportAble()));
                lines.get(ACTIVE_INFO).addAll(warrantyPartition.getActiveInfo());
                lines.get(REPAYMENTS).addAll(filterRepayed(warrantyPartition.getReportAble()));
                lines.get(REPORT_INFO).addAll(filterReportInfo(warrantyPartition.getReportAble()));
                break;
        }

        ViewReportResult viewReportResult = new ViewReportResult(lines, p);
        viewReportResult.getAllLines().stream().forEach((allLine) -> reportEm.detach(allLine));
        if ( !marginCalculator.isUnsatisfied() ) marginCalculator.get().recalc(viewReportResult);
        return viewReportResult;
    }
}
