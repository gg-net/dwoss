/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee.entity.dto;

import java.io.Serializable;

import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.Country;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;

/**
 * Simple representation of a customer, can only be used if the customer datamodel has a definied state.
 *
 * @author oliver.guenther
 */
// TODO: Consider Freebuilder or public field access.
public class SimpleCustomer implements Serializable {

    private long id;

    private short optLock;

    private String title;

    private String firstName;

    private String lastName;

    private String street;

    private String zipCode;

    private String city;

    private Country country;

    private String mobilePhone;

    private String landlinePhone;

    private String email;

    private Sex sex;

    private Source source;

    private String comment;

    private String companyName;

    private String taxId;

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public short getOptLock() {
        return optLock;
    }
    
    public void setOptLock(short optLock) {
        this.optLock = optLock;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getStreet() {
        return street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public Country getCountry() {
        return country;
    }
    
    public void setCountry(Country country) {
        this.country = country;
    }
    
    public String getMobilePhone() {
        return mobilePhone;
    }
    
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
    
    public String getLandlinePhone() {
        return landlinePhone;
    }
    
    public void setLandlinePhone(String landlinePhone) {
        this.landlinePhone = landlinePhone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Sex getSex() {
        return sex;
    }
    
    public void setSex(Sex sex) {
        this.sex = sex;
    }
    
    public Source getSource() {
        return source;
    }
    
    public void setSource(Source source) {
        this.source = source;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getTaxId() {
        return taxId;
    }
    
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return "SimpleCustomer{" + "id=" + id + ", optLock=" + optLock + ", title=" + title + ", firstName=" + firstName + ", lastName=" + lastName 
                + ", street=" + street + ", zipCode=" + zipCode + ", city=" + city + ", country=" + country + ", mobilePhone=" + mobilePhone 
                + ", landlinePhone=" + landlinePhone + ", email=" + email + ", sex=" + sex + ", source=" + source + ", comment=" + comment 
                + ", companyName=" + companyName + ", taxId=" + taxId + '}';
    }
    
    

}
