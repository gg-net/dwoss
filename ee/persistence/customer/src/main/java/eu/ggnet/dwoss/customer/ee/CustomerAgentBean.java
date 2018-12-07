/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.common.ee.log.AutoLogger;
import eu.ggnet.dwoss.customer.ee.entity.stash.*;
import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.saft.api.Reply;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.NonNull;

import static eu.ggnet.dwoss.customer.ee.entity.QAddressLabel.*;
import static eu.ggnet.dwoss.customer.ee.entity.QCustomer.*;
import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;

/**
 * implementaion of the CustomerAgent
 *
 * @author jens.papenhagen
 */
@Stateless
public class CustomerAgentBean extends AbstractAgentBean implements CustomerAgent {

    private final static Logger L = LoggerFactory.getLogger(CustomerAgentBean.class);

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    private CustomerEao customerEao;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesdata;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public List<PicoCustomer> search(String search, Set<SearchField> customerFields) {
        return customerEao.find(search, customerFields).stream()
                .map(Customer::toPico)
                .collect(Collectors.toList());
    }

    @Override
    public List<PicoCustomer> search(String search, Set<SearchField> customerFields, int start, int limit) {
        return customerEao.find(search, customerFields, start, limit).stream()
                .map(Customer::toPico)
                .collect(Collectors.toList());
    }

    @Override
    public int countSearch(String search, Set<SearchField> customerFields) {
        return customerEao.countFind(search, customerFields);
    }

    private <T> T request(List<T> in, Supplier<T> producer) {
        if ( in.isEmpty() ) {
            T t = producer.get();
            in.add(t);
            return t;
        }
        return in.get(0);
    }

    private void update(Customer customer, List<Communication> communications, Communication.Type type, String identifier) {
        if ( StringUtils.isBlank(identifier) ) {
            communications.stream()
                    .filter(co -> co.getType() == type)
                    .findFirst().ifPresent(comm -> {
                        communications.remove(comm);
                        if ( comm.getType() == EMAIL ) customer.setDefaultEmailCommunication(null);
                        em.remove(comm);
                    });
        } else {
            Optional<Communication> optComm = communications.stream()
                    .filter(co -> co.getType() == type)
                    .findFirst();
            optComm.ifPresent(email -> {
                email.setIdentifier(identifier);
            });
            if ( !optComm.isPresent() ) {
                Communication comm = new Communication();
                communications.add(comm);
                comm.setType(type);
                comm.setIdentifier(identifier);
                if ( comm.getType() == EMAIL ) customer.setDefaultEmailCommunication(comm);
            }
        }
    }

    @Override
    public Reply<Customer> store(SimpleCustomer simpleCustomer) {
        boolean exists = (simpleCustomer.getId() > 0);
        boolean bussines = !StringUtils.isBlank(simpleCustomer.getCompanyName());

        Customer customer;
        if ( exists ) {
            customer = em.find(Customer.class, simpleCustomer.getId());
            Objects.requireNonNull(customer, "No Customer with id " + simpleCustomer.getId() + ", error");
        } else {
            customer = new Customer();
        }

        Company comp = null;
        Address a;
        Contact cont;
        AddressLabel al;
        if ( bussines ) {
            comp = request(customer.getCompanies(), () -> new Company());
            comp.setName(simpleCustomer.getCompanyName());
            comp.setTaxId(simpleCustomer.getTaxId());
            cont = request(comp.getContacts(), () -> new Contact());
            a = request(comp.getAddresses(), () -> new Address());
        } else {
            cont = request(customer.getContacts(), () -> new Contact());
            a = request(cont.getAddresses(), () -> new Address());
        }
        al = new AddressLabel(comp, cont, a, AddressType.INVOICE);
        // TODO: Remove old, reuse old ?
        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(al);

        cont.setFirstName(simpleCustomer.getFirstName());
        cont.setLastName(simpleCustomer.getLastName());
        cont.setSex(simpleCustomer.getSex());
        cont.setTitle(simpleCustomer.getTitle());

        //Contact with only one Address
        a.setCity(simpleCustomer.getCity());
        a.setCountry(simpleCustomer.getCountry());
        a.setStreet(simpleCustomer.getStreet());
        a.setZipCode(simpleCustomer.getZipCode());

        update(customer, cont.getCommunications(), Type.EMAIL, simpleCustomer.getEmail());
        update(customer, cont.getCommunications(), Type.PHONE, simpleCustomer.getLandlinePhone());
        update(customer, cont.getCommunications(), Type.MOBILE, simpleCustomer.getMobilePhone());

        L.info("Trying Customer {} with coms: {} ", simpleCustomer.getLastName(), cont.getCommunications());

        customer.setSource(simpleCustomer.getSource());
        if ( !customer.isValid() ) return Reply.failure(customer.getViolationMessage());

        if ( !exists ) em.persist(customer);
        return Reply.success(customer);
    }

