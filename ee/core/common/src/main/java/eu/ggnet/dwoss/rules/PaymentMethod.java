package eu.ggnet.dwoss.rules;

/**
 * The PaymentMethod.
 *
 * @author pascal.perau
 */
public enum PaymentMethod {
    /**
     * Advance Payment - Vorkasse.
     */
    ADVANCE_PAYMENT("Vorkasse", 0,"Zahlungsbedingungen: Vorauskasse oder EC/Barzahlung bei Abholung.","Rechnungsbetrag bereits dankend erhalten."),
    /**
     * Direct Debit - Lastschrift.
     */
    DIRECT_DEBIT("Lastschrift", 3,"Zahlungsbedingungen: Lastschrift.","Rechnungsbetrag fällig, wird per Lastschrift eingezogen."),
    /**
     * Invoice - Rechnung.
     */
    INVOICE("Rechnung", 2,"Zahlungsbedingungen: Rechnung","Rechnungsbetrag fällig, zahlbar %s nach Erhalt der Ware"),
    /**
     * Cash on Delivery - Nachname.
     */
    CASH_ON_DELIVERY("Nachnahme", 1,"Zahlungsbedingungen: Nachnahme","Rechnungsbetrag fällig, wird bei Erhalt der Ware via Nachnahme eingezogen.");


    private final String note;

    private final int sopoPaymentMethodId;

    private final String orderText;

    private final String invoiceText;

    private PaymentMethod(String note, int sopoPaymentMethodId, String orderText, String invoiceText) {
        this.note = note;
        this.sopoPaymentMethodId = sopoPaymentMethodId;
        this.orderText = orderText;
        this.invoiceText = invoiceText;
    }

    public String getNote() {
        return note;
    }

    public int getSopoPaymentMethodId() {
        return sopoPaymentMethodId;
    }

    public String getOrderText() {
        return orderText;
    }

    /**
     * Return the InvoiceText with an addition of days.
     *
     * @param days the days a invoice may be paid
     * @return the text.
     */
    public String getInvoiceText(int days) {
        if (!invoiceText.contains("%s")) return invoiceText;
        String daysText = "sofort";
        if (days == 1) daysText = "1 Tag";
        if (days > 1) daysText = days + " Tage";
        return String.format(invoiceText, daysText);
    }

}
