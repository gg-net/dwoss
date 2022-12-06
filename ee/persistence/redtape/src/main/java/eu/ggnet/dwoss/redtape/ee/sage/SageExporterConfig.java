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
package eu.ggnet.dwoss.redtape.ee.sage;

import java.io.Serializable;

import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

/**
 * Exporter Engine Configuration.
 *
 * @author oliver.guenther
 */
public interface SageExporterConfig extends Serializable {

    /**
     * Must return a debitor ledger to be used by default.
     *
     * @return a debitor ledger
     */
    int getDefaultDebitorLedger();

    /**
     * If it returns true, the individual customer ledgers are ignored.
     *
     * @return default false
     */
    boolean isCustomerLedgersDisabled();

    /**
     * Must return a not black string for the beleg value of the xml export.
     *
     * @param doc      the actual document.
     * @param customer the customer
     * @return the beleg text
     */
    String beleg(Document doc, UiCustomer customer);

    /**
     * Must return a not black string for the buchText value of the xml export.
     *
     * @param doc      the actual document.
     * @param customer the customer
     * @return the buchText text
     */
    String buchText(Document doc, UiCustomer customer);

    /**
     * Must return a not black string for the wawiBeleg value of the xml export.
     *
     * @param doc      the actual document.
     * @param customer the customer
     * @return the wawiBeleg text
     */
    String wawiBeleg(Document doc, UiCustomer customer);

    /**
     * Must return a not black string for the stCode value of the xml export.
     *
     * @param doc the actual document.
     * @return the stCode text
     */
    String stCode(Document doc);

    /**
     * Returns a value for the field kakenn.
     * 
     * @return a value for the field kakenn.
     */
    String kakenn();
 
       /**
     * Returns a value for the field kbkenn.
     * 
     * @return a value for the field kbkenn.
     */
    String kbkenn();
    
}