    @Override
    public String findCustomerAsMandatorHtml(long id) {
        return Optional.ofNullable(customerEao.findById(id))
                .map(c -> c.toHtml(mandator.getMatchCode(), salesdata))
                .orElse("Kein Kunde mit id " + id + " vorhanden");
    }

    @Override
    public String findCustomerAsHtml(long id) {
        return Optional.ofNullable(customerEao.findById(id))
                .map(Customer::toHtml)
                .orElse("Kein Kunde mit id " + id + " vorhanden");
    }

    @AutoLogger
    @Override
    /**
     * Create a raw object on given root. If the root element is not supported or not found by this method an IllegalArgumentException gets thrown.
     * If the raw object is not supported by this method an IllegalArguemntException gets thrown.
     * Both root and raw are not allowed to be null.
     */
    public <T> T create(@NonNull Root root, @NonNull T raw) {
        Object rootElement = findById(root.getClazz(), root.getId());
        if ( rootElement == null ) throw new IllegalArgumentException("Root instance could not be found Root:" + root);
        if ( raw instanceof Address && AddressStash.class.isAssignableFrom(rootElement.getClass()) ) {
            ((AddressStash)rootElement).getAddresses().add((Address)raw);
        } else if ( raw instanceof AddressLabel && rootElement.getClass() == Customer.class ) {
            AddressLabel al = (AddressLabel)raw;
            Customer c = (Customer)rootElement;
            Address add = findById(Address.class, al.getAddress().getId());
            Company comp = al.getCompany() == null ? null : findById(Company.class, al.getCompany().getId());
            Contact cont = al.getContact() == null ? null : findById(Contact.class, al.getContact().getId());
            al.setCompany(comp);
            al.setContact(cont);
            al.setAddress(add);
            al.setCustomer(c);
        } else if ( raw instanceof Company && rootElement.getClass() == Customer.class ) {
            ((Customer)rootElement).getCompanies().add((Company)raw);
        } else if ( raw instanceof Contact && ContactStash.class.isAssignableFrom(rootElement.getClass()) ) {
            ((ContactStash)rootElement).getContacts().add((Contact)raw);
        } else if ( raw instanceof Communication && CommunicationStash.class.isAssignableFrom(rootElement.getClass()) ) {
            ((CommunicationStash)rootElement).getCommunications().add((Communication)raw);
        } else throw new IllegalArgumentException("Root and Raw instance are not supported. Root: " + root + ", Instance: " + raw);
        em.persist(raw);
        return raw;
    }

