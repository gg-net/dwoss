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
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
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

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String PHONE_PATTERN = "^[_0-9\\+]+(\\s?[0-9]+)";

    /**
     * Represents some of the common ways to communicate nowadays.
     */
    public enum Type {

        PHONE(PHONE_PATTERN), MOBILE(PHONE_PATTERN), FAX, ICQ, SKYPE, EMAIL(EMAIL_PATTERN), FACEBOOK;

        private final String validPattern;

        private Type() {
            this(null);
        }

        private Type(String validPattern) {
            this.validPattern = validPattern;
        }

        public Optional<String> getValidPattern() {
            return Optional.ofNullable(validPattern);
        }

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

    /**
     * Html representation of the class.
     *
     * @return a html string
     */
    public String toHtml() {
        return type + ":&nbsp;" + identifier;
    }

    /**
     * Returns null, if the Communication is valid.
     * Rules are:
     * <ul>
     * <li>The identifier pattern must match the Communication type</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    public String getViolationMessages() {
        if ( type == null ) return "Type is null";
        if ( StringUtils.isBlank(identifier) ) return "Identifier is blank";
        return type.getValidPattern()
                .map(pattern -> identifier.matches(pattern))
                .map(b -> (b ? null : "Identifier " + identifier + " vom Type: " + type + " passt nicht auf pattern " + type.getValidPattern().get()))
                .orElse(null);

        //new email pattern with longet domains
        //"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        //old pattern "^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";
//        if ( type.equals(Type.EMAIL) && !identifier.matches(Communication.EMAIL_PATTERN) ) return "the Communication are missmatchting the EMail Pattern";
//        if ( (type.equals(Type.MOBILE) || type.equals(Type.PHONE) || type.equals(Type.FAX)) && !identifier.matches(Communication.PHONE_PATTERN) )
//            return "the Communication are missmatchting the Phone Pattern";
//
//        return null;
    }

}
