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
package eu.ggnet.dwoss.customer.entity;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.*;

import eu.ggnet.dwoss.customer.entity.projection.AddressLabel;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.rules.*;

import lombok.*;

import static eu.ggnet.dwoss.rules.AddressType.INVOICE;
import static eu.ggnet.dwoss.rules.AddressType.SHIPPING;
import static javax.persistence.CascadeType.ALL;

/**
 * The datamodel of a purchaser from the view of GG-Net.
 * <p>
 * @has 0..1 - 0..n Company
 * @has 0..1 - 0..n Contact
 * @has 0..1 - 0..n MandatorMetadata
 * @has 0..m - 0..n CustomerFlag
 *
 * @author pascal.perau
 */
@Entity
@ToString(exclude = {"companies", "contacts", "mandatorMetadata"})
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@Indexed
@SuppressWarnings({"FieldMayBeFinal", "PersistenceUnitPresent"})
public class Customer implements Serializable {

    @AllArgsConstructor
    @Getter
    public static enum Source {
        EXISITING("Bestandskunde"),
        JH_CAM_TOOL("CAM Tool T&S"),
        SOPO_STORE("Sonderposten Store"),
        SOPO_ONLINE("Sonderposten Online"),
        ONEADO("Oneado Online Shop");

        private final String name;
    }

    public static enum ExternalSystem {
        SAGE, LEXWARE
    }

    @Id
    @Getter
    @GeneratedValue
    private long id;

    @Version
    private short optLock;

    /**
     * A list of {@link Company}<code>s</code> represented by the customer.
     */
    @NotNull
    @OneToMany(cascade = ALL)
    @IndexedEmbedded
    private List<Company> companies = new ArrayList<>();

    /**
     * All contacts association with the customer.
     */
    @NotNull
    @OneToMany(cascade = ALL)
    @IndexedEmbedded
    private List<Contact> contacts = new ArrayList<>();

    /**
     * Optional Mandator Metadate.
     */
    @NotNull
    @OneToMany(cascade = ALL)
    private List<MandatorMetadata> mandatorMetadata = new ArrayList<>();

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<CustomerFlag> flags = new HashSet<>();

    @Getter
    @Setter
    private Source source;

    @Getter
    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    private Map<ExternalSystem, String> additionalCustomerIds = new EnumMap<>(ExternalSystem.class);

    @Getter
    @Setter
    private String keyAccounter;  // Null is ok.

    @Lob
    @Column(length = 65535)
    @Getter
    @Setter
    @Field
    @Boost(0.5f)
    private String comment;

    public List<Company> getCompanies() {
        return new ArrayList<>(companies);
    }

    public List<Contact> getContacts() {
        return new ArrayList<>(contacts);
    }

    public List<MandatorMetadata> getMandatorMetadata() {
        return new ArrayList<>(mandatorMetadata);
    }

    public Set<CustomerFlag> getFlags() {
        return new HashSet<>(flags);
    }

    /**
     * Returns the Metadata based on the matchcode, may return null.
     * <p>
     * @param matchcode the matchcode.
     * @return metadata, based on the matchcode
     */
    public MandatorMetadata getMandatorMetadata(String matchcode) {
        for (MandatorMetadata metadata : mandatorMetadata) {
            if ( metadata.getMandatorMatchcode().equals(matchcode) ) return metadata;
        }
        return null;
    }

    /**
     * Defensivly adds an instance to {@link Customer#companies}.
     * Null values or duplicates will be ignored.
     * <p>
     * @param c the {@link Company}
     */
    public void add(Company c) {
        if ( c != null ) companies.add(c);
    }

    /**
     * Defensivly adds an instance to {@link Customer#contacts}.
     * Null values or duplicates will be ignored.
     * <p>
     * @param c the {@link Contact}
     */
    public void add(Contact c) {
        if ( c != null ) contacts.add(c);
    }

    /**
     * Defensivly adds an instance to {@link Customer#mandatorMetadata}.
     * Null values or duplicates will be ignored.
     * <p>
     * @param m the {@link MandatorMetadata}
     */
    public void add(MandatorMetadata m) {
        if ( m != null ) mandatorMetadata.add(m);
    }

    /**
     * Defensivly add customerFlag.
     * <p>
     * @param customerFlag
     */
    public void add(CustomerFlag customerFlag) {
        if ( customerFlag != null ) flags.add(customerFlag);
    }

    public void remove(CustomerFlag customerFlag) {
        flags.remove(customerFlag);
    }

