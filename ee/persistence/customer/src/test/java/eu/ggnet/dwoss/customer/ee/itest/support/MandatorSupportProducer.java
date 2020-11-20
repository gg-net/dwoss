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
package eu.ggnet.dwoss.customer.ee.itest.support;

import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.api.value.DeleteCustomers;
import eu.ggnet.dwoss.mandator.api.value.ScrapCustomers;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;

import java.util.HashMap;

import javax.annotation.ManagedBean;
import javax.enterprise.inject.Produces;


/**
 * Default datasource definition and empty mandator support informations for tests
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
    public static PostLedger pl = new PostLedger();

    @Produces
    public static RepaymentCustomers rc = new RepaymentCustomers(new HashMap<>());

    @Produces
    public static ScrapCustomers scrap = new ScrapCustomers(new HashMap<>());

    @Produces
    public static DeleteCustomers dc = new DeleteCustomers(new HashMap<>());

}
