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
package eu.ggnet.dwoss.customer.ee.assist.gen;

import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;
import eu.ggnet.dwoss.customer.ee.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.util.gen.*;

import static eu.ggnet.dwoss.customer.ee.entity.Contact.Sex.FEMALE;
import static eu.ggnet.dwoss.customer.ee.entity.Contact.Sex.MALE;
import static eu.ggnet.dwoss.common.api.values.SalesChannel.RETAILER;

/**
 *
 * @author oliver.guenther
 */
public class CustomerGenerator {

    private final static EnumSet<CustomerFlag> ALLOWED_FLAGS = EnumSet.complementOf(EnumSet.of(CustomerFlag.SYSTEM_CUSTOMER));

    private final Random R = new Random();

    private class RandomEnum<T extends Enum> {

        private final T[] values;

        public RandomEnum(Class<T> clazz) {
            values = clazz.getEnumConstants();
        }

        public T random() {
            return values[R.nextInt(values.length)];
        }
    }

    private final NameGenerator GEN = new NameGenerator();

    /**
     * Generates a {@link Customer}.
     * This customer will contain randomly generated collections for:<ul>
     * <li>{@link Customer#companies}</li>
     * <li>{@link Customer#contacts}</li>
     * <li>{@link Customer#mandatorMetadata}</li>
     * </ul>
     * with a maximum of 15 each.
     * <p>
     * @return a generated {@link Customer}.
     */
    public Customer makeCustomer() {
        Customer customer = new Customer();
        int r = R.nextInt(5) + 1;
        boolean prefered = false;
        for (int i = 0; i < r; i++) {
            Contact con = makeContact();
            if ( !prefered ) {
                prefered = R.nextBoolean();
                con.setPrefered(prefered);
            }
            customer.getContacts().add(con);
        }
        if ( !prefered ) {
            customer.getContacts().iterator().next().setPrefered(true);
        }

        customer.getAddressLabels().add(new AddressLabel(customer.getContacts().get(0), customer.getContacts().get(0).getAddresses().get(0), AddressType.INVOICE));
        customer.getMandatorMetadata().add(makeMandatorMetadata());
        if ( R.nextBoolean() ) {
            customer.getFlags().add(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY);
        }
        customer.setComment("Das ist ein Kommentar zum Kunden");
        if ( !customer.isValid() ) throw new RuntimeException("Generated a invalid customer, repair generator: " + customer.getViolationMessage());
        return customer;
    }

    /**
     * Generates a random cusomter, which is a consumer and simple.
     *
     * @return a random simple customer.
     */
    public Customer makeSimpleConsumerCustomer() {
        Customer customer = new Customer();
        int r = R.nextInt(5) + 1;

        Contact con = makeContact(new Contact(), makeAddress(), new Communication(Type.PHONE, "+49 (555) " + RandomStringUtils.randomNumeric(8)));
        con.getCommunications().add(new Communication(Type.EMAIL, con.getLastName().toLowerCase() + "@demo.int"));
        customer.getContacts().add(con);

        customer.getAddressLabels().add(new AddressLabel(customer.getContacts().get(0), customer.getContacts().get(0).getAddresses().get(0), AddressType.INVOICE));
        customer.setComment("Das ist ein Kommentar zum Kunden");
        if ( !customer.isValid() )
            throw new RuntimeException("makeSimpleConsumerCustomer(): Generated a invalid customer, repair generator: " + customer.getViolationMessage() + "|" + customer);
        if ( !customer.isConsumer() ) throw new RuntimeException("makeSimpleConsumerCustomer(): generated a bussines customer, repair generator: " + customer);
        if ( !customer.isSimple() )
            throw new RuntimeException("makeSimpleConsumerCustomer(): generated a complex customer, repair generator: " + customer.getSimpleViolationMessage() + "|" + customer);
        return customer;
    }

