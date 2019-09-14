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
import java.util.Set;
import java.util.stream.Collectors;

import org.inferred.freebuilder.FreeBuilder;

import eu.ggnet.dwoss.common.api.values.*;

/**
 * Mandator Specific Metadata.
 * <p>
 * @author oliver.guenther
 */
// Wildfly 15 cannot deserialize final collections remotly. This seeams not the case with freebuilder. Remove me later.
@FreeBuilder
public abstract class DefaultCustomerSalesdata implements Serializable {

    public static class Builder extends DefaultCustomerSalesdata_Builder {
    };

    /**
     * The default {@link ShippingCondition} of the Mandator.
     *
     * @return
     */
    public abstract ShippingCondition shippingCondition();

    /**
     * The default {@link PaymentCondition} of the Mandator.
     *
     * @return
     */
    public abstract PaymentCondition paymentCondition();

    /**
     * The default {@link PaymentMethod} of the Mandator.
     *
     * @return
     */
    public abstract PaymentMethod paymentMethod();

    public abstract Set<SalesChannel> allowedSalesChannels();

    public abstract Set<Long> viewOnlyCustomerIds();

    public String toHtml() {
        return "Mandantenstandard"
                + "<ul>"
                + "<li>Versandkonditionen:" + shippingCondition() + "</li>"
                + "<li>Zahlungskonditionen:" + paymentCondition().description + "</li>"
                + "<li>Zahlungsmodalität:" + paymentMethod().description + "</li>"
                + (allowedSalesChannels().isEmpty() ? "" : "<li>Erlaubte Verkaufskanäle:" + allowedSalesChannels().stream().map(SalesChannel::getName).collect(Collectors.toList()) + "</li>")
                + "</ul>";
    }

}
