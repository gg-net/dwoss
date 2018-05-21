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
import java.util.Locale;

import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.Country;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;

import lombok.Data;

/**
 * Simple representation of a customer, can only be used if the customer datamodel has a definied state.
 *
 * @author oliver.guenther
 */
@Data
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

}
