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
package eu.ggnet.dwoss.customer.ee.make;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Makes static data for test.
 *
 * @author oliver.guenther
 */
public class StaticCustomerMaker {

    /**
     *
     * @return Valid Company without Contact or Communication
     */
    public static Company makeValidCompany() {
        Company validcompany = new Company();
        validcompany.setName("Firma ABC");
        validcompany.setTaxId("textid123456789");
        validcompany.getAddresses().add(makeValidAddress());
        assertThat(validcompany.getViolationMessage()).as("valid Company").isNull();
        return validcompany;
    }

    /**
     *
     * @return Valid Contact as in Contact.getViolationMessage()
     *         with one Address and no Communication.
     */
    public static Contact makeValidContact() {
        Contact validContact = new Contact(Sex.MALE, "Dr", "Max", "Mustermann");
        validContact.getAddresses().add(makeValidAddress());
        assertThat(validContact.getViolationMessage()).as("Valid Contact does not violate any Rule").isNull();
        return validContact;
    }

    /**
     *
     * @return Valid Contact as in Contact.getViolationMessage()
     *         with no Address.
     */
    public static Contact makeValidCompanyContact() {
        Contact validContact = makeValidContact();
        validContact.getAddresses().clear();
        return validContact;
    }

    /**
     *
     * @param type       Not Null
     * @param identifier Not Null
     * @return Valid Communication as in Communication.getViolationMessage()
     */
    public static Communication makeValidCommunication(Type type, String identifier) {
        if ( type == null || identifier == null || StringUtils.isBlank(identifier) ) throw new NullPointerException();
        Communication validCommunication = new Communication(type, true);
        validCommunication.setIdentifier(identifier);
        assertThat(validCommunication.getViolationMessage()).as("Communication does not violate any rule").isNull();
        return validCommunication;
    }

    /**
     *
     * @return Valid Address as in Address.getViolationMessage()
     */
    public static Address makeValidAddress() {
        Address validAddress = new Address();
        validAddress.setCity("Munich");
        validAddress.setCountry(Country.GERMANY);
        validAddress.setStreet("Straße");
        validAddress.setZipCode("123456");
        assertThat(validAddress.getViolationMessage()).as("valid Address").isNull();
        return validAddress;
    }

    /**
     * Uses makeValidCompany, makeValidContact and makeValidAddress.
     *
     * @return Valid AddressLabel of Type INVOICE as in AddressLabel.getViolationMessage()
     *
     */
    public static AddressLabel makeValidInvoiceAddressLabel() {
        AddressLabel validAddressLabel = new AddressLabel(makeValidCompany(), makeValidContact(), makeValidAddress(), AddressType.INVOICE);
        assertThat(validAddressLabel.getViolationMessage()).as("valid validAddressLabel").isNull();
        return validAddressLabel;
    }

    /**
     * Uses makeValidCompany, makeValidContact and makeValidAddress.
     *
     * @return Valid AddressLabel of Type SHIPPING as in AddressLabel.getViolationMessage()
     *
     */
    public static AddressLabel makeValidShippingAddressLabel() {
        AddressLabel validAddressLabel = new AddressLabel(makeValidCompany(), makeValidContact(), makeValidAddress(), AddressType.SHIPPING);
        assertThat(validAddressLabel.getViolationMessage()).as("valid validAddressLabel").isNull();
        return validAddressLabel;
    }

