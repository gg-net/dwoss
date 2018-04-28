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

import java.util.HashMap;
import java.util.Map;

import eu.ggnet.dwoss.common.api.values.TradeName;

/**
 *
 * @author oliver.guenther
 */
public class CustomersBuilder {

    private final Map<TradeName, Long> contractorCustomers = new HashMap<>();

    public CustomersBuilder put(TradeName contractor, long id) {
        contractorCustomers.put(contractor, id);
        return this;
    }

    public DeleteCustomers toDelete() {
        return new DeleteCustomers(contractorCustomers);
    }

    public RepaymentCustomers toRepayment() {
        return new RepaymentCustomers(contractorCustomers);
    }

    public ScrapCustomers toScrap() {
        return new ScrapCustomers(contractorCustomers);
    }

}
