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
package eu.ggnet.dwoss.uniqueunit.ee.eao;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.core.system.util.Step;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnitHistory;
import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.uniqueunit.ee.entity.QProduct.product;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.QUniqueUnitHistory.uniqueUnitHistory;

/**
 *
 */
@Stateless
public class UniqueUnitEao extends AbstractEao<UniqueUnit> {

    private final Logger L = LoggerFactory.getLogger(UniqueUnitEao.class);

    @Inject
    @UniqueUnits
    private EntityManager em;

    public UniqueUnitEao() {
        super(UniqueUnit.class);
    }

    public UniqueUnitEao(EntityManager em) {
        super(UniqueUnit.class);
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * Return exectly one unit or null if no match is found.
     *
     * @param type
     * @param search the exact identifier
     * @return exectly one unit or null if no match is found.
     */
    public UniqueUnit findByIdentifier(UniqueUnit.Identifier type, String search) {
        if ( type == null ) return null;
        // TODO: Replace native Query
        Query query = em.createNativeQuery("SELECT b.UniqueUnit_id FROM UniqueUnit_identifiers as b where b.identifiers_KEY = :type and b.identifiers = :id");
        query.setParameter("type", type.ordinal());
        query.setParameter("id", search);
        try {
            Integer id = (Integer)query.getSingleResult();
            return findById(id);
        } catch (NoResultException e) {
            return null;
            // TODO: All other exceptions will go up, think about
        }
    }

    /**
     * Returns all Units, which match the indentifier type and search.
     * <p>
     * @param type     the type to match
     * @param searches the searchs
     * @return all Units, which match the indentifier type and search.
     */
    public List<UniqueUnit> findByIdentifiers(UniqueUnit.Identifier type, Collection<String> searches) {
        if ( type == null ) return null;
        // TODO: Replace native Query
        // With: return em.createNamedQuery("UnqiueUnit.findByIdenfiersTypeValue").setParameter(1, type).setParameter(2, searches).getResultList();
        // If Hibernate Bug is fixed.
        Query query = em.createNativeQuery(
                "SELECT b.UniqueUnit_id FROM UniqueUnit_identifiers b where b.identifiers_KEY = ?1 and b.identifiers IN (?2)");
        query.setParameter(1, type.ordinal());
        query.setParameter(2, searches);
        List<Integer> ids = query.getResultList();
        return findByIds(ids);
    }

    /**
     * Returns all UniqueUnits with a supplied partNo.
     * <p/>
     * @param partNo the partNo
     * @return all UniqueUnits with a supplied partNo.
     */
    public List<UniqueUnit> findByProductPartNo(String partNo) {
        return em.createNamedQuery("UniqueUnit.byProductPartNo", UniqueUnit.class).setParameter(1, partNo).getResultList();
    }

    /**
     * Returns all UniqueUnits between the supplied inputDate and with a matching Product.
     * <p/>
     * @param partNos the partNos of the prodcut
     * @param start   the starting Date, must not be null
     * @param end     the ending Date, must not be null
     * @return all UniqueUnits between the supplied inputDate and with a matching Product.
     */
    public List<UniqueUnit> findByProductPartNosInputDate(Collection<String> partNos, Date start, Date end) {
        if ( partNos == null || partNos.isEmpty() || start == null || end == null ) return new ArrayList<>();
        return em.createNamedQuery("UniqueUnit.byProductPartNosInputDate", UniqueUnit.class)
                .setParameter(1, partNos).setParameter(2, start).setParameter(3, end).getResultList();
    }

    /**
     * Returns a list of UniqueUnits which match the search string.
     *
     * @param search the search string, may start or end with '*' as wildcard, may not be null
     * @return a list of UniqueUnits which match the search string.
     */
    public List<UniqueUnit> find(String search) {
        List<UniqueUnit> result = Collections.EMPTY_LIST;
        if ( search == null ) return result;
        // TODO: Replace native Query
        Query query = em.createNativeQuery("SELECT a.id FROM UniqueUnit as a join UniqueUnit_identifiers as b on a.id = b.UniqueUnit_id where b.identifiers like :search");
        query.setParameter("search", search.replaceAll("\\*", "%"));
        List<Integer> ids = (List<Integer>)query.getResultList();
        return findByIds(ids);
    }

    /**
     * Returns a list of UniqueUnits which match the search string.
     *
     * @param search the search string, may start or end with '*' as wildcard, may not be null
     * @param start  the start of the result
     * @param limit  the limit of the result
     * @return a list of UniqueUnits which match the search string.
     */
    // TODO: Remove Copypast
    public List<UniqueUnit> find(String search, int start, int limit) {
        List<UniqueUnit> result = Collections.EMPTY_LIST;
        if ( search == null ) return result;
        // TODO: Replace native Query
        Query query = em.createNativeQuery("SELECT a.id FROM UniqueUnit as a join UniqueUnit_identifiers as b on a.id = b.UniqueUnit_id where b.identifiers like :search");
        query.setParameter("search", search.replaceAll("\\*", "%"));
        query.setFirstResult(start);
        query.setMaxResults(limit);
        List<Integer> ids = (List<Integer>)query.getResultList();
        return findByIds(ids);
    }

    /**
     * Count the UniqueUnit, which would match the search string.
     *
     * @param search the search string.
     * @return the amount of units.
     */
    public int countFind(String search) {
        if ( StringUtils.isBlank(search) ) return 0;
        // TODO: Replace native Query
        Query query = em.createNativeQuery("SELECT count(a.id) FROM UniqueUnit as a join UniqueUnit_identifiers as b on a.id = b.UniqueUnit_id where b.identifiers like :search");
        query.setParameter("search", search.replaceAll("\\*", "%"));
        return ((BigInteger)query.getSingleResult()).intValue();
    }

    /**
     * Returns a list of UniqueUnits which match the search string.
     *
     * @param type   the type, if null an empty list is returned
     * @param search the search string, may start or end with '*' as wildcard, may not be null
     * @return a list of UniqueUnits which match the search string.
     */
    public List<UniqueUnit> findByPartialIdentifier(UniqueUnit.Identifier type, String search) {
        List<UniqueUnit> result = Collections.EMPTY_LIST;
        if ( type == null ) return result;
        if ( search == null ) return result;
        // TODO: Replace native Query
        Query query = em.createNativeQuery("SELECT a.id FROM UniqueUnit as a join UniqueUnit_identifiers as b on a.id = b.UniqueUnit_id where b.identifiers_KEY = :type and b.identifiers like :search");
        query.setParameter("type", type.ordinal());
        query.setParameter("search", search.replaceAll("\\*", "%"));
        List<Integer> ids = (List<Integer>)query.getResultList();
        return findByIds(ids);
    }

    /**
     * Returns a list of units, which corespond to the ids
     *
     * @param idList the list of ids
     * @return a list of units, which corespond to the ids, never null.
     */
    public List<UniqueUnit> findByIds(List<Integer> idList) {
        if ( idList == null || idList.isEmpty() ) return Collections.EMPTY_LIST;
        return em.createNamedQuery("UniqueUnit.findByIds", UniqueUnit.class).setParameter("idList", idList).getResultList();
    }

    /**
     * Returns all UniqueUnits with an inputDate between start and end (including).
     *
     * @param start the start date
     * @param end   the end date
     * @return all UniqueUnits with an inputDate between start and end (including)
     */
    public List<UniqueUnit> findBetweenInputDates(Date start, Date end) {
        return em.createNamedQuery("UnqiueUnit.betweenInputDates", UniqueUnit.class).setParameter(1, start).setParameter(2, end).getResultList();
    }

    /**
     * Returns all UniqueUnits with an inputDate between start and end (including) and its contractor.
     *
     * @param start      the start date
     * @param end        the end date
     * @param contractor the contractor
     * @return all UniqueUnits with an inputDate between start and end (including) and its contractor
     */
    public List<UniqueUnit> findBetweenInputDatesAndContractor(Date start, Date end, TradeName contractor) {
        return em.createNamedQuery("UnqiueUnit.betweenInputDatesAndContractor", UniqueUnit.class).setParameter(1, start).setParameter(2, end).setParameter(3, contractor).getResultList();
    }

    /**
     * Return all UniqueUnits which have the supplied contractor.
     * <p/>
     * @param contractor the contractor.
     * @return all UniqueUnits which have the supplied contractor.
     */
    public List<UniqueUnit> findByContractor(TradeName contractor) {
        return em.createNamedQuery("UnqiueUnit.byContractor", UniqueUnit.class).setParameter(1, contractor).getResultList();
    }

    /**
     * Return the {@link UniqueUnit} assoiated with a refurbished id that occured in any {@link UniqueUnitHistory}.
     * <p>
     * @param refurbishedId
     * @return the {@link UniqueUnit} assoiated with a refurbished id that occured in any {@link UniqueUnitHistory}.
     */
    public UniqueUnit findByRefurbishedIdInHistory(String refurbishedId) {
        UniqueUnitHistory result = new JPAQuery<UniqueUnitHistory>(em).from(uniqueUnitHistory).where(
                uniqueUnitHistory.comment.like("Changed Identifier(type=REFURBISHED_ID) from '" + refurbishedId + "' to%")
                        .or(uniqueUnitHistory.comment.like("Unit changed refurbishedId=" + refurbishedId + " to%")
                                .or(uniqueUnitHistory.comment.eq("REFURBISHED_ID set to " + refurbishedId)))).fetchFirst();
        return (result == null ? null : result.getUniqueUnit());
    }

    /**
     * Returns a collection of manufactures, which are used by any product, like product.tradeName.getManufacturer.
     * <p>
     * @return a collection of manufactures, which are used by any product, like product.tradeName.getManufacturer.
     */
    public NavigableSet<TradeName> findUsedManufactuers() {
        return new JPAQuery<>(em).select(product.tradeName).from(product).groupBy(product.tradeName).fetch()
                .stream().map(TradeName::getManufacturer).collect(Collectors.toCollection(() -> new TreeSet<>()));
    }

    /**
     * Returns the amount of unique units, split by date, contractor and brand.
     * <p>
     * @param start
     * @param step  the step size.
     * @param end
     * @return the amount of unique units, split by date, contractor and brand.
     */
    public NavigableMap<Date, BrandContractorCount> countByInputDateContractor(Date start, Date end, Step step) {
        TypedQuery<CountHolder> q = em.createNamedQuery("UniqueUnit.countByInputDateContractor", CountHolder.class)
                .setParameter("start", start).setParameter("end", end);

        NavigableMap<Date, BrandContractorCount> result = prepare(start, end, step);
        for (CountHolder holder : q.getResultList()) {
            BrandContractorCount count = result.get(step.truncate(holder.inputDate));
            // Highly unlikely case, but if it happens a detail message might help.
            if ( count == null ) throw new RuntimeException("No prepared BrandContractorCount found for " + step.name()
                        + ":inputDate=" + Utils.ISO_DATE.format(holder.inputDate)
                        + ",truncated=" + Utils.ISO_DATE.format(step.truncate(holder.inputDate))
                        + ",keys=" + nice(result.keySet(), step)
                );
            count.addTo(holder.brand, holder.contractor, holder.count);
        }
        return result;
    }

    private NavigableMap<Date, BrandContractorCount> prepare(Date start, Date end, Step step) {
        NavigableMap<Date, BrandContractorCount> result = new TreeMap<>();
        Date actual = step.truncate(start);
        end = step.prepareEnd(end);
        while (actual.before(end)) {
            result.put(actual, new BrandContractorCount());
            actual = step.incement(actual);
        }
        return result;
    }

    private List<String> nice(Set<Date> dates, Step step) {
        List<String> result = new ArrayList<>();
        for (Date date : dates) {
            result.add(step.format(date) + "(" + Utils.ISO_DATE.format(date) + ")");
        }
        return result;
    }

}
