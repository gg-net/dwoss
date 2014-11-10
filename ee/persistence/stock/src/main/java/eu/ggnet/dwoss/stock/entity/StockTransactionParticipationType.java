package eu.ggnet.dwoss.stock.entity;

/**
 * Selects the type of the participant in a transaction. See {@link StockTransactionType} which participant is needed
 * and optionally allowed for each status.
 */
public enum StockTransactionParticipationType {

    /**
     * If a {@link StockTransactionStatusType} allowes only one person (e.g. {@link StockTransactionStatusType#PREPARED}
     * than this participant can allways be used.
     */
    ARRANGER,
    /**
     * The person that commisions the units for delivery
     */
    PICKER,
    /**
     * The one that is actually delivering the goods
     */
    DELIVERER,
    /**
     * The other person that takes the units in the destination stock
     */
    RECEIVER
}