    @AutoLogger
    @Override
    /**
     * Delete a raw object from given root. If the root element is not supported or not found by this method an IllegalArgumentException gets thrown.
     * If the raw object is not supported by this method an IllegalArguemntException gets thrown.
     * Both root and raw are not allowed to be null.
     *
     * @throws
     */
    public void delete(@NonNull Root root, @NonNull Object raw) throws IllegalArgumentException {

        Object rootElement = findById(root.getClazz(), root.getId());
        if ( rootElement == null ) throw new IllegalArgumentException("Root instance could not be found Root:" + root);
        if ( raw instanceof Address && AddressStash.class.isAssignableFrom(rootElement.getClass()) ) {
            Address address = (Address)raw;
            //validate possible address label reference
            if ( isReferencdByAddressLabel(addressLabel.address.id, address.getId()) )
                throw new IllegalStateException(address + " is part of an AddressLabel, delete not allowed");

            ((AddressStash)rootElement).getAddresses().remove(address);

        } else if ( raw instanceof Company && rootElement.getClass() == Customer.class ) {
            Company company = (Company)raw;
            //check for possible address label reference
            if ( isReferencdByAddressLabel(addressLabel.company.id, company.getId()) )
                throw new IllegalStateException(company + " is part of an AddressLabel, delete not allowed");
            //prevent last company removal
            if ( isLastCompanyOnCustomer(company) )
                throw new IllegalStateException("Company can not be deleted, because it`s only one present");

            ((Customer)rootElement).getCompanies().remove(company);

        } else if ( raw instanceof Contact && ContactStash.class.isAssignableFrom(rootElement.getClass()) ) {

            Contact contact = (Contact)raw;
            //check for possible address label reference
            if ( isReferencdByAddressLabel(addressLabel.contact.id, contact.getId()) )
                throw new IllegalStateException(contact + " is part of an AddressLabel, delete not allowed");
            
            //prevent last contact removal
            if (rootElement instanceof Customer && isLastContactOnCustomer(contact) )
                throw new IllegalStateException("Contact can not be deleted, because it`s only one present");

            ((ContactStash)rootElement).getContacts().remove(contact);

        } else if ( raw instanceof Communication && CommunicationStash.class.isAssignableFrom(rootElement.getClass()) ) {
            Communication comm = (Communication)raw;
            if ( isLastCommunicationOnCustomer(rootElement, comm) )
                throw new IllegalStateException(comm + " is the last communication in a customer. Delete not allowed.");
            
            ((CommunicationStash)rootElement).getCommunications().remove(comm);
            if ( comm.getType() == EMAIL ) {
                Optional.ofNullable(customerEao.findByDefaultEmailCommunication(comm)).ifPresent(c -> c.setDefaultEmailCommunication(null));
            }
        } else throw new IllegalArgumentException("Root and Raw instance are not supported. Root: " + root + ", Instance: " + raw);

    }

    private boolean isReferencdByAddressLabel(NumberPath<Long> pathidOfAddressCompanyOrContact, long id) {
        return new JPAQuery<>(em).from(addressLabel).where(pathidOfAddressCompanyOrContact.eq(id)).fetchCount() > 0;
    }

    /**
     * Checks if the given company is the last one present on the referenced customer.
     *
     * @param company company that might be the last one present
     * @return if the given company is the last one present on the referenced customer.
     */
    private boolean isLastCompanyOnCustomer(Company company) {
        return new JPAQuery<>(em).from(QCustomer.customer).select(QCustomer.customer)
                .where(QCustomer.customer.companies.contains(company)).fetchOne().getCompanies().size() <= 1;
    }

    /**
     * Checks if the given contact is the last one present on the referenced customer.
     *
     * This customer is expected to be on a consumer customer, not on a company.
     * 
     * @param contact contact that might be the last one present
     * @return if the given contact is the last one present on the referenced customer.
     */
    private boolean isLastContactOnCustomer(Contact contact) {
        return new JPAQuery<>(em).from(QCustomer.customer).select(QCustomer.customer)
                .where(QCustomer.customer.contacts.contains(contact)).fetchOne().getContacts().size() <= 1;
    }

