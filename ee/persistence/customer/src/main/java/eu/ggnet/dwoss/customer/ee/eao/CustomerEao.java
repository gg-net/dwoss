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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.*;
import org.hibernate.search.query.dsl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;
import static eu.ggnet.dwoss.customer.ee.entity.QCommunication.communication;
import static eu.ggnet.dwoss.customer.ee.entity.QCompany.company;
import static eu.ggnet.dwoss.customer.ee.entity.QContact.contact;
import static eu.ggnet.dwoss.customer.ee.entity.QCustomer.customer;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * CustomerEao.
 * <p>
 * @author oliver.guenther
 */
@Stateless
public class CustomerEao extends AbstractEao<Customer> {

    /**
     * Nice Helper Class, shorter code.
     */
    private static class WildCardHelper {

        private final boolean appendWildcard;

        public String trim(String s) {
            return s.toLowerCase() + (appendWildcard ? "%" : "");
        }

        public WildCardHelper(boolean appendWildcard) {
            this.appendWildcard = appendWildcard;
        }

    }

    private final static Logger L = LoggerFactory.getLogger(CustomerEao.class);

    private static final Set<String> SEARCH_FIRSTNAME = new HashSet<>();

    private static final Set<String> SEARCH_LASTNAME = new HashSet<>();

    private static final Set<String> SEARCH_COMPANY = new HashSet<>();

    private static final Set<String> SEARCH_ADDRESS = new HashSet<>();

    static {

        SEARCH_FIRSTNAME.add("companies.contacts.firstName");
        SEARCH_FIRSTNAME.add("contacts.firstName");

        SEARCH_LASTNAME.add("companies.contacts.lastName");
        SEARCH_LASTNAME.add("contacts.lastName");

        SEARCH_COMPANY.add("companies.name");

        SEARCH_ADDRESS.add("companies.addresses.street");
        SEARCH_ADDRESS.add("companies.addresses.city");
        SEARCH_ADDRESS.add("companies.addresses.zipCode");

        SEARCH_ADDRESS.add("contacts.addresses.street");
        SEARCH_ADDRESS.add("contacts.addresses.city");
        SEARCH_ADDRESS.add("contacts.addresses.zipCode");
    }

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
        WildCardHelper W = new WildCardHelper(appendWildcard);
        JPAQuery<Customer> query = new JPAQuery<Customer>(em).from(customer);
        if ( !isBlank(companyName) ) {
            query.join(customer.companies, company)
                    .on(company.name.lower()
                            .like(W.trim(companyName)));
        }
        if ( !isBlank(firstName) || !isBlank(lastName) ) {
            query.join(customer.contacts, contact).on();
            BooleanExpression on = null;
            if ( !isBlank(firstName) ) {
                on = contact.firstName.lower().like(W.trim(firstName));
            }
            if ( !isBlank(lastName) ) {
                BooleanExpression second = contact.lastName.lower().like(W.trim(lastName));
                if ( on != null ) {
                    on = on.and(second);
                } else {
                    on = second;
                }
            }
            query.on(on);
        }
        if ( !isBlank(email) ) {
            query.join(customer.contacts, contact)
                    .join(contact.communications, communication)
                    .on(communication.type.eq(EMAIL)
                            .and(communication.identifier.lower().like(W.trim(email))));
        }
        L.debug("calling query");
        List<Customer> list = query.fetch();
        L.debug("Query successful wiht {}", list);
        return list;
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
     * This Method search for a Customer by his Id or, Company or Firstname or Lastname.
     * First it searchs for the CustomerId via sql. Second an index search using the fields company, firsname, lastname is executed.
     * The combiened result is returned.
     * <p>
     * @param search      the search parameter
     * @param searchField pre filter
     * @return the result of the search
     */
    public List<Customer> find(String search, Set<SearchField> searchField) {
        if ( StringUtils.isBlank(search) ) {
            return new ArrayList<>();
        }
        //fill the searchField
        if ( searchField == null || searchField.isEmpty() ) {
            searchField = new HashSet<>(Arrays.asList(SearchField.values()));
        }
        search = search.trim();
        List<Customer> result = new ArrayList<>();

        findCustomerIfSearchIsId(search).ifPresent(e -> result.add(e));

        result.addAll(buildSearchQuery(search, searchField).getResultList());

        return result;
    }