    /**
     * Generates a random customer, which is bussines and simple.
     *
     * @return a random simple bussines.
     */
    public Customer makeSimpleBussinesCustomer() {
        Customer customer = new Customer();
        int r = R.nextInt(5) + 1;

        Contact contact = makeContact(new Contact(), null, new Communication(Type.PHONE, "+49 (555) " + RandomStringUtils.randomNumeric(8)));
        contact.getCommunications().add(new Communication(Type.EMAIL, contact.getLastName().toLowerCase() + "@demo.int"));

        Company company = new Company(GEN.makeCompanyName(), 1000 + R.nextInt(800), true, "DE " + RandomStringUtils.randomNumeric(8));
        company.getContacts().add(contact);
        Address address = makeAddress();
        company.getAddresses().add(address);
        contact.getAddresses().add(address);

        customer.getCompanies().add(company);       

        customer.getAddressLabels().add(new AddressLabel(company, contact, address, AddressType.INVOICE));
        customer.setComment("Das ist ein Kommentar zum Kunden");
        if ( !customer.isValid() )
            throw new RuntimeException("makeSimpleBussinesCustomer(): Generated a invalid customer, repair generator: " + customer.getViolationMessage() + "|" + customer);
        if ( !customer.isBusiness() ) throw new RuntimeException("makeSimpleBussinesCustomer(): generated a consumer customer, repair generator: " + customer);
        if ( !customer.isSimple() )
            throw new RuntimeException("makeSimpleBussinesCustomer(): generated a complex customer, repair generator: " + customer.getSimpleViolationMessage() + "|" + customer);
        return customer;
    }

    /**
     * Generates a {@link Company}.
     * {@link Company#prefered} is never set.
     * <p>
     * @return a generated {@link Company}.
     */
    private Company makeCompany(Company company) {
        company.setLedger(R.nextInt(1000) + 1);
        company.setName(GEN.makeCompanyName());
        Contact contact = makeContact();
        contact.getAddresses().clear();
        contact.getCommunications().clear();

        company.getContacts().add(contact);

        company.getAddresses().add(makeAddress());
        company.getCommunications().add(makeCommunication());

        return company;
    }

    public Company makeCompany() {
        return makeCompany(new Company());
    }

    public Company makeCompanyWithId(long companyId) {
        return makeCompany(new Company(companyId));

    }

