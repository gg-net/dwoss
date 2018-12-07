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
package eu.ggnet.dwoss.customer.ee.entity.dto;

import java.io.Serializable;
import java.util.Optional;

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;

import lombok.Data;
import lombok.NonNull;

/**
 * DTO for AddressLabel.
 * 
 * @author oliver.guenther
 */
// TODO: Consider Validate. customerId,addressId must be > 0. type never null.
@Data
public class AddressLabelDto implements Serializable {

    public AddressLabelDto() {
    }    

    public AddressLabelDto(@NonNull AddressLabel al) {
        this.id = al.getId();
        this.customerId = al.getCustomer().getId();
        this.addressId = al.getAddress().getId();
        this.type = al.getType();
        Optional.ofNullable(al.getCompany()).ifPresent(c -> this.companyId = c.getId());
        Optional.ofNullable(al.getContact()).ifPresent(c -> this.contactId = c.getId());
    }
    
    private long id;

    private long customerId;

    private long companyId;

    private long contactId;

    private long addressId;

    private AddressType type;
    
}
