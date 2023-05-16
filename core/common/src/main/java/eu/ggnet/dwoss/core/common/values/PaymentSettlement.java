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
package eu.ggnet.dwoss.core.common.values;

/**
 * Possible receipts of Payment.
 * Represents the way, the customer balanced the receipt of a Payment.<br />
 * Settlement may have restrictions according to {@link PaymentMethod} of the {@link Document#dossier}
 */
public enum PaymentSettlement {
    /**
     * Direct physical transfer of money.
     * 
     * @deprecated No longer in use sice 2023.
     */
    @Deprecated
    CASH("Barzahlung"),
    /**
     * Direct electronic transfer of money.
     */
    E_CASH("EC-Zahlung"),
    /**
     * Transfer of money provided by the customer.
     */
    REMITTANCE("Bank"),
    /**
     * Paypal.
     */    
    PAYPAL("Paypal"),
    /**
     * eBay.
     */    
    EBAY("eBay");

    private final String description;

    private PaymentSettlement(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

}
