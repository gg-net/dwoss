package eu.ggnet.dwoss.mandator.api.service;

import eu.ggnet.dwoss.mandator.api.value.ReceiptCustomers;
import eu.ggnet.dwoss.mandator.api.value.PostLedger;
import eu.ggnet.dwoss.mandator.api.value.ShippingTerms;
import eu.ggnet.dwoss.mandator.api.value.DeleteCustomers;
import eu.ggnet.dwoss.mandator.api.value.Contractors;
import eu.ggnet.dwoss.mandator.api.value.SpecialSystemCustomers;
import eu.ggnet.dwoss.mandator.api.value.ScrapCustomers;
import eu.ggnet.dwoss.mandator.api.value.RepaymentCustomers;

import java.util.HashMap;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import eu.ggnet.dwoss.mandator.api.value.ShippingTerms.ConditionValue;
import eu.ggnet.dwoss.rules.ShippingCondition;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.util.MapBuilder;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;

import static eu.ggnet.dwoss.rules.DocumentType.CAPITAL_ASSET;
import static eu.ggnet.dwoss.rules.DocumentType.RETURNS;

/**
 *
 * @author Bastian Venz <bastian.venz at gg-net.de>
 */
@Singleton // Be sure we are only once in the system.
public class CustomerProvider {

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
    public final static ShippingTerms st = new ShippingTerms(new MapBuilder<ShippingCondition, ConditionValue>()
            .put(ShippingCondition.DEFAULT, new ConditionValue("6€/Gerät min. 10€", 10, 6))
            .put(ShippingCondition.DEALER_ONE, new ConditionValue("5€/Gerät", 5, 5))
            .put(ShippingCondition.DEALER_TWO, new ConditionValue("6€/Gerät", 6, 6))
            .toHashMap());

    @Produces
    public final static PostLedger pl = new PostLedger(new HashMap<>());

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
