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
import java.util.Objects;
import java.util.Optional;

import eu.ggnet.dwoss.common.api.values.AddressType;
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;

/**
 * DTO for AddressLabel.
 * 
 * @author oliver.guenther
 */
// TODO: Consider Validate. customerId,addressId must be > 0. type never null.
public class AddressLabelDto implements Serializable {

    private long id;

    private long customerId;

    private long companyId;

    private long contactId;

    private long addressId;

    private AddressType type;
    
    public AddressLabelDto() {
    }    

    public AddressLabelDto(AddressLabel al) {
        Objects.requireNonNull(al,"AddressLabel must not be null");
        this.id = al.getId();
        this.customerId = al.getCustomer().getId();
        this.addressId = al.getAddress().getId();
        this.type = al.getType();
        Optional.ofNullable(al.getCompany()).ifPresent(c -> this.companyId = c.getId());
        Optional.ofNullable(al.getContact()).ifPresent(c -> this.contactId = c.getId());
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AddressLabelDto{" + "id=" + id + ", customerId=" + customerId + ", companyId=" + companyId + ", contactId=" + contactId + ", addressId=" + addressId + ", type=" + type + '}';
    }
    
}
