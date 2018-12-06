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
package tryout.stub;

import java.util.EnumSet;

import eu.ggnet.dwoss.common.api.values.*;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.ee.Mandators;

/**
 *
 * @author oliver.guenther
 */
public class MandatorsStub implements Mandators {

    @Override
    public Mandator loadMandator() {
          return Mandator.builder()
                .matchCode("SAMPLE")
                .bugMail("error@localhost")
                .build();
    }

    @Override
    public DefaultCustomerSalesdata loadSalesdata() {
        return DefaultCustomerSalesdata.builder()
                .paymentCondition(PaymentCondition.CUSTOMER)
                .shippingCondition(ShippingCondition.FIVE)
                .paymentMethod(PaymentMethod.DIRECT_DEBIT)
                .allowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER)).build();
    }

    @Override
    public ReceiptCustomers loadReceiptCustomers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SpecialSystemCustomers loadSystemCustomers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Contractors loadContractors() {
        return new Contractors(EnumSet.allOf(TradeName.class), EnumSet.allOf(TradeName.class));
    }

    @Override
    public PostLedger loadPostLedger() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
