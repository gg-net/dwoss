package eu.ggnet.dwoss.mandator;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.mandator.api.value.*;

/**
 * Support for the Mandator, allows loading in the Remote UI.
 * <p/>
 * @author oliver.guenther
 */
@Local
@Remote
public interface MandatorSupporter {

    /**
     * Loads the Mandator.
     * <p/>
     * @return the Mandator.
     */
    Mandator loadMandator();

    /**
     * Loads the default Salesdata.
     * <p>
     * @return the default salesdata.
     */
    DefaultCustomerSalesdata loadSalesdata();

    /**
     * Loads the ReceiptCustomers.
     * <p>
     * @return the receiptCustomers.
     */
    ReceiptCustomers loadReceiptCustomers();

    /**
     * Return SystemCustomers which need special document handling.
     * <p>
     * @return SystemCustomers which need special document handling.
     */
    SpecialSystemCustomers loadSystemCustomers();

    /**
     * Returns the contractors
     * <p>
     * @return the contractors.
     */
    Contractors loadContractors();

    /**
     * Returns the {@link PostLedger}.
     * <p>
     * @return the {@link PostLedger}.
     */
    PostLedger loadPostLedger();
    
    /**
     * Returns the {@link ShippingTerms}.
     * <p>
     * @return the {@link ShippingTerms}.
     */
    ShippingTerms loadShippingTerms();
}
