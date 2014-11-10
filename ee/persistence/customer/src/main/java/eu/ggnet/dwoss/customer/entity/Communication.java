package eu.ggnet.dwoss.customer.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import lombok.*;

/**
 * Specifies a way of communication.
 * <p>
 * @author oliver.guenther
 */
@Entity
@Getter
@ToString
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Indexed
public class Communication implements Serializable {

    /**
     * Represents some of the common ways to communicate nowadays.
     */
    public enum Type {

        PHONE, MOBILE, FAX, ICQ, SKYPE, EMAIL, FACEBOOK
    }

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * The type of communication.
     */
    @Setter
    @NotNull
    @Enumerated
    private Type type;

    /**
     * The value for the {@link Type} of communication.
     * I.e. Phone: 0123 456789, E-Mail: max.mustermann@mustermail.de
     */
    @Setter
    @NotNull
    @Field
    private String identifier;

    /**
     * Is this a prefered way of communication.
     */
    @Setter
    private boolean prefered;

    public Communication(Type type, boolean prefered) {
        this.type = type;
        this.prefered = prefered;
    }

}
