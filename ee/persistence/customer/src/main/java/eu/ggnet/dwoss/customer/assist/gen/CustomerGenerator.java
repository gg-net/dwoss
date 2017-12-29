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
package eu.ggnet.dwoss.customer.assist.gen;

import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.entity.Customer.Source;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.priv.ConverterUtil;
import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.gen.*;

import static eu.ggnet.dwoss.customer.entity.Contact.Sex.FEMALE;
import static eu.ggnet.dwoss.customer.entity.Contact.Sex.MALE;
import static eu.ggnet.dwoss.rules.SalesChannel.RETAILER;

/**
 *
 * @author oliver.guenther
 */
public class CustomerGenerator {

    private final static EnumSet<CustomerFlag> ALLOWED_FLAG = EnumSet.of(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY, CustomerFlag.CONFIRMS_DOSSIER);

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
        Customer c = new Customer();
        int r = R.nextInt(5) + 1;
        boolean prefered = false;
        for (int i = 0; i < r; i++) {
            Contact con = makeContact();
            if ( !prefered ) {
                prefered = R.nextBoolean();
                con.setPrefered(prefered);
            }
            c.add(con);
        }
        r = R.nextInt(2) + 1;
        for (int i = 0; i < r; i++) {
            Company com = makeCompany();
            if ( !prefered ) {
                prefered = R.nextBoolean();
                com.setPrefered(prefered);
            }
            c.add(com);
        }
        if ( !prefered ) c.getContacts().iterator().next().setPrefered(true);
        c.add(makeMandatorMetadata());
        if ( R.nextBoolean() ) c.add(CustomerFlag.CONFIRMED_CASH_ON_DELIVERY);
        c.setComment("Date ist eine Kommentar zum Kunden");
        return c;
    }

    /**
     * Generates a {@link Company}.
     * {@link Company#prefered} is never set.
     * <p>
     * @return a generated {@link Company}.
     */
    public Company makeCompany() {
        Company c = new Company();
        c.setLedger(R.nextInt(1000) + 1);
        c.setName(GEN.makeCompanyName());
        while (R.nextBoolean()) {
            c.add(makeCommunication());
        }
        while (R.nextBoolean()) {
            c.add(makeAddress());
        }
        return c;
    }

    /**
     * Generates an amount of {@link Company}.
     * <p>
     * @param amount the amount
     * @return the generated instances.
     */
    public List<Company> makeCompanies(int amount) {
        List<Company> contacts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            contacts.add(makeCompany());
        }
        return contacts;
    }

    /**
     * Generates a {@link Contact}.
     * {@link Contact#prefered} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public Contact makeContact() {
        Contact c = new Contact();
        Name n = GEN.makeName();
        c.setFirstName(n.getFirst());
        c.setLastName(n.getLast());
        c.setSex(n.getGender().ordinal() == 1 ? MALE : FEMALE);
        c.setTitle(R.nextInt(1000) % 3 == 0 ? "Dr." : null);
        while (R.nextBoolean()) {
            c.add(makeCommunication());
        }
        c.add(makeAddress());
        while (R.nextInt() > 70) {
            c.add(makeAddress());
        }
        return c;
    }

    /**
     * Generates an amount of {@link Contact}.
     * <p>
     * @param amount the amount
     * @return the generated instances.
     */
    public List<Contact> makeContacts(int amount) {
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            contacts.add(makeContact());
        }
        return contacts;
    }

    /**
     * Generates a {@link Address}.
     * {@link Address#preferedType} is never set.
     * <p>
     * @return a generated {@link Contact}.
     */
    public Address makeAddress() {
        GeneratedAddress a = GEN.makeAddress();
        Address address = new Address();
        if ( R.nextBoolean() ) address.setPreferedType(new RandomEnum<>(AddressType.class).random());
        address.setCity(a.getTown());
        address.setStreet(a.getStreet());
        address.setZipCode(a.getPostalCode());
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
     * Generates a non persisted {@link Communication}.
     * {@link Communication#prefered} is never set.
     * <p>
     * @return a generated {@link Communication}.
     */
    private Communication makeCommunication() {
        Communication c = new Communication();
        c.setType(new RandomEnum<>(Communication.Type.class).random());
        c.setIdentifier(RandomStringUtils.randomAlphanumeric(5));
        return c;
    }

    private MandatorMetadata makeMandatorMetadata() {
        MandatorMetadata m = new MandatorMetadata();
        m.setMandatorMatchcode(RandomStringUtils.randomAlphanumeric(4));
        m.setPaymentCondition(new RandomEnum<>(PaymentCondition.class).random());
        m.setPaymentMethod(new RandomEnum<>(PaymentMethod.class).random());
        m.setShippingCondition(new RandomEnum<>(ShippingCondition.class).random());
        EnumSet.allOf(SalesChannel.class).stream().filter(t -> R.nextInt(10) < 3).forEach(t -> m.add(t));
        return m;
    }

    public Customer makeOldCustomer() {
        DefaultCustomerSalesdata salesdata = new DefaultCustomerSalesdata(ShippingCondition.DEFAULT, PaymentCondition.CUSTOMER, PaymentMethod.ADVANCE_PAYMENT, Arrays.asList(SalesChannel.CUSTOMER), Arrays.asList(0L));
        return makeOldCustomer("GEN", salesdata);
    }

    public Customer makeOldCustomer(String mandatorMatchCode, DefaultCustomerSalesdata defaults) {
        Name name = GEN.makeName();
        GeneratedAddress address = GEN.makeAddress();
        OldCustomer old = new OldCustomer(null, (name.getGender() == Name.Gender.MALE ? "Herr" : "Frau"), name.getFirst(), name.getLast(), null, address.getStreet() + " " + address.getNumber(), address.getPostalCode(), address.getTown());
        if ( R.nextInt(10) < 3 ) old.setAnmerkung("Eine wichtige Anmerkung");
        if ( R.nextInt(10) < 3 ) old.setSource(Source.values()[R.nextInt(Source.values().length - 1)]);
        if ( R.nextInt(10) < 3 ) {
            old.getAdditionalCustomerIds().put(ExternalSystem.values()[R.nextInt(ExternalSystem.values().length - 1)], RandomStringUtils.randomAlphabetic(5, 10));
        }
        for (CustomerFlag f : ALLOWED_FLAG) {
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

        Customer c = new Customer();
        ConverterUtil.mergeFromOld(old, c, mandatorMatchCode, defaults);
        return c;
    }

}
