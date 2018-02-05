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
package eu.ggnet.dwoss.customer.test;

import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.rules.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author jens.papenhagen
 */
public class MandatorMetadataTest {

    private MandatorMetadata mandatorMetadata;

    @Before
    public void executeBeforeEach() {
        mandatorMetadata = new MandatorMetadata();
        mandatorMetadata.setShippingCondition(ShippingCondition.DEALER_ONE);
        mandatorMetadata.setPaymentCondition(PaymentCondition.CUSTOMER);
        mandatorMetadata.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        mandatorMetadata.add(SalesChannel.UNKNOWN);
    }

    @Test
    public void testGetViolationMessages() {
        assertThat(mandatorMetadata.getViolationMessages()).as("MandatorMetadata with valid values").isNull();
    }

    @Test
    public void testGetViolationMessagesNonValid() {
        mandatorMetadata.setShippingCondition(null);
        assertThat(mandatorMetadata.getViolationMessages()).as("MandatorMetadata without ShippingCondition").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValid2() {
        mandatorMetadata.setPaymentCondition(null);
        assertThat(mandatorMetadata.getViolationMessages()).as("MandatorMetadata without PaymentCondition").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValid3() {
        mandatorMetadata.setPaymentMethod(null);
        assertThat(mandatorMetadata.getViolationMessages()).as("MandatorMetadata without Payment Method").isNotBlank();
    }

    @Test
    public void testGetViolationMessagesNonValid4() {
        mandatorMetadata.clearSalesChannels();
        assertThat(mandatorMetadata.getViolationMessages()).as("MandatorMetadata without Allowed Sales Channels").isNotBlank();
    }

}
