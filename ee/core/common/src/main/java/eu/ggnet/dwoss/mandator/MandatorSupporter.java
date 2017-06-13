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
package eu.ggnet.dwoss.mandator;

import javax.ejb.Remote;

import eu.ggnet.dwoss.mandator.api.value.*;

/**
 * Support for the Mandator, allows loading in the Remote UI.
 * <p/>
 * @author oliver.guenther
 */
@Remote
public interface MandatorSupporter {

    /**
     * Loads the Mandator.
     * <p/>
     * @return the Mandator.
     */
    Mandator loadMandator();

    /**
     * Loads the default Salesdata.
     * <p>
     * @return the default salesdata.
     */
    DefaultCustomerSalesdata loadSalesdata();

    /**
     * Loads the ReceiptCustomers.
     * <p>
     * @return the receiptCustomers.
     */
    ReceiptCustomers loadReceiptCustomers();

    /**
     * Return SystemCustomers which need special document handling.
     * <p>
     * @return SystemCustomers which need special document handling.
     */
    SpecialSystemCustomers loadSystemCustomers();

    /**
     * Returns the contractors
     * <p>
     * @return the contractors.
     */
    Contractors loadContractors();

    /**
     * Returns the {@link PostLedger}.
     * <p>
     * @return the {@link PostLedger}.
     */
    PostLedger loadPostLedger();

    /**
     * Returns the {@link ShippingTerms}.
     * <p>
     * @return the {@link ShippingTerms}.
     */
    ShippingTerms loadShippingTerms();
}
