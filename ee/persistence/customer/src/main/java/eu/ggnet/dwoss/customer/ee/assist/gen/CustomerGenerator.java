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

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.values.ShippingCondition;
import eu.ggnet.dwoss.core.common.values.AddressType;
import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.core.common.values.PaymentCondition;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;

import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.core.system.generator.*;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.EMAIL;
import static eu.ggnet.dwoss.customer.ee.entity.Contact.Sex.FEMALE;
import static eu.ggnet.dwoss.customer.ee.entity.Contact.Sex.MALE;

/**
 *
 * @author oliver.guenther
 */
public class CustomerGenerator {

    private final static EnumSet<CustomerFlag> ALLOWED_FLAGS = EnumSet.complementOf(EnumSet.of(CustomerFlag.SYSTEM_CUSTOMER));

    private final static Random R = new Random();

    private static class RandomEnum<T extends Enum> {

        private final T[] values;

        public RandomEnum(Class<T> clazz) {
            values = clazz.getEnumConstants();
        }

        public T random() {
            return values[R.nextInt(values.length)];
        }
    }

    private static final NameGenerator GEN = new NameGenerator();

    public static Company makeCompany() {
        return makeCompany(new Company());
    }

    public static Company makeCompanyWithId(long companyId) {
        return makeCompany(new Company(companyId));

    }

    /**
     * Generates a {@link Contact} without address.
     * {@link Contact#prefered} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public static Contact makeBusinessContact() {
        return makeContact(new Contact(), null, makeCommunication());
    }

    /**
     * Generates a {@link Contact}.
     * {@link Contact#prefered} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public static Contact makeContact() {
        return makeContact(new Contact(), makeAddress(), makeCommunication());
    }

    public static Contact makeContactWithId(long contactId, long addressId, long communicationId) {
        return makeContact(new Contact(contactId), makeAddressWithId(addressId), makeCommunicationWithId(communicationId));
    }

    /**
     * Generates a {@link Address}.
     * {@link Address#preferedType} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public static Address makeAddress() {
        return makeAddress(new Address());
    }

    public static Address makeAddressWithId(long id) {
        return makeAddress(new Address(id));
    }

    /**
     * Generates a non persisted {@link Communication}.
     * {@link Communication#prefered} is never set.
     * <p>
     * @return a generated {@link Communication}.
     */
    public static Communication makeCommunication() {
        return makeCommunication(new Communication());
    }

    /**
     * Generates a non persisted valid {@link Communication} of the supplied type.
     *
     * @param type the type to be generated
     * @return a new communication
     */
    public static Communication makeCommunication(Type type) {
        return makeCommunication(new Communication(), type, Assure.defaults().emailDomain());
    }

    public static Communication makeCommunicationWithId(long id) {
        return makeCommunication(new Communication(id));
    }

    /**
     * Generates a non persisted {@link MandatorMetadata}.
     * <p>
     * @return a generated {@link MandatorMetadata}.
     */
    public static MandatorMetadata makeMandatorMetadata() {
        return makeMandatorMetadata(RandomStringUtils.randomAlphanumeric(4));
    }

    /**
     * Generates a random cusomter, which is a consumer and simple.
     *
     * @return a random simple customer.
     */
    public static Customer makeSimpleConsumerCustomer() {
        return makeCustomer(new Assure.Builder().simple(true).consumer(true).build());
    }

    /**
     * Generates a random customer, which is bussines and simple.
     *
     * @return a random simple bussines.
     */
    public static Customer makeSimpleBussinesCustomer() {
        return makeCustomer(new Assure.Builder().simple(true).business(true).build());
    }

    /**
     * Generates a random {@link Customer}, simple or complex, business or consumer, never a systemcustomer.
     * <p>
     * This customer will contain randomly generated collections for:<ul>
     * <li>{@link Customer#contacts}</li>
     * <li>{@link Customer#mandatorMetadata}</li>
     * </ul>
     * with a maximum of 15 each.
     * <p>
     * @return a generated {@link Customer}.
     */
    public static Customer makeCustomer() {
        return makeCustomer(Assure.defaults());
    }

