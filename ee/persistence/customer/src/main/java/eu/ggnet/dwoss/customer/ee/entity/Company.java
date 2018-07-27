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

import eu.ggnet.dwoss.common.api.values.AddressType;

import lombok.*;

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
@ToString(exclude = {"addresses", "communications", "contacts"})
@EqualsAndHashCode(of = {"id"})
@Indexed
public class Company implements Serializable {

    @Id
    @Getter
    @GeneratedValue
    private long id;

    /**
     * Name of the company.
     */
    @Getter
    @Setter
    @NotNull
    @Field
    @Boost(2f)
    private String name;

    /**
     * The finance ledger of this company.
     */
    @Getter
    @Setter
    private int ledger;

    /**
     * Is this a prefered company to use.
     */
    @Getter
    @Setter
    private boolean prefered;

    @Getter
    @Setter
    @Size(max = 255)
    private String taxId;

    /**
     * All contacts association with the customer.
     */
    @OneToMany(cascade = ALL)
    @NotNull
    @IndexedEmbedded
    @Getter
    private final List<Contact> contacts = new ArrayList<>();

    /**
     * All {@link Address}<code>es</code> associated stored for the company.
     */
    @OneToMany(cascade = ALL)
    @NotNull
    @IndexedEmbedded
    @Getter
    private final List<Address> addresses = new ArrayList<>();

    /**
     * All ways of {@link Communication}<code>s</code> stored for the company.
     */
    @OneToMany(cascade = ALL)
    @NotNull
    @IndexedEmbedded
    @Getter
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

    @Builder
    public Company(String name, int ledger, boolean prefered, String taxId) {
        this.name = name;
        this.ledger = ledger;
        this.prefered = prefered;
        this.taxId = taxId;
    }

    /**
     * Returns the first prefered communication of that Type, may return null.
     * If multiple Values of the same type are prefered the result is not gurantied.
     * <p>
     * @param type type of communication.
     * @return the first prefered communication of that Type, may return null.
     * @deprecated prefered is gone be removed.
     */
    @Deprecated
    public Communication prefered(Communication.Type type) {
        for (Communication communication : communications) {
            if ( communication.getType() == type && communication.isPrefered() ) return communication;
        }
        return null;
    }

    /**
     * Returns the first {@link Address} of the specified {@link AddressType}, may return null.
     * If multiple Values of the same type are prefered the result is not gurantied.
     * <p>
     * @param type type of the prefered address
     * @return the first prefered Address of that Type, may return null.
     * @deprecated prefered is gone be removed.
     */
    @Deprecated
    public Address prefered(AddressType type) {
        for (Address address : addresses) {
            if ( address.getPreferedType() == type ) return address;
        }
        return null;
    }

    /**
     * Html representation of the class.
     *
     * @return a html string
     */
    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if ( prefered ) sb.append("&nbsp;<b>&oplus;</b>");
        if ( taxId != null || ledger > 0 ) sb.append("<br />");
        if ( taxId != null ) sb.append("TaxId: " + taxId);
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
        sb.append(prefered ? "</b>" : "");
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
     * <li>all Communications have to be valid</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    // TODO: Validate, that the contact may only contain addresses of the company.
    public String getViolationMessage() {
        if ( StringUtils.isBlank(name) ) return "Name is blank";
        if ( addresses.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "One Address: " + addresses.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if ( contacts.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Contacts: " + contacts.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if ( communications.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Communications: " + communications.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        return null;
    }

}
