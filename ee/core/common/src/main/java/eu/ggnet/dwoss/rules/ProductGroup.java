package eu.ggnet.dwoss.rules;

import eu.ggnet.dwoss.util.INoteModel;

/**
 * All constant ProductGroups.
 *
 * @author oliver.guenther
 */
public enum ProductGroup implements INoteModel {

    /**
     * Represents a Product, which has no physical representative, like a service or a free text.
     *//**
     * Represents a Product, which has no physical representative, like a service or a free text.
     */
    COMMENTARY("!!"),
    MISC("Sonstige"),
    DESKTOP("Desktop"),
    DESKTOP_BUNDLE("Desktop/Monitor Bundle"),
    ALL_IN_ONE("All in one PC"),
    NOTEBOOK("Notebook"),
    MONITOR("Monitor"),
    PROJECTOR("Projektor"),
    TV("TV"),
    SERVER("Server"),
    TABLET_SMARTPHONE("Tablet/SmartPhone"),
    PHONE("SimplePhone");

    private final String note;

    private ProductGroup(String note) {
        this.note = note;
    }

    @Override
    public String getNote() {
        return note;
    }

    public String getName() {
        return note;
    }
}
