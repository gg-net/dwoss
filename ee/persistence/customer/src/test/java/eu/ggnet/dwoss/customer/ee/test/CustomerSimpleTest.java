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
package eu.ggnet.dwoss.customer.ee.test;

import java.util.Locale;

import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.common.api.values.AddressType;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.*;
import static eu.ggnet.dwoss.customer.ee.entity.Country.GERMANY;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jacob.weinhold
 */
public class CustomerSimpleTest {

    public static Company makeValidCompany() {
        Company validcompany = new Company();
        validcompany.setName("Firma ABC");
        validcompany.setTaxId("textid123456789");
        validcompany.getAddresses().add(makeValidAddress());

        assertThat(validcompany.getViolationMessage()).as("valid Company").isNull();
        return validcompany;
    }

    public static Contact makeValidContact() {
        Contact validContact = new Contact(Sex.MALE, true, "Dr", "Max", "Mustermann");
        validContact.getAddresses().add(makeValidAddress());

        assertThat(validContact.getViolationMessage()).as("valid Contact").isNull();
        return validContact;
    }

    public static Communication makeValidCommunication() {
        Communication validCommunication = new Communication(Type.EMAIL, "Max.mustermann@mustermail.de");

        assertThat(validCommunication.getViolationMessage()).as("valid Communication").isNull();
        return validCommunication;
    }

    public static Communication makeValidCommunicationMobile() {
        Communication validCommunication = new Communication(Type.MOBILE, "0174 123456789");

        assertThat(validCommunication.getViolationMessage()).as("valid Communication").isNull();
        return validCommunication;
    }

    public static Communication makeValidCommunicationLandline() {
        Communication validCommunication = new Communication(Type.PHONE, "040 123456789");

        assertThat(validCommunication.getViolationMessage()).as("valid Communication").isNull();
        return validCommunication;
    }

    public static Address makeValidAddress() {
        Address validAddress = new Address();
        validAddress.setCity("Munich");
        validAddress.setCountry(GERMANY);
        validAddress.setStreet("Straße");
        validAddress.setZipCode("123456");

        assertThat(validAddress.getViolationMessage()).as("valid Address").isNull();
        return validAddress;
    }

    public static AddressLabel makeValidAddressLabel() {
        AddressLabel validAddressLabel = new AddressLabel(makeValidCompany(), makeValidContact(), makeValidAddress(), AddressType.INVOICE);

        assertThat(validAddressLabel.getViolationMessage()).as("valid validAddressLabel").isNull();
        return validAddressLabel;
    }

    /**
     *
     * @return Valid SimpleBusinessCustomer with Communication of Type EMAIL on it's Companies' Contact
     */
    public static Customer makeValidSimpleBusiness() {
        Address address = makeValidAddress();
        assertThat(address.getViolationMessage()).as("address does not violate any rule").isNull();

        Company company = new Company("Musterfirma", 0, true, "1203223");
        company.getAddresses().add(address);
        assertThat(company.getViolationMessage()).as("company does not violate any rule").isNull();

        Contact validContact = new Contact(Sex.FEMALE, true, "", "Testkunde", "Testkunde");
        Communication validCommunication = new Communication(Type.EMAIL, "Max.mustermann@mustermail.de");        
        validContact.getCommunications().add(validCommunication);
        validContact.getAddresses().add(makeValidAddress());
        assertThat(validContact.getViolationMessage()).as("valid Contact").isNull();
        company.getContacts().add(validContact);

        Customer customer = new Customer();
        customer.getCompanies().add(company);
        customer.getAddressLabels().add(makeValidAddressLabel());

        assertThat(customer.getViolationMessage()).overridingErrorMessage("Customer is not valid because :", customer.getViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.isBusiness()).as("customer is a business customer").isTrue();
        assertThat(customer.isValid()).as("customer is a simple valid business customer").isTrue();

        return customer;
    }

    public static Customer makeValidSimpleConsumer() {
        Customer customer = new Customer();
        Contact makeValidContact = makeValidContact();
        makeValidContact.getCommunications().add(makeValidCommunication());
        customer.getContacts().add(makeValidContact);
        customer.getAddressLabels().add(makeValidAddressLabel());

        assertThat(customer.isSimple()).overridingErrorMessage("Customer is not simple, because: " + customer.getSimpleViolationMessage()).isTrue();
        assertThat(customer.getSimpleViolationMessage()).as("customer does not violate any rule").isNull();
        assertThat(customer.isValid()).isTrue(); // optional

        assertThat(customer.isConsumer()).isTrue();

        return customer;
    }

