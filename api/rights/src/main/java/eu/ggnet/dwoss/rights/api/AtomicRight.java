/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.rights.api;

/**
 * Enum Implementation of Authorisations.
 *
 * @author oliver.guenther
 */
public enum AtomicRight {

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
     * Allows to create the old Sales Report.
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
    EXPORT_INPUT_REPORT("Report nach Aufnahmedatum exportieren"),
    /**
     * Unused, can be reassigned.
     */
    EXPORT_ALL_CUSTOMERS("Alle Kundendaten exportieren"),
    /**
     * Allows to prepare a Transfer for a single Unit.
     */
    CREATE_TRANSACTION_FOR_SINGLE_UNIT("Erstelle Umfuhr für (ein) einzelne(s) Gerät(e)"),
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
     * Change taxes in positions.
     */
    CHANGE_TAX("Steuer ändern"),
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
    EXPORT_DOCUMENTS_FOR_SAGE_IN_XML("Exportiere Dokumente zu Sage(GsOffice) XML"),
    /**
     * On/Off and modificaton of the auto logout timeout.
     */
    MODIFY_LOGGED_IN_TIMEOUT("Ein-/Ausschalten des automatischen Abmeldens von Benutzern"),
    /**
     * Allows to create a DebitorsReport.
     */
    CREATE_DEBITOR_REPORT("Debitorenreport Rechnung"),
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
    DELETE_UNIT("Gerät(e) löschen"),
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
    SCRAP_UNIT("Gerät(e) verschrotten"),
    /**
     * Allow to Create or Update of a Product.
     */
    UPDATE_PRODUCT("Bearbeiten eines Artikels/Produktes"),
    /**
     * Unused, can be reassigned.
     */
    RESOLVE_REPAYMENT("Gutschriften/Stornorechnung auflösen"),
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
    VIEW_COST_AND_REFERENCE_PRICES("Darf Cost und Referenzpreise sehen"),
    /**
     * Allows to export the product unit report.
     */
    EXPORT_PRODUCT_UNIT_HISTORY_REPORT("Artikel Geräte History exportieren");

    private final String description;

    private AtomicRight(String description) {
        this.description = description;
    }

    /**
     * Returns description
     *
     * @return description
     * @deprecated use {@link AtomicRight#description() }.
     */
    @Deprecated
    public String toName() {
        return description;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString() {
        return "AtomicRight{" + "description=" + description + '}';
    }

}