    /**
     * Generates a new customer assuring the supplied restrictions, never a Systemcustomer.
     *
     * @param assure the assurence
     * @return the new customer
     */
    public static Customer makeCustomer(Assure assure) {
        Objects.requireNonNull(assure, "assure must not be null");
        Customer c;
        if ( assure.simple() && assure.business() ) {
            c = internalMakeSimpleBussinesCustomer(assure);
        } else if ( assure.simple() && assure.consumer() ) {
            c = internalMakeSimpleConsumerCustomer(assure);
        } else if ( assure.simple() ) {
            c = (R.nextBoolean() ? internalMakeSimpleConsumerCustomer(assure) : internalMakeSimpleBussinesCustomer(assure));
        } else {
            // Still a 30% chance of a simple customer.
            c = R.nextDouble() > 0.3 ? internalMakeConsumerCustomer(assure)
                    : (R.nextBoolean() ? internalMakeSimpleConsumerCustomer(assure) : internalMakeSimpleBussinesCustomer(assure));
        }
        // Customer will have at least one email.
        if ( assure.useResellerListEmailCommunication() || R.nextDouble() >= 0.7 ) c.setResellerListEmailCommunication(c.getAllCommunications(EMAIL).get(0));
        // Valid simple customer has that by default.
        if ( assure.defaultEmailCommunication() || R.nextDouble() >= 0.7 ) c.setDefaultEmailCommunication(c.getAllCommunications(EMAIL).get(0));

        assure.mandatorMetadataMatchCodes().forEach((mc) -> {
            c.getMandatorMetadata().add(makeMandatorMetadata(mc));
        });
        c.getFlags().remove(CustomerFlag.SYSTEM_CUSTOMER); // Never generate a Systemcustomer here.
        if ( !c.isValid() ) throw new RuntimeException("Generated Customer is invalid, should never happen: " + c.getViolationMessage());
        return c;
    }

    /**
     * Allways creates an email.
     *
     * @param assure
     * @return
     */
    private static Customer internalMakeConsumerCustomer(Assure assure) {
        Customer customer = new Customer();
        int r = R.nextInt(5) + 1;
        for (int i = 0, mail = 0; i < r || mail == 0; i++) { // mail, make sure that at least one contact with email exists
            Contact con = makeContact(new Contact(), makeAddress(),
                    makeCommunication(new Communication(), new RandomEnum<>(Communication.Type.class).random(), assure.emailDomain()));
            if ( con.getCommunications().get(0).getType() == EMAIL ) mail = 1;
            customer.getContacts().add(con);
        }

        // vial the mail = 1 it is ensured, that at least one email exsists. So we can take the first one.
        if ( assure.useResellerListEmailCommunication() || R.nextDouble() >= 0.7 )
            customer.setResellerListEmailCommunication(customer.getAllCommunications(EMAIL).get(0));

        customer.getAddressLabels().add(new AddressLabel(customer.getContacts().get(0), customer.getContacts().get(0).getAddresses().get(0), AddressType.INVOICE));
        if ( R.nextDouble() < 0.4 ) {
            customer.getFlags().add(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY);
        }
        customer.setComment("Das ist ein Kommentar zum Kunden");

        if ( !customer.isValid() ) throw new RuntimeException("Generated a invalid customer, repair generator: " + customer.getViolationMessage());
        return customer;
    }

    /**
     * Allways creates an email.
     *
     * @param assure
     * @return
     */
    private static Customer internalMakeSimpleConsumerCustomer(Assure assure) {
        Customer customer = new Customer();

        Contact con = makeContact(new Contact(), makeAddress(), makeCommunication(Type.PHONE));
        Communication email = makeCommunication(new Communication(), Type.EMAIL, assure.emailDomain());
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
     * Allways creates an email.
     *
     * @param assure
     * @return
     */
    private static Customer internalMakeSimpleBussinesCustomer(Assure assure) {
        Customer customer = new Customer();

        Contact contact = makeContact(new Contact(), null, makeCommunication(Type.PHONE));
        Communication email = makeCommunication(new Communication(), Type.EMAIL, assure.emailDomain());
        contact.getCommunications().add(email);
        customer.setDefaultEmailCommunication(email);

        if ( assure.useResellerListEmailCommunication() || R.nextDouble() >= 0.7 ) customer.setResellerListEmailCommunication(email);

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

    private static Communication makeCommunication(Communication c) {
        return makeCommunication(c, new RandomEnum<>(Communication.Type.class).random(), "example.local");
    }

    private static Communication makeCommunication(Communication c, Type type, String domain) {
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
                c.setIdentifier(RandomStringUtils.randomAlphabetic(10) + "@" + domain);
                break;
            default:
                c.setIdentifier(RandomStringUtils.randomAlphanumeric(10));
                break;
        }
        return c;
    }

    private static MandatorMetadata makeMandatorMetadata(String matchcode) {
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
    private static Company makeCompany(Company company) {
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

    private static Contact makeContact(Contact contact, Address address, Communication communication) {
        Name name = GEN.makeName();
        contact.setFirstName(name.getFirst());
        contact.setLastName(name.getLast());
        contact.setSex(name.getGender().ordinal() == 1 ? FEMALE : MALE);
        contact.setTitle(R.nextInt(1000) % 3 == 0 ? "Dr." : null);
        if ( communication != null ) contact.getCommunications().add(communication);
        if ( address != null ) contact.getAddresses().add(address);
        return contact;
    }

    private static Address makeAddress(Address address) {
        GeneratedAddress genereratedAddress = GEN.makeAddress();
        address.setCity(genereratedAddress.getTown());
        address.setStreet(genereratedAddress.getStreet());
        address.setZipCode(genereratedAddress.getPostalCode());
        return address;
    }

}
