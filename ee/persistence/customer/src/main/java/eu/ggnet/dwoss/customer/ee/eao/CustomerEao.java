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
package eu.ggnet.dwoss.customer.ee.eao;

import java.util.*;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.core.system.persistence.AbstractEao;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.jpa.impl.*;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;
import static eu.ggnet.dwoss.customer.ee.entity.QCommunication.communication;
import static eu.ggnet.dwoss.customer.ee.entity.QCompany.company;
import static eu.ggnet.dwoss.customer.ee.entity.QContact.contact;
import static eu.ggnet.dwoss.customer.ee.entity.QCustomer.customer;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Stateless
public class CustomerEao extends AbstractEao<Customer> {

    /**
     * Nice Helper Class, shorter code.
     */
    private static class WildCardHelper {

        private final boolean appendWildcard;

        public String trim(String s) {
            if ( isBlank(s) ) return "%"; // Blank means ignore
            return s.toLowerCase() + (appendWildcard ? "%" : "");
        }

        public WildCardHelper(boolean appendWildcard) {
            this.appendWildcard = appendWildcard;
        }

    }

    private final static Logger L = LoggerFactory.getLogger(CustomerEao.class);

    @Inject
    @Customers
    private EntityManager em;

    public CustomerEao() {
        super(Customer.class);
    }

