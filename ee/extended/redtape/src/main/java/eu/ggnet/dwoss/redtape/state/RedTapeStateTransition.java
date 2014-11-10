package eu.ggnet.dwoss.redtape.state;

import java.util.EnumSet;
import java.util.Set;

import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.statemachine.StateTransition;

import lombok.Getter;

/**
 *
 * @author oliver.guenther
 */
@Getter
public abstract class RedTapeStateTransition extends StateTransition<CustomerDocument> {

    /**
     * Hints for the Ui, to add extra dialogs.
     */
    public enum Hint {

        ADD_SHIPPING_COSTS, REMOVE_SHIPPING_COSTS, 
        /**
         * Transition creates a Invoice.
         */
        CREATES_INVOICE, 
        /**
         * Transition that is setted that the document informed.
         */
        SENDED_INFORMATION,
        /**
         * Transition creates a Credit Memo.
         */
        CREATES_CREDIT_MEMO, 
        /**
         * Transition creates a Complaint (Reklamation).
         */
        CREATES_COMPLAINT,
        /**
         * Transition creates a Annulation Invoice (Stornorechnung).
         */
        CREATES_ANNULATION_INVOICE,
        CHANGES_PAYMENT_METHOD_TO_INVOICE, CHANGES_PAYMENT_METHOD_TO_DIRECT_DEBIT,
        CHANGES_PAYMENT_METHOD_TO_ADVENCED_PAYMENT, CHANGES_PAYMENT_METHOD_TO_CASH_ON_DELIVERY,
        ADDS_SETTLEMENT, UNIT_LEAVES_STOCK
    }

    private AtomicRight enablingRight;

    private Set<Hint> hints;

    public RedTapeStateTransition(String name) {
        this(name, null, null);
    }

    public RedTapeStateTransition(String name, String description, String toolTip) {
        this(name, description, toolTip, EnumSet.noneOf(Hint.class));
    }

    public RedTapeStateTransition(String name, String description, String toolTip, Set<Hint> hints) {
        this(name, description, toolTip, hints, null);
    }    

    public RedTapeStateTransition(String name, String description, String toolTip, Set<Hint> hints, AtomicRight enablingRight) {
        super(name, description, toolTip);
        this.enablingRight = enablingRight;
        this.hints = hints;
    }
}
