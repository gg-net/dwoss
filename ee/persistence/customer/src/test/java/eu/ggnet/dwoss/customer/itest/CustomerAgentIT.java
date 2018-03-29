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
package eu.ggnet.dwoss.customer.itest;

import java.util.Locale;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.customer.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.rules.AddressType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jens.papenhagen
 */
@RunWith(Arquillian.class)
public class CustomerAgentIT extends ArquillianProjectArchive {

    @EJB
    private CustomerAgent customerAgent;

    @Inject
    @Customers
    private EntityManager em;

    @Inject
    UserTransaction utx;

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
        validAddress.setIsoCountry(Locale.GERMANY);
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

    public static Customer makeValidBusinessCustomer() {
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

    public static Customer makeValidConsumer() {
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

    /**
     * store i will test the store methode
     * Customer is not valid in moment
     * <p>
     * @throws java.lang.Exception
     */
    @Test
    public void testStoreForConsumerCustomer() throws Exception {
        Customer c1 = makeValidConsumer();

        assertThat(c1.isSimple()).as("Customer can be transform to a simple customer").isTrue();

        Customer consumerpayload = customerAgent.store(c1.toSimple().get()).getPayload();

        assertThat(consumerpayload.isValid()).as("the payload is a valid customer").isTrue();
        assertThat(consumerpayload.isConsumer()).as("Consumer Customer").isTrue();
        assertThat(consumerpayload.isSimple()).as("the payload can be transform to a simple customer").isTrue();
        assertThat(consumerpayload).as("check that store the same customer").isEqualTo(c1);
    }

    @Test
    public void testStoreForBussnisCustomer() {
        Customer c2 = makeValidBusinessCustomer();

        assertThat(c2.isSimple()).as("Customer can be transform to a simple customer").isTrue();
        Customer businesspayload = customerAgent.store(c2.toSimple().get()).getPayload();

        assertThat(businesspayload.isValid()).as("the payload is a valid customer").isTrue();
        assertThat(businesspayload.isBusiness()).as("Business Customer").isTrue();
        assertThat(businesspayload.isSimple()).as("the payload can be transform to a simple customer").isTrue();
        assertThat(businesspayload).as("check that store the same customer").isEqualTo(c2);
    }


    @Test
    public void testFindCustomerAsMandatorHtml() {
        String feedback = "Kein Kunde mit id 123 vorhanden";
        String findCustomerAsMandatorHtml = customerAgent.findCustomerAsMandatorHtml(123);
        assertThat(findCustomerAsMandatorHtml).as("give back the Error Message").isEqualToIgnoringCase(feedback);
    }


    @Test
    public void testFindCustomerAsHtml() {
        String feedback = "Kein Kunde mit id 123 vorhanden";
        String findCustomerAsHtml = customerAgent.findCustomerAsHtml(123);
        assertThat(findCustomerAsHtml).as("give back the Error Message").isEqualToIgnoringCase(feedback);
    }

 }
