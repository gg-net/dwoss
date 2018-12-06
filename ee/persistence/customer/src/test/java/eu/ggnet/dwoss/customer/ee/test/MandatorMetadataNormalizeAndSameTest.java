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
package eu.ggnet.dwoss.customer.ee.test;

import java.util.EnumSet;

import org.junit.Before;

import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;

import static eu.ggnet.dwoss.common.api.values.PaymentCondition.DEALER_2_PERCENT_DISCOUNT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jens.papenhagen
 */
public class MandatorMetadataNormalizeAndSameTest {

    private MandatorMetadata mm;
    
    private DefaultCustomerSalesdata dcs;
  
    @Before
    public void setUpEqualMetadataAndDefaultSalesdata() {
        mm = new MandatorMetadata();
        mm.setShippingCondition(ShippingCondition.FIVE);
        mm.setPaymentCondition(PaymentCondition.CUSTOMER);
        mm.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        mm.getAllowedSalesChannels().add(SalesChannel.CUSTOMER);
        
        dcs = DefaultCustomerSalesdata.builder()
                .shippingCondition(ShippingCondition.FIVE)
                .paymentCondition(PaymentCondition.CUSTOMER)
                .paymentMethod(PaymentMethod.DIRECT_DEBIT)
                .allowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER))
                .build();
    }
    
    @Test
    public void allIsEqual() {
        assertThat(mm.isSameAs(dcs)).as("Metadata should be same as defaults").isTrue();
        mm.normalize(dcs);
        assertThat(mm.getAllowedSalesChannels()).as("Saleschannels should be emtpy").isEmpty();
        assertThat(mm).as("Normalized Metadata with equal defaults, all should be null")
                .returns(null, MandatorMetadata::getPaymentCondition)
                .returns(null, MandatorMetadata::getPaymentMethod)
                .returns(null, MandatorMetadata::getShippingCondition);
    }

    @Test
    public void differentSalesChannels() {
        mm.getAllowedSalesChannels().add(SalesChannel.RETAILER);        
        assertThat(mm.isSameAs(dcs)).as("Metadata differs from defaults").isFalse();
        mm.normalize(dcs);
        assertThat(mm.getAllowedSalesChannels()).as("Saleschannels differ, should not be emtpy").isNotEmpty();
        assertThat(mm).as("Normalized Metadata with equal defaults, all should be null")
                .returns(null, MandatorMetadata::getPaymentCondition)
                .returns(null, MandatorMetadata::getPaymentMethod)
                .returns(null, MandatorMetadata::getShippingCondition);
    }

        @Test
    public void differentPaymentMethod() {
        mm.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);        
        assertThat(mm.isSameAs(dcs)).as("Metadata differs from defaults").isFalse();
        mm.normalize(dcs);
        assertThat(mm.getAllowedSalesChannels()).as("Saleschannels should be emtpy").isEmpty();
        assertThat(mm).as("Normalized Metadata with different payment method defaults")
                .returns(null, MandatorMetadata::getPaymentCondition)
                .returns(PaymentMethod.ADVANCE_PAYMENT, MandatorMetadata::getPaymentMethod)
                .returns(null, MandatorMetadata::getShippingCondition);
    }

        @Test
    public void differentPaymentCondition() {
        mm.setPaymentCondition(DEALER_2_PERCENT_DISCOUNT);        
        assertThat(mm.isSameAs(dcs)).as("Metadata differs from defaults").isFalse();
        mm.normalize(dcs);
        assertThat(mm.getAllowedSalesChannels()).as("Saleschannels should be emtpy").isEmpty();
        assertThat(mm).as("Normalized Metadata with different payment method defaults")
                .returns(DEALER_2_PERCENT_DISCOUNT, MandatorMetadata::getPaymentCondition)
                .returns(null, MandatorMetadata::getPaymentMethod)
                .returns(null, MandatorMetadata::getShippingCondition);
    }
        @Test
    public void differentShippingCondition() {
        mm.setShippingCondition(ShippingCondition.SIX);
        assertThat(mm.isSameAs(dcs)).as("Metadata differs from defaults").isFalse();
        mm.normalize(dcs);
        assertThat(mm.getAllowedSalesChannels()).as("Saleschannels should be emtpy").isEmpty();
        assertThat(mm).as("Normalized Metadata with different payment method defaults")
                .returns(null, MandatorMetadata::getPaymentCondition)
                .returns(null, MandatorMetadata::getPaymentMethod)
                .returns(ShippingCondition.SIX, MandatorMetadata::getShippingCondition);
    }
}
