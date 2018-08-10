/*
 * Copyright (C) 2017 GG-Net GmbH
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
import java.util.Objects;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import eu.ggnet.dwoss.common.ee.GlobalConfig;
import eu.ggnet.dwoss.common.api.values.AddressType;

import lombok.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.EAGER;

/**
 * A combination of address, optional company and contact.
 *
 * @author oliver.guenther
 */
@Entity
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Getter
public class AddressLabel implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    private Customer customer;

    @Setter
    @ManyToOne(fetch = EAGER, cascade = {DETACH, MERGE, PERSIST, REFRESH})
    private Company company;

    @Setter
    @ManyToOne(fetch = EAGER, cascade = {DETACH, MERGE, PERSIST, REFRESH})
    private Contact contact;

    @Setter
    @NotNull
    @Valid
    @ManyToOne(fetch = EAGER, cascade = {DETACH, MERGE, PERSIST, REFRESH})
    private Address address;

    @Setter
    @Enumerated
    private AddressType type;

    public AddressLabel(Contact contact, Address address, AddressType type) {
        this.contact = contact;
        this.address = address;
        this.type = type;
    }

    public AddressLabel(Company company, Contact contact, Address address, AddressType type) {
        this.company = company;
        this.contact = contact;
        this.address = address;
        this.type = type;
    }

    /**
     * Sets the {@link Customer} in consideration of equalancy and bidirectional
     * behaviour.
     * <p>
     * @param customer
     */
    public void setCustomer(Customer customer) {
        if ( Objects.equals(customer, this.customer) ) return;
        if ( this.customer != null ) this.customer.addressLabels.remove(this);
        if ( customer != null ) customer.addressLabels.add(this);
        this.customer = customer;
    }

    /**
     * Returns a HTML representation of the address label.
     * If this label is empty, an appropriated text is shown.
     *
     * @return a html representation of the address label.
     */
    public String toHtml() {
        if ( isEmpty() ) return "Keine bevorzugten Addresdaten hinterlegt";

        return Optional.ofNullable(company).map(c -> c.getName() + "<br />").orElse("")
                + Optional.ofNullable(contact).map(c -> c.toFullName() + "<br />").orElse("")
                + Optional.ofNullable(address).map(a -> addressLine(a, "<br />")).orElse("");
    }

    /**
     * Prints, what you would expect on a smail envelope.
     *
     * @return multiline string to be printed.
     */
    public String toLabel() {
        if ( isEmpty() ) return "Keine bevorzugten Addresdaten hinterlegt";
        return Optional.ofNullable(company).map(c -> c.getName() + System.lineSeparator()).orElse("")
                + Optional.ofNullable(contact).map(c -> c.toFullName() + System.lineSeparator()).orElse("")
                + Optional.ofNullable(address).map(a -> addressLine(a, System.lineSeparator())).orElse("");
    }

    private String addressLine(Address address, String nl) {
        return address.getStreet() + nl
                + (GlobalConfig.LOCAL_ISO_COUNTRY.equalsIgnoreCase(address.getIsoCountry()) ? "" : address.getIsoCountry() + " ")
                + address.getZipCode() + " " + address.getCity();
    }

    /**
     * Returns true if company,contact and address are empty.
     *
     * @return true if company,contact and address are empty.
     */
    // Will be removed, as an entiy addresslabel can not be empty.
    public boolean isEmpty() {
        return company == null && contact == null && address == null;
    }

    /**
     * Returns null, if the AddressLabel is valid.
     * Rules are:
     * <ul>
     * <li>Address is not null and all Address have to be valid</li>
     * <li>either a contact or a companie is set, but never both</li>
     * <li>Company has to be valid</li>
     * <li>Contact has to be valid</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getViolationMessage() {
        if ( address == null ) return "Address is null";
        if ( address.getViolationMessage() != null ) return "Address: " + address.getViolationMessage();
        if ( company == null && contact == null ) return "Company and Contact are null.";
        if ( company != null && company.getViolationMessage() != null ) return "Company: " + company.getViolationMessage();
        if ( contact != null && contact.getViolationMessage() != null ) return "Contact: " + contact.getViolationMessage();
        return null;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + "id=" + id + ", optLock=" + optLock
                + ", company=" + Optional.ofNullable(company).map(c -> "(id=" + c.getId() + ") " + c.getName()).orElse("null")
                + ", contact=" + Optional.ofNullable(contact).map(c -> "(id=" + c.getId() + ") " + c.toFullName()).orElse("null")
                + ", address=" + address + ", type=" + type + '}';
    }

}
