package eu.ggnet.dwoss.mandator;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.mandator.api.value.*;

/**
 * Support for the Mandator.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class MandatorSupporterBean implements MandatorSupporter {

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesdata;

    @Inject
    private ReceiptCustomers receiptCustomers;

    @Inject
    private SpecialSystemCustomers specialSystemCustomers;

    @Inject
    private Contractors contractors;

    @Inject
    private PostLedger postLedger;

    @Inject
    private ShippingTerms shippingTerms;

    @Override
    public Mandator loadMandator() {
        return mandator;
    }

    @Override
    public DefaultCustomerSalesdata loadSalesdata() {
        return salesdata;
    }

    @Override
    public ReceiptCustomers loadReceiptCustomers() {
        return receiptCustomers;
    }

    @Override
    public SpecialSystemCustomers loadSystemCustomers() {
        return specialSystemCustomers;
    }

    @Override
    public Contractors loadContractors() {
        return contractors;
    }

    @Override
    public PostLedger loadPostLedger() {
        return postLedger;
    }

    @Override
    public ShippingTerms loadShippingTerms() {
        return shippingTerms;
    }

}
