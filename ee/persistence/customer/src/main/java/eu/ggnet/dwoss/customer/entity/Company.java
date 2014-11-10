package eu.ggnet.dwoss.customer.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.*;

import eu.ggnet.dwoss.rules.AddressType;

import lombok.*;
import lombok.experimental.Builder;

import static javax.persistence.CascadeType.ALL;

/**
 * Represents a company.
 * <p>
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
     */
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
     */
    public Address prefered(AddressType type) {
        for (Address address : addresses) {
            if ( address.getPreferedType() == type ) return address;
        }
        return null;
    }

    public void add(Contact c) {
        if ( c != null ) contacts.add(c);
    }

    public void add(Address a) {
        if ( a != null ) addresses.add(a);
    }

    public void add(Communication c) {
        if ( c != null ) communications.add(c);
    }

    public void remove(Contact c) {
        contacts.remove(c);
    }

    public void remove(Address a) {
        addresses.remove(a);
    }

    public void remove(Communication c) {
        communications.remove(c);
    }

    public List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }

    public List<Address> getAddresses() {
        return new ArrayList<>(addresses);
    }

    public List<Communication> getCommunications() {
        return new ArrayList<>(communications);
    }

}
