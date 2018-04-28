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
package eu.ggnet.dwoss.customer.ee.priv;

import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.*;

import javax.validation.constraints.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.ee.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;

import lombok.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@ToString
public class OldCustomer implements Serializable {

    private static final String NO_WHITESPACE_AT_START_OR_END = "^[^\\s].*[^\\s]$";

    private final static String NL = System.lineSeparator();

    public static final String PROP_FIRMA = "firma";

    public static final String PROP_EMAIL = "email";

    public static final String PROP_VORNAME = "vorname";

    public static final String PROP_NACHNAME = "nachname";

    private transient PropertyChangeSupport propertyChangeSupport = null;

    private int kundenID;

    private String anmerkung;

    private String email;

    private String faxnummer;

    private String firma;

    private boolean haendler;

    private String handynummer;

    private String LIAdresse;

    private String LIOrt;

    private String LIPlz;

    @NotNull
    private String nachname;

    @Pattern(regexp = NO_WHITESPACE_AT_START_OR_END, message = "Muss mit Buchstabe/Zahl beginnen und enden")
    private String REAdresse;

    @Pattern(regexp = NO_WHITESPACE_AT_START_OR_END, message = "Muss mit Buchstabe/Zahl beginnen und enden")
    private String REOrt;

    @Pattern(regexp = "^\\d+$", message = "PLZ aus Ziffern bestehen")
    private String REPlz;

    private String telefonnummer;

    private String titel;

    @Pattern(regexp = NO_WHITESPACE_AT_START_OR_END, message = "Muss mit Buchstabe/Zahl beginnen und enden")
    private String vorname;

    private Set<CustomerFlag> flags = new HashSet<>();

    private PaymentMethod paymentMethod;

    private ShippingCondition shippingCondition;

    private PaymentCondition paymentCondition;

    private Set<SalesChannel> allowedSalesChannels;

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int ledger;

    @Getter
    @Setter
    @Size(max = 14)
    private String taxId;

    @NotNull
    @Size(min = 2, max = 2)
    private String shipIsoCountry = "DE";

    @NotNull
    @Size(min = 2, max = 2)
    private String payIsoCountry = "DE";

    @Getter
    @Setter
    private Source source;

    @Getter
    private final Map<ExternalSystem, String> additionalCustomerIds = new EnumMap<>(ExternalSystem.class);

    @Getter
    @Setter
    private String keyAccounter;

    /**
     * Default values of Customers.
     * - allowedSalesChannels = new HashSet<>();
     * - PaymentMethod.ADVANCE_PAYMENT;
     * - PaymentCondition.CUSTOMER;
     * - ShippingCondition.STANDARD;
     */
    public OldCustomer() {
        this.allowedSalesChannels = new HashSet<>();
        this.paymentMethod = PaymentMethod.ADVANCE_PAYMENT;
        this.paymentCondition = PaymentCondition.CUSTOMER;
        this.shippingCondition = ShippingCondition.DEFAULT;
    }

    public OldCustomer(OldCustomer oldCustomer) {
        this.kundenID = oldCustomer.kundenID;
        this.anmerkung = oldCustomer.anmerkung;
        this.email = oldCustomer.email;
        this.faxnummer = oldCustomer.faxnummer;
        this.firma = oldCustomer.firma;
        this.haendler = oldCustomer.haendler;
        this.handynummer = oldCustomer.handynummer;
        this.LIAdresse = oldCustomer.LIAdresse;
        this.LIOrt = oldCustomer.LIOrt;
        this.LIPlz = oldCustomer.LIPlz;
        this.nachname = oldCustomer.nachname;
        this.REAdresse = oldCustomer.REAdresse;
        this.REOrt = oldCustomer.REOrt;
        this.REPlz = oldCustomer.REPlz;
        this.telefonnummer = oldCustomer.telefonnummer;
        this.titel = oldCustomer.titel;
        this.vorname = oldCustomer.vorname;
        this.paymentMethod = oldCustomer.paymentMethod;
        this.shippingCondition = oldCustomer.shippingCondition;
        this.paymentCondition = oldCustomer.paymentCondition;
        this.allowedSalesChannels = oldCustomer.allowedSalesChannels;
        this.keyAccounter = oldCustomer.keyAccounter;
        this.additionalCustomerIds.putAll(oldCustomer.additionalCustomerIds);
        this.source = oldCustomer.source;
    }

