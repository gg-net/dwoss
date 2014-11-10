package eu.ggnet.dwoss.rights.api;

import eu.ggnet.saft.api.Authorisation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Bastian Venz
 */
@AllArgsConstructor
@Getter
public enum AtomicRight implements Authorisation {

    /**
     * Allows the import of Image Ids.
     */
    IMPORT_IMAGE_IDS("BilderId's Importieren"),
    /**
     * Allows to Create new Weekly/Monthly Reports.
     */
    CREATE_SALES_REPORT("Neue Verkaufsmeldung erstellen"),
    /**
     * Allows to Create Return reports.
     */
    CREATE_RETUNRS_REPORT("Rückläufermeldung erstellen"),
    /**
     * Allows to read all stored reports.
     */
    READ_STORED_REPORTS("Vorhandene Verkaufsmeldung anzeigen"),
    /**
     * Allows to view the Daily Closing Data.
     */
    READ_RAW_REPORT_DATA("Tagesabschluss Daten anzeigen"),
    /**
     * Allows to Export the Products Specs
     */
    READ_PRODUCT_SPEC_FOR_XML_EXPORT("ProductSpecs zu XML exportieren"),
    /**
     * Allows to createthe old Sales Report.
     */
    CREATE_OLD_SALES_REPORT("Verkaufsmeldung erstellen, Legacy Version"),
    /**
     * Allows to Open the SalesChannelManger.
     */
    OPEN_SALES_CHANNEL_MANAGER("Öffne den Verkaufskanalmanager"),
    /**
     * Allows to set a single Price.
     */
    CREATE_ONE_PRICE("Einzelnen Preis generieren"),
    /**
     * Allows to Create a Price Blocker.
     */
    UPDATE_SET_UNIT_PRICE("Einzelnen Preis manuell festlegen"),
    /**
     * Allows to Import Prices as XLS.
     */
    IMPORT_PRICE_BY_XLS("Importieren der Preise als XLS Datei."),
    /**
     * Allow to Export the PriceManagment
     */
    EXPORT_PRICEMANAGMENT("Exportieren des PriceManagments"),
    /**
     * Allows to Import the PriceManagment.
     */
    IMPORT_PRICEMANGMENT("Importieren des PreisManagments"),
    /**
     * Allows to Export and Import the Pricemanagment.
     */
    EXPORT_AND_IMPORT_PRICEMANAGMENT("Erzeugen und Importieren des PriceManagments"),
    /**
     * Allows to Export the CPU Data for the Pricemangment.
     */
    EXPORT_OF_CPU_DATA_FOR_PRICEMANAGMENT("Export Cpu Daten für PreisManagement"),
    /**
     * Allows to import missing Cost or Reference Prices.
     */
    IMPORT_MISSING_CONTRACTOR_PRICES_DATA("Import fehlender Lieferanten Preise und Artikelnummern"),
    /**
     * Unused, can be reassigned.
     */
    @Deprecated
    UNUSED_2("Unused 2, kann neu verwendet werden"),
    /**
     * Unused, can be reassigned.
     */
    @Deprecated
    UNUSED_3("Unused 3, kann neu verwendet werden"),
    /**
     * Allows to prepare a Transfer for a single Unit.
     */
    CREATE_TRANSACTION_FOR_SINGLE_UNIT("Erstelle Umfuhr für ein einzelnes Gerät"),
    /**
     * Allows to Remove a Single Unit from a transaction.
     */
    REMOVE_SINGE_UNIT_FROM_TRANSACTION("Entferne einzelnes Gerät aus der Umfuhr"),
    /**
     * Allows to create a Annulation Invoice.
     */
    CREATE_ANNULATION_INVOICE("Erstellen einer Stornorechnung"),
    /**
     * Allows to create a Complaint.
     */
    CREATE_COMPLAINT("Erstellen einer Reklamation"),
    /**
     * Allows to update a Annulation invoice to abort it.
     */
    UPDATE_ANNULATION_INVOICE_TO_ABORT("Reklamation abbrechen"),
    /**
     * Allows to update a Annulation invoice to accept it.
     */
    UPDATE_ANNULATION_INVOICE_TO_ACCEPT("Reklamation akzeptieren"),
    /**
     * Allows to update a Annulation invoice to withdraw it.
     */
    UPDATE_ANNULATION_INVOICE_TO_WITHDRAW("Reklamation zurückgezogen"),
    /**
     * Allows to update a Annulation invoice to balanced it.
     */
    UPDATE_ANNULATION_INVOICE_TO_BALANCED("Reklamation ausgeglichen"),
    /**
     * Unused, can be reassigned.
     */
    @Deprecated
    UNUSED_1("Unused 1, Kann umbenannt und neu verwendet werden."),
    /**
     * Allows to Create a Creditmemo.
     */
    CREATE_CREDITMEMO("Erstellen einer Gutschrift"),
    /**
     * Allows to delete a Dossier.
     */
    DELETE_DOSSIER("Lösche Vorgang"),
    /**
     * Allows to read the Doccuments to export them to the GsOffice XML Format.
     */
    EXPORT_DOCUMENTS_FOR_GSOFFICE_IN_XML("Exportiere Dokumente zu GsOffice XML"),
    /**
     * Unused, can be reassigned.
     */
    @Deprecated
    UNUSED_4("Unused 4, Kann umbenannt und neu verwendet werden."),
    /**
     * Allows to create a DebitorsReport.
     */
    CREATE_DEBITOR_REPORT("Erstellen eines Debitorenreport"),
    /**
     * Allows to export dossiers to XLS.
     */
    EXPORT_DOSSIER_TO_XLS("Exportiere Vorgänge zu XLS"),
    /**
     * Allows the manual clossing.
     */
    EXECUTE_MANUAL_CLOSING("Tagesabschluss jetzt manuell durchführen"),
    /**
     * Allows to delete a UniqueUnit.
     */
    DELETE_UNIQUE_UNIT("Gerät Löschen"),
    /**
     * Allows to do anything with Shipment.
     */
    READ_CREATE_UPDATE_DELETE_SHIPMENTS("Shipment und Aufnahme"),
    /**
     * Allows to Roll in all Prepared Transactions.
     */
    CREATE_ROLL_IN_OF_PREPARED_TRANSACTIONS("Vorbereitete Transactionen einlagern"),
    /**
     * Allows to Update a Unique Unit to make it a Scrap Unique Unit.
     */
    UPDATE_UNIQUE_UNIT_TO_SCRAP_UNIT("Gerät verschrotten"),
    /**
     * Allow to Create or Update of a Product.
     */
    UPDATE_PRODUCT("Bearbeiten eines Artikels/Produktes"),
    /**
     * Unused, can be reassigned.
     */
    @Deprecated
    UNUSED_5("Unused 5, Kann umbenannt und neu verwendet werden."),
    /**
     * Allows to create a UniqueUnitHistory that is only a comment.
     */
    CREATE_COMMENT_UNIQUE_UNIT_HISTORY("Kommentar zu einer Unit hinzufügen"),
    /**
     * Allows to update a UniqueUnite.
     */
    UPDATE_UNIQUE_UNIT("Gerät bearbeiten"),
    /**
     * Allows to Create a Revenue Report on daily base.
     */
    CREATE_DAILY_BASED_REVENUE_REPORT("Erstellenen eines tagesbasierten Umsatz-/Gutschriftenreports"),
    /**
     * Allows to update a Transaction to cancel it.
     */
    UPDATE_TRANSACTION_TO_CANCLE("Transaktion abbrechen"),
    /**
     * Allows to Remove a Unit from a Transaction.
     */
    UPDATE_TRANSACTION_TO_REMOVE_UNIT("Gerät aus einer Transaktion entfernen"),
    /**
     * Allows to Edit a Customer to aadd/remove him as SystemCustomer.
     */
    UPDATE_CUSTOMER_TO_SYSTEM_CUSTOMER("Systemkunden Flag hinzufügen/entfernen"),
    /**
     * Allow the update of the PaymentMethod of the Customer.
     */
    UPDATE_CUSTOMER_PAYMENT_METHOD("Ändern der Zahlungsmodalität"),
    /**
     * Allow the update of the Payment Condition of the Customer.
     */
    UPDATE_CUSTOMER_PAYMENT_CONDITION("Ändern der Zahlungskondition"),
    /**
     * Allow the update of the Shipping Condition of the Customer.
     */
    UPDATE_CUSTOMER_SHIPPING_CONDITION("Ändern der Versandkondition"),
    /**
     * Allow to update of a Position with a Existing Document.
     */
    UPDATE_POSITION_WITH_EXISTING_DOCUMENT("Ändern von Positionen mit existierenden Dokument"),
    /**
     * Allow to update the Price of Units and ProductBatches.
     */
    UPDATE_PRICE_OF_UNITS_AND_PRODUCT_BATCH("Ändern des Preises an Geräte/Product Positionen"),
    /**
     * Allows to change the prices in invoices.
     */
    UPDATE_PRICE_INVOICES("Ändern von Preisen an Rechnungen"),
    /**
     * Creation of the Revenu Report.
     */
    EXPORT_REVENUE_REPORT("Umsatzreport exportiern"),
    /**
     * Allows to Create new Personas and modify Operators and Personas rights.
     */
    CREATE_UPDATE_RIGHTS("Erstellen oder Ändern von Rechten"),
    /**
     * Allows to view cost an reference prices.
     */
    VIEW_COST_AND_REFERENCE_PRICES("Darf Cost und Referencpreise sehen");

    private final String name;

    @Override
    public String toName() {
        return name;
    }

}
