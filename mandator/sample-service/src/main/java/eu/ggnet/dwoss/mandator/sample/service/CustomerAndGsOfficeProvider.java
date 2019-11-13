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
package eu.ggnet.dwoss.mandator.sample.service;

import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.api.value.ScrapCustomers;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.DeleteCustomers;
import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.redtape.ee.sage.DefaultSageExporterConfig;
import eu.ggnet.dwoss.redtape.ee.sage.SageExporterConfig;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;

import static eu.ggnet.dwoss.core.common.values.DocumentType.CAPITAL_ASSET;
import static eu.ggnet.dwoss.core.common.values.DocumentType.RETURNS;
import static eu.ggnet.dwoss.core.common.values.PositionType.*;
import static eu.ggnet.dwoss.core.common.values.TaxType.*;

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
    public final static PostLedger NEW_POST_LEDGER = new PostLedger(
            PostLedger.add()
                    .positionTypes(UNIT)
                    .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                    .primaryLedger(1000, "Standard Gerät")
                    .alternativeLedger(1001, "Nicht Standard Geräte"),
            PostLedger.add()
                    .positionTypes(UNIT, SERVICE, PRODUCT_BATCH, SHIPPING_COST, UNIT_ANNEX)
                    .taxTypes(UNTAXED)
                    .primaryLedger(2000, "Geräte ohne Ust."),
            PostLedger.add()
                    .positionTypes(UNIT, SERVICE, PRODUCT_BATCH, SHIPPING_COST, UNIT_ANNEX)
                    .taxTypes(REVERSE_CHARGE)
                    .primaryLedger(3000, "Reverse Charge"),
            PostLedger.add()
                    .positionTypes(SERVICE)
                    .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                    .primaryLedger(4000, "Service")
                    .alternativeLedger(4001, "VIP Service"),
            PostLedger.add()
                    .positionTypes(PRODUCT_BATCH)
                    .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                    .primaryLedger(5000, "ProductBatch"),
            PostLedger.add()
                    .positionTypes(SHIPPING_COST)
                    .taxTypes(GENERAL_SALES_TAX_DE_SINCE_2007)
                    .primaryLedger(6000, "Versand"));

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
