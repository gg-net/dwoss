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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import eu.ggnet.dwoss.customer.api.AddressService;
import eu.ggnet.dwoss.customer.ee.eao.CustomerEao;
import eu.ggnet.dwoss.customer.ee.priv.ConverterUtil;
import eu.ggnet.dwoss.redtape.api.event.AddressChange;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.common.api.values.AddressType;

import static eu.ggnet.dwoss.common.api.values.AddressType.INVOICE;

/**
 * Really necessary?
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

    @Override
    public String defaultAddressLabel(long customerId, AddressType type) {
        if ( type == INVOICE )
            return ConverterUtil.convert(customerEao.findById(customerId), mandator.getMatchCode(), salesData).toInvoiceAddress();
        else
            return ConverterUtil.convert(customerEao.findById(customerId), mandator.getMatchCode(), salesData).toShippingAddress();
    }

    @Override
    public void notifyAddressChange(AddressChange changeEvent) {
        adressChangeEvent.fire(changeEvent);
    }

}