    public OldCustomer(String firma, String vorname, String nachname, String anmerkung) {
        this();
        this.anmerkung = anmerkung;
        this.firma = firma;
        this.nachname = nachname;
        this.vorname = vorname;
    }

    public OldCustomer(String firma, String titel, String vorname, String nachname, String anmerkung, String REAdresse, String REPlz, String REOrt) {
        this();
        this.anmerkung = anmerkung;
        this.firma = firma;
        this.nachname = nachname;
        this.REAdresse = REAdresse;
        this.REOrt = REOrt;
        this.REPlz = REPlz;
        this.titel = titel;
        this.vorname = vorname;
    }

    public OldCustomer(String firma, String titel, String vorname, String nachname, String anmerkung, String REAdresse, String REPlz, String REOrt, String LIAdresse, String LIPlz, String LIOrt) {
        this();
        this.anmerkung = anmerkung;
        this.firma = firma;
        this.LIAdresse = LIAdresse;
        this.LIOrt = LIOrt;
        this.LIPlz = LIPlz;
        this.nachname = nachname;
        this.REAdresse = REAdresse;
        this.REOrt = REOrt;
        this.REPlz = REPlz;
        this.titel = titel;
        this.vorname = vorname;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if ( propertyChangeSupport == null ) propertyChangeSupport = new PropertyChangeSupport(this);
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if ( propertyChangeSupport != null ) propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public int getKundenID() {
        return this.kundenID;
    }

    public Locale getShipCountry() {
        return new Locale("de", shipIsoCountry);
    }

    public void setShipCountry(Locale country) {
        if ( country == null ) throw new NullPointerException("Null not allowed");
        else shipIsoCountry = country.getCountry();
    }

    public Locale getPayCountry() {
        return new Locale("de", payIsoCountry);
    }

    public void setPayCountry(Locale country) {
        if ( country == null ) throw new NullPointerException("Null not allowed");
        else payIsoCountry = country.getCountry();
    }

    public void setId(int id) {
        this.kundenID = id;
    }

    public int getId() {
        return kundenID;
    }

    public void setKundenID(int kundenID) {
        this.kundenID = kundenID;
    }

    public String getAnmerkung() {
        return this.anmerkung;
    }

    public void setAnmerkung(String anmerkung) {
        this.anmerkung = anmerkung;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String eMail) {
        String oldEmail = this.email;
        this.email = eMail;
        if ( propertyChangeSupport != null ) propertyChangeSupport.firePropertyChange(PROP_EMAIL, oldEmail, eMail);
    }

    public String getFaxnummer() {
        return this.faxnummer;
    }

    public void setFaxnummer(String faxnummer) {
        this.faxnummer = faxnummer;
    }

    public String getFirma() {
        return this.firma;
    }

    public void setFirma(String firma) {
        String oldFirma = this.firma;
        this.firma = firma;
        if ( propertyChangeSupport != null ) propertyChangeSupport.firePropertyChange(PROP_FIRMA, oldFirma, firma);
    }

    public boolean isHaendler() {
        return this.haendler;
    }

    public void setHaendler(boolean haendler) {
        this.haendler = haendler;
    }

    public String getHandynummer() {
        return this.handynummer;
    }

    public void setHandynummer(String handynummer) {
        this.handynummer = handynummer;
    }

    public String getLIAdresse() {
        return this.LIAdresse;
    }

    public void setLIAdresse(String LIAdresse) {
        this.LIAdresse = LIAdresse;
    }

    public String getLIOrt() {
        return this.LIOrt;
    }

    public void setLIOrt(String LIOrt) {
        this.LIOrt = LIOrt;
    }

    public String getLIPlz() {
        return this.LIPlz;
    }

    public void setLIPlz(String LIPlz) {
        this.LIPlz = LIPlz;
    }

    public String getNachname() {
        return this.nachname;
    }

    public void setNachname(String nachname) {
        String oldNachname = this.nachname;
        this.nachname = nachname;
        if ( propertyChangeSupport != null ) propertyChangeSupport.firePropertyChange(PROP_NACHNAME, oldNachname, nachname);
    }

    public String getREAdresse() {
        return this.REAdresse;
    }

    public void setREAdresse(String REAdresse) {
        this.REAdresse = REAdresse;
    }

    public String getREOrt() {
        return this.REOrt;
    }

    public void setREOrt(String REOrt) {
        this.REOrt = REOrt;
    }

    public String getREPlz() {
        return this.REPlz;
    }

    public void setREPlz(String REPlz) {
        this.REPlz = REPlz;
    }

    public String getTelefonnummer() {
        return this.telefonnummer;
    }

    public void setTelefonnummer(String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getTitel() {
        return this.titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getVorname() {
        return this.vorname;
    }

    public void setVorname(String vorname) {
        String oldVorname = this.vorname;
        this.vorname = vorname;
        if ( propertyChangeSupport != null ) propertyChangeSupport.firePropertyChange(PROP_VORNAME, oldVorname, vorname);
    }

    public PaymentCondition getPaymentCondition() {
        if ( paymentCondition == null && haendler ) return PaymentCondition.DEALER;
        if ( paymentCondition == null ) return PaymentCondition.CUSTOMER;
        return paymentCondition;
    }

    public void setPaymentCondition(PaymentCondition paymentCondition) {
        if ( paymentCondition == PaymentCondition.DEALER ) haendler = true;
        this.paymentCondition = paymentCondition;
    }

    public PaymentMethod getPaymentMethod() {
        if ( paymentMethod == null ) return PaymentMethod.ADVANCE_PAYMENT;
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ShippingCondition getShippingCondition() {
        if ( shippingCondition == null ) return ShippingCondition.DEFAULT;
        return shippingCondition;
    }

    public void setShippingCondition(ShippingCondition shippingCondition) {
        this.shippingCondition = shippingCondition;
    }

    public Set<SalesChannel> getAllowedSalesChannels() {
        if ( allowedSalesChannels.isEmpty() ) {
            allowedSalesChannels.add(SalesChannel.CUSTOMER);
            if ( haendler ) allowedSalesChannels.add(SalesChannel.RETAILER);
        }
        return allowedSalesChannels;
    }

    public void setAllowedSalesChannels(Set<SalesChannel> allowedSalesChannels) {
        this.allowedSalesChannels = allowedSalesChannels;
    }

    public Set<CustomerFlag> getFlags() {
        return flags;
    }

    public Set<CustomerFlag> addFlag(CustomerFlag flag) {
        this.flags.add(flag);
        return flags;
    }

    public void setFlags(Set<CustomerFlag> flags) {
        this.flags = flags;
    }

    public String toInvoiceAddress() {
        String result = "";
        if ( !isEmpty(getFirma()) ) result = getFirma().trim() + NL;
        if ( !isEmpty(getVorname()) ) result += getVorname().trim() + " ";
        if ( !isEmpty(getNachname()) ) result += getNachname().trim();
        result += NL + NL;
        if ( !isEmpty(getREAdresse()) ) result += getREAdresse().trim() + NL;
        if ( !isEmpty(getREPlz()) ) result += getREPlz().trim() + " ";
        if ( !isEmpty(getREOrt()) ) result += getREOrt().trim();
        return result;
    }

    public String toShippingAddress() {
        if ( isBlank(getLIAdresse()) ) return toInvoiceAddress();
        String result = "";
        if ( !isEmpty(getFirma()) ) result = getFirma().trim() + NL;
        if ( !isEmpty(getVorname()) ) result += getVorname().trim() + " ";
        if ( !isEmpty(getNachname()) ) result += getNachname().trim();
        result += NL + NL;
        if ( !isEmpty(getLIAdresse()) ) result += getLIAdresse().trim() + NL;
        if ( !isEmpty(getLIPlz()) ) result += getLIPlz().trim() + " ";
        if ( !isEmpty(getLIOrt()) ) result += getLIOrt().trim();
        return result;
    }

    public String toHtmlHighDetailed() {
        String cuId = "<b>KID: " + getKundenID() + "</b><br />";
        String cuName = (getFirma() == null ? "" : getFirma() + "<br />")
                + (getTitel() == null ? "" : getTitel() + " ") + (getVorname() == null ? "" : getVorname() + " ")
                + (getNachname() == null ? "" : getNachname()) + "<br />";
        String invoiceAddress = "Rechnungsadresse:<br />"
                + (getREAdresse() == null ? "" : getREAdresse() + "<br />")
                + (getREPlz() == null ? "" : getREPlz() + " " + getPayCountry().getCountry() + "<br />")
                + (getREOrt() == null ? "" : getREOrt() + "<br />");
        String shippingAddress = "Lieferadresse:<br />"
                + (getLIAdresse() == null ? "<b>wie Rechnungsadresse</b>" : getLIAdresse() + "<br />"
                   + (getLIPlz() == null ? "" : getLIPlz() + " " + getShipCountry().getCountry() + "<br />")
                   + (getLIOrt() == null ? "" : getLIOrt() + "<br />"));

        String cflags = "Besonderheiten:" + getFlags().stream().map(f -> "<br /> - " + f.getName()).reduce((a, b) -> a + b).orElse("<br />");
        String salesChannel = "Erlaubte Kanäle:" + getAllowedSalesChannels().stream().map(c -> "<br /> - " + c.getName()).reduce((t, u) -> t + u).orElse("<br />");

        String accountancy = "FiBu Konto: " + (getLedger() != 0 ? "" + getLedger() : "");
        String ctaxId = "USt-Nr: " + (StringUtils.isNotBlank(getTaxId()) ? "" + getTaxId() : "");

        String contact = "Erreichbarkeit:<br />"
                + "Telefon: " + (getTelefonnummer() == null ? "<br />" : getTelefonnummer() + "<br />")
                + "Mobil: " + (getHandynummer() == null ? "<br />" : getHandynummer() + "<br />")
                + "e-mail: " + (getEmail() == null ? "<br />" : getEmail());

        String comments = "Bemerkungen:<br />" + (getAnmerkung() == null ? "" : (getAnmerkung()));

        String result = "<table width=\"100%\" height=\"100%\" border=\"3\">"
                + "<tr>"
                + "<th align=\"left\">" + cuId + "</th>"
                + "<th></th>"
                + "</tr><tr>"
                + "<td align=\"left\" valign=\"top\">" + cuName + "</td>"
                + "<td>" + contact + "</td>"
                + "</tr><tr>"
                + "<td align=\"left\" valign=\"top\">" + invoiceAddress + "</td>"
                + "<td align=\"left\" valign=\"top\">" + shippingAddress + "</td>"
                + "</tr><tr>"
                + "<td align=\"left\"   valign=\"top\"><u>Standart Zahlungsmodalität:</u><br />" + getPaymentMethod().getNote()
                + "<br /><u>Konditionen:</u><br />"
                + (getPaymentCondition() == null ? "" : getPaymentCondition().getNote() + "<br />")
                + (getShippingCondition() == null ? "" : getShippingCondition()) + "</td>"
                + "<td align=\"left\"   valign=\"top\">" + cflags + "</td>"
                + "</tr><tr>"
                + "<td align=\"left\" valign=\"top\">" + salesChannel + "</td>"
                + "<td align=\"left\" valign=\"top\">" + accountancy + "<br />" + ctaxId + "</td>"
                + "</tr><tr>"
                + "<td colspan=\"2\">" + comments + "</td>"
                + "</tr></table>";
        return result;
    }

    public String toHtmlSimple() {
        String re = "<b>KID: " + getKundenID() + "</b> | " + toNameCompanyLine() + "<br />";
        String reAdresse = (getREAdresse() == null ? "" : getREAdresse());
        String rePlz = (getREPlz() == null ? "" : (", " + getREPlz()));
        String reOrt = (getREOrt() == null ? "" : (" " + getREOrt())) + "<br />";
        re += reAdresse + rePlz + reOrt;
        return re;
    }

    public String toNameCompanyLine() {
        return (getFirma() == null ? "" : getFirma() + " | ")
                + (getTitel() == null ? "" : getTitel() + " ")
                + (getVorname() == null ? "" : getVorname() + " ")
                + (getNachname() == null ? "" : getNachname());
    }

}
