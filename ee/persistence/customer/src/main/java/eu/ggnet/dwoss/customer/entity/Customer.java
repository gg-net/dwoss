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
 * @has 0..1 - 0..n Company
 * @has 0..1 - 0..n Contact
 * @has 0..1 - 0..n MandatorMetadata
 * @has 0..m - 0..n CustomerFlag
 *
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

    /**
     * Generates a human readable representation of the customers name.
     * Works as follows:
     * If there is a company get the prefered otherwise the first.
     * If there is no company get the prefered contact otherwise the first.
     *
     * @return a human readable representation
     */
    public String toName() {
        String contactName = null;
        String companyName = null;
        if ( !companies.isEmpty() ) {
            for (Company company : companies) {
                if ( companyName == null ) companyName = company.getName();
                if ( company.isPrefered() ) companyName = company.getName();
            }
        }
        if ( !contacts.isEmpty() ) {
            for (Contact contact : contacts) {
                if ( contactName == null ) contactName = contact.toFullName();
                if ( contact.isPrefered() ) contactName = contact.toFullName();
            }
        }
        if ( contactName == null && companyName == null ) return "Customer without company and contact" + id;
        StringBuilder sb = new StringBuilder();
        if ( companyName != null ) sb.append(companyName);
        if ( companyName != null && contactName != null ) sb.append(" - ");
        if ( contactName != null ) sb.append(contactName);
        return sb.toString();
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

    public String toHtml() {
        StringBuilder sb = new StringBuilder("<p><u>" + toName() + "</u></p>");
        if ( !companies.isEmpty() ) {
            sb.append("Firmen(n):<ul>");
            for (Company company : companies) {
                sb.append("<li>").append(company.toHtml()).append("</li>");
            }
            sb.append("</ul>");
        }
        if ( !contacts.isEmpty() ) {
            sb.append("Kontakt(e):<ul>");
            for (Contact contact : contacts) {
                sb.append("<li>").append(contact.toHtml()).append("</li>");
            }
            sb.append("</ul>");
        }
        if ( !mandatorMetadata.isEmpty() ) {
            sb.append("Mandantenspezifische Informationen:<ul>");
            for (MandatorMetadata mandatorMetadata : mandatorMetadata) {
                sb.append("<li>").append(mandatorMetadata.toHtml()).append("</li>");
            }
            sb.append("</ul>");
        }
        if ( !flags.isEmpty() ) {
            sb.append("Kundenparameter:<ul>");
            for (CustomerFlag flag : flags) {
                sb.append("<li>").append(flag.getName()).append("</li>");
            }
            sb.append("</ul>");
        }
        if ( comment != null ) {
            sb.append("<p>Kommentar: ").append(comment).append("</p>");
        }
        return sb.toString();
    }
}
