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
import java.util.Locale;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.*;

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
     * Is this a address to be used for a specific type by default, null indicates no preference.
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
        if ( country == null ) {
            throw new NullPointerException("Null not allowed");
        } else {
            this.isoCountry = country.getCountry();
        }
    }

    public Address(AddressType preferedType) {
        this.preferedType = preferedType;
    }

    public String toHtml() {
        return (preferedType == null ? "" : "<i>Bevorzugte: " + preferedType.getName() + "<br />"
                + street + "<br />"
                + isoCountry + "&nbsp;" + zipCode + "&nbsp;" + city + (preferedType == null ? "" : "</i>"));
    }

    /**
     * Returns null, if the Address is valid.
     * Rules are:
     * <ul>
     * <li>Street is not blank</li>
     * <li>City is not blank</li>
     * <li>ZipCode is not blank</li>
     * <li>Country is not blank</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    @Null
    public String getViolationMessages() {
        if ( StringUtils.isBlank(street) ) return "Street is blank";
        if ( StringUtils.isBlank(city) ) return "City is blank";
        if ( StringUtils.isBlank(zipCode) ) return "ZipCode is blank";
        if ( StringUtils.isBlank(isoCountry) ) return "Country is blank";
        return null;
    }

}
