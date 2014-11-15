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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;

import lombok.Value;

/**
 * Parameters for export to finalcial accounting Systems.
 * For now this is only used in the GSOffice Export.
 * <p>
 * @author oliver.guenther
 */
@Value
public class FinancialAccounting  implements Serializable{

    /**
     * Default Ledger of customers, if no extra ledger is set.
     */
    private final int defaultLedger;

    /**
     * If set, the individual customer ledgers are ignored.
     */
    private final boolean disableCustomerLedgers;

}
