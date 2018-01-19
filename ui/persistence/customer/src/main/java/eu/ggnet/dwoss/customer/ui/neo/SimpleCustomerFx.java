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
package eu.ggnet.dwoss.customer.ui.neo;

import javafx.beans.property.*;

import eu.ggnet.dwoss.customer.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.entity.Customer.Source;

import lombok.ToString;

/**
 * The Fx variant of a SimpleCustomer.
 * 
 * @author jens.papenhagen
 */
@ToString
public class SimpleCustomerFx {

    private LongProperty idProperty = new SimpleLongProperty();

    private StringProperty titleProperty = new SimpleStringProperty();

    private StringProperty firstNameProperty = new SimpleStringProperty();

    private StringProperty lastNameProperty = new SimpleStringProperty();

    private StringProperty streetProperty = new SimpleStringProperty();

    private StringProperty zipCodeProperty = new SimpleStringProperty();

    private StringProperty cityProperty = new SimpleStringProperty();

    private StringProperty isoCountryProperty = new SimpleStringProperty();

    private StringProperty mobilePhoneProperty = new SimpleStringProperty();

    private StringProperty landlinePhoneProperty = new SimpleStringProperty();

    private ObjectProperty<Sex> sexProperty = new SimpleObjectProperty<>(Sex.MALE);

    private ObjectProperty<Source> dataSourceProperty = new SimpleObjectProperty<>(Source.EXISITING);

    private StringProperty commentProperty = new SimpleStringProperty();

    public SimpleCustomerFx() {
    }

    public SimpleCustomerFx(long id, String title, String firstName, String lastName, String street, String zipCode, String city, String isoCountry, String mobilePhone, String landlinePhone, Sex sex, Source dataSource, String comment) {
        this.idProperty.set(id);
        this.titleProperty.set(title);
        this.firstNameProperty.set(firstName);
        this.lastNameProperty.set(lastName);
        this.streetProperty.set(street);
        this.zipCodeProperty.set(zipCode);
        this.cityProperty.set(city);
        this.isoCountryProperty.set(isoCountry);
        this.mobilePhoneProperty.set(mobilePhone);
        this.landlinePhoneProperty.set(landlinePhone);
        this.sexProperty.set(sex);
        this.dataSourceProperty.set(dataSource);
        this.commentProperty.set(comment);
    }

    public void setIdProperty(LongProperty idProperty) {
        this.idProperty = idProperty;
    }

    public void setTitleProperty(StringProperty titleProperty) {
        this.titleProperty = titleProperty;
    }

    public void setFirstNameProperty(StringProperty firstNameProperty) {
        this.firstNameProperty = firstNameProperty;
    }

    public void setLastNameProperty(StringProperty lastNameProperty) {
        this.lastNameProperty = lastNameProperty;
    }

    public void setStreetProperty(StringProperty streetProperty) {
        this.streetProperty = streetProperty;
    }

    public void setZipCodeProperty(StringProperty zipCodeProperty) {
        this.zipCodeProperty = zipCodeProperty;
    }

    public void setCityProperty(StringProperty cityProperty) {
        this.cityProperty = cityProperty;
    }

    public void setIsoCountryProperty(StringProperty isoCountryProperty) {
        this.isoCountryProperty = isoCountryProperty;
    }

    public void setMobilePhoneProperty(StringProperty mobilePhoneProperty) {
        this.mobilePhoneProperty = mobilePhoneProperty;
    }

    public void setLandlinePhoneProperty(StringProperty landlinePhoneProperty) {
        this.landlinePhoneProperty = landlinePhoneProperty;
    }

    public void setSexProperty(ObjectProperty<Sex> sexProperty) {
        this.sexProperty = sexProperty;
    }

    public void setDataSourceProperty(ObjectProperty<Source> dataSourceProperty) {
        this.dataSourceProperty = dataSourceProperty;
    }

    public void setCommentProperty(StringProperty commentProperty) {
        this.commentProperty = commentProperty;
    }

    public LongProperty getIdProperty() {
        return idProperty;
    }

