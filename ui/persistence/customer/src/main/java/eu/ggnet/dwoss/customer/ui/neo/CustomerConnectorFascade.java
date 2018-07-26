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
        customer.getAddressLabels().clear();
        customer.getAddressLabels().add(invoiceLabel);
        if (shippingLabel.isPresent()) customer.getAddressLabels().add(shippingLabel.get());
        System.out.println("updateAddressLabels customerId = " + customerId + " invoice = " + invoiceLabel + ", shipping = " + shippingLabel);
        return customer;
    }
    
    public static Contact updateAddressOnContact(long contactId, Address address) {
        Contact contact = findById(contactId);
        // Update just happeing like magic :-)
        System.out.println("updateAddress = " + address);        
        return contact;
    }
    
    public static Contact deleteAddressOnContact(long contactId, Address address) {
        Contact contact = findById(contactId);
        for (Iterator<Address> iterator = contact.getAddresses().iterator(); iterator.hasNext();) {
            Address selectedAddress = iterator.next();
            if (selectedAddress.getId() == address.getId()) iterator.remove();            
        }
        System.out.println("delete address = " + address);
        return contact;
    }
    
    public static Contact createAddressOnContact(long contactId, Address address) {
        Contact contact = findById(contactId);
        contact.getAddresses().add(address);
        System.out.println("create address = " + address);
        return contact;
    }
    
    private static Contact findById(long contactId) {
        Optional<Contact> found = Stream.concat(customer.getContacts().stream(), customer.getCompanies().stream().flatMap((com) -> com.getContacts().stream())).filter(c -> c.getId() == contactId).findAny();
        if (!found.isPresent()) throw new RuntimeException("contactid = " + contactId + " not found in customer " + customer);
        return found.get();        
    }
 
    public static Contact updateCommunicationOnContact(long contactId, Communication communication) {
        Contact contact = findById(contactId);
        // Update just happeing like magic :-)
        System.out.println("update communication = " + communication);        
        return contact;
    }
    
    public static Contact deleteCommunicationOnContact(long contactId, Communication communication) {
        Contact contact = findById(contactId);
        for (Iterator<Communication> iterator = contact.getCommunications().iterator(); iterator.hasNext();) {
            Communication selectedCommunication = iterator.next();
            if (selectedCommunication.getId() == communication.getId()) iterator.remove();            
        }
        System.out.println("delete communication = " + communication);
        return contact;
    }
    public static Contact createCommunicationOnContact(long contactId, Communication communication) {
        Contact contact = findById(contactId);
        contact.getCommunications().add(communication);
        System.out.println("create communication = " + communication);
        return contact;        
    }
    
}
