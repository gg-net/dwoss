/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.rules;

import lombok.*;

/**
 * Payment Conditions for Customers.
 *
 * @author bastian.venz
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum PaymentCondition {

    /**
     * Customer, can only buy from SalesChannel Customer.
     */
    CUSTOMER("Endkunde"),
    /**
     * Dealer, can buy from SalesChannel Dealer and Customer.
     */
    DEALER("Händler"),
    /**
     * Employee, can buy from SalesChannel Dealer and Customer.
     */
    EMPLOYEE("Angesteller Mitarbeiter"),
    /**
     * External Employee, can buy from SalesChannel Dealer and Customer.
     */
    EXTERNAL_EMPLOYEE("Externer Mitarbeiter"),
    /**
     * Dealer, can buy from SalesChannel Dealer and Customer.
     */
    DEALER_1_PERCENT_DISCOUNT("Händler 1% Rabat"),
    /**
     * Dealer, can buy from SalesChannel Dealer and Customer.
     */
    DEALER_2_PERCENT_DISCOUNT("Händler 2% Rabat"),
    /**
     * Dealer, can buy from SalesChannel Dealer and Customer.
     */
    DEALER_3_PERCENT_DISCOUNT("Händler 3% Rabat");

    private final String note;

}
