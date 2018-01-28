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

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import lombok.Value;

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
    @Value
    private static class WildCardHelper {

        private final boolean appendWildcard;

        public String trim(String s) {
            return s.toLowerCase() + (appendWildcard ? "%" : "");
        }

    }

    private final static Logger L = LoggerFactory.getLogger(CustomerEao.class);

    private static final Set<String> SEARCH_FIELDS = new HashSet<>();

    static {;
        SEARCH_FIELDS.add("comment");
        SEARCH_FIELDS.add("companies.name");
        SEARCH_FIELDS.add("companies.contacts.title");
        SEARCH_FIELDS.add("companies.contacts.firstName");
        SEARCH_FIELDS.add("companies.contacts.lastName");
        SEARCH_FIELDS.add("companies.contacts.addresses.street");
        SEARCH_FIELDS.add("companies.contacts.addresses.city");
        SEARCH_FIELDS.add("companies.contacts.addresses.zipCode");
        SEARCH_FIELDS.add("companies.contacts.communications.identifier");
        SEARCH_FIELDS.add("companies.addresses.street");
        SEARCH_FIELDS.add("companies.addresses.city");
        SEARCH_FIELDS.add("companies.addresses.zipCode");
        SEARCH_FIELDS.add("companies.communications.identifier");
        SEARCH_FIELDS.add("contacts.title");
        SEARCH_FIELDS.add("contacts.firstName");
        SEARCH_FIELDS.add("contacts.lastName");
        SEARCH_FIELDS.add("contacts.addresses.street");
        SEARCH_FIELDS.add("contacts.addresses.city");
        SEARCH_FIELDS.add("contacts.addresses.zipCode");
        SEARCH_FIELDS.add("contacts.communications.identifier");
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
        JPAQuery query = new JPAQuery(em).from(customer);
        if ( !isBlank(companyName) ) {
            query.join(customer.companies, company).on(company.name.lower().like(W.trim(companyName)));
        }
        if ( !isBlank(firstName) || !isBlank(lastName) ) {
            query.join(customer.contacts, contact).on();
            BooleanExpression on = null;
            if ( !isBlank(firstName) ) {
                on = contact.firstName.lower().like(W.trim(firstName));
            }
            if ( !isBlank(lastName) ) {
                BooleanExpression second = contact.lastName.lower().like(W.trim(lastName));
                if ( on != null ) on = on.and(second);
                else on = second;
            }
            query.on(on);
        }
        if ( !isBlank(email) ) {
            query.join(customer.contacts, contact).join(contact.communications, communication)
                    .on(communication.type.eq(EMAIL).and(communication.identifier.lower().like(W.trim(email))));
        }
        L.debug("calling query");
        List<Customer> list = query.list(customer);
        L.debug("Query successful wiht {}", list);
        return list;
    }

    /**
     * This Method search for a Customer by his Id or, Company or Firstname or Lastname.
     * First it searchs for the CustomerId via sql. Second an index search using the fields company, firsname, lastname is executed.
     * The combiened result is returned.
     * <p/>
     * @param search the search parameter
     * @return the result of the search
     */
    public List<Customer> find(String search) {
        if ( StringUtils.isBlank(search) ) return new ArrayList<>();
        search = search.trim();
        List<Customer> result = new ArrayList<>();
        findCustomerIfSearchIsId(search).ifPresent(e -> result.add(e));
        result.addAll(buildSearchQuery(search).getResultList());
        return result;
    }

    private void findOneCustomer(String search, List<Customer> result) {
        try {
            Long kid = Long.valueOf(search);
            Customer find = em.find(Customer.class, kid);
            if ( find != null ) result.add(find);
        } catch (NumberFormatException numberFormatException) {
            // If not a number, ignore.
        }
    }

    /**
     * See {@link CustomerEao#find(java.lang.String) } but with limits for partial result retrieval.
     * Hint: The first result may contain one element extra.
     *
     * @param start  the starting result
     * @param limit  the ending result
     * @param search the search parameter
     * @return the result of the search
     */
    public List<Customer> find(String search, int start, int limit) {
        if ( StringUtils.isBlank(search) ) return new ArrayList<>();
        search = search.trim();
        // Ensure, that the first result is the customer, if the search is a matching customer id.
        List<Customer> result = new ArrayList<>();
        if ( start == 0 ) findCustomerIfSearchIsId(search).ifPresent(e -> result.add(e));
        result.addAll(buildSearchQuery(search).setFirstResult(start).setMaxResults(limit).getResultList());
        return result;
    }

    /**
     * Count the result of the search.
     *
     * @param search the search.
     * @return the estimated amount for the search
     */
    public int countFind(String search) {
        return buildSearchQuery(search).getResultSize();
    }

    /**
     * Returns a list of all System customer Ids.
     * <p>
     * @return a list of all System customer Ids.
     */
    public List<Long> findAllSystemCustomerIds() {
        return new JPAQuery(em).from(customer).where(customer.flags.contains(CustomerFlag.SYSTEM_CUSTOMER)).list(customer.id);
    }

    private FullTextQuery buildSearchQuery(String search) {
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(Customer.class).get();
        Query query;
        if ( StringUtils.containsWhitespace(search) ) {
            // Multiple Words
            TermContext keyword = qb.keyword();
            TermMatchingContext onField = null;
            for (String string : SEARCH_FIELDS) {
                if ( onField == null ) onField = keyword.onField(string);
                else onField = onField.andField(string);
            }
            query = onField.matching(search.toLowerCase()).createQuery();
        } else {
            // One Word, wildcards are possibel
            WildcardContext keyword = qb.keyword().wildcard();
            TermMatchingContext onField = null;
            for (String string : SEARCH_FIELDS) {
                if ( onField == null ) onField = keyword.onField(string);
                else onField = onField.andField(string);
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
