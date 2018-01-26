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
package eu.ggnet.dwoss.mandator.api.service;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms.ConditionValue;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.redtape.ee.sage.DefaultSageExporterConfig;
import eu.ggnet.dwoss.redtape.ee.sage.SageExporterConfig;
import eu.ggnet.dwoss.rules.ShippingCondition;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.MapBuilder;

import static eu.ggnet.dwoss.rules.DocumentType.CAPITAL_ASSET;
import static eu.ggnet.dwoss.rules.DocumentType.RETURNS;

/**
 *
 * @author Bastian Venz <bastian.venz at gg-net.de>
 */
@Singleton // Be sure we are only once in the system.
public class CustomerAndGsOfficeProvider {

    @Inject
    private CustomerGeneratorOperation gen;

    @Inject
    private Contractors contractors;

    private ReceiptCustomers receiptCustomers;

    private DeleteCustomers deleteCustomers;

    private ScrapCustomers scrapCustomers;

    private RepaymentCustomers repaymentCustomers;

    private SpecialSystemCustomers specialSystemCustomers;

    @Produces
    public final static SageExporterConfig SAGE_EXPORTER_CONFIG = new DefaultSageExporterConfig(1, false);

    @Produces
    public final static ShippingTerms st = new ShippingTerms(new MapBuilder<ShippingCondition, ConditionValue>()
            .put(ShippingCondition.DEFAULT, new ConditionValue("6€/Gerät min. 10€", 10, 6))
            .put(ShippingCondition.DEALER_ONE, new ConditionValue("5€/Gerät", 5, 5))
            .put(ShippingCondition.DEALER_TWO, new ConditionValue("6€/Gerät", 6, 6))
            .toHashMap());

    @Produces
    public final static PostLedger pl = new PostLedger();

    // Rewired the system customers.
    @Produces
    public synchronized ReceiptCustomers buildReceiptCustomers() {
        if ( receiptCustomers == null ) receiptCustomers = gen.makeReceiptCustomers(contractors.all().toArray(new TradeName[0]));
        return receiptCustomers;
    }

    @Produces
    public synchronized ScrapCustomers buildScrapCustomers() {
        if ( scrapCustomers == null ) scrapCustomers = gen.makeScrapCustomers(contractors.all().toArray(new TradeName[0]));
        return scrapCustomers;
    }

    @Produces
    public synchronized DeleteCustomers buildDeleteCustomers() {
        if ( deleteCustomers == null ) deleteCustomers = gen.makeDeleteCustomers(contractors.all().toArray(new TradeName[0]));
        return deleteCustomers;
    }

    @Produces
    public synchronized RepaymentCustomers buildRepaymentCustomers() {
        if ( repaymentCustomers == null ) repaymentCustomers = gen.makeRepaymentCustomers(contractors.all().toArray(new TradeName[0]));
        return repaymentCustomers;
    }

    @Produces
    public synchronized SpecialSystemCustomers buildSystemCustomers() {
        if ( specialSystemCustomers == null ) specialSystemCustomers = gen.makeSpecialCustomers(CAPITAL_ASSET, RETURNS);
        return specialSystemCustomers;
    }
}