    @Test
    public void testToSimpleConsumer() {
        Customer validSimpleConsumer = makeValidSimpleConsumer();
        validSimpleConsumer.getContacts().get(0).getCommunications().clear();
        validSimpleConsumer.getContacts().get(0).getCommunications().add(makeValidCommunication());
        validSimpleConsumer.getContacts().get(0).getCommunications().add(makeValidCommunicationMobile());
        validSimpleConsumer.getContacts().get(0).getCommunications().add(makeValidCommunicationLandline());
        assertThat(validSimpleConsumer.isSimple()).as("still simplecustomer").isTrue();

        SimpleCustomer simpleConsumerCustomer = validSimpleConsumer.toSimple().get();

        assertThat(simpleConsumerCustomer.getTitle()).as("title").isEqualTo(validSimpleConsumer.getContacts().get(0).getTitle());
        assertThat(simpleConsumerCustomer.getFirstName()).as("firstname").isEqualTo(validSimpleConsumer.getContacts().get(0).getFirstName());
        assertThat(simpleConsumerCustomer.getLastName()).as("lastname").isEqualTo(validSimpleConsumer.getContacts().get(0).getLastName());
        assertThat(simpleConsumerCustomer.getStreet()).as("street").isEqualTo(validSimpleConsumer.getContacts().get(0).getAddresses().get(0).getStreet());
        assertThat(simpleConsumerCustomer.getZipCode()).as("zipcode").isEqualTo(validSimpleConsumer.getContacts().get(0).getAddresses().get(0).getZipCode());
        assertThat(simpleConsumerCustomer.getCity()).as("city").isEqualTo(validSimpleConsumer.getContacts().get(0).getAddresses().get(0).getCity());
        assertThat(simpleConsumerCustomer.getCountry()).as("country").isEqualTo(validSimpleConsumer.getContacts().get(0).getAddresses().get(0).getCountry());
        assertThat(simpleConsumerCustomer.getMobilePhone()).as("mobilePhone").isEqualTo(validSimpleConsumer.getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().get());
        assertThat(simpleConsumerCustomer.getLandlinePhone()).as("landlinePhone").isEqualTo(validSimpleConsumer.getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().get());
        assertThat(simpleConsumerCustomer.getEmail()).as("email").isEqualTo(validSimpleConsumer.getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().get());

        assertThat(simpleConsumerCustomer.getSex()).as("sex").isEqualTo(validSimpleConsumer.getContacts().get(0).getSex());
        assertThat(simpleConsumerCustomer.getSource()).as("source").isEqualTo(validSimpleConsumer.getSource());
        assertThat(simpleConsumerCustomer.getComment()).as("comment").isEqualTo(validSimpleConsumer.getComment());

        assertThat(simpleConsumerCustomer.getCompanyName()).as("companyName").isEqualTo(null);
        assertThat(simpleConsumerCustomer.getTaxId()).as("taxId").isEqualTo(null);

    }

    @Test
    public void testToSimpleBusiness() {
        Customer validBusinessCustomer = makeValidSimpleBusiness();
        validBusinessCustomer.getCompanies().get(0).getCommunications().clear();
        assertThat(validBusinessCustomer.isSimple()).as("still simplecustomer").isTrue();

        Contact makeValidContact = makeValidContact();
        makeValidContact.getCommunications().clear();
        makeValidContact.getCommunications().add(makeValidCommunication());
        makeValidContact.getCommunications().add(makeValidCommunicationMobile());
        makeValidContact.getCommunications().add(makeValidCommunicationLandline());
        assertThat(makeValidContact.getViolationMessage()).as("valid contact").isNull();

        validBusinessCustomer.getCompanies().get(0).getContacts().clear();
        validBusinessCustomer.getCompanies().get(0).getContacts().add(makeValidContact);

        assertThat(validBusinessCustomer.isSimple()).as("still simplecustomer").isTrue();

        assertThat(validBusinessCustomer.toSimple().isPresent()).as("to simple").isTrue();
        SimpleCustomer simpleBusinessCustomer = validBusinessCustomer.toSimple().get();

        assertThat(simpleBusinessCustomer.getTitle()).as("title").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getTitle());
        assertThat(simpleBusinessCustomer.getFirstName()).as("firstname").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getFirstName());
        assertThat(simpleBusinessCustomer.getLastName()).as("lastname").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getLastName());

        assertThat(simpleBusinessCustomer.getSex()).as("sex").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getSex());

        //did Companys have a Address this Address have to match the Address on Contact
        if ( !validBusinessCustomer.getCompanies().get(0).getAddresses().isEmpty() ) {
            assertThat(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getStreet())
                    .as("address of contact have to be the same as on Address Street")
                    .isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getStreet());

            assertThat(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getCity())
                    .as("address of contact have to be the same as on Address City")
                    .isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getCity());

            assertThat(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getZipCode())
                    .as("address of contact have to be the same as on Address zipcode")
                    .isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getZipCode());

            assertThat(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getIsoCountry())
                    .as("address of contact have to be the same as on Address iso country")
                    .isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getIsoCountry());

            assertThat(simpleBusinessCustomer.getStreet()).as("street").isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getStreet());
            assertThat(simpleBusinessCustomer.getZipCode()).as("zipcode").isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getZipCode());
            assertThat(simpleBusinessCustomer.getCity()).as("city").isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getCity());
            assertThat(simpleBusinessCustomer.getCountry()).as("country").isEqualTo(validBusinessCustomer.getCompanies().get(0).getAddresses().get(0).getCountry());
        } else {
            assertThat(simpleBusinessCustomer.getStreet()).as("street").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getStreet());
            assertThat(simpleBusinessCustomer.getZipCode()).as("zipcode").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getZipCode());
            assertThat(simpleBusinessCustomer.getCity()).as("city").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getCity());
            assertThat(simpleBusinessCustomer.getCountry()).as("country").isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getAddresses().get(0).getCountry());
        }

        assertThat(simpleBusinessCustomer.getMobilePhone()).as("mobilePhone")
                .isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().get());
        assertThat(simpleBusinessCustomer.getLandlinePhone()).as("landlinePhone")
                .isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().get());
        assertThat(simpleBusinessCustomer.getEmail()).as("email")
                .isEqualTo(validBusinessCustomer.getCompanies().get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().get());

        assertThat(simpleBusinessCustomer.getSource()).as("source").isEqualTo(validBusinessCustomer.getSource());
        assertThat(simpleBusinessCustomer.getComment()).as("comment").isEqualTo(validBusinessCustomer.getComment());

        assertThat(simpleBusinessCustomer.getCompanyName()).as("companyName").isEqualTo(validBusinessCustomer.getCompanies().get(0).getName());
        assertThat(simpleBusinessCustomer.getTaxId()).as("taxId").isEqualTo(validBusinessCustomer.getCompanies().get(0).getTaxId());

    }

}