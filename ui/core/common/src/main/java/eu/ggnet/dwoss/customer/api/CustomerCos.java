package eu.ggnet.dwoss.customer.api;

/**
 * This interface shall provide the possibility to create or modify Customers.
 * <p/>
 * @author pascal.perau
 */
public interface CustomerCos {

    /**
     * Method for Customer creation.
     * <p/>
     * @return id of a new, persisted customer or zero if canceled
     */
    long createCustomer();

    /**
     * Method for Customer modification.
     * <p/>
     * @param customerId the SopoCustomer to be modified
     * @return weither the customer has changed
     */
    boolean updateCustomer(long customerId);
}
