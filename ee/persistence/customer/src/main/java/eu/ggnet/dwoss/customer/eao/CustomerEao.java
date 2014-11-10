package eu.ggnet.dwoss.customer.eao;

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.customer.entity.Communication.Type;

import eu.ggnet.dwoss.rules.CustomerFlag;

import eu.ggnet.dwoss.util.persistence.eao.AbstractEao;
import eu.ggnet.dwoss.customer.entity.*;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import lombok.Value;

import static eu.ggnet.dwoss.customer.entity.Communication.Type.EMAIL;
import static eu.ggnet.dwoss.customer.entity.QCommunication.communication;
import static eu.ggnet.dwoss.customer.entity.QCompany.company;
import static eu.ggnet.dwoss.customer.entity.QContact.contact;
import static eu.ggnet.dwoss.customer.entity.QCustomer.customer;
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

    private static final Set<String> searchFields = new HashSet<>();

    static {;
        searchFields.add("comment");
        searchFields.add("companies.name");
        searchFields.add("companies.contacts.title");
        searchFields.add("companies.contacts.firstName");
        searchFields.add("companies.contacts.lastName");
        searchFields.add("companies.contacts.addresses.street");
        searchFields.add("companies.contacts.addresses.city");
        searchFields.add("companies.contacts.addresses.zipCode");
        searchFields.add("companies.contacts.communications.identifier");
        searchFields.add("companies.addresses.street");
        searchFields.add("companies.addresses.city");
        searchFields.add("companies.addresses.zipCode");
        searchFields.add("companies.communications.identifier");
        searchFields.add("contacts.title");
        searchFields.add("contacts.firstName");
        searchFields.add("contacts.lastName");
        searchFields.add("contacts.addresses.street");
        searchFields.add("contacts.addresses.city");
        searchFields.add("contacts.addresses.zipCode");
        searchFields.add("contacts.communications.identifier");
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
     * This Method search for a Customer by his Id or, Company or First- or Lastname.
     * First it searchs for the CustomerId when it is possible. If not it search if its 'like' the Company, First- or Lastname.
     * <p/>
     * @param search
     * @return
     */
    public List<Customer> find(String search) {
        if ( StringUtils.isBlank(search) ) return new ArrayList<>();
        search = search.trim();
        List<Customer> result = new ArrayList<>();
        try {
            Long kid = Long.valueOf(search);
            Customer find = em.find(Customer.class, kid);
            if ( find != null ) result.add(find);
        } catch (NumberFormatException numberFormatException) {
            // If not a number, ignore.
        }
        FullTextEntityManager ftem = Search.getFullTextEntityManager(em);
        QueryBuilder qb = ftem.getSearchFactory().buildQueryBuilder().forEntity(Customer.class).get();
        Query query;
        if ( StringUtils.containsWhitespace(search) ) {
            // Multiple Words
            TermContext keyword = qb.keyword();
            TermMatchingContext onField = null;
            for (String string : searchFields) {
                if ( onField == null ) onField = keyword.onField(string);
                else onField = onField.andField(string);
            }
            query = onField.matching(search.toLowerCase()).createQuery();
        } else {
            // One Word, wildcards are possibel
            WildcardContext keyword = qb.keyword().wildcard();
            TermMatchingContext onField = null;
            for (String string : searchFields) {
                if ( onField == null ) onField = keyword.onField(string);
                else onField = onField.andField(string);
            }
            query = onField.matching(search.toLowerCase()).createQuery();
        }

        result.addAll(ftem.createFullTextQuery(query, Customer.class).getResultList());
        return result;
    }

    /**
     * Returns a list of all System customer Ids.
     * <p>
     * @return a list of all System customer Ids.
     */
    public List<Long> findAllSystemCustomerIds() {
        return new JPAQuery(em).from(customer).where(customer.flags.contains(CustomerFlag.SYSTEM_CUSTOMER)).list(customer.id);
    }

}
