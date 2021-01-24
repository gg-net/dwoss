package tryout.stub;

import java.util.Collections;
import java.util.EnumSet;

import eu.ggnet.dwoss.core.common.values.*;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.spi.CachedMandators;

/*
 * Copyright (C) 2021 GG-Net GmbH
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
/**
 *
 * @author oliver.guenther
 */
public class CachedMandatorsStub implements CachedMandators {

    @Override
    public DefaultCustomerSalesdata loadSalesdata() {
        return new DefaultCustomerSalesdata.Builder()
                .shippingCondition(ShippingCondition.SIX_MIN_TEN)
                .paymentCondition(PaymentCondition.CUSTOMER)
                .paymentMethod(PaymentMethod.DIRECT_DEBIT)
                .addAllAllowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER, SalesChannel.RETAILER))
                .addViewOnlyCustomerIds(666)
                .build();
    }

    @Override
    public SpecialSystemCustomers loadSystemCustomers() {
        return new SpecialSystemCustomers(Collections.emptyMap());
    }

    //<editor-fold defaultstate="collapsed" desc="unused methods">
    @Override
    public Mandator loadMandator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReceiptCustomers loadReceiptCustomers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Contractors loadContractors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PostLedger loadPostLedger() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //</editor-fold>

}
