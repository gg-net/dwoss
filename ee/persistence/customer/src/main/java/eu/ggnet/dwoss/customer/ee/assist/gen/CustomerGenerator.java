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

import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.util.gen.*;

import lombok.*;

import static eu.ggnet.dwoss.customer.ee.entity.Contact.Sex.FEMALE;
import static eu.ggnet.dwoss.customer.ee.entity.Contact.Sex.MALE;

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

    /**
     * Flags for the generator instead of a million extra methods.
     */
    @Builder
    @Getter
    public static class Assure {

        /**
         * Indicates, that only simple customers must be generated.
         */
        @Builder.Default
        private final boolean simple = false;

        /**
         * For all supplied strings, metadata will be generated. Null or empty indicates no metadata generation.
         */
        @Builder.Default
        private final List<String> mandatorMetadataMatchCodes = null;

    }

    private final NameGenerator GEN = new NameGenerator();

    /**
     * Generates a new customer assureing the supplied restrictions, never a Systemcustomer.
     *
     * @param assure the assurence
     * @return the new customer
     */
    public Customer makeCustomer(@NonNull Assure assure) {
        Customer c = null;
        if ( assure.isSimple() ) {
            c = R.nextBoolean() ? makeSimpleConsumerCustomer() : makeSimpleBussinesCustomer();
        } else {
            c = makeCustomer();
        }
        if ( assure.getMandatorMetadataMatchCodes() != null && assure.getMandatorMetadataMatchCodes().size() > 0 ) {
            for (String mc : assure.getMandatorMetadataMatchCodes()) {
                c.getMandatorMetadata().add(makeMandatorMetadata(mc));
            }
        }
        c.getFlags().remove(CustomerFlag.SYSTEM_CUSTOMER); // Never generate a Systemcustomer here.
        return c;
    }

    /**
     * Generates a {@link Customer}.
     * This customer will contain randomly generated collections for:<ul>
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
        for (int i = 0; i < r; i++) {
            Contact con = makeContact();
            customer.getContacts().add(con);
        }

        customer.getAddressLabels().add(new AddressLabel(customer.getContacts().get(0), customer.getContacts().get(0).getAddresses().get(0), AddressType.INVOICE));
        if ( R.nextDouble() < 0.4 ) {
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

        Contact con = makeContact(new Contact(), makeAddress(), makeCommunication(Type.PHONE));
        Communication email = makeCommunication(Type.EMAIL);
        con.getCommunications().add(email);
        customer.setDefaultEmailCommunication(email);
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

        Contact contact = makeContact(new Contact(), null, makeCommunication(Type.PHONE));
        Communication email = makeCommunication(Type.EMAIL);
        contact.getCommunications().add(email);
        customer.setDefaultEmailCommunication(email);

        Company company = new Company(GEN.makeCompanyName(), 1000 + R.nextInt(800), "DE " + RandomStringUtils.randomNumeric(8));
        company.getContacts().add(contact);
        Address address = makeAddress();
        company.getAddresses().add(address);

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

    public Company makeCompany() {
        return makeCompany(new Company());
    }

    public Company makeCompanyWithId(long companyId) {
        return makeCompany(new Company(companyId));

    }

    /**
     * Generates a {@link Contact} without address.
     * {@link Contact#prefered} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public Contact makeBusinessContact() {
        return makeContact(new Contact(), null, makeCommunication());
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

    /**
     * Generates a non persisted {@link Communication}.
     * {@link Communication#prefered} is never set.
     * <p>
     * @return a generated {@link Communication}.
     */
    public Communication makeCommunication() {
        return makeCommunication(new Communication());
    }

    /**
     * Generates a non persisted valid {@link Communication} of the supplied type.
     *
     * @param type the type to be generated
     * @return a new communication
     */
    public Communication makeCommunication(Type type) {
        return makeCommunication(new Communication(), type);
    }

    public Communication makeCommunicationWithId(long id) {
        return makeCommunication(new Communication(id));
    }

    /**
     * Generates a non persisted {@link MandatorMetadata}.
     * <p>
     * @return a generated {@link MandatorMetadata}.
     */
    public MandatorMetadata makeMandatorMetadata() {
        return makeMandatorMetadata(RandomStringUtils.randomAlphanumeric(4));
    }

    private Communication makeCommunication(Communication c) {
        return makeCommunication(c, new RandomEnum<>(Communication.Type.class).random());
    }

    private Communication makeCommunication(Communication c, Type type) {
        c.setType(type);
        switch (c.getType()) {
            case PHONE:
            case FAX:
            case MOBILE:
                c.setIdentifier("+" + (R.nextInt(8) + 1) + R.nextInt(8)
                        + " " + (R.nextInt(8) + 1) + RandomStringUtils.randomNumeric(3)
                        + " " + (R.nextInt(8) + 1) + RandomStringUtils.randomNumeric(7)
                        + (R.nextBoolean() ? "" : "-" + (R.nextInt(8) + 1) + R.nextInt(8))
                );
                break;
            case EMAIL:
                c.setIdentifier(RandomStringUtils.randomAlphabetic(10) + "@" + RandomStringUtils.randomAlphabetic(8) + ".com");
                break;
            default:
                c.setIdentifier(RandomStringUtils.randomAlphanumeric(10));
                break;
        }
        return c;
    }

    private MandatorMetadata makeMandatorMetadata(String matchcode) {
        MandatorMetadata m = new MandatorMetadata();
        m.setMandatorMatchcode(matchcode);
        m.setPaymentCondition(new RandomEnum<>(PaymentCondition.class).random());
        m.setPaymentMethod(new RandomEnum<>(PaymentMethod.class).random());
        m.setShippingCondition(new RandomEnum<>(ShippingCondition.class).random());
        EnumSet.allOf(SalesChannel.class).stream().forEach(t -> m.getAllowedSalesChannels().add(t));
        return m;
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

    private Address makeAddress(Address address) {
        GeneratedAddress genereratedAddress = GEN.makeAddress();
        address.setCity(genereratedAddress.getTown());
        address.setStreet(genereratedAddress.getStreet());
        address.setZipCode(genereratedAddress.getPostalCode());
        return address;
    }

}
