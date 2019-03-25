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
package eu.ggnet.dwoss.report.ee.eao;

import eu.ggnet.dwoss.common.ee.Step;
import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.api.values.DocumentType;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.util.DateFormats;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.report.ee.entity.QReportLine.reportLine;
import static eu.ggnet.dwoss.report.ee.entity.ReportLine.SingleReferenceType.WARRANTY;
import static eu.ggnet.dwoss.report.ee.entity.partial.QSimpleReportLine.simpleReportLine;
import static eu.ggnet.dwoss.common.api.values.PositionType.*;

/**
 * Entity Access Object for ReportLine.
 * <p>
 * @author pascal.perau
 */
@Stateless
public class ReportLineEao extends AbstractEao<ReportLine> {

    private static final Logger L = LoggerFactory.getLogger(ReportLineEao.class);

    @Inject
    @Reports
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ReportLineEao(EntityManager em) {
        this();
        this.em = em;
    }

    public ReportLineEao() {
        super(ReportLine.class);
    }

    public List<SimpleReportLine> findAllSimple() {
        return em.createQuery("SELECT r FROM SimpleReportLine r", SimpleReportLine.class).getResultList();
    }

    /**
     * Returns all ReportLines, limited by first and max ordered by reportDate descending.
     * <p>
     * @param firstResult the first result to return
     * @param maxResults  the maximum results to return
     * @return all ReportLines, limited by first and max ordered by reportDate descending.
     */
    public List<ReportLine> findAllReverse(int firstResult, int maxResults) {
        return em.createNamedQuery("ReportLine.allReverse", ReportLine.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    /**
     * Returns all ReportLines ordered by reportDate descending.
     * <p>
     * @return all ReportLines ordered by reportDate descending.
     */
    public List<ReportLine> findAllReverse() {
        return em.createNamedQuery("ReportLine.allReverse", ReportLine.class).getResultList();
    }

    public Date findLastReported() {
        return em.createNamedQuery("ReportLine.lastReported", Date.class).getSingleResult();
    }

    public List<ReportLine> findByUniqueUnitId(long id) {
        return em.createNamedQuery("ReportLine.byUniqueUnitId", ReportLine.class).setParameter(1, id).getResultList();
    }

    /**
     * Returns all lines matching the refurbishId.
     * <p>
     * @param id the refurbishId
     * @return all lines matching the refurbishId.
     */
    public List<ReportLine> findByRefurbishId(String id) {
        return em.createNamedQuery("ReportLine.byRefurbishId", ReportLine.class).setParameter(1, id).getResultList();
    }

    public List<ReportLine> findBetweenDates(Date start, Date end) {
        return em.createNamedQuery("ReportLine.betweenDates", ReportLine.class).setParameter(1, start).setParameter(2, end).getResultList();
    }

    /**
     * Returns all Reportlines, which are not jet in a Report of the contractor and have a reporting date between (including) from and till.
     * <p/>
     * If the contractor is null the Query unreportedFromTillAll will be executed.
     * <p/>
     * @param type        the contractor as filter
     * @param till        the date as upper border
     * @param from        the date as lower border
     * @param includeOnly selects positionTypes which should only be included.
     * @return the matching report lines.
     */
    public List<ReportLine> findUnreported(TradeName type, Date from, Date till, PositionType... includeOnly) {
        L.info("findUnreported(type={},from={},till={},includeOnly={})", type, from, till, includeOnly);
        Objects.requireNonNull(type, "The Type must not be null");
        List<PositionType> positionTypes = new ArrayList<>();
        List<TradeName> contractors = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(2001, 01, 01);
        if ( includeOnly != null && includeOnly.length > 0 ) positionTypes = Arrays.asList(includeOnly);
        contractors.add(type);
        TypedQuery<ReportLine> query;
        if ( positionTypes.isEmpty() && contractors.isEmpty() ) {
            L.debug("Using Query ReportLine.unreported");
            query = em.createNamedQuery("ReportLine.unreported", ReportLine.class);
        } else if ( positionTypes.isEmpty() ) {
            L.debug("Using Query ReportLine.unreportedbyContractors");
            query = em.createNamedQuery("ReportLine.unreportedbyContractors", ReportLine.class).setParameter("contractors", contractors);
        } else if ( contractors.isEmpty() ) {
            L.debug("Using Query ReportLine.unreportedbyPositionTypes");
            query = em.createNamedQuery("ReportLine.unreportedbyPositionTypes", ReportLine.class).setParameter("positionTypes", positionTypes);
        } else {
            L.debug("Using Query ReportLine.unreportedbyContractorsPositionTypes");
            query = em.createNamedQuery("ReportLine.unreportedbyContractorsPositionTypes", ReportLine.class).setParameter("contractors", contractors).setParameter("positionTypes", positionTypes);
        }
        query.setParameter("from", (from == null ? cal.getTime() : from));
        query.setParameter("till", till);
        query.setParameter("type", type);
        return query.getResultList();
    }

    public List<ReportLine> findUnreportedUnits(TradeName type, Date from, Date till) {
        return findUnreported(type, from, till, PositionType.UNIT, PositionType.UNIT_ANNEX);
    }

    /**
     * Returns a collection with {@link PositionType#UNIT} or {@link PositionType#UNIT_ANNEX} with the same uniqueUnitId and dossierId.
     * <p>
     * @param uniqueUnitId the uniqueUnitId
     * @param dossierId    the dossierId
     * @return a collection with {@link PositionType#UNIT} or {@link PositionType#UNIT_ANNEX} with the same uniqueUnitId and dossierId.
     */
    public List<ReportLine> findUnitsAlike(long uniqueUnitId, long dossierId) {
        return new JPAQuery<ReportLine>(em).from(reportLine)
                .where(reportLine.positionType.in(Arrays.asList(UNIT, UNIT_ANNEX)), reportLine.uniqueUnitId.eq(uniqueUnitId), reportLine.dossierId.eq(dossierId)
                ).fetch();
    }

    public List<ReportLine> findReportedUnitsbyRefurbishId(Collection<String> refurbishId) {
        return new JPAQuery<ReportLine>(em).from(reportLine)
                .where(reportLine.positionType.in(Arrays.asList(UNIT, UNIT_ANNEX)), reportLine.refurbishId.in(refurbishId), reportLine.reports.isNotEmpty())
                .fetch();
    }

    /**
     * This Method returns all unreported Warranties.
     * <p>
     * @return all unreported warranties.
     */
    public List<ReportLine> findUnreportedWarrentys() {
        return new JPAQuery<ReportLine>(em)
                .from(reportLine)
                .where(reportLine.positionType.eq(PRODUCT_BATCH),
                        reportLine.reports.isEmpty())
                .fetch()
                .stream()
                .filter(l -> l.getReference(WARRANTY) != null)
                .collect(Collectors.toList());
    }

    /**
     * Returns all ReportLines, which are at the given Customer id from to till the given Dates
     * <p/>
     * @param type the customer Id which the ReportLines must be have
     * @param till the date as upper border
     * @param from the date as lower border
     * @return the matching report lines.
     */
    public List<ReportLine> findbyDocumentTypeFromTill(DocumentType type, Date from, Date till) {
        return new JPAQuery<ReportLine>(em).from(reportLine).where(reportLine.documentType.eq(type),
                reportLine.reportingDate.between(from, till)).fetch();
    }

    /**
     * Returns all reportlines matching the productId, the contractor and have no contractor part no set.
     * <p>
     * @param productId  the product id
     * @param contractor the contractor
     * @return all reportlines matching the productId, the contractor and have no contractor part no set.
     */
    public List<ReportLine> findByProductIdMissingContractorPartNo(long productId, TradeName contractor) {
        return em.createNamedQuery("ReportLine.byProductIdMissingContractorPartNo", ReportLine.class)
                .setParameter(1, productId).setParameter(2, contractor).getResultList();
    }

    /**
     * Returns all lines, which have no contractorPartNo and are oft the type of the supplied contractor.
     *
     * @param contractor the contractor.
     * @return all lines, which have no contractorPartNo and are oft the type of the supplied contractor.
     */
    public List<ReportLine> findMissingContractorPartNo(TradeName contractor) {
        return new JPAQuery<ReportLine>(em).from(reportLine)
                .where(reportLine.positionType.eq(UNIT)
                        .and(reportLine.contractorPartNo.isNull())
                        .and(reportLine.contractor.eq(contractor)))
                .fetch();
    }

    /**
     * Returns all lines, which have no gtin.
     *
     * @param partNo the manufacturerPartNo
     * @return all lines, which have no gtin.
     */
    public List<ReportLine> findMissingGtin(String partNo) {
        return new JPAQuery<ReportLine>(em).from(reportLine)
                .where(reportLine.partNo.eq(partNo)
                        .and(reportLine.gtin.eq(0l)))
                .fetch();
    }

    /**
     * Generates a list of {@link DailyRevenue} that hold report data for INVOICES - ANNULATION_INVOICES in a date range containing daily summed prices
     * for specific {@link PositionType}s.
     * <p>
     * @param posTypes the {@link PositionType} that is searched for
     * @param start    the starting date range for the collected data
     * @param end      the end date range for the collected data
     * @return a list of {@link DailyRevenue} that hold report data for INVOICES - ANNULATION_INVOICES in a date range containing daily summed prices
     *         for specific {@link PositionType}s
     */
    public List<Set<DailyRevenue>> findRevenueDataByPositionTypesAndDate(List<PositionType> posTypes, Date start, Date end) {
        try {
            L.info("Attempt to find revenue report data with posType={}, start={}, end={}", posTypes, start, end);

            List<Integer> posTypeOrdinals = new ArrayList<>();
            for (PositionType positionType : posTypes) {
                posTypeOrdinals.add(positionType.ordinal());
            }
            Query q = em.createNativeQuery("SELECT reportingDate, documentTypeName, sum(price), salesChannelName"
                    + " FROM ReportLine rl WHERE rl.positionType in(:positions) and rl.reportingDate >= :start"
                    + " and rl.reportingDate <= :end and rl.documentType in(1,3) GROUP BY rl.reportingDate, rl.documentTypeName, rl.salesChannelName");
            q.setParameter("positions", posTypeOrdinals);
            q.setParameter("start", start);
            q.setParameter("end", end);
            List<Object[]> data = q.getResultList();
            List<DailyRevenue> reportData = new ArrayList<>();
            for (Object[] object : data) {
                reportData.add(new DailyRevenue((Date)object[0], (String)object[1], (double)object[2], (String)object[3]));
            }

            Map<Date, Set<DailyRevenue>> revReports = new HashMap<>();
            for (DailyRevenue revenueReportCarrier : reportData) {
                Date d = DateUtils.truncate(revenueReportCarrier.getReportingDate(), Calendar.DATE);
                Set<DailyRevenue> neededSet = revReports.get(d);
                if ( neededSet == null ) {
                    neededSet = new HashSet<>();
                    neededSet.add(revenueReportCarrier);
                    revReports.put(d, neededSet);
                } else {
                    neededSet.add(revenueReportCarrier);
                }
            }

            return new ArrayList<>(revReports.values());
        } catch (Exception e) {
            L.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * Returns the revenue in a range with the supplied Stepsize.
     * A step size of day will return daily revenues, while a stepsize of month returns monthly revenues.
     * For each stepsize the earliest possible day is used as identifier. e.g.: January 2012 it would be 2012-01-01.
     * <p>
     * @param posTypes      the positiontypes to include
     * @param start         the start
     * @param end           the end
     * @param step          the stepsize.
     * @param extraReported
     * @return the Revenue by Date in the stepsize.
     */
    public NavigableMap<Date, Revenue> revenueByPositionTypesAndDate(List<PositionType> posTypes, Date start, Date end, Step step, boolean extraReported) {
        L.debug("Attempt to find revenue report data with posType={}, start={}, end={}, {}", posTypes, start, end, step);
        TypedQuery<RevenueHolder> q = em.createNamedQuery("ReportLine.revenueByPositionTypesAndDate", RevenueHolder.class);
        q.setParameter("positions", posTypes).setParameter("start", start).setParameter("end", end);

        NavigableMap<Date, Revenue> result = prepare(start, end, step);
        for (RevenueHolder holder : q.getResultList()) {
            Revenue revenueStep = result.get(step.truncate(holder.getReportingDate()));
            // Highly unlikely case, but if it happens a detail message might help.
            if ( revenueStep == null ) throw new RuntimeException("No prepared RevenueStep found for " + step.name()
                        + ":reportingDate=" + DateFormats.ISO.format(holder.getReportingDate())
                        + ",truncated=" + DateFormats.ISO.format(step.truncate(holder.getReportingDate()))
                        + ",keys=" + nice(result.keySet(), step)
                );
            L.debug("revenueByPositionTypesAndDate() first call to Revenue.add({},{},{},{},0,0)",holder.getSalesChannel(),holder.getDocumentType(), holder.getContractor(),holder.getPrice());
            revenueStep.addTo(holder.getSalesChannel(), holder.getDocumentType(), holder.getContractor(), holder.getPrice(), 0., 0.);
        }
        if ( !extraReported ) return result;
        q = em.createNamedQuery("ReportLine.revenueByPositionTypesAndDateReported", RevenueHolder.class);
        q.setParameter("positions", posTypes).setParameter("start", start).setParameter("end", end);
        List<RevenueHolder> resultList = q.getResultList();
        L.debug("Second run size:" + resultList.size());
        for (RevenueHolder holder : resultList) {
            L.debug("Second run: " + holder);
            Revenue revenueStep = result.get(step.truncate(holder.getReportingDate()));
            // Highly unlikely case, but if it happens a detail message might help.
            if ( revenueStep == null ) throw new RuntimeException("No prepared RevenueStep found for " + step.name()
                        + ":reportingDate=" + DateFormats.ISO.format(holder.getReportingDate())
                        + ",truncated=" + DateFormats.ISO.format(step.truncate(holder.getReportingDate()))
                        + ",keys=" + nice(result.keySet(), step)
                );
            L.debug("revenueByPositionTypesAndDate() second call to Revenue.add({},{},{},0,{},{})",holder.getSalesChannel(),holder.getDocumentType(), holder.getContractor(),holder.getPrice(), holder.getPurchasePrice());
            revenueStep.addTo(holder.getSalesChannel(), holder.getDocumentType(), holder.getContractor(), 0., holder.getPrice(), holder.getPurchasePrice());
        }
        return result;
    }

    public List<ReportLine> findBySerialAndPositionTypeAndDossierId(String serial, PositionType positionType, long dossierId) {
        return em.createNamedQuery("ReportLine.bySerialAndPositionTypeAndDossierId", ReportLine.class)
                .setParameter(1, serial)
                .setParameter(2, positionType)
                .setParameter(3, dossierId)
                .getResultList();
    }

    /**
     * This method search for a single Report line which is identify by Serial or Refurbish id.
     * <p>
     * @param key Serial or Refurbish id.
     * @return return a found ReportLine.
     */
    public List<SimpleReportLine> findReportLinesByIdentifiers(String key) {
        return new JPAQuery<SimpleReportLine>(em).from(simpleReportLine)
                .where(simpleReportLine.refurbishId.eq(key).or(simpleReportLine.serial.eq(key))).fetch();
    }

    private NavigableMap<Date, Revenue> prepare(Date start, Date end, Step step) {
        NavigableMap<Date, Revenue> result = new TreeMap<>();
        Date actual = step.truncate(start);
        end = step.prepareEnd(end);
        while (actual.before(end)) {
            result.put(actual, new Revenue());
            actual = step.incement(actual);
        }
        return result;
    }

    private List<String> nice(Set<Date> dates, Step step) {
        List<String> result = new ArrayList<>();
        for (Date date : dates) {
            result.add(step.format(date) + "(" + DateFormats.ISO.format(date) + ")");
        }
        return result;
    }

}
