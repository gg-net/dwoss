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

import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jens.papenhagen
 */
public class MandatorMetadataTest {

  

    public static MandatorMetadata makeValidMandatorMetadata() {
        MandatorMetadata validMandatorMetadata = new MandatorMetadata();
        validMandatorMetadata.setShippingCondition(ShippingCondition.DEALER_ONE);
        validMandatorMetadata.setPaymentCondition(PaymentCondition.CUSTOMER);
        validMandatorMetadata.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        validMandatorMetadata.add(SalesChannel.UNKNOWN);

        assertThat(validMandatorMetadata.getViolationMessage()).as("valid Communication").isNull();
        return validMandatorMetadata;
    }

   

    @Test
    public void testGetViolationMessages() {
        MandatorMetadata makeValidMandatorMetadata = makeValidMandatorMetadata();
        assertThat(makeValidMandatorMetadata.getViolationMessage()).as("MandatorMetadata with valid values").isNull();
    }

    @Test
    public void testGetViolationMessagesNonValidShippingCondition() {
        MandatorMetadata makeInValidMandatorMetadata = makeValidMandatorMetadata();
        makeInValidMandatorMetadata.setShippingCondition(null);
        assertThat(makeInValidMandatorMetadata.getViolationMessage()).as("MandatorMetadata without ShippingCondition").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValidPaymentCondition() {
        MandatorMetadata makeInValidMandatorMetadata = makeValidMandatorMetadata();
        makeInValidMandatorMetadata.setPaymentCondition(null);
        assertThat(makeInValidMandatorMetadata.getViolationMessage()).as("MandatorMetadata without PaymentCondition").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValidPaymentMethod() {
        MandatorMetadata makeInValidMandatorMetadata = makeValidMandatorMetadata();
        makeInValidMandatorMetadata.setPaymentMethod(null);
        assertThat(makeInValidMandatorMetadata.getViolationMessage()).as("MandatorMetadata without Payment Method").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonSalesChannels() {
        MandatorMetadata makeInValidMandatorMetadata = makeValidMandatorMetadata();
        makeInValidMandatorMetadata.clearSalesChannels();
        assertThat(makeInValidMandatorMetadata.getViolationMessage()).as("MandatorMetadata without Allowed Sales Channels").isNotBlank();
    }

}