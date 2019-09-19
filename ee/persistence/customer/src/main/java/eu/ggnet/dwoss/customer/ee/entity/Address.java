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

import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import eu.ggnet.dwoss.common.ee.BaseEntity;

/**
 * Address data.
 * <p>
 * @author pascal.perau
 */

@Entity
@Indexed
@SuppressWarnings("PersistenceUnitPresent")
public class Address extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    @NotNull
    @Field
    private String street;

    @NotNull
    @Field
    private String city;

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

    public Address() {
    }

    /**
     * Tryout constructor, do not use in productive.
     *
     * @param id
     */
    public Address(long id) {
        this.id = id;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    @Override
    public long getId() {
        return id;
    }
    
    
    public short getOptLock() {
        return optLock;
    }
    
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getIsoCountry() {
        return isoCountry;
    }
    
    public void setIsoCountry(String isoCountry) {
        this.isoCountry = isoCountry;
    }
    //</editor-fold>
    
    public void setCountry(Country country) {
        if ( country == null ) return;
        setIsoCountry(country.isoCode);
    }

    public Country getCountry() {
        return Country.ofIsoCode(isoCountry);
    }

    public String toHtml() {
        return street + "<br />"
                + isoCountry + "&nbsp;" + zipCode + "&nbsp;" + city;
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
    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getViolationMessage() {
        if ( StringUtils.isBlank(street) ) return "Street is blank";
        if ( StringUtils.isBlank(city) ) return "City is blank";
        if ( StringUtils.isBlank(zipCode) ) return "ZipCode is blank";
        if ( StringUtils.isBlank(isoCountry) ) return "Country is blank";
        return null;
    }

    public String toSingleLineString() {
        StringBuilder sb = new StringBuilder();
        return sb.append(street).append(" ").append(zipCode)
                .append(" ").append(city).toString();
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
