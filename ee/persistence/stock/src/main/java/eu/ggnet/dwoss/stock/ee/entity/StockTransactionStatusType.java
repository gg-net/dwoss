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
package eu.ggnet.dwoss.stock.ee.entity;

/**
 * The status of a transaction. See {@link StockTransactionType} for different transaction types and the allowed stats.
 */
public enum StockTransactionStatusType {

    /**
     * A transaction is prepared. All units on this transaction are still in stock, for the point of view of an inventory taking.
     * For the point of view of what can still be taken from a stock, these units are not in the stock.
     */
    PREPARED,
    /**
     * A transaction is commissioned. All units are out of the stock
     */
    COMMISSIONED,
    /**
     * A transaction is in transfer, now actually the units are moved.
     */
    IN_TRANSFER,
    /**
     * A transaction is received. After this state all units must be set in the destination stock.
     */
    RECEIVED,
    /**
     * (Final State) A failed transaction is a transaction, that really failed. This can be, that something was lost during the transaction, or something was
     * gained.
     * By definition a transaction, that ends in the status failed, must contain at least one alternative transaction, which ends in a normal state and
     * represents what happend in reality.
     * <ul>
     * <li>1st transaction contains unit 1,2 and 3. It fails because 2 is somehow lost.</li>
     * <li>2nd transaction contains unit 1 and 3 and completes normal</li>
     * <li>3rd transaction contains unit 2, has a {@link StockTransactionType} of "Lost" or something like that and no destination, but completes normal</li>
     * <li>1st transaction contains reference to 2nd and 3rd transaction</li>
     * </ul>
     *
     */
    FAILED,
    /**
     * (Final State) In some states a transaction can be cancelled legally.
     */
    CANCELLED,
    /**
     * (Final State) A generic status, marking a transaction as complete. No other status can come after this on.
     */
    COMPLETED
}
