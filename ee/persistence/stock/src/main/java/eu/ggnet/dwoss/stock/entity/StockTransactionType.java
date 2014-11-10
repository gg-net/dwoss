package eu.ggnet.dwoss.stock.entity;

/**
 * The different types of transactions.
 * <p>
 * Vision: Add types and participations for the following cases:
 * <ul>
 * <li>units are added to the stocks</li>
 * <li>units leave all stocks, are sold,scraped or else</li>
 * <li>units are sent to internal repair/rework</li>
 * </ul>
 */
// TODO: Invent a Validation concept, that may allow something like validation.preConditionTransmutation(TargetStatusType,TransactionToTest)
public enum StockTransactionType {

    /**
     * A transaction, which transfers some thing between two different stocks.<br />
     * Allowed status changes.
     * <ol>
     * <li>{@link StockTransactionStatusType#PREPARED}
     * <p>
     * ParticipationTypes<br />
     * - Needed: none<br />
     * - Optional: {@link StockTransactionParticipationType#ARRANGER}
     * <b>A transaction in this state can be {@link StockTransactionStatusType#CANCELLED}, with a needed {@link StockTransactionParticipationType#ARRANGER}</b>
     * <li>{@link StockTransactionStatusType#COMMISSIONED}
     * <p>
     * ParticipationTypes<br />
     * Needed: {@link StockTransactionParticipationType#PICKER}, {@link StockTransactionParticipationType#DELIVERER} <br />
     * Optional: none
     * </p>
     * </li>
     * <li>{@link StockTransactionStatusType#IN_TRANSFER}
     * <p>
     * ParticipationTypes<br />
     * Needed: {@link StockTransactionParticipationType#DELIVERER} <br />
     * Optional: none
     * </p>
     * </li>
     * <li>{@link StockTransactionStatusType#RECEIVED}
     * <p>
     * ParticipationTypes<br />
     * Needed: {@link StockTransactionParticipationType#RECEIVER} , {@link StockTransactionParticipationType#DELIVERER} <br />
     * Optional: none
     * </p>
     * </li>
     * </ol>
     * This transaction can fail {@link StockTransactionStatusType#FAILED}.
     */
    TRANSFER,
    /**
     * The only transaction at the moment, which can create StockUnits.
     * This Transaction can only have one status.
     * <ol>
     * <li>{@link StockTransactionStatusType#COMPLETED}
     * <p>
     * ParticipationTypes<br />
     * Optional: {@link StockTransactionParticipationType#ARRANGER}
     * </p>
     * </li>
     * </ol>
     * This transaction can not fail.
     */
    ROLL_IN,
    /**
     * Allows the deletion of stockUnits.
     * This Transaction can only have one status.
     * <ol>
     * <li>{@link StockTransactionStatusType#COMPLETED}
     * <p>
     * ParticipationTypes<br />
     * Optional: {@link StockTransactionParticipationType#ARRANGER}
     * </p>
     * </li>
     * </ol>
     * This transaction can not fail.
     */
    ROLL_OUT,
    /**
     * Allows the deletion of stockUnits.
     * This Transaction can only have one status.
     * <ol>
     * <li>{@link StockTransactionStatusType#COMPLETED}
     * <p>
     * ParticipationTypes<br />
     * Optional: {@link StockTransactionParticipationType#ARRANGER}
     * </p>
     * </li>
     * </ol>
     * This transaction can not fail.
     */
    DESTROY,
    /**
     * Allows the transfer between stocks in only one step.
     * This is used if a Unit returns through some external operation on a different stock, than the one it left.
     * This Transaction can only have one status.
     * <ol>
     * <li>{@link StockTransactionStatusType#COMPLETED}
     * <p>
     * ParticipationTypes<br />
     * Optional: {@link StockTransactionParticipationType#ARRANGER}
     * </p>
     * </li>
     * </ol>
     * This transaction can not fail.
     */
    EXTERNAL_TRANSFER
}
