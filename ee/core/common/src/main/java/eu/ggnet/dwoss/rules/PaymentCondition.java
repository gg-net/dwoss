package eu.ggnet.dwoss.rules;

import lombok.*;

/**
 * Payment Conditions for Customers.
 *
 * @author bastian.venz
 */
@Getter
@RequiredArgsConstructor
@ToString
public enum PaymentCondition {

    /**
     * Customer, can only buy from SalesChannel Customer.
     */
    CUSTOMER("Endkunde"),
    /**
     * Dealer, can buy from SalesChannel Dealer and Customer.
     */
    DEALER("Händler"),
    /**
     * Employee, can buy from SalesChannel Dealer and Customer.
     */
    EMPLOYEE("Angesteller Mitarbeiter"),
    /**
     * External Employee, can buy from SalesChannel Dealer and Customer.
     */
    EXTERNAL_EMPLOYEE("Externer Mitarbeiter"),
    /**
     * Dealer, can buy from SalesChannel Dealer and Customer.
     */
    DEALER_1_PERCENT_DISCOUNT("Händler 1% Rabat"),
    /**
     * Dealer, can buy from SalesChannel Dealer and Customer.
     */
    DEALER_2_PERCENT_DISCOUNT("Händler 2% Rabat"),
    /**
     * Dealer, can buy from SalesChannel Dealer and Customer.
     */
    DEALER_3_PERCENT_DISCOUNT("Händler 3% Rabat");

    private final String note;

}
