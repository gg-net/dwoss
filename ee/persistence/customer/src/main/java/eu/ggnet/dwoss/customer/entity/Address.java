package eu.ggnet.dwoss.customer.entity;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import eu.ggnet.dwoss.rules.AddressType;

import lombok.*;

/**
 * Address data.
 * <p>
 * @author pascal.perau
 */
@Getter
@Entity
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of = {"id"})
@Indexed
public class Address implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * Is this a address to be used for a specific type by default.
     */
    @Setter
    @Enumerated
    private AddressType preferedType;

    @Setter
    @NotNull
    @Field
    private String street;

    @Setter
    @NotNull
    @Field
    @Boost(1.5F)
    private String city;

    @Setter
    @NotNull
    @Field
    private String zipCode;

    /**
     * The 'ISO 3166 2' country code.
     * As default DE is used.
     */
    @NotNull
    @Size(min = 2, max = 2)
    @Field
    private String isoCountry = "DE";

    public void setIsoCountry(Locale country) {
        if ( country == null ) throw new NullPointerException("Null not allowed");
        else isoCountry = country.getCountry();
    }

    public Address(AddressType preferedType) {
        this.preferedType = preferedType;
    }

}