    public StringProperty getTitleProperty() {
        return titleProperty;
    }

    public StringProperty getFirstNameProperty() {
        return firstNameProperty;
    }

    public StringProperty getLastNameProperty() {
        return lastNameProperty;
    }

    public StringProperty getStreetProperty() {
        return streetProperty;
    }

    public StringProperty getZipCodeProperty() {
        return zipCodeProperty;
    }

    public StringProperty getCityProperty() {
        return cityProperty;
    }

    public StringProperty getIsoCountryProperty() {
        return isoCountryProperty;
    }

    public StringProperty getMobilePhoneProperty() {
        return mobilePhoneProperty;
    }

    public StringProperty getLandlinePhoneProperty() {
        return landlinePhoneProperty;
    }

    public ObjectProperty<Sex> getSexProperty() {
        return sexProperty;
    }

    public ObjectProperty<Source> getDataSourceProperty() {
        return dataSourceProperty;
    }

    public StringProperty getCommentProperty() {
        return commentProperty;
    }

    public final long getId() {
        return idProperty.get();
    }

    public final void setId(long value) {
        idProperty.set(value);
    }

    public LongProperty idProperty() {
        return idProperty;
    }

    public final String getTitle() {
        return titleProperty.get();
    }

    public final void setTitle(String value) {
        titleProperty.set(value);
    }

    public StringProperty titleProperty() {
        return titleProperty;
    }

    public final String getFirstName() {
        return firstNameProperty.get();
    }

    public final void setFirstName(String value) {
        firstNameProperty.set(value);
    }

    public StringProperty firstNameProperty() {
        return firstNameProperty;
    }

    public final String getLastName() {
        return lastNameProperty.get();
    }

    public final void setLastName(String value) {
        lastNameProperty.set(value);
    }

    public StringProperty lastNameProperty() {
        return lastNameProperty;
    }

    public final String getStreet() {
        return streetProperty.get();
    }

    public final void setStreet(String value) {
        streetProperty.set(value);
    }

    public StringProperty streetProperty() {
        return streetProperty;
    }

    public final String getZipCode() {
        return zipCodeProperty.get();
    }

    public final void setZipCode(String value) {
        zipCodeProperty.set(value);
    }

    public StringProperty zipCodeProperty() {
        return zipCodeProperty;
    }

    public final String getCity() {
        return cityProperty.get();
    }

    public final void setCity(String value) {
        cityProperty.set(value);
    }

    public StringProperty cityProperty() {
        return cityProperty;
    }

    public final String getIsoCountry() {
        return isoCountryProperty.get();
    }

    public final void setIsoCountry(String value) {
        isoCountryProperty.set(value);
    }

    public StringProperty isoCountryProperty() {
        return isoCountryProperty;
    }

    public final String getMobilePhone() {
        return mobilePhoneProperty.get();
    }

    public final void setMobilePhone(String value) {
        mobilePhoneProperty.set(value);
    }

    public StringProperty mobilePhoneProperty() {
        return mobilePhoneProperty;
    }

    public final String getLandlinePhone() {
        return landlinePhoneProperty.get();
    }

    public final void setLandlinePhone(String value) {
        landlinePhoneProperty.set(value);
    }

    public StringProperty landlinePhoneProperty() {
        return landlinePhoneProperty;
    }

    public final Sex getSex() {
        return sexProperty.get();
    }

    public final void setSex(Sex value) {
        sexProperty.set(value);
    }

    public ObjectProperty<Sex> sexProperty() {
        return sexProperty;
    }

    public final Source getDataSource() {
        return dataSourceProperty.get();
    }

    public final void setDataSource(Source value) {
        dataSourceProperty.set(value);
    }

    public ObjectProperty<Source> dataSourceProperty() {
        return dataSourceProperty;
    }

    public final String getComment() {
        return commentProperty.get();
    }

    public final void setComment(String value) {
        commentProperty.set(value);
    }

    public StringProperty commentProperty() {
        return commentProperty;
    }

}