    /**
     *
     * @return Valid SimpleBusinessCustomer with Communication of Type EMAIL on it's Companies' Contact
     */
    public static Customer makeValidSimpleBusiness() {
        Address address = makeValidAddress();
        assertThat(address.getViolationMessage()).as("Address does not violate any rule").isNull();
        Company company = makeValidCompany();
        assertThat(company.getViolationMessage()).as("Company does not violate any rule").isNull();
        Contact validContact = makeValidCompanyContact();
        validContact.getCommunications().add(makeValidCommunication(Type.EMAIL, "Max.mustermann@mustermail.de"));
        assertThat(validContact.getViolationMessage()).as("Contact is valid").isNull();
        company.getContacts().add(validContact);
        Customer customer = new Customer();
        customer.getCompanies().add(company);
        customer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        assertThat(customer.getViolationMessage()).overridingErrorMessage("SimpleBusinessCustomer is not valid because :", customer.getViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isSimple()).overridingErrorMessage("SimpleBusinessCustomer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.isBusiness()).as("SimpleBusinessCustomer is a BusinessCustomer").isTrue();
        assertThat(customer.isValid()).as("SimpleBusinessCustomer is a valid Customer").isTrue();
        return customer;
    }

    /**
     *
     * @return Valid BusinessCustomer with two AddressLabels, one Communication on it's Company and it's Company's Contact and a CustomerFlag
     */
    public static Customer makeValidBusinessCustomer() {
        Customer customer = new Customer();
        customer.getCompanies().add(makeValidCompany());
        customer.getCompanies().get(0).getContacts().add(makeValidCompanyContact());
        customer.getCompanies().get(0).getCommunications().add(makeValidCommunication(Communication.Type.EMAIL, "testMail@test.net"));
        customer.getCompanies().get(0).getContacts().get(0).getCommunications().add(makeValidCommunication(Communication.Type.EMAIL, "testMail@test.net"));
        customer.getCompanies().get(0).getAddresses().add(makeValidAddress());
        customer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        customer.getAddressLabels().add(makeValidShippingAddressLabel());
        customer.getFlags().add(CustomerFlag.CS_UPDATE_CANDIDATE);
        assertThat(customer.getViolationMessage()).as("BusinessCustomer does not violate any rule").isNull();
        assertThat(customer.isValid()).as("BusinessCustomer is a simple valid business customer").isTrue();
        assertThat(customer.isBusiness()).as("BusinessCustomer is a business customer").isTrue();
        assertThat(customer.isConsumer()).as("BusinessCustomer is no ConsumerCustomer").isFalse();
        assertThat(customer.isSimple()).as("BusinessCustomer is not SimpleBusinessCustomer").isFalse();
        assertThat(customer.getSimpleViolationMessage()).as("BusinessCustomer is not simple").isNotNull();
        return customer;
    }

    /**
     *
     * @return Valid ConsumerCustomer with two AddressLabels, a valid Contact with one valid Communication and a CustomerFlag
     */
    public static Customer makeValidConsumerCustomer() {
        Customer customer = new Customer();
        customer.getContacts().add(makeValidContact());
        customer.getContacts().get(0).getCommunications().add(makeValidCommunication(Type.MOBILE, "0170 123456"));
        customer.getAddressLabels().add(new AddressLabel(null, customer.getContacts().get(0), customer.getContacts().get(0).getAddresses().get(0), AddressType.INVOICE));
        customer.getAddressLabels().add(new AddressLabel(null, customer.getContacts().get(0), makeValidAddress(), AddressType.SHIPPING));
        customer.getFlags().add(CustomerFlag.values()[(int)Math.random() * (CustomerFlag.values().length - 1)]);
        assertThat(customer.getViolationMessage()).overridingErrorMessage("ConsumerCustomer is not valid, because: " + customer.getViolationMessage()).isNull();
        assertThat(customer.isSimple()).overridingErrorMessage("ConsumerCustomer is not simple, because: " + customer.getSimpleViolationMessage()).isFalse();
        assertThat(customer.getSimpleViolationMessage()).as("ConsumerCustomer is not simple").isNotNull();
        assertThat(customer.isValid()).isTrue();
        assertThat(customer.isConsumer()).isTrue();
        return customer;
    }

    /**
     *
     * @return Valid SimpleCustomer as in Customer.getSimpleViolationMessage() with one Communication
     *         of Type EMAIL and one Address on it's Contact
     *
     */
    public static Customer makeValidSimpleConsumer() {
        Customer customer = new Customer();
        Contact makeValidContact = makeValidContact();
        makeValidContact.getCommunications().add(makeValidCommunication(Communication.Type.EMAIL, "testMail@test.net"));
        customer.getContacts().add(makeValidContact);
        customer.getAddressLabels().add(makeValidInvoiceAddressLabel());
        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.getSimpleViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isValid()).isTrue();
        assertThat(customer.isConsumer()).isTrue();
        return customer;
    }

}
