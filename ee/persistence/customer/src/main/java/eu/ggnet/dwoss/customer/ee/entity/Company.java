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
package eu.ggnet.dwoss.customer.ee.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.*;

import eu.ggnet.dwoss.core.system.persistence.BaseEntity;
import eu.ggnet.dwoss.customer.ee.entity.stash.*;
import eu.ggnet.dwoss.core.system.persistence.EagerAble;

import static javax.persistence.CascadeType.ALL;

/**
 * Represents a company.
 * <p>
 *
 * @has 0..1 - 0..n Address
 * @has 0..1 - 0..n Contact
 * @has 0..1 - 0..n Communication
 *
 * @author oliver.guenther
 */
@Entity
@Indexed
@SuppressWarnings("PersistenceUnitPresent")
public class Company extends BaseEntity implements Serializable, AddressStash, ContactStash, CommunicationStash, EagerAble {

    @Id
    @GeneratedValue
    private long id;

    /**
     * Name of the company.
     */
    @NotNull
    @Field
    private String name;

    /**
     * The finance ledger of this company.
     */
    private int ledger;

    @Size(max = 255)
    private String taxId;

    /**
     * All contacts association with the customer.
     */
    @OneToMany(cascade = ALL)
    @NotNull
    @IndexedEmbedded
    private final List<Contact> contacts = new ArrayList<>();

    /**
     * All {@link Address}<code>es</code> associated stored for the company.
     */
    @OneToMany(cascade = ALL)
    @NotNull
    @IndexedEmbedded
    private final List<Address> addresses = new ArrayList<>();

    /**
     * All ways of {@link Communication}<code>s</code> stored for the company.
     */
    @OneToMany(cascade = ALL)
    @NotNull
    @IndexedEmbedded
    private final List<Communication> communications = new ArrayList<>();

    public Company() {
    }

    /**
     * Constructor for tryouts, do not use in productive.
     *
     * @param id the db id.
     */
    public Company(long id) {
        this.id = id;
    }

    public Company(String name, int ledger, String taxId) {
        this.name = name;
        this.ledger = ledger;
        this.taxId = taxId;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getLedger() {
        return ledger;
    }
    
    public void setLedger(int ledger) {
        this.ledger = ledger;
    }
    
    public String getTaxId() {
        return taxId;
    }
    
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
    
    @Override
    public long getId() {
        return id;
    }
    
    @Override
    public List<Contact> getContacts() {
        return contacts;
    }
    
    @Override
    public List<Address> getAddresses() {
        return addresses;
    }
    
    @Override
    public List<Communication> getCommunications() {
        return communications;
    }    
    //</editor-fold>
    
    /**
     * Html representation of the class.
     *
     * @return a html string
     */
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if ( taxId != null || ledger > 0 ) sb.append("<br />");
        if ( taxId != null ) sb.append("TaxId: ").append(taxId);
        if ( taxId != null && ledger > 0 ) sb.append(", ");
        if ( ledger > 0 ) sb.append("FiBu-Konto: ").append(ledger);
        if ( taxId != null || ledger > 0 ) sb.append("<br />");
        if ( !contacts.isEmpty() ) {
            sb.append("Kontakt(e):<ul>");
            for (Contact contact : contacts) {
                sb.append("<li>").append(contact.toHtml()).append("</li>");
            }
            sb.append("</ul>");
        }
        if ( !addresses.isEmpty() ) {
            sb.append("Adresse(n):<ul>");
            for (Address address : addresses) {
                sb.append("<li>").append(address.toHtml()).append("</li>");
            }
            sb.append("</ul>");
        }
        if ( !communications.isEmpty() ) {
            sb.append("Kommunikationsinformationen:<ul>");
            for (Communication communication : communications) {
                sb.append("<li>").append(communication.toHtml()).append("</li>");
            }
            sb.append("</ul>");
        }
        return sb.toString();
    }

    /**
     * Returns null, if the Company is valid.
     * Rules are:
     * <ul>
     * <li>Name is not blank</li>
     * <li>at least one Address</li>
     * <li>all Address have to be valid</li>
     * <li>all Contacts have to be valid</li>
     * <li>all Contacts can not have any address assigned</li>
     * <li>all Communications have to be valid</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    public String getViolationMessage() {
        if ( StringUtils.isBlank(name) ) return "Name is blank";
        if ( addresses.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "One Address: " + addresses.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if ( contacts.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Contacts: " + contacts.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if(contacts.stream().flatMap(c -> c.getAddresses().stream()).count() > 0)
            return "Contacts on a Company can not have adresses assigned to them.";
        if ( communications.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Communications: " + communications.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        return null;
    }

    /**
     * Multi line string representation of the company, the contacts, the addresses and communications.
     *
     * @return a multi line string
     */
    public String toMultiLineString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);

        if ( !contacts.isEmpty() ) {
            contacts.forEach((contact) -> {
                sb.append("\n \t").append(contact.toFullName());
            });
        }

        if ( !addresses.isEmpty() ) {
            addresses.forEach((addresse) -> {
                sb.append("\n \t").append(addresse.toSingleLineString());
            });
        }

        if ( !communications.isEmpty() ) {
            for (Communication communication : communications) {
                sb.append("\n \t").append(communication.toSingleLineString());
            }
        }

        return sb.toString();
    }

    @Override
    public void fetchEager() {
        contacts.size();
        contacts.forEach(c -> {
            c.getAddresses().forEach(a -> a.getId());
            c.getCommunications().size();
        });
        communications.size();
        addresses.size();
    }

    @Override
    public String toString() {
        return "Company{" + "id=" + id + ", name=" + name + ", ledger=" + ledger + ", taxId=" + taxId + '}';
    }
   
}
