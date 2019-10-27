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

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.common.ee.log.AutoLogger;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Customer.SearchField;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.dto.AddressLabelDto;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.customer.ee.entity.stash.*;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.util.persistence.AbstractAgentBean;
import eu.ggnet.saft.api.Reply;

import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;
import static eu.ggnet.dwoss.customer.ee.entity.QAddressLabel.addressLabel;
import static eu.ggnet.dwoss.customer.ee.entity.QCompany.company;
import static eu.ggnet.dwoss.customer.ee.entity.QContact.contact;
import static eu.ggnet.dwoss.customer.ee.entity.QCustomer.customer;

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

    @Override
    public Reply<Customer> store(SimpleCustomer simpleCustomer) {
        L.info("store({})", simpleCustomer);
        boolean exists = (simpleCustomer.getId() > 0);
        boolean bussines = !StringUtils.isBlank(simpleCustomer.getCompanyName());

        Customer customer;
        if ( exists ) {
            customer = findByIdEager(Customer.class, simpleCustomer.getId());
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

        update(customer, cont.getCommunications(), Type.EMAIL, simpleCustomer.getEmail(), simpleCustomer.isUseEmailForResellerList());
        update(customer, cont.getCommunications(), Type.PHONE, simpleCustomer.getLandlinePhone());
        update(customer, cont.getCommunications(), Type.MOBILE, simpleCustomer.getMobilePhone());

        customer.setSource(simpleCustomer.getSource());
        if ( !customer.isValid() ) return Reply.failure(customer.getViolationMessage());

        if ( !exists ) em.persist(customer);
        return Reply.success(customer);
    }

    @Override
    public String findCustomerAsMandatorHtml(long id) {
        return Optional.ofNullable(customerEao.findById(id))
                .map(c -> c.toHtml(mandator.matchCode(), salesdata))
                .orElse("Kein Kunde mit id " + id + " vorhanden");
    }

    @Override
    public String findCustomerAsHtml(long id) {
        return Optional.ofNullable(customerEao.findById(id))
                .map(Customer::toHtml)
                .orElse("Kein Kunde mit id " + id + " vorhanden");
    }

    /**
     * Stores the addresslabels on the customer, all addresslabels must be from one customer.
     * Creating all labels with an id == 0. updateing all with an id <> 0. Deleting all that are missing.
     *
     * @param aldtos
     * @return
     * @throws IllegalArgumentException if the collection is empty.
     */
    @Override
    public Customer autostore(Collection<AddressLabelDto> aldtos) throws IllegalArgumentException {
        Objects.requireNonNull(aldtos, "Collection of AddressLabelDtos must not be null");
        if ( aldtos.isEmpty() ) throw new IllegalArgumentException("Empty collection of addresslabels not allowed");
        // TODO: Verify, that all dtos have only one customerid.
        AddressLabelDto first = aldtos.iterator().next();
        Customer c = findByIdEager(Customer.class, first.getCustomerId());
        Map<Long, AddressLabel> existing = c.getAddressLabels().stream().collect(Collectors.toMap(AddressLabel::getId, a -> a));
        for (AddressLabelDto dto : aldtos) {
            AddressLabel active = null;
            if ( dto.getId() == 0 ) {
                active = new AddressLabel();
            } else {
                active = existing.get(dto.getId());
                existing.remove(dto.getId());
            }
            // Saftynet, schould never happen
            if ( active == null ) throw new IllegalArgumentException("DTO did not find an existing Label" + dto);

            active.setCustomer(c);
            active.setType(dto.getType());
            active.setAddress(em.find(Address.class, dto.getAddressId()));
            if ( dto.getContactId() > 0 ) active.setContact(em.find(Contact.class, dto.getContactId()));
            if ( dto.getCompanyId() > 0 ) active.setCompany(em.find(Company.class, dto.getCompanyId()));
            L.debug("autostore: creating or updating {}", active);
            if ( dto.getId() == 0 ) em.persist(active);
        }
        for (AddressLabel deleteme : existing.values()) {
            deleteme.setAddress(null);
            deleteme.setCustomer(null);
            deleteme.setCompany(null);
            deleteme.setContact(null);
            em.remove(deleteme);
            L.debug("autostore: deleting: {}", deleteme);
        }
        return c;
    }

    @AutoLogger
    @Override
    /**
     * Create a raw object on given root. If the root element is not supported or not found by this method an IllegalArgumentException gets thrown.
     * If the raw object is not supported by this method an IllegalArguemntException gets thrown.
     * Both root and raw are not allowed to be null.
     */
    public <T> T create(Root root, T raw) {
        Objects.requireNonNull(root, "root must not be null");
        Objects.requireNonNull(raw, "raw must not be null");
        Object rootElement = findById(root.clazz, root.id);
        if ( rootElement == null ) throw new IllegalArgumentException("Root instance could not be found Root:" + root);
        if ( raw instanceof Address && AddressStash.class.isAssignableFrom(rootElement.getClass()) ) {
            ((AddressStash)rootElement).getAddresses().add((Address)raw);
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
    public void delete(Root root, Object raw) throws LastDeletionExecption, IllegalArgumentException, IllegalStateException {
        Objects.requireNonNull(root, "root must not be null");
        Objects.requireNonNull(raw, "raw must not be null");
        Object rootElement = findById(root.clazz, root.id);
        if ( rootElement == null ) throw new IllegalArgumentException("Root instance could not be found Root:" + root);
        if ( raw instanceof Address && AddressStash.class.isAssignableFrom(rootElement.getClass()) ) {
            Address address = (Address)raw;
            //validate possible address label reference
            if ( isReferencdByAddressLabel(addressLabel.address.id, address.getId()) )
                throw new LastDeletionExecption(address + " is part of an AddressLabel, delete not allowed");

            ((AddressStash)rootElement).getAddresses().remove(address);

        } else if ( raw instanceof Company && rootElement.getClass() == Customer.class ) {
            Company company = (Company)raw;
            //check for possible address label reference
            if ( isReferencdByAddressLabel(addressLabel.company.id, company.getId()) )
                throw new LastDeletionExecption(company + " is part of an AddressLabel, delete not allowed");
            //prevent last company removal
            if ( isLastCompanyOnCustomer(company) )
                throw new LastDeletionExecption("Company can not be deleted, because it`s only one present");
            for (Address addresse : company.getAddresses()) {
                if ( isReferencdByAddressLabel(addressLabel.address.id, addresse.getId()) )
                    throw new LastDeletionExecption(company + " is part of an AddressLabel, delete not allowed");
            }
            for (Contact contact : company.getContacts()) {
                if ( isReferencdByAddressLabel(addressLabel.contact.id, contact.getId()) )
                    throw new LastDeletionExecption(company + " is part of an AddressLabel, delete not allowed");
            }
            ((Customer)rootElement).getCompanies().remove(company);

        } else if ( raw instanceof Contact && ContactStash.class.isAssignableFrom(rootElement.getClass()) ) {

            Contact contact = (Contact)raw;
            //check for possible address label reference
            if ( isReferencdByAddressLabel(addressLabel.contact.id, contact.getId()) )
                throw new LastDeletionExecption(contact + " is part of an AddressLabel, delete not allowed");
            for (Address addresse : contact.getAddresses()) {
                if ( isReferencdByAddressLabel(addressLabel.address.id, addresse.getId()) )
                    throw new LastDeletionExecption(contact + " is part of an AddressLabel, delete not allowed");
            }
            //prevent last contact removal
            if ( rootElement instanceof Customer && isLastContactOnCustomer(contact) )
                throw new LastDeletionExecption("Contact can not be deleted, because it`s only one present");

            ((ContactStash)rootElement).getContacts().remove(contact);

        } else if ( raw instanceof Communication && CommunicationStash.class.isAssignableFrom(rootElement.getClass()) ) {
            Communication comm = (Communication)raw;
            if ( isLastCommunicationOnCustomer(rootElement, comm) )
                throw new LastDeletionExecption(comm + " is the last communication in a customer. Delete not allowed.");

            ((CommunicationStash)rootElement).getCommunications().remove(comm);
            if ( comm.getType() == EMAIL ) {
                Optional.ofNullable(customerEao.findByDefaultEmailCommunication(comm)).ifPresent(c -> c.setDefaultEmailCommunication(null));
                Optional.ofNullable(customerEao.findByResellerListEmailCommunication(comm)).ifPresent(c -> c.setResellerListEmailCommunication(null));
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
     * <p>
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
        List<Customer> oneCustomer = new JPAQuery<>(em)
                .select(QCustomer.customer)
                .from(customer)
                .join(customer.companies, company)
                .join(company.contacts, contact)
                .where(contact.communications.contains(communication).or(company.communications.contains(communication)))
                .fetch();
        oneCustomer.addAll(new JPAQuery<>(em)
                .select(QCustomer.customer)
                .from(customer)
                .join(customer.contacts, contact)
                .where(contact.communications.contains(communication))
                .fetch());

        if ( oneCustomer.size() > 1 || oneCustomer.isEmpty() || oneCustomer.get(0) == null ) {
            throw new IllegalStateException("In LastCommunicationonCustomer, should never be thorwn: Too many or none or null found: " + oneCustomer);
        }
        return oneCustomer.get(0).getAllCommunications().size() <= 1;
    }

    @AutoLogger
    @Override
    /**
     * Update object t. t is not allowed to be null.
     */
    public <T> T update(T t) {
        Objects.requireNonNull(t);
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

    @AutoLogger
    @Override
    public Customer clearResellerListEmailCommunication(long customerId) {
        Customer c = findByIdEager(Customer.class, customerId);
        c.setResellerListEmailCommunication(null);
        return c;
    }

    @AutoLogger
    @Override
    public Customer setResellerListEmailCommunication(long customerId, long communicationId) {
        Customer c = findByIdEager(Customer.class, customerId);
        Communication com = em.find(Communication.class, communicationId);
        c.setResellerListEmailCommunication(com);
        return c;
    }

    @Override
    public Customer normalizedStoreMandatorMetadata(long customerId, MandatorMetadata mm) {
        Customer customer = findByIdEager(Customer.class, customerId);
        // The supplied instance is the same as defaults, don't store.
        if ( customer.getMandatorMetadata(mandator.matchCode()) == null && mm.isSameAs(salesdata) ) {
            return customer;
        } else if ( customer.getMandatorMetadata(mandator.matchCode()) != null ) { // The Metadata exist on the customer, just merge
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

    private <T> T request(List<T> in, Supplier<T> producer) {
        if ( in.isEmpty() ) {
            T t = producer.get();
            in.add(t);
            return t;
        }
        return in.get(0);
    }

    private void update(Customer customer, List<Communication> communications, Communication.Type type, String identifier) {
        update(customer, communications, type, identifier, false);
    }

    /**
     * Updates the customer with the supplied communications with the new or updated type and identifier.
     *
     * @param customer                      the customer to be updated, impact on defaultEmailCommunication
     * @param communications                the relevant list of communications of the customer (different on consumer and business)
     * @param type                          the type of the communication to update
     * @param identifier                    the identifier, blank means removal
     * @param useEmailAsResellerMailingList if true and type is email, the resellerListMailCommunication will be updated
     */
    private void update(Customer customer, List<Communication> communications, Communication.Type type, String identifier, boolean useEmailAsResellerMailingList) {
        if ( StringUtils.isBlank(identifier) ) {
            communications.stream()
                    .filter(co -> co.getType() == type)
                    .findFirst().ifPresent(comm -> {
                        communications.remove(comm);
                        if ( comm.getType() == EMAIL ) {
                            customer.setDefaultEmailCommunication(null);
                            customer.setResellerListEmailCommunication(null);
                        }
                        em.remove(comm);
                    });
        } else {
            Communication comm = communications.stream()
                    .filter(c -> c.getType() == type)
                    .findFirst()
                    .orElseGet(() -> {
                        Communication c = new Communication();
                        communications.add(c);
                        c.setType(type);
                        c.setIdentifier(identifier);
                        return c;
                    });

            comm.setIdentifier(identifier);
            if ( comm.getType() == EMAIL ) {
                customer.setDefaultEmailCommunication(comm);
                if ( useEmailAsResellerMailingList ) customer.setResellerListEmailCommunication(comm);
                else customer.setResellerListEmailCommunication(null);
            }
        }
    }
}
