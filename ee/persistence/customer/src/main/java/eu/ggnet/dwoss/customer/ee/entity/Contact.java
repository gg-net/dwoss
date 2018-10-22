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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.*;

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.customer.ee.entity.stash.AddressStash;
import eu.ggnet.dwoss.customer.ee.entity.stash.CommunicationStash;
import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

import static javax.persistence.CascadeType.ALL;

/**
 * A contact bound by a specific customer.
 * <p>
 * All contact relevant information is held here.
 * <p>
 * @has 0..n - 0..1 Sex
 * @has 0..1 - 0..n Address
 * @has 0..1 - 0..n Communication
 *
 * @author pascal.perau
 */
@Entity
@ToString(exclude = {"addresses", "communications"})
@EqualsAndHashCode(of = {"id"})
@Indexed
public class Contact implements Serializable, AddressStash, CommunicationStash, EagerAble {

    @RequiredArgsConstructor
    public enum Sex {

        MALE("m"), FEMALE("w");

        @Getter
        private final String sign;
    }

    @Id
    @Getter
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * Salutation for the contact.
     * Seperated from the title for more flexibility.
     */
    @Getter
    @Setter
    private Sex sex;

    /**
     * All titles the contact carries.
     */
    @Getter
    @Setter
    @Field
    private String title;

    @Getter
    @Setter
    @NotNull
    @Field
    @Boost(1.5F)
    private String firstName;

    @Getter
    @Setter
    @NotNull
    @Field
    @Boost(2f)
    private String lastName;

    /**
     * All {@link Address}<code>es</code> associated with the contact.
     */
    @OneToMany(cascade = ALL)
    @NonNull
    @Getter
    @IndexedEmbedded
    private final List<Address> addresses = new ArrayList<>();

    /**
     * All {@link Address}<code>es</code> associated with the contact.
     */
    @OneToMany(cascade = ALL)
    @NonNull
    @Getter
    @IndexedEmbedded
    private final List<Communication> communications = new ArrayList<>();

    public Contact() {
    }

    /**
     * Constructor for tryouts, do not use in productive.
     *
     * @param id the db id.
     */
    public Contact(long id) {
        this.id = id;
    }

    @Builder
    public Contact(Sex sex, String title, String firstName, String lastName) {
        this.sex = sex;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Returns the first prefered communication of that Type, may return null.
     * If multiple Values of the same type are prefered the result is not gurantied.
     * <p>
     * @param type the {@link Communication#type}
     * @return the first prefered communication of that Type, may return null.
     */
    public Communication prefered(Communication.Type type) {
        for (Communication communication : communications) {
            if ( communication.getType() == type && communication.isPrefered() ) return communication;
        }
        return null;
    }

    /**
     * Returns a human readable representation of title, first and lastname.
     *
     * @return a human readable representation of title, first and lastname.
     */
    public String toFullName() {
        return (title == null ? "" : title + " ") + (firstName == null ? "" : firstName + " ") + (lastName == null ? "" : lastName);
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append(title == null ? "" : title + "&nbsp;").append(firstName == null ? "" : firstName + "&nbsp;").append(lastName == null ? "" : lastName)
                .append(sex == null ? "" : "&nbsp;(" + sex.getSign() + ")").append("<br />");
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
     * Returns null, if the Contact is valid.
     * Rules are:
     * <ul>
     * <li>lastName is not blank</li>
     * <li>all Address have to be valid</li>
     * <li>all Communications have to be valid</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    public String getViolationMessage() {
        if ( StringUtils.isBlank(lastName) ) return "LastName is blank";
        if ( addresses.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Address: " + addresses.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if ( communications.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Communications: " + communications.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        return null;
    }

    /**
     * Multi line String representation of the contact with addresses and communications
     *
     * @return a multi line string
     */
    public String toMultiLineString() {
        StringBuilder sb = new StringBuilder();
        sb.append(toFullName());

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
        getAddresses().forEach(a -> a.getId());
        getCommunications().size();
    }

}
