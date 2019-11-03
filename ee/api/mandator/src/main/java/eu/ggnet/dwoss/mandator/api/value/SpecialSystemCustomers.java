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
import java.util.*;

import eu.ggnet.dwoss.core.common.values.DocumentType;


/**
 * Contains SystemCustomers, which do not use the {@link DocumentType#BLOCK}, but a special one.
 * <p>
 * @author oliver.guenther
 */
public class SpecialSystemCustomers implements Serializable {

    private final Map<Long, DocumentType> specialCustomers;

    public Optional<DocumentType> get(Long customerId) {
        return Optional.ofNullable(specialCustomers.get(customerId));
    }

    public SpecialSystemCustomers(Map<Long, DocumentType> specialCustomers) {
        this.specialCustomers = specialCustomers;
    }

    public Map<Long, DocumentType> getSpecialCustomers() {
        return Collections.unmodifiableMap(specialCustomers);
    }
    
}