    /**
     * Generates an amount of {@link Company}.
     * <p>
     * @param amount the amount
     * @return the generated instances.
     */
    public List<Company> makeCompanies(int amount) {
        List<Company> companylist = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            companylist.add(makeCompany());
        }
        return companylist;
    }

    /**
     * Generates a {@link Contact}.
     * {@link Contact#prefered} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public Contact makeContact() {
        return makeContact(new Contact(), makeAddress(), makeCommunication());
    }

    public Contact makeContactWithId(long contactId, long addressId, long communicationId) {
        return makeContact(new Contact(contactId), makeAddressWithId(addressId), makeCommunicationWithId(communicationId));
    }

    private Contact makeContact(Contact contact, Address address, Communication communication) {
        Name name = GEN.makeName();
        contact.setFirstName(name.getFirst());
        contact.setLastName(name.getLast());
        contact.setSex(name.getGender().ordinal() == 1 ? FEMALE : MALE);
        contact.setTitle(R.nextInt(1000) % 3 == 0 ? "Dr." : null);
        if ( communication != null ) contact.getCommunications().add(communication);
        if ( address != null ) contact.getAddresses().add(address);
        return contact;
    }

    /**
     * Generates an amount of {@link Contact}.
     * <p>
     * @param amount the amount
     * @return the generated instances.
     */
    public List<Contact> makeContacts(int amount) {
        List<Contact> contactslist = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            contactslist.add(makeContact());
        }
        return contactslist;
    }

    /**
     * Generates a {@link Address}.
     * {@link Address#preferedType} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public Address makeAddress() {
        return makeAddress(new Address());
    }

    public Address makeAddressWithId(long id) {
        return makeAddress(new Address(id));
    }

    private Address makeAddress(Address address) {
        GeneratedAddress genereratedAddress = GEN.makeAddress();
        if ( R.nextBoolean() ) address.setPreferedType(new RandomEnum<>(AddressType.class).random());
        address.setCity(genereratedAddress.getTown());
        address.setStreet(genereratedAddress.getStreet());
        address.setZipCode(genereratedAddress.getPostalCode());
        return address;
    }

    /**
     * Generates an amount of persisted {@link Address}.
     * <p>
     * @param amount the amount
     * @return the generated instances.
     */
    public List<Address> makeAddresses(int amount) {
        List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            addresses.add(makeAddress());
        }
        return addresses;
    }

    /**
     * Generates a non persisted invoice {@link AddressLabel}.
     * <p>
     * @return a generated {@link AddressLabel}.
     */
    public AddressLabel makeInVoiceAddressLabel() {
        return new AddressLabel(makeCompany(), makeContact(), makeAddress(), AddressType.INVOICE);
    }

    /**
     * Generates a non persisted shipping {@link AddressLabel}.
     * <p>
     * @return a generated {@link AddressLabel}.
     */
    public AddressLabel makeShippingAddressLabel() {
        return new AddressLabel(makeCompany(), makeContact(), makeAddress(), AddressType.SHIPPING);
    }

    /**
     * Generates a non persisted {@link Communication}.
     * {@link Communication#prefered} is never set.
     * <p>
     * @return a generated {@link Communication}.
     */
    public Communication makeCommunication() {
        return makeCommunication(new Communication());
    }

    public Communication makeCommunicationWithId(long id) {
        return makeCommunication(new Communication(id));
    }

    private Communication makeCommunication(Communication c) {
        c.setType(new RandomEnum<>(Communication.Type.class).random());
        c.setIdentifier(RandomStringUtils.randomAlphanumeric(5));
        if ( c.getType().equals(Type.PHONE) || c.getType().equals(Type.FAX) || c.getType().equals(Type.MOBILE) ) {
            c.setIdentifier(RandomStringUtils.randomNumeric(5));
        }
        if ( c.getType().equals(Type.EMAIL) ) {
            c.setIdentifier("test@test.de");
        }

        return c;
    }

    /**
     * Generates a non persisted {@link MandatorMetadata}.
     * <p>
     * @return a generated {@link MandatorMetadata}.
     */
    public MandatorMetadata makeMandatorMetadata() {
        MandatorMetadata m = new MandatorMetadata();
        m.setMandatorMatchcode(RandomStringUtils.randomAlphanumeric(4));
        m.setPaymentCondition(new RandomEnum<>(PaymentCondition.class).random());
        m.setPaymentMethod(new RandomEnum<>(PaymentMethod.class).random());
        m.setShippingCondition(new RandomEnum<>(ShippingCondition.class).random());
        EnumSet.allOf(SalesChannel.class).stream().forEach(t -> m.add(t));
        return m;
    }

    @Deprecated
    private Customer makeOldCustomer() {
        DefaultCustomerSalesdata salesdata = new DefaultCustomerSalesdata(ShippingCondition.DEFAULT, PaymentCondition.CUSTOMER, PaymentMethod.ADVANCE_PAYMENT, Arrays.asList(SalesChannel.CUSTOMER), Arrays.asList(0L));
        return makeOldCustomer("GEN", salesdata);
    }

    @Deprecated
    private Customer makeOldCustomer(String mandatorMatchCode, DefaultCustomerSalesdata defaults) {
        Name name = GEN.makeName();
        GeneratedAddress address = GEN.makeAddress();
        OldCustomer old = new OldCustomer(null, (name.getGender() == Name.Gender.MALE ? "Herr" : "Frau"), name.getFirst(), name.getLast(), null, address.getStreet() + " " + address.getNumber(), address.getPostalCode(), address.getTown());
        if ( R.nextInt(10) < 3 ) old.setAnmerkung("Eine wichtige Anmerkung");
        if ( R.nextInt(10) < 3 ) old.setSource(Source.values()[R.nextInt(Source.values().length)]);
        if ( R.nextInt(10) < 3 ) {
            old.getAdditionalCustomerIds().put(ExternalSystem.values()[R.nextInt(ExternalSystem.values().length)], RandomStringUtils.randomNumeric(5, 10));
        }
        for (CustomerFlag f : ALLOWED_FLAGS) {
            if ( R.nextInt(10) < 3 ) old.addFlag(f);
        }
        old.setLedger(R.nextInt(10000));

        if ( R.nextInt(10) < 3 ) {
            old.setFirma(old.getNachname() + " GmbH");
            old.setTaxId("HRB123456");
            old.getAllowedSalesChannels().add(RETAILER);
        }
        if ( R.nextInt(10) < 3 ) {
            GeneratedAddress lia = GEN.makeAddress();
            old.setLIAdresse(lia.getStreet());
            old.setLIOrt(lia.getTown());
            old.setLIPlz(lia.getPostalCode());
        }
        old.setEmail(name.getLast().toLowerCase() + "@example.com");
        old.setTelefonnummer("+49 99 123456789");
        old.setHandynummer("+49 555 12344321");
        if ( R.nextInt(10) < 3 ) old.setFaxnummer("+49 88 123456789");

        Customer customer = new Customer();
        ConverterUtil.mergeFromOld(old, customer, mandatorMatchCode, defaults);

        MandatorMetadata makeMandatorMetadata = makeMandatorMetadata();
        makeMandatorMetadata.setMandatorMatchcode(mandatorMatchCode);
        customer.getMandatorMetadata().add(makeMandatorMetadata);

        return customer;
    }

}