    public void clearFlags() {
        flags.clear();
    }

    /**
     * Returns an addresslabel with prefered elements for invoice never null.
     * This method returns never null, but all elements may be null.
     * The following rules are applied;
     * <ul>
     * <li>Company: prefered</li>
     * <li>Contact: prefered</li>
     * <li>Adreess: preferedInvoice</li>
     * </ul>
     * TODO: The following scenarios are not considered for now, Contacts and Addresses assosiated with the company only. Multiple prefereds. e.t.c.
     *
     * @return an addresslabel with prefered elements for invoice.
     */
    public AddressLabel toPreferedInvoiceAddress() {
        return new AddressLabel(
                companies.stream().filter(Company::isPrefered).findFirst(),
                contacts.stream().filter(Contact::isPrefered).findFirst(),
                INVOICE);
    }

    /**
     * Returns an addresslabel with prefered elements for shipping never null.
     * If there is no sippingAddress explicitly set, the invoicelabel is returned.
     * This method returns never null, but all elements may be null.
     * The following rules are applied;
     * <ul>
     * <li>Company: prefered</li>
     * <li>Contact: prefered</li>
     * <li>Adreess: preferedShipping</li>
     * </ul>
     *
     * @return an addresslabel with prefered elements for shipping.
     */
    public AddressLabel toPreferedShippingAddress() {
        Optional<Contact> preferedContact = contacts.stream().filter(Contact::isPrefered).findFirst();
        // Setting type to shipping if there exists a shipping address otherwise invoice.
        AddressType type = preferedContact.map(c -> c.prefered(SHIPPING)).map(a -> SHIPPING).orElse(INVOICE);
        return new AddressLabel(
                companies.stream().filter(Company::isPrefered).findFirst(),
                preferedContact,
                type);
    }

    /**
     * Generates a human readable representation of the customers name.
     * Works as follows:
     * If there is a company get the prefered otherwise the first.
     * If there is no company get the prefered contact otherwise the first.
     *
     * @return a human readable representation
     */
    public String toName() {
        String contactName = null;
        String companyName = null;
        if ( !companies.isEmpty() ) {
            for (Company company : companies) {
                if ( companyName == null ) companyName = company.getName();
                if ( company.isPrefered() ) companyName = company.getName();
            }
        }
        if ( !contacts.isEmpty() ) {
            for (Contact contact : contacts) {
                if ( contactName == null ) contactName = contact.toFullName();
                if ( contact.isPrefered() ) contactName = contact.toFullName();
            }
        }
        if ( contactName == null && companyName == null ) return "Customer without company and contact" + id;
        StringBuilder sb = new StringBuilder();
        if ( companyName != null ) sb.append(companyName);
        if ( companyName != null && contactName != null ) sb.append(" - ");
        if ( contactName != null ) sb.append(contactName);
        return sb.toString();
    }

    public String toMultiLine() {
        String result = toString();
        for (Company company : companies) {
            result += "\n -" + company;
            for (Address a : company.getAddresses()) {
                result += "\n  -" + a;
            }
            for (Communication communication : company.getCommunications()) {
                result += "\n  -" + communication;
            }
        }
        for (Contact contact : contacts) {
            result += "\n -" + contact;
            for (Address a : contact.getAddresses()) {
                result += "\n  -" + a;
            }
            for (Communication communication : contact.getCommunications()) {
                result += "\n  -" + communication;
            }
        }
        for (MandatorMetadata metadata : mandatorMetadata) {
            result += "\n -" + metadata;
        }
        return result;
    }

    public String toHtml() {
        return toHtml(
                mandatorMetadata.stream().map(m -> "<li>" + m.toHtml() + "</li>").reduce((u, t) -> u + t).map(s -> "<b>Mandantenspezifische Informationen</b>:<ul>" + s + "</ul>").orElse(""),
                Optional.ofNullable(comment).map(c -> "<b>Kommentar</b>:<br />" + c).orElse("")
        );
    }

