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
import java.util.Map;
import java.util.Optional;

import eu.ggnet.dwoss.common.api.values.TradeName;


/**
 * Contains SystemCustomers which are used for the Repayment operation based on the Contractor.
 * <p>
 * @author oliver.guenther
 */
public class RepaymentCustomers  implements Serializable{

    private final Map<TradeName, Long> contractorCustomers;

    public Optional<Long> get(TradeName contractor) {
        return Optional.ofNullable(contractorCustomers.get(contractor));
    }

    public RepaymentCustomers(Map<TradeName, Long> contractorCustomers) {
        this.contractorCustomers = contractorCustomers;
    }

    @Override
    public String toString() {
        return "RepaymentCustomers{" + "contractorCustomers=" + contractorCustomers + '}';
    }
    
}
