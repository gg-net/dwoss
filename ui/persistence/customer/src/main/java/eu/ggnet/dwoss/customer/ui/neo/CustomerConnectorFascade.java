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

import java.util.Optional;
import java.util.stream.Stream;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.CustomerAgent.Root;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.saft.core.Dl;

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
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(address);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Contact deleteAddressOnContact(long contactId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Contact.class, contactId), address);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Contact createAddressOnContact(long contactId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Contact.class, contactId), address);
        return agent.findByIdEager(Contact.class, contactId);
        // INFO: DB must fail if contact is part of a bussines customer.
    }

    public static Contact updateCommunicationOnContact(long contactId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(communication);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Contact deleteCommunicationOnContact(long contactId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Contact.class, contactId), communication);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Contact createCommunicationOnContact(long contactId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Contact.class, contactId), communication);
        return agent.findByIdEager(Contact.class, contactId);
    }

    public static Customer createContactOnCustomer(long customerid, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Customer.class, customerid), contact);
        return agent.findByIdEager(Customer.class, customerid);
    }

    public static Customer updateContactOnCustomer(long customerId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(contact);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Customer deleteContactOnCustomer(long customerId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Customer.class, customerId), contact);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Customer createCompanyOnCustomer(long customerId, Company company) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Customer.class, customerId), company);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Customer updateCompanyOnCustomer(long customerId, Company company) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(company);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Customer deleteCompanyOnCustomer(long customerId, Company company) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Customer.class, customerId), company);
        return agent.findByIdEager(Customer.class, customerId);
    }

    public static Company createCommunicationOnCompany(long companyId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Company.class, companyId), communication);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company updateCommunicationOnCompany(long companyId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(communication);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company deleteCommunicationOnCompany(long companyId, Communication communication) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Company.class, companyId), communication);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Customer reload(Customer customer) {
        return customer;
    }

    public static Company reload(Company company) {
        return company;
    }

    public static Company updateAddressOnCompany(long companyId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(address);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company deleteAddressOnCompany(long companyId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Company.class, companyId), address);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company createAddressOnCompany(long companyId, Address address) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Company.class, companyId), address);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company createContactOnCompany(long companyId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Company.class, companyId), contact);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company updateContactOnCompany(long companyId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.update(contact);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Company deleteContactOnCompany(long companyId, Contact contact) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.delete(new Root(Company.class, companyId), contact);
        return agent.findByIdEager(Company.class, companyId);
    }

    public static Customer createOrUpdateMandatorMetadata(long customerId, MandatorMetadata mandatorMetadata) {
        CustomerAgent agent = Dl.remote().lookup(CustomerAgent.class);
        agent.create(new Root(Customer.class, customerId), mandatorMetadata);
        return agent.findByIdEager(Customer.class, customerId);
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

}
