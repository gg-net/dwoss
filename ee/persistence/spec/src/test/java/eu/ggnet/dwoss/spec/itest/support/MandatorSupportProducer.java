/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.spec.itest.support;

import java.util.HashMap;

import javax.annotation.ManagedBean;
import javax.enterprise.inject.Produces;

import eu.ggnet.dwoss.mandator.api.value.*;

/**
 *
 * @author oliver.guenther
 */
@ManagedBean
public class MandatorSupportProducer {

    @Produces
    public static ReceiptCustomers c = new ReceiptCustomers(new HashMap<>());

    @Produces
    public static SpecialSystemCustomers sc = new SpecialSystemCustomers(new HashMap<>());

    @Produces
    public static ShippingTerms st = new ShippingTerms(new HashMap<>());

    @Produces
    public static PostLedger pl = new PostLedger(new HashMap<>());
}