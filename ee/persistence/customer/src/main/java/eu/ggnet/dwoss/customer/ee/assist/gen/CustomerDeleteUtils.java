/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.customer.ee.assist.gen;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static eu.ggnet.dwoss.customer.ee.entity.QAddress.address;
import static eu.ggnet.dwoss.customer.ee.entity.QAddressLabel.addressLabel;
import static eu.ggnet.dwoss.customer.ee.entity.QCommunication.communication;
import static eu.ggnet.dwoss.customer.ee.entity.QCompany.company;
import static eu.ggnet.dwoss.customer.ee.entity.QContact.contact;
import static eu.ggnet.dwoss.customer.ee.entity.QCustomer.customer;
import static eu.ggnet.dwoss.customer.ee.entity.QMandatorMetadata.mandatorMetadata;

/**
 * Utility class to clear the customer database.
 *
 * @author oliver.guenther
 */
public class CustomerDeleteUtils {

    private CustomerDeleteUtils() {
    }

    public static void deleteAll(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.delete(addressLabel).execute();
        queryFactory.delete(customer).execute();
        queryFactory.delete(company).execute();
        queryFactory.delete(contact).execute();
        queryFactory.delete(address).execute();
        queryFactory.delete(communication).execute();
        queryFactory.delete(mandatorMetadata).execute();
        em.flush();
    }

    /**
     * If the database is empty, the result is null, otherwise a message with more details.
     *
     * @param em the custmoer emtitymanager
     * @return null if empty, otherwise a message with details.
     */
    public static String validateEmpty(EntityManager em) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        if ( !queryFactory.from(customer).fetch().isEmpty() ) return "customer.Customer is not empty";
        if ( !queryFactory.from(contact).fetch().isEmpty() ) return "customer.Contact is not empty";
        if ( !queryFactory.from(company).fetch().isEmpty() ) return "customer.Company is not empty";
        if ( !queryFactory.from(address).fetch().isEmpty() ) return "customer.Address is not empty";
        if ( !queryFactory.from(addressLabel).fetch().isEmpty() ) return "customer.AddressLabel is not empty";
        if ( !queryFactory.from(communication).fetch().isEmpty() ) return "customer.Communication is not empty";
        if ( !queryFactory.from(mandatorMetadata).fetch().isEmpty() ) return "customer.MandatorMetadata is not empty";
        return null;
    }

}
