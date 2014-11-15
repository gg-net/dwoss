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
package eu.ggnet.dwoss.customer.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.*;

import eu.ggnet.dwoss.rules.CustomerFlag;

import lombok.*;

import static javax.persistence.CascadeType.ALL;

/**
 * The datamodel of a purchaser from the view of GG-Net.
 * <p>
 * @author pascal.perau
 */
@Entity
@ToString(exclude = {"companies", "contacts", "mandatorMetadata"})
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Indexed
public class Customer implements Serializable {

    @Id
    @Getter
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * A list of {@link Company}<code>s</code> represented by the customer.
     */
    @NotNull
    @OneToMany(cascade = ALL)
    @IndexedEmbedded
    private List<Company> companies = new ArrayList<>();

    /**
     * All contacts association with the customer.
     */
    @NotNull
    @OneToMany(cascade = ALL)
    @IndexedEmbedded
    private List<Contact> contacts = new ArrayList<>();

    /**
     * Optional Mandator Metadate.
     */
    @NotNull
    @OneToMany(cascade = ALL)
    private List<MandatorMetadata> mandatorMetadata = new ArrayList<>();

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<CustomerFlag> flags = new HashSet<>();

    @Lob
    @Column(length = 65535)
    @Getter
    @Setter
    @Field
    @Boost(0.5f)
    private String comment;

    public List<Company> getCompanies() {
        return new ArrayList<>(companies);
    }

    public List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }

    public List<MandatorMetadata> getMandatorMetadata() {
        return new ArrayList<>(mandatorMetadata);
    }

    public Set<CustomerFlag> getFlags() {
        return new HashSet<>(flags);
    }

    /**
     * Returns the Metadata based on the matchcode, may return null.
     * <p>
     * @param matchcode the matchcode.
     * @return metadata, based on the matchcode
     */
    public MandatorMetadata getMandatorMetadata(String matchcode) {
        for (MandatorMetadata metadata : mandatorMetadata) {
            if ( metadata.getMandatorMatchcode().equals(matchcode) ) return metadata;
        }
        return null;
    }

    /**
     * Defensivly adds an instance to {@link Customer#companies}.
     * Null values or duplicates will be ignored.
     * <p>
     * @param c the {@link Company}
     */
    public void add(Company c) {
        if ( c != null ) companies.add(c);
    }

    /**
     * Defensivly adds an instance to {@link Customer#contacts}.
     * Null values or duplicates will be ignored.
     * <p>
     * @param c the {@link Contact}
     */
    public void add(Contact c) {
        if ( c != null ) contacts.add(c);
    }

    /**
     * Defensivly adds an instance to {@link Customer#mandatorMetadata}.
     * Null values or duplicates will be ignored.
     * <p>
     * @param m the {@link MandatorMetadata}
     */
    public void add(MandatorMetadata m) {
        if ( m != null ) mandatorMetadata.add(m);
    }

    /**
     * Defensivly add customerFlag.
     * <p>
     * @param customerFlag
     */
    public void add(CustomerFlag customerFlag) {
        if ( customerFlag != null ) flags.add(customerFlag);
    }

    public void remove(CustomerFlag customerFlag) {
        flags.remove(customerFlag);
    }

    public void clearFlags() {
        flags.clear();
    }

    public String toMultiLine() {
        String result = toString();
        for (Company company : companies) {
            result += "\n -" + company;
            for (Address a : company.getAddresses()) {
                result += "\n  -" + a;
            }
            for (Communication communication : company.getCommunications()) {
                result += "\n  -" + communication;
            }
        }
        for (Contact contact : contacts) {
            result += "\n -" + contact;
            for (Address a : contact.getAddresses()) {
                result += "\n  -" + a;
            }
            for (Communication communication : contact.getCommunications()) {
                result += "\n  -" + communication;
            }
        }
        for (MandatorMetadata metadata : mandatorMetadata) {
            result += "\n -" + metadata;
        }
        return result;
    }

}
