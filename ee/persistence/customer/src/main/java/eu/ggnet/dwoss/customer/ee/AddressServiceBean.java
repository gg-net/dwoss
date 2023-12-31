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
package eu.ggnet.dwoss.customer.ee;

import java.util.Optional;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import eu.ggnet.dwoss.customer.api.AddressService;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.api.AddressChange;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.core.common.values.AddressType;
import eu.ggnet.dwoss.customer.ee.entity.AddressLabel;
import eu.ggnet.dwoss.customer.ee.entity.Customer;

/**
 * Interface for other operations to get address information.
 * <p>
 * @author pascal.perau
 */
@Stateless
@LocalBean
public class AddressServiceBean implements AddressService {

    @Inject
    private CustomerEao customerEao;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesData;

    @Inject
    private Event<AddressChange> adressChangeEvent;

    // TODO: Wird im Moment nicht verwendet, das es in der Ui jetzt sehr schwer wäre festzustellen, ob ein Änderung an einem Kunden auch ein relevantes Adresslabel betrifft. Vielleicht 2025.
    @Override
    public void notifyAddressChange(AddressChange changeEvent) {
        adressChangeEvent.fire(changeEvent);
    }

    @Override
    public String defaultAddressLabel(long customerId, AddressType type) {
        Customer customer = customerEao.findById(customerId);
        Optional<AddressLabel> findAddressLable = customer.getAddressLabels()
                .stream()
                .filter(a -> a.getType() == type)
                .findFirst();
        if ( findAddressLable.isPresent() ) {
            return findAddressLable.get().toLabel();
        }

        return customer.getAddressLabels()
                .stream()
                .findAny().get()
                .toLabel();
    }

}
