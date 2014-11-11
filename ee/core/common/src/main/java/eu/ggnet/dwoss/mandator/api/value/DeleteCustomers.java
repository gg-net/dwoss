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
package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.TradeName;

import lombok.Value;

/**
 * Contains SystemCustomers which are used for the delete operation based on the Contractor.
 * <p>
 * @author oliver.guenther
 */
@Value
public class DeleteCustomers  implements Serializable{

    private final Map<TradeName, Long> contractorCustomers;

    public Optional<Long> get(TradeName contractor) {
        return Optional.ofNullable(contractorCustomers.get(contractor));
    }

}
