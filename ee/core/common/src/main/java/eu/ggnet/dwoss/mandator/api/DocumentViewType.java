package eu.ggnet.dwoss.mandator.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Document View Types.
 * These types can be used, to supply different templates or texts in the resulting documents.
 * Hint: Only on this component to have type safty in the Mandator.
 *
 * @author oliver.guenther
 */
@RequiredArgsConstructor
@Getter
public enum DocumentViewType {

    /**
     * The default viewtype. (Alternative to null)
     */
    DEFAULT("Default", "Default", "Document_Template.jrxml"),
    /**
     * Represents a reservation.
     */
    RESERVATION("Reservierung", "Angebot/Reservierung für 48 Stunden", "Document_Template.jrxml"),
    /**
     * Represents the shipping document.
     */
    SHIPPING("Lieferschein", "Lieferschein", "Shipping_Template.jrxml");

    private final String name;

    private final String documentTitle;
    
    private final String fileName;
}
