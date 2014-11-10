package eu.ggnet.dwoss.uniqueunit.entity;

/**
 * The different types of prices for {@link UniqueUnit}s and {@link Product}s.
 * <p>
 * @author oliver.guenther
 */
public enum PriceType {

    /**
     * The Price something is sold.
     */
    SALE,
    /**
     * A Price, something is bought.
     */
    PURCHASE,
    /**
     * The Costprice of the Manufacturer.
     */
    MANUFACTURER_COST,
    /**
     * A Reference Price for the Contractor.
     */
    CONTRACTOR_REFERENCE,
    /**
     * The price used in the retailer chanel.
     */
    RETAILER,
    /**
     * The price used in the customer chanel.
     */
    CUSTOMER

}
