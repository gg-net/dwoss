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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.*;

import eu.ggnet.dwoss.rules.AddressType;

import lombok.*;
import lombok.experimental.Builder;

import static javax.persistence.CascadeType.ALL;

/**
 * A contact bound by a specific customer.
 * <p>
 * All contact relevant information is held here.
 * <p>
 * @author pascal.perau
 */
@Entity
@ToString(exclude = {"addresses", "communications"})
@EqualsAndHashCode(of = {"id"})
@Indexed
public class Contact implements Serializable {

    public static final String EMAIL_PATTERN = "^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";

    public static final String PHONE_PATTERN = "^[_0-9\\\\+]+(\\s?[0-9]+)";

    @RequiredArgsConstructor
    public enum Sex {

        MALE, FEMALE;
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
     * Is this Element prefered.
     * Hint: Makes things simpler and nice, but may result in conflicts. If this becomes an issue, we can consider changing the database.
     */
    @Getter
    @Setter
    @Field
    private boolean prefered;

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
    @IndexedEmbedded
    private final List<Address> addresses = new ArrayList<>();

    /**
     * All {@link Address}<code>es</code> associated with the contact.
     */
    @OneToMany(cascade = ALL)
    @NonNull
    @IndexedEmbedded
    private final List<Communication> communications = new ArrayList<>();

    public Contact() {
    }

    @Builder
    public Contact(Sex sex, boolean prefered, String title, String firstName, String lastName) {
        this.sex = sex;
        this.prefered = prefered;
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
     * Returns the first prefered {@link Address} of that Type, may return null.
     * If multiple Values of the same type are prefered the result is not gurantied.
     * <p>
     * @param type the {@link AddressType}
     * @return the first prefered communication of that Type, may return null.
     */
    public Address prefered(AddressType type) {
        for (Address address : addresses) {
            if ( address.getPreferedType() == type ) return address;
        }
        return null;
    }

    public void add(Communication c) {
        if ( c != null ) communications.add(c);
    }

    public void add(Address a) {
        if ( a != null ) addresses.add(a);
    }

    /**
     * Returns a human readable representation of title, first and lastname.
     *
     * @return a human readable representation of title, first and lastname.
     */
    public String toFullName() {
        return (title == null ? "" : title + " ") + (firstName == null ? "" : firstName + " ") + (lastName == null ? "" : lastName);
    }

    /**
     *
     * @return
     */
    public List<Address> getAddresses() {
        return new ArrayList<>(addresses);
    }

    public List<Communication> getCommunications() {
        return new ArrayList<>(communications);
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append(prefered ? "<b>" : "");
        sb.append(title == null ? "" : title + "&nbsp;").append(firstName == null ? "" : firstName + "&nbsp;").append(lastName == null ? "" : lastName)
                .append(sex == null ? "&nbsp;(?)" : "(" + sex + ")").append(prefered ? "&nbsp;(Bevorzugt)" : "").append("<br />");
        if ( !addresses.isEmpty() ) {
            sb.append("Adresse(n):<ul>");
            for (Address address : addresses) {
                sb.append("<li>").append(address.toHtml()).append("</li>");
            }
            sb.append("</ul>");
        }
        if ( !communications.isEmpty() ) {
            sb.append("Com:<ul>");
            for (Communication communication : communications) {
                sb.append("<li>").append(communication.toHtml()).append("</li>");
            }
            sb.append("</ul>");
        }
        sb.append(prefered ? "</b>" : "");
        return sb.toString();
    }

}