    public String toHtml(String matchcode, DefaultCustomerSalesdata defaults) {

        StringBuilder sb = new StringBuilder();
        sb.append("<b>Verkaufsinformationen</b> <i>(mit Mandatenstandard)</i>:<ul>");

        MandatorMetadata customerExtras = mandatorMetadata.stream().filter(m -> m.getMandatorMatchcode().equals(matchcode)).findFirst().orElse(new MandatorMetadata());

        sb.append("<li>Versandkonditionen: ")
                .append(customerExtras.getShippingCondition() == null ? defaults.getShippingCondition() + " <i>(Standard)</i>" : customerExtras.getShippingCondition())
                .append("</li>");
        sb.append("<li>Zahlungskonditionen: ")
                .append(customerExtras.getPaymentCondition() == null ? defaults.getPaymentCondition().getNote() + " <i>(Standard)</i>" : customerExtras.getPaymentCondition().getNote())
                .append("</li>");
        sb.append("<li>Zahlungsmodalität: ")
                .append(customerExtras.getPaymentMethod() == null ? defaults.getPaymentMethod().getNote() + " <i>(Standard)</i>" : customerExtras.getPaymentMethod().getNote())
                .append("</li>");
        sb.append("<li>Verkaufskanäle: ")
                .append(customerExtras.getAllowedSalesChannels().isEmpty()
                        ? defaults.getAllowedSalesChannels().stream().map(SalesChannel::getName).collect(Collectors.toList()) + " <i>(Standard)</i>"
                        : customerExtras.getAllowedSalesChannels().stream().map(SalesChannel::getName).collect(Collectors.toList()))
                .append("</li>");

        sb.append("</ul>");

        return toHtml(sb.toString(),
                Optional.ofNullable(comment).map(c -> "<b>Kommentar</b>:<br />" + c).orElse("")
        );
    }

    private String toHtml(String salesRow, String comment) {
        final boolean misc = (!additionalCustomerIds.isEmpty() || source != null || keyAccounter != null);

        StringBuilder sb = new StringBuilder();
        sb.append("<table width=\"100%\"><tr>");
        sb.append("<td colspan=2 ><b>Kid: ").append(id).append("&nbsp;-&nbsp;").append(toName()).append("</b></td>");
        sb.append("</tr><tr>");

        sb.append("<td valign=top>");
        if ( toPreferedShippingAddress().getType() == INVOICE ) sb.append("<b>Bevorzugte Rechnungs- und Lieferadresse</b><br />");
        else sb.append("<b>Bevorzugte Rechnungsadresse</b><br />");
        sb.append(toPreferedInvoiceAddress().toHtml());
        if ( toPreferedShippingAddress().getType() == SHIPPING ) {
            sb.append("<br /><b>Bevorzugte Lieferadresse</b><br />");
            sb.append(toPreferedShippingAddress().toHtml());
        }
        sb.append("</td>");

        int rowSpan = 1;
        if ( !flags.isEmpty() ) rowSpan++;
        if ( misc ) rowSpan++;
        if ( !StringUtils.isBlank(salesRow) ) rowSpan++;

        sb.append("<td valign=top rowspan=").append(rowSpan).append(" >");
        sb.append(companies.stream().map(c -> "<li>" + c.toHtml() + "</li>").reduce((t, u) -> t + u).map(s -> "<b>Firmen(n)</b>:<ul>" + s + "</ul>").orElse(""));
        sb.append(contacts.stream().map(c -> "<li>" + c.toHtml() + "</li>").reduce((t, u) -> t + u).map(s -> "<b>Kontakt(e)</b>:<ul>" + s + "</ul>").orElse(""));
        sb.append("</td>");
        sb.append("</tr>");
        sb.append(flags.stream().map(f -> "<li>" + f.getName() + "</li>").reduce((t, u) -> t + u)
                .map(s -> "<tr><td valign=top><b>Kundeneigenschaften</b>:<ul>" + s + "</ul></td></tr>")
                .orElse(""));
        if ( misc ) sb.append("<tr><td valign=top><b>Sonstiges</b>:<br />");
        if ( source != null ) sb.append("Datenquelle: ").append(source).append("<br />");
        if ( keyAccounter != null ) sb.append("Betreuer: ").append(keyAccounter).append("<br />");
        sb.append(additionalCustomerIds.entrySet().stream()
                .map(e -> "<li>" + e.getKey() + ": " + e.getValue() + "</li>")
                .reduce((t, u) -> t + u)
                .map(s -> "Weitere Kundennummer(n):<ul>" + s + "</ul>")
                .orElse(""));
        if ( misc ) sb.append("</td></tr>");

        if ( !StringUtils.isBlank(salesRow) ) sb.append("<tr><td valign=top>").append(salesRow).append("</tr></td>");
        if ( !StringUtils.isAllBlank(comment) ) sb.append("<tr><td colspan=2>").append(comment).append("</tr></td>");
        sb.append("</table>");
        return sb.toString();
    }

}
