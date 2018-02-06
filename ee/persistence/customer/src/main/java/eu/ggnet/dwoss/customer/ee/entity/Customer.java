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
package eu.ggnet.dwoss.customer.ee.entity;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.*;

import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.rules.*;

import lombok.*;

import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.*;
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
 * @has 1 - 1..n AddressLabel
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

    /**
     * Fields for detailed Search.
     */
    public static enum SearchField {
        ID,
        FIRSTNAME,
        LASTNAME,
        COMPANY,
        ADDRESS
    }

    @AllArgsConstructor
    @Getter
    public static enum Source {
        EXISTING("Bestandskunde"),
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

    @Getter
    @Version
    private short optLock;

    /**
     * A list of {@link Company}<code>s</code> represented by the customer.
     */
    @Getter
    @NotNull
    @OneToMany(cascade = ALL)
    @IndexedEmbedded
    private List<Company> companies = new ArrayList<>();

    /**
     * All contacts association with the customer.
     */
    @Getter
    @NotNull
    @OneToMany(cascade = ALL)
    @IndexedEmbedded
    private List<Contact> contacts = new ArrayList<>();

    /**
     * Optional Mandator Metadate.
     */
    @Getter
    @NotNull
    @OneToMany(cascade = ALL)
    private List<MandatorMetadata> mandatorMetadata = new ArrayList<>();

    @Getter
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

    /**
     * maximum of size2, consisting of
     */
    @Getter
    @Transient // Will be in the entity model later
    private List<AddressLabel> addressLabels = new ArrayList<>();

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
     * @deprecated use {@link Customer#getCompanies()} add.
     */
    @Deprecated
    public void add(Company c) {
        if ( c != null ) companies.add(c);
    }

    /**
     * Defensivly adds an instance to {@link Customer#contacts}.
     * Null values or duplicates will be ignored.
     * <p>
     * @param c the {@link Contact}
     * @deprecated use {@link Customer#getContacts()} add.
     */
    @Deprecated
    public void add(Contact c) {
        if ( c != null ) contacts.add(c);
    }

    /**
     * Defensivly adds an instance to {@link Customer#mandatorMetadata}.
     * Null values or duplicates will be ignored.
     * <p>
     * @param m the {@link MandatorMetadata}
     * @deprecated use {@link Customer#getMandatorMetadatas()} add.
     */
    @Deprecated
    public void add(MandatorMetadata m) {
        if ( m != null ) mandatorMetadata.add(m);
    }

    /**
     * Defensivly add customerFlag.
     * <p>
     * @param customerFlag
     * @deprecated use {@link Customer#getFlags()} add.
     */
    @Deprecated
    public void add(CustomerFlag customerFlag) {
        if ( customerFlag != null ) flags.add(customerFlag);
    }

    /**
     *
     * @param customerFlag
     * @deprecated use {@link Customer#getFlags()} remove.
     */
    @Deprecated
    public void remove(CustomerFlag customerFlag) {
        flags.remove(customerFlag);
    }

    /**
     * @deprecated use {@link Customer#getFlags()} clear.
     */
    @Deprecated
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
                companies.stream().filter(Company::isPrefered).findFirst().orElse(null),
                contacts.stream().filter(Contact::isPrefered).findFirst().orElse(null),
                contacts.stream().filter(Contact::isPrefered).findFirst().map(c -> c.prefered(INVOICE)).orElse(null),
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
        AddressType type = contacts.stream().filter(Contact::isPrefered).findFirst().map(c -> c.prefered(SHIPPING)).map(a -> SHIPPING).orElse(INVOICE);
        return new AddressLabel(
                companies.stream().filter(Company::isPrefered).findFirst().orElse(null),
                contacts.stream().filter(Contact::isPrefered).findFirst().orElse(null),
                contacts.stream().filter(Contact::isPrefered).findFirst().map(c -> c.prefered(type)).orElse(null),
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

    /**
     * Converts the customer to the simple dto form if possible.
     * If the customer is to complex, it cannot be converted an therefor an empty optional is returned.
     *
     * @return an optional of the simple customer
     */
    public Optional<SimpleCustomer> toSimple() {
        if ( !isSimple() ) return Optional.empty();
        if ( isConsumer() ) {
            SimpleCustomer sc = new SimpleCustomer();
            sc.setId(id);
            sc.setTitle(contacts.get(0).getTitle());
            sc.setFirstName(contacts.get(0).getFirstName());
            sc.setLastName(contacts.get(0).getLastName());
            sc.setStreet(contacts.get(0).getAddresses().get(0).getStreet());
            sc.setZipCode(contacts.get(0).getAddresses().get(0).getZipCode());
            sc.setCity(contacts.get(0).getAddresses().get(0).getCity());
            sc.setIsoCountry(contacts.get(0).getAddresses().get(0).getIsoCountry());
            sc.setMobilePhone(contacts.get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setLandlinePhone(contacts.get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setEmail(contacts.get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setSex(contacts.get(0).getSex());
            sc.setSource(source);
            sc.setComment(comment);

            return Optional.of(sc);
        }
        if ( isBusiness() ) {
            SimpleCustomer sc = new SimpleCustomer();
            sc.setId(id);
            sc.setTitle(companies.get(0).getContacts().get(0).getTitle());
            sc.setFirstName(companies.get(0).getContacts().get(0).getFirstName());
            sc.setLastName(companies.get(0).getContacts().get(0).getLastName());
            sc.setStreet(companies.get(0).getContacts().get(0).getAddresses().get(0).getStreet());
            sc.setZipCode(companies.get(0).getContacts().get(0).getAddresses().get(0).getZipCode());
            sc.setCity(companies.get(0).getContacts().get(0).getAddresses().get(0).getCity());
            sc.setIsoCountry(companies.get(0).getContacts().get(0).getAddresses().get(0).getIsoCountry());
            sc.setMobilePhone(companies.get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setLandlinePhone(companies.get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setEmail(companies.get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setSex(companies.get(0).getContacts().get(0).getSex());
            sc.setSource(source);
            sc.setComment(comment);
            sc.setCompanyName(companies.get(0).getName());
            sc.setTaxId(companies.get(0).getTaxId());

            return Optional.of(sc);
        }
        throw new RuntimeException("is Simple, but neither consumer nor bussiness. Invaid");
    }

    /**
     * Validtes a simple customer.
     * Rules are:
     * <ul>
     * <li>either a contact or a companie is set, but never both</li>
     * <li>At least one contact has a address</li>
     * </ul>
     *
     * @return true for a Vaild Customer
     */
    public boolean isSimple() {
        return getSimpleViolationMessage() == null;
    }

    public boolean isConsumer() {
        return !contacts.isEmpty();
    }

    public boolean isBusiness() {
        return !companies.isEmpty();
    }

    /**
     * Validates a customer.
     * Rules are:
     * <ul>
     * <li>either a contact or a company is set, but never both</li>
     * <li>AddressLabel from type Invoice is set</li>
     * <li>At least one contact has a address</li>
     * </ul>
     *
     * @return true for a Vaild Customer
     */
    public boolean isValid() {
        return getViolationMessage() == null;
    }

    /**
     * Returns null, if the customer is simple otherwise a string, why the customer is not simple.
     * Overall Rules are:
     * <ul>
     * <li>Either a Contact or a Company are set.</li>
     * <li>Contains only one Contact or one Company.</li>
     * <li>One AddressLabels of Type Invoice</li>
     * </ul>
     * <p>
     * Consumer Customer Rules are:
     * <ul>
     * <li>Contact with only one Address</li>
     * <li>one Communication from each type: "EMAIL, PHONE, MOBILE" is allowed</li>
     * </ul>
     * <p>
     * Business Customer Rules are:
     * <ul>
     * <li>Company with only one Address and one Contact</li>
     * <li>The Address of the Company Contact has to match the Company Address</li>
     * <li>One Communication from each type: "EMAIL, PHONE, MOBILE" is allowed</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    public String getSimpleViolationMessage() {

        if ( !isValid() ) return getViolationMessage();
        if ( !flags.isEmpty() ) return "CustomerFlag must be empty";
        if ( !StringUtils.isBlank(keyAccounter) ) return "Keyaccounter is set";
        if ( !mandatorMetadata.isEmpty() ) return "MandatorMetadata is set";
        if ( addressLabels.size() > 1 ) return "More than one AddressLabel is set";

        List<Communication.Type> allowedCommunicationTypes = new ArrayList<>();
        allowedCommunicationTypes.add(EMAIL);
        allowedCommunicationTypes.add(MOBILE);
        allowedCommunicationTypes.add(PHONE);

        if ( isConsumer() ) {
            if ( contacts.size() > 1 ) return "More than one Contact";
            if ( contacts.stream().flatMap(c -> c.getAddresses().stream()).count() > 1 ) return "Contact has more than one address";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).count() > 3 )
                return "Contact of the consumer  has more than 3 communications";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).filter(c -> !allowedCommunicationTypes.contains(c.getType())).count() >= 1 )
                return "Communications not allowed for consumers were found";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == EMAIL).count() > 1 )
                return "multiple EMAIL type communications found";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == MOBILE).count() > 1 )
                return "multiple MOBILE type communications found";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == PHONE).count() > 1 )
                return "multiple PHONE type communications found";
        }
        if ( isBusiness() ) {
            if ( companies.size() > 1 ) return "More than one Company";
            if ( companies.stream().flatMap(c -> c.getAddresses().stream()).count() > 1 ) return "The Company has more than one Address";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).count() > 1 ) return "The Company has more than one Contact";
            if ( companies.stream().flatMap(c -> c.getAddresses().stream()).
                    equals(companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getAddresses().stream()).findAny().isPresent()) )
                return "The Address of the Company mismatches the Address of the Contact from the Company";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).count() > 3 )
                return "The Company of the Business Customer has more than 3 Communications";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).filter(c -> !allowedCommunicationTypes.contains(c.getType())).count() >= 1 )
                return "Communications not allowed for business customers were found";
            if ( companies.stream().flatMap(company -> company.getCommunications().stream()).filter(communication -> communication.getType() == EMAIL).count()
                    + companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == EMAIL).count()
                    > 1 )
                return "multiple EMAIL type communications found";
            if ( companies.stream().flatMap(company -> company.getCommunications().stream()).filter(communication -> communication.getType() == MOBILE).count()
                    + companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == MOBILE).count()
                    > 1 )
                return "multiple MOBILE type communications found";
            if ( companies.stream().flatMap(company -> company.getCommunications().stream()).filter(communication -> communication.getType() == PHONE).count()
                    + companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == PHONE).count()
                    > 1 )
                return "multiple PHONE type communications found";
        }
        return null;
    }
    // The null annotation can only be activated after the next big release, as the customers in the database are all invalid.
    // @Null(message = "ViolationMessage is not null, but '${validatedValue}'")

    /**
     * Returns null, if the Customer is valid.
     * Overall Rules are:
     * <ul>
     * <li>Either a Contact or a Company are set.</li>
     * <li>Contains only Contacts or Companies.</li>
     * <li>One AddressLabels of Type Invoice</li>
     * <li>Only 2 AddressLabels</li>
     * </ul>
     * <p>
     * Consumer Customer Rules are:
     * <ul>
     * <li>A least one Contact.</li>
     * </ul>
     * <p>
     * Bussnis Customer Rules are:
     * <ul>
     * <li>A least one Company.</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    public String getViolationMessage() {
        if ( contacts.isEmpty() && companies.isEmpty() ) return "Neither Contact nor Company are set.";
        if ( !contacts.isEmpty() && !companies.isEmpty() ) return "Contact and Company is set. Not allowed, only one of each.";
        if ( addressLabels.size() > 2 ) return "More than two AddressLables are set";
        if ( !addressLabels.stream().anyMatch(al -> al.getType() == INVOICE) ) return "No Addresslabel of type Invoice";
        if ( contacts.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Contacts: " + contacts.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if ( companies.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Companies: " + companies.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if ( isConsumer() ) {
            if ( !contacts.stream().flatMap(c -> c.getAddresses().stream()).findAny().isPresent() ) return "Consumer: No Address on any Contact";
            if ( !contacts.stream().flatMap(c -> c.getCommunications().stream()).findAny().isPresent() ) return "Consumer: No Communication on any Contact";
        }
        if ( isBusiness() ) {
            if ( !companies.stream().flatMap(c -> c.getCommunications().stream()).findAny().isPresent() )
                return "No Communication ";

        }
        return null;
    }

    public String toHtml() {
        return toHtml(
                mandatorMetadata.stream().map(m -> "<li>" + m.toHtml() + "</li>").reduce((u, t) -> u + t).map(s -> "<b>Mandantenspezifische Informationen</b>:<ul>" + s + "</ul>").orElse(""),
                Optional.ofNullable(comment).map(c -> "<b>Kommentar</b>:<br />" + c).orElse("")
        );
    }

    /**
     * Returns the customer as html, containing only the mandator metadata of the supplied matchcode, reflecting the defaults.
     *
     * @param matchcode the matchcode of the mandator to show the metadata.
     * @param defaults  the defaults to reflect agains.
     * @return the customer as html with only the metadata of the matchcode.
     */
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
