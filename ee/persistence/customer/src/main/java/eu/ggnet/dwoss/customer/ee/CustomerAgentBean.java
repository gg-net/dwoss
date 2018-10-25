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
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.saft.api.Reply;

import lombok.NonNull;

import static eu.ggnet.dwoss.common.api.values.AddressType.INVOICE;
import static eu.ggnet.dwoss.common.api.values.AddressType.SHIPPING;

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

    @Inject
    private MonitorFactory monitorFactory;

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

    private void update(List<Communication> communications, Communication.Type type, String identifier) {
        if ( StringUtils.isBlank(identifier) ) {
            Optional<Communication> optEmail = communications.stream()
                    .filter(co -> co.getType() == type)
                    .findFirst();
            optEmail.ifPresent(email -> {
                communications.remove(email);
                em.remove(email);
            });
        } else {
            Optional<Communication> optEmail = communications.stream()
                    .filter(co -> co.getType() == type)
                    .findFirst();
            optEmail.ifPresent(email -> {
                email.setIdentifier(identifier);
            });
            if ( !optEmail.isPresent() ) {
                Communication email = new Communication();
                communications.add(email);
                email.setType(type);
                email.setIdentifier(identifier);
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
            a = request(comp.getAddresses(), () -> new Address());
            cont = request(comp.getContacts(), () -> new Contact());
            cont.getAddresses().add(a);
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

        update(cont.getCommunications(), Type.EMAIL, simpleCustomer.getEmail());
        update(cont.getCommunications(), Type.PHONE, simpleCustomer.getLandlinePhone());
        update(cont.getCommunications(), Type.MOBILE, simpleCustomer.getMobilePhone());

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
        if ( raw instanceof Address && AddressStash.class.isAssignableFrom(rootElement.getClass()) )
            ((AddressStash)rootElement).getAddresses().add((Address)raw);

        //TODO: Address detached Entity while creating
        else if ( raw instanceof AddressLabel && rootElement.getClass() == Customer.class ) {
            AddressLabel al = (AddressLabel)raw;
            Customer c = (Customer)rootElement;
            Address add = findById(Address.class, al.getAddress().getId());
            Company comp = al.getCompany() == null ? null : findById(Company.class, al.getCompany().getId());
            Contact cont = al.getContact() == null ? null : findById(Contact.class, al.getContact().getId());
            al.setCompany(comp);
            al.setContact(cont);
            al.setAddress(add);
            al.setCustomer(c);
        } else if ( raw instanceof Company && rootElement.getClass() == Customer.class )
            ((Customer)rootElement).getCompanies().add((Company)raw);

        else if ( raw instanceof Contact && ContactStash.class.isAssignableFrom(rootElement.getClass()) )
            ((ContactStash)rootElement).getContacts().add((Contact)raw);

        else if ( raw instanceof MandatorMetadata && rootElement.getClass() == Customer.class )
            ((Customer)rootElement).getMandatorMetadata().add((MandatorMetadata)raw);

        else if ( raw instanceof Communication && CommunicationStash.class.isAssignableFrom(rootElement.getClass()) )
            ((CommunicationStash)rootElement).getCommunications().add((Communication)raw);

        else throw new IllegalArgumentException("Root and Raw instance are not supported. Root: " + root + ", Instance: " + raw);
        em.persist(raw);
        return raw;
    }

    @AutoLogger
    @Override
    /**
     * Delete a raw object from given root. If the root element is not supported or not found by this method an IllegalArgumentException gets thrown.
     * If the raw object is not supported by this method an IllegalArguemntException gets thrown.
     * Both root and raw are not allowed to be null.
     */
    public void delete(@NonNull Root root, @NonNull Object raw) {
        Object rootElement = findByIdEager(root.getClazz(), root.getId());
        if ( rootElement == null ) throw new IllegalArgumentException("Root instance could not be found Root:" + root);
        if ( raw instanceof Address && AddressStash.class.isAssignableFrom(rootElement.getClass()) )
            ((AddressStash)rootElement).getAddresses().remove((Address)raw);

        else if ( raw instanceof Company && rootElement.getClass() == Customer.class )
            ((Customer)rootElement).getCompanies().remove((Company)raw);

        else if ( raw instanceof Contact && ContactStash.class.isAssignableFrom(rootElement.getClass()) )
            ((ContactStash)rootElement).getContacts().remove((Contact)raw);

        else if ( raw instanceof Communication && CommunicationStash.class.isAssignableFrom(rootElement.getClass()) )
            ((CommunicationStash)rootElement).getCommunications().remove((Communication)raw);

        else throw new IllegalArgumentException("Root and Raw instance are not supported. Root: " + root + ", Instance: " + raw);

        em.merge(rootElement);
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

    /**
     * Merges the customer to have at least one invoice adresslabel and a shippinglabel if previously present.
     * <p>
     * This function will not modify anything if any addresslabel is already present.
     *
     * @return
     * @throws IllegalStateException if any of the created addresslabels is not valid
     */
    @Deprecated
    @Override
    public List<String> mergeCustomerAfterAddressLabel() {

        List<String> violations = new ArrayList<>();

        final SubMonitor m = monitorFactory.newSubMonitor("Merging Customer after AddressLabel implementation...");
        m.start();

        List<Customer> allCustomers = findAllEager(Customer.class);

        m.setWorkRemaining(allCustomers.size());

        for (Customer customer : allCustomers) {
            m.message("Merging Customer: " + customer.getId());

            //safety if addresslabels are already present
            if ( customer.getAddressLabels().isEmpty() ) {

                //build invoice label
                AddressLabel invoice = new AddressLabel(
                        customer.getCompanies().stream().filter(Company::isPrefered).findFirst().orElse(null),
                        customer.getContacts().stream().filter(Contact::isPrefered).findFirst().orElse(null),
                        customer.getContacts().stream().filter(Contact::isPrefered).findFirst().map(c -> c.prefered(INVOICE)).orElse(null),
                        INVOICE);

                //build shipping label only if neccesary
                AddressLabel shipping = null;
                AddressType type = customer.getContacts().stream().filter(Contact::isPrefered).findFirst().map(c -> c.prefered(SHIPPING)).map(a -> SHIPPING).orElse(INVOICE);
                if ( type == SHIPPING ) {
                    shipping = new AddressLabel(
                            customer.getCompanies().stream().filter(Company::isPrefered).findFirst().orElse(null),
                            customer.getContacts().stream().filter(Contact::isPrefered).findFirst().orElse(null),
                            customer.getContacts().stream().filter(Contact::isPrefered).findFirst().map(c -> c.prefered(type)).orElse(null),
                            type);
                }

                //add violation info if neccesary and continue to next customer
                if ( invoice.getViolationMessage() != null || (shipping != null && shipping.getViolationMessage() != null) ) {
                    violations.add("Customer: " + customer.getId() + " - " + invoice.getViolationMessage());
                    if ( shipping != null ) violations.add("Customer: " + customer.getId() + " - " + shipping.getViolationMessage());
                    m.worked(1, "Failed merge for Customer: " + customer.getId());
                    continue;
                }

                //add addresslabel and update customer
                customer.getAddressLabels().add(invoice);
                if ( shipping != null ) customer.getAddressLabels().add(shipping);
                update(customer);
                m.worked(1, "Merged Customer: " + customer.getId());
            } else {
                m.worked(1, "Skipped Customer: " + customer.getId());
            }
        }

        m.finish();
        return violations;
    }
}
