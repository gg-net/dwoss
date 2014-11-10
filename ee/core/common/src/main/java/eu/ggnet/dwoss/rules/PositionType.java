package eu.ggnet.dwoss.rules;


import lombok.Getter;

/**
 * The different Types of position.
 * <p>
 * @author oliver.guenther
 */
@Getter
public enum PositionType {

    /**
     * A Position representing a full Unit.
     */
    UNIT("Gerät"),
    /**
     * Unit Annex.
     * Is used in a CreditMemo for partial cash backs.
     */
    UNIT_ANNEX("Zusatzinformation zum Gerät"),
    /**
     * A Service Position.
     */
    SERVICE("Dienstleistungen"),
    /**
     * Multiple Units, one PartNo, same Price.
     */
    PRODUCT_BATCH("Mehrere Artikel mit Preis"),
    /**
     * A Comment.
     */
    COMMENT("Komentar"),
    /**
     * The Shipping Costs.
     */
    SHIPPING_COST("Versandkosten");

    private final String name;

    private PositionType(String name) {
        this.name = name;
    }

}
