package eu.ggnet.dwoss.rules;

/**
 *
 * @author pascal.perau
 */
public enum CustomerFlag {

    /**
     * System Customer - Customer is special and has only Documents of Type Block.
     */
    SYSTEM_CUSTOMER("Systemkunde"),
    /**
     * Customer has confirmed the terms and conditions for cash on delivery.
     */
    CONFIRMED_CASH_ON_DELIVERY("Nachnahme bestätigt"), /**
     * Customer wants to confirm the pickup or shipping of a Dossier.
     */
    CONFIRMS_DOSSIER("Kunde bestätigt Aufträge");

    private String name;

    private CustomerFlag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
