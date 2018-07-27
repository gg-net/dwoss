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
package eu.ggnet.dwoss.customer.ui.neo;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;

/**
 * Contains all opperations for modification of customer objects in the database.
 * Fasaced of all operations, not really any logic.
 *
 * @author oliver.guenther
 */
public class CustomerConnectorFascade {

    private static Customer customer;

    /* REMOVE Me LATER */
    public static void setCustomer(Customer c) {
        CustomerConnectorFascade.customer = c;
    }

    public static Customer updateAddressLabels(long customerId, AddressLabel invoiceLabel, Optional<AddressLabel> shippingLabel) {        
        // Info, the server will have to do more.
        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(invoiceLabel);
        if ( shippingLabel.isPresent() ) customer.getAddressLabels().add(shippingLabel.get());
        System.out.println("updateAddressLabels customerId = " + customerId + " invoice = " + invoiceLabel + ", shipping = " + shippingLabel);
        return customer;
    }

    public static Contact updateAddressOnContact(long contactId, Address address) {
        Contact contact = findContactById(contactId);
        // Update just happeing like magic :-)
        System.out.println("updateAddress = " + address);
        return contact;
    }

    public static Contact deleteAddressOnContact(long contactId, Address address) {
        Contact contact = findContactById(contactId);
        for (Iterator<Address> iterator = contact.getAddresses().iterator(); iterator.hasNext();) {
            Address selectedAddress = iterator.next();
            if ( selectedAddress.getId() == address.getId() ) iterator.remove();
        }
        System.out.println("delete address = " + address);
        return contact;
    }

    public static Contact createAddressOnContact(long contactId, Address address) {
        // INFO: DB must fail if contact is part of a bussines customer.
        Contact contact = findContactById(contactId);
        contact.getAddresses().add(address);
        System.out.println("create address = " + address);
        return contact;
    }

    private static Contact findContactById(long contactId) {
        Optional<Contact> found = Stream.concat(customer.getContacts().stream(), customer.getCompanies().stream().flatMap((com) -> com.getContacts().stream())).filter(c -> c.getId() == contactId).findAny();
        if ( !found.isPresent() ) throw new RuntimeException("contactid = " + contactId + " not found in customer " + customer);
        return found.get();
    }

    private static Company findCompanyById(long companyId) {
        Optional<Company> found = customer.getCompanies().stream().filter(c -> c.getId() == companyId).findAny();
        if ( !found.isPresent() ) throw new RuntimeException("companyId = " + companyId + " not found in customer " + customer);
        return found.get();
    }

    public static Contact updateCommunicationOnContact(long contactId, Communication communication) {
        Contact contact = findContactById(contactId);
        // Update just happeing like magic :-)
        System.out.println("update communication = " + communication);
        return contact;
    }

    public static Contact deleteCommunicationOnContact(long contactId, Communication communication) {
        Contact contact = findContactById(contactId);
        for (Iterator<Communication> iterator = contact.getCommunications().iterator(); iterator.hasNext();) {
            Communication selectedCommunication = iterator.next();
            if ( selectedCommunication.getId() == communication.getId() ) iterator.remove();
        }
        System.out.println("delete communication = " + communication);
        return contact;
    }

    public static Contact createCommunicationOnContact(long contactId, Communication communication) {
        Contact contact = findContactById(contactId);
        contact.getCommunications().add(communication);
        System.out.println("create communication = " + communication);
        return contact;
    }

    public static Customer createContactOnCustomer(long customerid, Contact contact) {
        customer.getContacts().add(contact);
        System.out.println("create contact = " + contact);
        return customer;
    }

    public static Customer updateContactOnCustomer(long customerId, Contact contact) {
        // magic
        System.out.println("update contact = " + contact);
        return customer;
    }

    public static Customer deleteContactOnCustomer(long custmoerId, Contact contact) {
        for (Iterator<Contact> iterator = customer.getContacts().iterator(); iterator.hasNext();) {
            Contact next = iterator.next();
            if ( next == contact ) iterator.remove();
        }
        System.out.println("delete contact");
        return customer;
    }

    public static Customer createCompanyOnCustomer(long customerid, Company company) {
        customer.getCompanies().add(company);
        System.out.println("create company = " + company);
        return customer;
    }

    public static Customer updateCompanyOnCustomer(long customerId, Company company) {
        // magic
        System.out.println("update company = " + company);
        return customer;
    }

    public static Customer deleteCompanyOnCustomer(long custmoerId,  Company company) {
        for (Iterator<Company> iterator = customer.getCompanies().iterator(); iterator.hasNext();) {
            Company next = iterator.next();
            if ( next == company ) iterator.remove();
        }
        System.out.println("delete company");
        return customer;
    }
    
    public static Company createCommunicationOnCompany(long companyId, Communication communication) {
        Company company = findCompanyById(companyId);
        company.getCommunications().add(communication);
        System.out.println("create communication = " + communication);
        return company;
    }

    public static Company updateCommunicationOnCompany(long companyId, Communication communication) {
        Company company = findCompanyById(companyId);
        // Update just happeing like magic :-)
        return company;
    }

    public static Company deleteCommunicationOnCompany(long companyId, Communication communication) {
        Company company = findCompanyById(companyId);
        for (Iterator<Communication> iterator = company.getCommunications().iterator(); iterator.hasNext();) {
            Communication selectedCommunication = iterator.next();
            if ( selectedCommunication.getId() == communication.getId() ) iterator.remove();
        }
        System.out.println("delete communication = " + communication);
        return company;
    }

    public static Customer reload(Customer customer) {
        return customer;
    }
    
    public static Company reload(Company company) {
        return company;
    }

    public static Company updateAddressOnCompany(long companyId, Address address) {
        Company company = findCompanyById(companyId);
        // Update just happeing like magic :-)
        System.out.println("updateAddress = " + address);
        return company;
    }

    public static Company deleteAddressOnCompany(long companyId, Address address) {
        Company company = findCompanyById(companyId);
        for (Iterator<Address> iterator = company.getAddresses().iterator(); iterator.hasNext();) {
            Address selectedAddress = iterator.next();
            if ( selectedAddress.getId() == address.getId() ) iterator.remove();
        }
        System.out.println("delete address = " + address);
        return company;
    }

    public static Company createAddressOnCompany(long companyId, Address address) {
        Company company = findCompanyById(companyId);
        company.getAddresses().add(address);
        System.out.println("create address = " + address);
        return company;
    }

    public static Company createContactOnCompany(long companyId, Contact contact) {
        Company company = findCompanyById(companyId);
        company.getContacts().add(contact);
        System.out.println("create contact = " + contact);
        return company;
    }

    public static Company updateContactOnCompany(long companyId, Contact contact) {
        Company company = findCompanyById(companyId);
        System.out.println("update contact = " + contact);
        return company;
    }

    public static Company deleteContactOnCompany(long companyId, Contact contact) {
        Company company = findCompanyById(companyId);
        for (Iterator<Contact> iterator = company.getContacts().iterator(); iterator.hasNext();) {
            Contact next = iterator.next();
            if ( next == contact ) iterator.remove();
        }
        System.out.println("delete customer");
        return company;
    }
}
