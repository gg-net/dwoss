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
package eu.ggnet.dwoss.uniqueunit.eao;

import java.util.*;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnitHistory;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

import com.mysema.query.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.uniqueunit.entity.QUniqueUnitHistory.uniqueUnitHistory;

/**
 *
 */
public class UniqueUnitEao extends AbstractEao<UniqueUnit> {

    private final Logger L = LoggerFactory.getLogger(UniqueUnitEao.class);

    private EntityManager em;

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
        UniqueUnitHistory result = new JPAQuery(em).from(uniqueUnitHistory).where(
                uniqueUnitHistory.comment.like("Changed Identifier(type=REFURBISHED_ID) from '" + refurbishedId + "' to%")
                .or(uniqueUnitHistory.comment.like("Unit changed refurbishedId=" + refurbishedId + " to%")
                        .or(uniqueUnitHistory.comment.eq("REFURBISHED_ID set to " + refurbishedId)))).singleResult(uniqueUnitHistory);
        return (result == null ? null : result.getUniqueUnit());
    }
}