    private void findOneCustomer(String search, List<Customer> result) {
        try {
            Long kid = Long.valueOf(search);
            Customer find = em.find(Customer.class, kid);
            if ( find != null ) {
                result.add(find);
            }
        } catch (NumberFormatException numberFormatException) {
            // If not a number, ignore.
        }
    }

    /**
     * See {@link CustomerEao#find(java.lang.String) } but with limits for partial result retrieval.
     * Hint: The first result may contain one element extra.
     *
     * @param start       the starting result
     * @param searchField pre filter
     * @param limit       the ending result
     * @param search      the search parameter
     * @return the result of the search
     */
    public List<Customer> find(String search, Set<SearchField> searchField, int start, int limit) {
        if ( StringUtils.isBlank(search) ) {
            return new ArrayList<>();
        }
        //fill the searchField
        if ( searchField == null || searchField.isEmpty() ) {
            searchField = new HashSet<>(Arrays.asList(SearchField.values()));
        }

        search = search.trim();
        // Ensure, that the first result is the customer, if the search is a matching customer id.
        List<Customer> result = new ArrayList<>();
        if ( start == 0 ) {
            findCustomerIfSearchIsId(search).ifPresent(e -> result.add(e));
        }
        List resultList = buildSearchQuery(search, searchField)
                .setFirstResult(start)
                .setMaxResults(limit)
                .getResultList();

        result.addAll(resultList);

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
        //fill the searchField if it does not contain any fields
        if ( searchField == null || searchField.isEmpty() ) searchField = new HashSet<>(Arrays.asList(SearchField.values()));

        return buildSearchQuery(search, searchField).getResultSize();
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

    private Set<String> getSearchFieldStringSet(Set<SearchField> searchField) {
        Set<String> searchFieldStringSet = new HashSet<>();

        if ( searchField == null ) {
            return searchFieldStringSet;
        }

        for (SearchField sf : searchField) {
            switch (sf) {
                case FIRSTNAME:
                    searchFieldStringSet.addAll(SEARCH_FIRSTNAME);
                    break;
                case LASTNAME:
                    searchFieldStringSet.addAll(SEARCH_LASTNAME);
                    break;
                case COMPANY:
                    searchFieldStringSet.addAll(SEARCH_COMPANY);
                    break;
                case ADDRESS:
                    searchFieldStringSet.addAll(SEARCH_ADDRESS);
                    break;
                default:
                    break;
            }
        }
        L.debug("Searchfields {} generated hibernate search string values {}", searchField, searchFieldStringSet);
        return searchFieldStringSet;
    }

    private FullTextQuery buildSearchQuery(String search, Set<SearchField> searchField) {
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(Customer.class).get();
        Query query;
        Set<String> searchFieldStringSet = getSearchFieldStringSet(searchField);

        if ( StringUtils.containsWhitespace(search) ) {
            // Multiple Words
            TermContext keyword = qb.keyword();
            TermMatchingContext onField = null;

            if ( !searchFieldStringSet.isEmpty() ) {
                for (String string : searchFieldStringSet) {
                    if ( onField == null ) {
                        onField = keyword.onField(string);
                    } else {
                        onField = onField.andField(string);
                    }
                }
            }

            if ( onField == null ) {
                onField = keyword.onField("customer.id");
            }

            query = onField.matching(search.toLowerCase()).createQuery();
        } else {
            // One Word, wildcards are possibel
            WildcardContext keyword = qb.keyword().wildcard();
            TermMatchingContext onField = null;
            if ( !searchFieldStringSet.isEmpty() ) {
                for (String string : searchFieldStringSet) {
                    if ( onField == null ) {
                        onField = keyword.onField(string);
                    } else {
                        onField = onField.andField(string);
                    }
                }
            }

            if ( onField == null ) {
                onField = keyword.onField("");
            }

            query = onField.matching(search.toLowerCase()).createQuery();
        }
        return ftem.createFullTextQuery(query, Customer.class);
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
