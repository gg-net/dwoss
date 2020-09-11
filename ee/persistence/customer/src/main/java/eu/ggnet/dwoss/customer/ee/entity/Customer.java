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
import java.util.stream.Stream;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.*;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.core.system.persistence.*;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.customer.ee.entity.stash.ContactStash;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;

import static eu.ggnet.dwoss.core.common.values.AddressType.INVOICE;
import static eu.ggnet.dwoss.core.common.values.AddressType.SHIPPING;
import static eu.ggnet.dwoss.customer.ee.entity.Communication.Type.*;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

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
@Indexed
@SuppressWarnings({"FieldMayBeFinal", "PersistenceUnitPresent"})
public class Customer extends BaseEntity implements Serializable, EagerAble, ContactStash {

    /**
     * Fields for detailed Search.
     */
    public enum SearchField {
        ID,
        FIRSTNAME,
        LASTNAME,
        COMPANY,
        ADDRESS,
        COMMUNICATION
    }

    public enum Source {
        EXISTING("Bestandskunde"),
        JH_CAM_TOOL("CAM Tool T&S"),
        SOPO_STORE("Sonderposten Store"),
        SOPO_ONLINE("Sonderposten Online"),
        ONEADO("Oneado Online Shop");

        private final String name;

        Source(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public enum ExternalSystem {
        SAGE_FIBU_DEBITOR_LEDGER("Sage Fibu Debitorenkonto"), LEXWARE("Lexware Kundenid");

        private final String description;

        private ExternalSystem(String description) {
            this.description = description;
        }

        public String description() {
            return description;
        }
    }

    @Id
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

    private Source source;

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyEnumerated
    private Map<ExternalSystem, String> additionalCustomerIds = new EnumMap<>(ExternalSystem.class);

    private String keyAccounter;  // Null is ok.

    @Lob
    @Column(length = 65535)
    @Field
    private String comment;

    @OneToOne
    private Communication defaultEmailCommunication;

    /**
     * The supplied value is used to send out a newsletter called Händlerliste, may be null.
     */
    @OneToOne
    private Communication resellerListEmailCommunication;

    /**
     * maximum of size2, consisting of
     */
    @OneToMany(orphanRemoval = true, cascade = ALL, fetch = EAGER, mappedBy = "customer")
    List<AddressLabel> addressLabels = new ArrayList<>();

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Map<ExternalSystem, String> getAdditionalCustomerIds() {
        return additionalCustomerIds;
    }

    public void setAdditionalCustomerIds(Map<ExternalSystem, String> additionalCustomerIds) {
        this.additionalCustomerIds = additionalCustomerIds;
    }

    public String getKeyAccounter() {
        return keyAccounter;
    }

    public void setKeyAccounter(String keyAccounter) {
        this.keyAccounter = keyAccounter;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Optional<Communication> getDefaultEmailCommunication() {
        return Optional.ofNullable(defaultEmailCommunication);
    }

    public void setDefaultEmailCommunication(Communication defaultEmailCommunication) {
        this.defaultEmailCommunication = defaultEmailCommunication;
    }

    public Optional<Communication> getResellerListEmailCommunication() {
        return Optional.ofNullable(resellerListEmailCommunication);
    }

    public void setResellerListEmailCommunication(Communication resellerListEmailCommunication) {
        this.resellerListEmailCommunication = resellerListEmailCommunication;
    }

    @Override
    public long getId() {
        return id;
    }

    public short getOptLock() {
        return optLock;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    @Override
    public List<Contact> getContacts() {
        return contacts;
    }

    public List<MandatorMetadata> getMandatorMetadata() {
        return mandatorMetadata;
    }

    public Set<CustomerFlag> getFlags() {
        return flags;
    }
    //</editor-fold>

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
     * Returns a bidirectional wrapper List, mapping changes to the UniqueUnit.
     *
     * @return a bidirectional wrapper List
     */
    public List<AddressLabel> getAddressLabels() {
        return new AbstractBidirectionalListWrapper<AddressLabel>(addressLabels) {
            @Override
            protected void update(AddressLabel e, boolean add) {
                if ( add ) e.setCustomer(Customer.this);
                else e.setCustomer(null);
            }
        };
    }

    /**
     * Returns the first addresslabel of type invoice or null if nothing is found.
     *
     * @return the first addresslabel of type invoice or null if nothing is found.
     */
    public AddressLabel toInvoiceAddress() {
        return addressLabels.stream()
                .filter(al -> al.getType() == INVOICE)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns an addresslabel with elements for shipping or null if none is set.
     *
     * @return an addresslabel with elements for shipping.
     */
    public AddressLabel toShippingAddress() {
        return addressLabels.stream()
                .filter(al -> al.getType() == SHIPPING)
                .findFirst()
                .orElse(null);
    }

    /**
     * Generates a human readable representation of the customers name.
     * This funktion will build a string that contains the company name (optional) and the contacts full name.
     *
     * @return a human readable representation of the customer
     */
    public String toName() {
        return preferedCompany().map(c -> c.getName() + " - ").orElse("")
                + preferedContact().map(Contact::toFullName).orElse("No Contact");
    }

    /**
     * Returns the prefered company of the customer.
     * Preference is done by the following rules:
     * <ol>
     * <li>The company on the AddressLabel of type invoice, if any</li>
     * <li>The first company of all companyies, if any</li>
     * <li>empty</li>
     * </ol>
     *
     * @return the prefered company.
     */
    public Optional<Company> preferedCompany() {
        return Optional.ofNullable(addressLabels.stream()
                .filter(al -> al.getType() == INVOICE)
                .findFirst()
                .map(AddressLabel::getCompany)
                .orElse(getCompanies().stream().findFirst().orElse(null)));
    }

    /**
     * Returns the prefered contact of the customer.
     * Preference is done by the following rules:
     * <ol>
     * <li>The contact on the AddressLabel of type invoice, if any</li>
     * <li>The first contact on the first company with a contact, if any</li>
     * <li>The first contact of all contacts if any</li>
     * <li>empty</li>
     * </ol>
     *
     * @return the prefered contact of the customer.
     */
    public Optional<Contact> preferedContact() {
        return Optional.ofNullable(addressLabels.stream()
                .filter(al -> al.getType() == INVOICE)
                .findFirst()
                .map(AddressLabel::getContact)
                .orElse(Stream.concat(
                        companies.stream().flatMap((con) -> con.getContacts().stream()),
                        contacts.stream()
                ).findFirst().orElse(null)));
    }

    /**
     * Returns all communications of the customer (communication of contacts, communication of companies and communications of contacts of companies).
     *
     * @return all communications.
     */
    public List<Communication> getAllCommunications() {
        return streamAllCommunications().collect(Collectors.toList());
    }

    /**
     * Returns all communications of the customer (communication of contacts, communication of companies and communications of contacts of companies) of a
     * selected type.
     *
     * @param type the type to filter
     * @return all communications.
     */
    public List<Communication> getAllCommunications(Communication.Type type) {
        return streamAllCommunications().filter(c -> c.getType() == type).collect(Collectors.toList());
    }

    private Stream<Communication> streamAllCommunications() {
        return Stream.concat(
                Stream.concat(
                        contacts.stream().flatMap((con) -> con.getCommunications().stream()),
                        companies.stream().flatMap((con) -> con.getCommunications().stream())),
                companies.stream().flatMap((con) -> con.getContacts().stream()).flatMap((con) -> con.getCommunications().stream()));
    }

    /**
     * Converts the customer to a SimpleCustomer if possible.
     * <p>
     * The customer has to be Simple({@link #isSimple()}) or else an empty optional is returned.
     * For an optional of a SimpleCustomer the customer has to be either a consumer({@link #isConsumer()}) or a business customer({@link #isBusiness()}).
     *
     * @throws RuntimeException if the customer {@link #isSimple()} but neither a consumer({@link #isConsumer()}) or a business customer({@link #isBusiness()}).
     * @return an optional of the simple customer.
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
            sc.setCountry(contacts.get(0).getAddresses().get(0).getCountry());
            sc.setMobilePhone(contacts.get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setLandlinePhone(contacts.get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setEmail(contacts.get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().orElse(null));
            sc.setSex(contacts.get(0).getSex());
            sc.setSource(source);
            sc.setComment(comment);
            // Hint: Cause this is a simple customer, if default it must be the one and only email communication.
            getResellerListEmailCommunication().ifPresent(c -> sc.setUseEmailForResellerList(true));

            return Optional.of(sc);
        }
        if ( isBusiness() ) {
            SimpleCustomer sc = new SimpleCustomer();
            sc.setId(id);

            sc.setTitle(companies.get(0).getContacts().get(0).getTitle());
            sc.setFirstName(companies.get(0).getContacts().get(0).getFirstName());
            sc.setLastName(companies.get(0).getContacts().get(0).getLastName());
            sc.setSex(companies.get(0).getContacts().get(0).getSex());

            //TODO: should not happen anymore, remove the if part
            //if the Company have a Contact use this Address
            if ( !companies.get(0).getContacts().get(0).getAddresses().isEmpty() ) {
                sc.setStreet(companies.get(0).getContacts().get(0).getAddresses().get(0).getStreet());
                sc.setZipCode(companies.get(0).getContacts().get(0).getAddresses().get(0).getZipCode());
                sc.setCity(companies.get(0).getContacts().get(0).getAddresses().get(0).getCity());
                sc.setCountry(companies.get(0).getContacts().get(0).getAddresses().get(0).getCountry());
            } else {
                sc.setStreet(companies.get(0).getAddresses().get(0).getStreet());
                sc.setZipCode(companies.get(0).getAddresses().get(0).getZipCode());
                sc.setCity(companies.get(0).getAddresses().get(0).getCity());
                sc.setCountry(companies.get(0).getAddresses().get(0).getCountry());
            }

            //first check the Contact for the needed Communications than use the Communications on the Company
            if ( !companies.get(0).getContacts().get(0).getCommunications().isEmpty() ) {
                sc.setMobilePhone(companies.get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().orElse(null));
                sc.setLandlinePhone(companies.get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().orElse(null));
                sc.setEmail(companies.get(0).getContacts().get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().orElse(null));
            }
            if ( sc.getMobilePhone() == null ) {
                sc.setMobilePhone(companies.get(0).getCommunications().stream().filter(c -> c.getType() == MOBILE).map(Communication::getIdentifier).findFirst().orElse(null));
            }
            if ( sc.getLandlinePhone() == null ) {
                sc.setLandlinePhone(companies.get(0).getCommunications().stream().filter(c -> c.getType() == PHONE).map(Communication::getIdentifier).findFirst().orElse(null));
            }
            if ( sc.getEmail() == null ) {
                sc.setEmail(companies.get(0).getCommunications().stream().filter(c -> c.getType() == EMAIL).map(Communication::getIdentifier).findFirst().orElse(null));
            }

            sc.setSource(source);
            sc.setComment(comment);
            sc.setCompanyName(companies.get(0).getName());
            sc.setTaxId(companies.get(0).getTaxId());
            // Hint: Cause this is a simple customer, if default it must be the one and only email communication.
            getResellerListEmailCommunication().ifPresent(c -> sc.setUseEmailForResellerList(true));

            return Optional.of(sc);
        }

        throw new RuntimeException("is Simple, but neither consumer nor bussiness. Invalid and impossible");
    }

    /**
     * Validtes a simple customer via the {@link #getSimpleViolationMessage()} method.
     *
     * @return true if {@link #getSimpleViolationMessage()} returns null for the customer.
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
     * Returns null, if the customer is simple.
     * Overall Rules are:
     * <ul>
     * <li>Either a Contact or a Company is set</li>
     * <li>Contains only one Contact or one Company</li>
     * <li>Exactly One AddressLabels of Type Invoice</li>
     * <li>No CustomerFlag is set</li>
     * <li>No KeyAccounter is set</li>
     * <li>No MandatorMetadata is set</li>
     * <li>Communications are only allowed to be of one of the following types: EMAIL, PHONE, MOBILE</li>
     * <li>Communications on a simple Customer mustn't be more frequent than one from each allowed type</li>
     * </ul>
     * <p>
     * Consumer Customer Rules are:
     * <ul>
     * <li>Contact with only one Address</li>
     * <li>Contact has at least one Communication</li>
     * </ul>
     * <p>
     * Business Customer Rules are:
     * <ul>
     * <li>Company with only one Address, one Contact and no Communication</li>
     * <li>The Company's Contact has at least one Communication</li>
     * <li>The Address of the Company's Contact has to match the Company's Address</li>
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

        List<Communication.Type> allowedCommunicationTypes = Arrays.asList(EMAIL, MOBILE, PHONE);

        if ( isConsumer() ) {
            if ( contacts.size() > 1 )
                return "More than one Contact";
            if ( contacts.stream().flatMap(c -> c.getAddresses().stream()).count() > 1 )
                return "Contact has more than one address";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).count() > 3 )
                return "Contact of the consumer has more than 3 communications";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).filter(c -> !allowedCommunicationTypes.contains(c.getType())).findAny().isPresent() )
                return "At least one Communication Type not allowed for consumers were found";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == EMAIL).count() > 1 )
                return "multiple EMAIL type communications found";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == MOBILE).count() > 1 )
                return "multiple MOBILE type communications found";
            if ( contacts.stream().flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == PHONE).count() > 1 )
                return "multiple PHONE type communications found";
        }
        if ( isBusiness() ) {
            if ( companies.get(0).getContacts().isEmpty() )
                return "SimpleBusinessCustomer has no contact";
            if ( !companies.get(0).getCommunications().isEmpty() )
                return "SimpleBusinessCustomer's violates it's rule not to have a Company with Communication.";
            if ( companies.get(0).getContacts().size() > 1 )
                return "SimpleBusinessCustomer has more than one Contact";
            if ( companies.size() > 1 )
                return "SimpleBusinessCustomer has more than one Company";
            if ( companies.stream().flatMap(c -> c.getAddresses().stream()).count() != 1 )
                return "SimpleBusinessCustomer's Company has not exactly one address";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getAddresses().stream()).count() > 0 )
                return "SimpleBusinessCustomer's Contact can not have an address assigned";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).count() > 1 )
                return "The Company has more than one Contact";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).count() > 3 )
                return "The Company of the Business Customer has more than 3 Communications";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).filter(c -> !allowedCommunicationTypes.contains(c.getType())).count() >= 1 )
                return "Communications not allowed for business customers were found";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == EMAIL).count()
                    > 1 )
                return "multiple EMAIL type communications found";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == MOBILE).count()
                    > 1 )
                return "multiple MOBILE type communications found";
            if ( companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).filter(c -> c.getType() == PHONE).count()
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
     * <li>Either a Contact or a Company is set</li>
     * <li>Contains only Contacts or Companies</li>
     * <li>One AddressLabel of Type Invoice</li>
     * <li>Only 2 AddressLabels</li>
     * <li>Default eMail Communication, if not null must be of type email</li>
     * <li>Reseller eMail Communication, if not null must be of type email</li>
     * </ul>
     * <p>
     * Consumer Customer Rules are:
     * <ul>
     * <li>At least one Contact with at least one Communication.</li>
     * </ul>
     * <p>
     * Business Customer Rules are:
     * <ul>
     * <li>At least one Company and one Communication either on the Company or the Company's Contact.</li>
     * </ul>
     *
     * @return null if instance is valid, else a string representing the invalidation.
     */
    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getViolationMessage() {
        if ( contacts.isEmpty() && companies.isEmpty() ) return "Neither Contact nor Company are set.";
        if ( !contacts.isEmpty() && !companies.isEmpty() ) return "Contact and Company is set. Not allowed, only one of each.";
        if ( addressLabels.size() > 2 ) return "More than two AddressLables are set";
        if ( !addressLabels.stream().anyMatch(al -> al.getType() == INVOICE) ) return "No Addresslabel of type Invoice";
        if ( contacts.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Contacts: " + contacts.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if ( companies.stream().anyMatch(a -> a.getViolationMessage() != null) )
            return "Companies: " + companies.stream().filter(a -> a.getViolationMessage() != null).map(a -> a.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        if ( defaultEmailCommunication != null && defaultEmailCommunication.getType() != EMAIL ) return "Default email communication is not of type email";
        if ( resellerListEmailCommunication != null && resellerListEmailCommunication.getType() != EMAIL )
            return "Reseller email communication is not of type email";

        if ( isConsumer() ) {
            if ( !contacts.stream().flatMap(c -> c.getAddresses().stream()).findAny().isPresent() )
                return "Consumer: No Address on any Contact";
            if ( !contacts.stream().flatMap(c -> c.getCommunications().stream()).findAny().isPresent() )
                return "Consumer: No Communication on any Contact";
        }
        if ( isBusiness() ) {
            if ( companies.stream().flatMap(c -> c.getAddresses().stream()).count() == 0 ) return "BusinessCustomer has no Address.";
            if ( !companies.stream().flatMap(c -> c.getCommunications().stream()).findAny().isPresent()
                    && !companies.stream().flatMap(c -> c.getContacts().stream()).flatMap(c -> c.getCommunications().stream()).findAny().isPresent() )
                return "BusinessCustomer: No Communication";
            if ( companies.stream().flatMap(cmp -> cmp.getContacts().stream()).flatMap(cntct -> cntct.getCommunications().stream()).filter(comm -> comm.getViolationMessage() != null).findAny().isPresent() )
                return "Communications: " + companies.stream().flatMap(cmp -> cmp.getCommunications().stream()).filter(comm -> comm.getViolationMessage() != null).map(comm -> comm.getViolationMessage()).reduce((t, u) -> t + ", " + u).get();
        }
        return null;
    }

    /**
     * Returns the Customer as html.
     *
     * @return the customer as html.
     */
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
                .append(customerExtras.getShippingCondition() == null ? defaults.shippingCondition().getName() + " <i>(Standard)</i>" : customerExtras.getShippingCondition().getName())
                .append("</li>");
        sb.append("<li>Zahlungskonditionen: ")
                .append(customerExtras.getPaymentCondition() == null ? defaults.paymentCondition().getNote() + " <i>(Standard)</i>" : customerExtras.getPaymentCondition().getNote())
                .append("</li>");
        sb.append("<li>Zahlungsmodalität: ")
                .append(customerExtras.getPaymentMethod() == null ? defaults.paymentMethod().getNote() + " <i>(Standard)</i>" : customerExtras.getPaymentMethod().getNote())
                .append("</li>");
        sb.append("<li>Verkaufskanäle: ")
                .append(customerExtras.getAllowedSalesChannels().isEmpty()
                        ? defaults.allowedSalesChannels().stream().map(s -> s.description).collect(Collectors.toList()) + " <i>(Standard)</i>"
                        : customerExtras.getAllowedSalesChannels().stream().map(s -> s.description).collect(Collectors.toList()))
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

        sb.append("<td colspan=2 ><div style=\"background-color:")
                .append(getViolationMessage() == null ? "none;\">" : "tomato;\">(INVALID)")
                .append("<b>Kid: ").append(id)
                .append("&nbsp;-&nbsp;")
                .append(toName())
                .append(" (")
                .append(isBusiness() ? "Geschäftskunde" : "Endkunde")
                .append(isSimple() ? "" : ", <i>komplex</i>")
                .append(")</b></div></td>");

        sb.append("</tr><tr>");
        sb.append("<td valign=top>");

        //get possible invoice and shipping address labels
        AddressLabel shippingAddress = toShippingAddress();
        AddressLabel invoiceAddress = toInvoiceAddress();

        sb.append("<p>");
        //format invoice label
        if ( invoiceAddress == null ) {
            sb.append("<b>Rechnungsadresse fehlt!</b><br />");
        } else {
            sb.append("<b>Rechnungsadresse</b><br />");
            sb.append(invoiceAddress.toHtml());
        }
        sb.append("</p><p>");

        //format shipping label
        if ( shippingAddress == null ) {
            sb.append("<b>Lieferadresse wie Rechnungsadresse</b><br />");
        } else {
            sb.append("<b>Lieferadresse</b><br />");
            sb.append(toShippingAddress().toHtml());
        }
        sb.append("</p>");
        sb.append("</td>");

        int rowSpan = 1;
        if ( !flags.isEmpty() ) rowSpan++;
        if ( misc ) rowSpan++;
        if ( !StringUtils.isBlank(salesRow) ) rowSpan++;

        sb.append("<td valign=top rowspan=").append(rowSpan).append(" >");
        sb.append("<b>Standard eMail</b>:<ul><li>").append(defaultEmailCommunication == null ? "Keine!" : defaultEmailCommunication.getIdentifier()).append("</li></ul>");
        getResellerListEmailCommunication().ifPresent(c -> sb.append("<b>eMail für Händlerliste:</b>:").append(c.getIdentifier()).append("<br />"));
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

    /**
     * Projects this customer to a PicoCustomer.
     *
     * @return the picocustomer
     */
    public PicoCustomer toPico() {
        return new PicoCustomer(id, toName());
    }

    /**
     * Projects this customer to a UiCustomer.
     *
     * @return the ui customer
     */
    public UiCustomer toUiCustomer() {
        return new UiCustomer(
                getId(),
                preferedContact().map(Contact::getTitle).orElse(""),
                preferedContact().map(Contact::getFirstName).orElse(""),
                preferedContact().map(Contact::getLastName).orElse(""),
                preferedCompany().map(Company::getName).orElse(null),
                toName(),
                getDefaultEmailCommunication().map(Communication::getIdentifier).orElse(null),
                toFibuLedger());
    }

    private int toFibuLedger() {
        if ( !getAdditionalCustomerIds().containsKey(ExternalSystem.SAGE_FIBU_DEBITOR_LEDGER) ) return 0;
        String value = getAdditionalCustomerIds().get(ExternalSystem.SAGE_FIBU_DEBITOR_LEDGER);
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            LoggerFactory.getLogger(UiCustomer.class).warn("Customer(id={}, {}={} is not numeric", id, ExternalSystem.SAGE_FIBU_DEBITOR_LEDGER, value);
            return 0;
        }
    }

    @Override
    public void fetchEager() {
        getMandatorMetadata().size();
        companies.size();
        companies.forEach(c -> {
            c.getCommunications().size();
            c.getAddresses().size();
            c.getContacts().size();
            c.getContacts().forEach(con -> {
                con.getAddresses().forEach(a -> a.getId());
                con.getCommunications().size();
            });
        });
        contacts.forEach(c -> {
            c.getAddresses().size();
            c.getCommunications().size();
        });
        addressLabels.size();
    }

    @PostLoad
    public final void postLoad() {
        //TODO: Das hat Olli vergessen zu dokumentieren. Ich glaube es war ein duplikateworkarround.
        for (Iterator<AddressLabel> iterator = addressLabels.iterator(); iterator.hasNext();) {
            AddressLabel next = iterator.next();
            List<AddressLabel> without = new ArrayList<>(addressLabels);
            without.remove(next);
            if ( without.contains(next) ) iterator.remove();
        }
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + id + ", optLock=" + optLock + ", flags=" + flags + ", source=" + source + ", additionalCustomerIds=" + additionalCustomerIds
                + ", keyAccounter=" + keyAccounter + ", comment=" + comment + ", defaultEmailCommunication=" + defaultEmailCommunication
                + ", resellerListEmailCommunication=" + resellerListEmailCommunication + ", addressLabels=" + addressLabels + '}';
    }

}