    public CustomerEao(EntityManager em) {
        this();
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    /**
     * A more specific search based on parameters and possible wildcard handlig.
     * All parameters witch are blank will be ignored. The search itself ist like based, so explicit as implicit wildcards are possible
     * <p>
     * @param companyName    the {@link Company#name} of {@link Customer#companies}
     * @param firstName      the {@link Contact#firstName} of {@link Customer#contacts}
     * @param lastName       the {@link Contact#lastName} of {@link Customer#contacts}
     * @param email          the {@link Communication#identifier} with {@link Communication#type} == {@link Type#EMAIL} of {@link Contact#communications} of
     *                       {@link Customer#contacts}
     * @param appendWildcard if true, adds a '%' wildcard sign to all set parameters
     * @return a list of customers matching the paramters.
     */
    public List<Customer> find(String companyName, String firstName, String lastName, String email, boolean appendWildcard) {
        // No Search, Empty Result.
        if ( isBlank(companyName) && isBlank(firstName) && isBlank(lastName) && isBlank(email) ) return Collections.emptyList(); 

        WildCardHelper W = new WildCardHelper(appendWildcard);
        // Company Tree        
        JPAQuery<Customer> query = new JPAQuery<Customer>(em)
                .distinct()
                .from(customer)
                .join(customer.companies, company)
                .on(company.name.lower().like(W.trim(companyName)))
                .join(company.contacts, contact)
                .on(contact.firstName.lower().like(W.trim(firstName)).and(contact.lastName.lower().like(W.trim(lastName))))
                .join(contact.communications, communication)
                .on(communication.identifier.lower().like(W.trim(email)));

        L.debug("calling Query 1 (Company) {}", query);
        Set<Customer> result = new HashSet<>();
        result.addAll(query.fetch());

        // Only companyNme is given, second search not needed.
        if ( isBlank(firstName) && isBlank(lastName) && isBlank(email) ) return new ArrayList<>(result); 

        query = new JPAQuery<Customer>(em)
                .distinct()
                .from(customer)
                .join(customer.contacts, contact)
                .on(contact.firstName.lower().like(W.trim(firstName)).and(contact.lastName.lower().like(W.trim(lastName))))
                .join(contact.communications, communication)
                .on(communication.identifier.lower().like(W.trim(email)));

        L.debug("calling Query 2 (Customer) {}", query);
        result.addAll(query.fetch());
        return new ArrayList<>(result);
    }

    /**
     * Finds a customer, which has the supplied communication set as default email communication.
     *
     * @param comm the communication
     * @return a customer or null if non found
     * @thorws NonUniqueResultException {@link AbstractJPAQuery#fetchOne() }.
     */
    public Customer findByDefaultEmailCommunication(Communication comm) throws NonUniqueResultException {
        if ( comm == null || comm.getType() != EMAIL ) return null;
        return new JPAQuery<Customer>(em).from(customer).where(customer.defaultEmailCommunication.eq(comm)).fetchOne();
    }

    /**
     * Finds a customer, which has the supplied communication set as reseller list email communication.
     *
     * @param comm the communication
     * @return a customer or null if non found
     * @thorws NonUniqueResultException {@link AbstractJPAQuery#fetchOne() }.
     */
    public Customer findByResellerListEmailCommunication(Communication comm) throws NonUniqueResultException {
        if ( comm == null || comm.getType() != EMAIL ) return null;
        return new JPAQuery<Customer>(em).from(customer).where(customer.resellerListEmailCommunication.eq(comm)).fetchOne();
    }

    /**
     * Returns all Customers with resellerListEmail set.
     *
     * @return all Customers with resellerListEmail set.
     */
    public List<Customer> findAllWithResellerListEmailCommunication() {
        return new JPAQuery<Customer>(em).from(customer).where(customer.resellerListEmailCommunication.isNotNull()).fetch();
    }

    /**
     * This Method search for a Customer by his Id or, Company or Firstname or Lastname.
     * First it searchs for the CustomerId via sql. Second an index search using the fields company, firsname, lastname is executed.
     * The combiened result is returned.
     * <p>
     * @param search      the search parameter
     * @param searchField pre filter
     * @return the result of the search
     */
    public List<Customer> find(String search, Set<SearchField> searchField) {
        return find(search, searchField, 0, -1);
    }

    /**
     * See {@link CustomerEao#find(java.lang.String) } but with limits for partial result retrieval.
     * Hint: The first result may contain one element extra.
     *
     * @param start       the starting result
     * @param searchField pre filter
     * @param limit       the ending result, if zero or negative, no limit is expected.
     * @param search      the search parameter
     * @return the result of the search
     */
    public List<Customer> find(String search, Set<SearchField> searchField, int start, int limit) {
        if ( StringUtils.isBlank(search) ) return Collections.emptyList();
        search = search.trim();
        List<Customer> result = new ArrayList<>();
        // Ensure, that the first result is the customer, if the search is a matching customer id.
        if ( start == 0 ) findCustomerIfSearchIsId(search).ifPresent(e -> result.add(e));
        // If only the id was given, no search is needed.
        if ( EnumSet.of(SearchField.ID).equals(searchField) ) return result;

        SearchQuery<Customer> sq = buildSearchQuery(search, searchField);
        SearchResult<Customer> sr = (limit > 0 ? sq.fetch(start, limit) : sq.fetchAll());

        result.addAll(sr.hits());
        return result;
    }

    /**
     * Count the result of the search.
     *
     * @param search      the search.
     * @param searchField pre filter
     * @return the estimated amount for the search
     */
    public int countFind(String search, Set<SearchField> searchField) {
        if ( StringUtils.isBlank(search) ) return 0;
        search = search.trim();
        if ( EnumSet.of(SearchField.ID).equals(searchField) ) return findCustomerIfSearchIsId(search).isPresent() ? 1 : 0;
        SearchQuery<Customer> sq = buildSearchQuery(search, searchField);
        return (int)sq.fetchTotalHitCount();
    }

    /**
     * Returns a list of all System customer Ids.
     * <p>
     * @return a list of all System customer Ids.
     */
    public List<Long> findAllSystemCustomerIds() {
        return new JPAQuery<Long>(em).from(customer)
                .where(customer.flags.contains(CustomerFlag.SYSTEM_CUSTOMER))
                .fetch();
    }

    private SearchQuery<Customer> buildSearchQuery(String search, Collection<SearchField> searchFields) {
        SearchSession ss = Search.session(em);

        if ( !StringUtils.containsWhitespace(search) ) { // One Word, Wildcards allowed.
            return ss.search(Customer.class)
                    .where(f -> f
                    .wildcard()
                    .fields(SearchField.toQueryFields(searchFields))
                    .matching(search.toLowerCase()))
                    .toQuery();
        } else {
            return ss.search(Customer.class)
                    .where(f -> f
                    .match()
                    .fields(SearchField.toQueryFields(searchFields))
                    .matching(search.toLowerCase()))
                    .toQuery();
        }
    }

    private Optional<Customer> findCustomerIfSearchIsId(String search) {
        try {
            Long kid = Long.valueOf(search);
            return Optional.ofNullable(em.find(Customer.class, kid)); // returns the customer or null, both expercted
        } catch (NumberFormatException numberFormatException) {
            return Optional.empty();
        }
    }

}