    /**
     * Validates if a given communcation is the last one present on a customer.
     *
     * @param root          root object the communication comes from
     * @param communication the communication instance
     */
    private boolean isLastCommunicationOnCustomer(Object root, Communication communication) {

        List<Customer> onlyOnePresent = null;
        if ( root instanceof Company ) {
            //find customer based on company root object
            onlyOnePresent = new JPAQuery<>(em)
                    .from(QCustomer.customer)
                    .select(QCustomer.customer)
                    .where(QCustomer.customer.companies.contains((Company)root))
                    .fetch();
        } else if ( root instanceof Contact ) {
            //find customer based on contact root object
            onlyOnePresent = new JPAQuery<>(em)
                    .select(QCustomer.customer)
                    .from(QContact.contact)
                    .join(QCustomer.customer).on(QCustomer.customer.contacts.contains(QContact.contact))
                    .where(QContact.contact.communications.contains(communication))
                    .fetch();
        } else {
            throw new IllegalArgumentException("Object: " + root + " is not allowed for communication removal");
        }

        if ( onlyOnePresent.size() > 1 ) {
            throw new IllegalStateException("Multiple customers with the same Contact. Found: " + onlyOnePresent);
        } else if ( onlyOnePresent.isEmpty() ) {
            throw new IllegalStateException("No customers found for contact: " + root);
        }

        Customer c = onlyOnePresent.get(0);
        if ( root instanceof Company ) {
            return c.getCompanies().stream()
                    .flatMap(
                            com -> Stream.concat(
                                    com.getContacts().stream().flatMap(con -> con.getCommunications().stream()),
                                    com.getCommunications().stream()
                            )
                    ).count() <= 1;

        } else if ( root instanceof Contact ) {
            return c.getContacts().stream().flatMap(con -> con.getCommunications().stream()).count() <= 1;

        }

        return false;
    }

    @AutoLogger
    @Override
    /**
     * Update object t. t is not allowed to be null.
     */
    public <T> T update(@NonNull T t) {
        return em.merge(t);
    }

    @Override
    public List<Customer> search(String company, String firstName, String lastName, String email, boolean appendWildcard) {
        List<Customer> results = customerEao.find(company, firstName, lastName, email, appendWildcard);
        results.forEach(Customer::fetchEager);
        return results;
    }

    @AutoLogger
    @Override
    public Customer clearDefaultEmailCommunication(long customerId) {
        Customer c = findByIdEager(Customer.class, customerId);
        c.setDefaultEmailCommunication(null);
        return c;
    }

    @AutoLogger
    @Override
    public Customer setDefaultEmailCommunication(long customerId, long communicationId) {
        Customer c = findByIdEager(Customer.class, customerId);
        Communication com = em.find(Communication.class, communicationId);
        c.setDefaultEmailCommunication(com);
        return c;
    }

    @Override
    public Customer normalizedStoreMandatorMetadata(long customerId, MandatorMetadata mm) {
        Customer customer = findByIdEager(Customer.class, customerId);
        // The supplied instance is the same as defaults, don't store.
        if ( customer.getMandatorMetadata(mandator.getMatchCode()) == null && mm.isSameAs(salesdata) ) {
            return customer;
        } else if ( customer.getMandatorMetadata(mandator.getMatchCode()) != null ) { // The Metadata exist on the customer, just merge
            // TODO: This is shit, make it better please
            if ( mm.isSameAs(salesdata) ) { // Delete case
                for (Iterator<MandatorMetadata> iterator = customer.getMandatorMetadata().iterator(); iterator.hasNext();) {
                    MandatorMetadata next = iterator.next();
                    if ( next.equals(mm) ) {// Id equals
                        iterator.remove();
                    }
                }
            } else { // update case
                mm.normalize(salesdata);
                em.merge(mm);
                em.flush();
                customer = findByIdEager(Customer.class, customerId); // reload the customer after the merge
            }
        } else { // New Metadata.
            mm.normalize(salesdata);
            customer.getMandatorMetadata().add(mm);
        }
        return customer;
    }
}
