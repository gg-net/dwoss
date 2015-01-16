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
package eu.ggnet.dwoss.redtape.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.redtape.entity.util.DocumentEquals;
import eu.ggnet.dwoss.redtape.format.DocumentFormater;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.persistence.entity.IdentifiableEntity;

import static eu.ggnet.dwoss.redtape.entity.util.DocumentEquals.Property.*;
import static javax.persistence.CascadeType.*;

/**
 * Represents a Document, like the paper in a real dossier.
 * A Document has a type which represents it function.
 * More about the Types, the allowed workflow and validity are at {@link Type}.
 *
 * @has 1 - n Position
 * @has 1 - n Document.Type
 * @has 1 - n Document.Flag
 * @has 2 - n Address
 *
 * @author bastian.venz, oliver.guenther
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Document.activeOpenByTypeDirective", query = "select d from Document d where d.active = TRUE and d.closed = FALSE and d.type = ?1 and d.directive = ?2"),
    @NamedQuery(name = "Document.betweenDates", query = "select d from Document d where d.actual between ?1 and ?2 and d.type in (?3) and d.active = true ORDER BY d.identifier ASC"),
    @NamedQuery(name = "Document.findActiveAndOpenByCustomerId", query = "SELECT d FROM Document d WHERE d.dossier.customerId = ?2 AND d.type = ?1 AND d.active = TRUE AND d.closed = FALSE ORDER BY d.dossier.id DESC"),
    @NamedQuery(name = "Document.findActiveByDirective", query = "SELECT d FROM Document d WHERE d.active = TRUE AND d.directive = ?1"),
    @NamedQuery(name = "Document.byIdentifier", query = "SELECT d FROM Document d WHERE d.identifier like ?1 and d.type = ?2 and d.active = true"),
    @NamedQuery(name = "Document.findOpenInvoiceUnpaidByTypePaymentMethod", query = "SELECT d FROM Document d WHERE d.closed = FALSE AND d.active = true AND d.type = ?1 AND d.dossier.paymentMethod = ?2"),
    @NamedQuery(name = "Document.findOpenAnulationByCustomerPaymentMethod", query = "SELECT d FROM Document d WHERE d.closed = FALSE AND d.active = TRUE AND d.dossier.customerId = ?1 AND d.type IN (?2) AND d.dossier.paymentMethod=?3 AND d.directive=?4"),
    @NamedQuery(name = "Document.productIdAndType", query = "SELECT DISTINCT p.document FROM Position p WHERE p.uniqueUnitProductId = ?1 AND p.document.active = TRUE AND p.document.type = ?2 ORDER BY p.document.actual DESC")
})
public class Document extends IdentifiableEntity implements Serializable, Comparable<Document> {

    /**
     * A Condition that can be added to a Document. Conditions are meant only to be added.
     * Most of the validation, order and consistency of Conditions is done in RedTape :: Operations in de.dw.redtape.state .
     */
    public enum Condition {

        /**
         * Designated for a customer with Flag.CONFIRM_DOSSIER set and an Order with CASH_ON_DELIVERY,
         * this condition defines, that the customer has confirmed the Document.
         */
        CONFIRMED("bestätigt"),
        /**
         * Defines, that the Document is Paid.
         */
        PAID("bezahlt"),
        /**
         * The contents of the Document are picked up.
         */
        PICKED_UP("abgeholt"),
        /**
         * The contents of the Document are sent via UPS.
         */
        SENT("versendet"),
        /**
         * Designated for {@link DocumentType#ANNULATION_INVOICE} and {@link DocumentType#CREDIT_MEMO}.
         * A CreditMemo/Annulation Invoice was has been balanced, meaning the money was paid to the customer.
         */
        REPAYMENT_BALANCED("Storno Rechnung/Gutschrift Zahlung erledigt"),
        /**
         * Designated only for {@link DocumentType#COMPLAINT} which has been rejected, if it is completely unacceptable (e.g., Unit dropped).
         */
        REJECTED("abgelehnt"),
        /**
         * Designated only for {@link DocumentType#COMPLAINT} which has been withdrawn, if the customer itself tells us that everything is ok now.
         */
        WITHDRAWN("zurückgezogen"),
        /**
         * Designated only for {@link DocumentType#COMPLAINT} which has been accepted,
         * if the customer is right and we accept it, moving to a CreditMemo or Annulation Invoice.
         */
        ACCEPTED("angenomen"),
        /**
         * A Document that is closed without completion.
         */
        CANCELED("storniert");

        private final String name;

        private Condition(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Directive {

        /**
         * Nothing else to do, Dossier is complete.
         */
        NONE("Alles Erledigt", "Der Vorgang ist abgeschlossen, es ist nichts mehr zu tun."),
        /**
         * Send Contract Note - (Auftragsbestätigung versenden).
         */
        SEND_ORDER("Auftragsbestätigung senden", "Dem Kunden die Auftragsbestätigung zusenden bzw. den Kunden über den Auftrag informieren."),
        /**
         * Wait for the Money.
         */
        WAIT_FOR_MONEY("Warten auf Zahlungseingang",
                "Wir warten auf den Kunden, daß dieser seine Ware bezahlt. Im Falle von Lastschrift muss diese durch."),
        /**
         * Wait for the Money, reminded.
         */
        WAIT_FOR_MONEY_REMINDED("Warten auf Zahlungseingang. Erinnert!",
                "Wir warten auf den Kunden, daß dieser seine Ware bezahlt. Er wurde mindestens einmal erinnert."),
        /**
         * Create an Invoice.
         */
        CREATE_INVOICE("Rechnung erstellen", "Das Dokument Rechnung erstellen und ausdrucken/versenden."),
        /**
         * Deliver/Hand over Goods or Wait for Pick Up.
         */
        HAND_OVER_GOODS("Ware aushändigen", "Dem Kunden seine Ware aushändigen."),
        /**
         * Ship Goods.
         */
        PREPARE_SHIPPING("Versenden", "Die Ware versenden."),
        /**
         * Sent Cash on Delivery Contract.
         */
        SEND_CASH_ON_DELIVERY_CONTRACT("Nachnahmebedingungen senden", "Dem Kunden die Nachnahmebestätigung zusenden."),
        /**
         * Wait for the confirmation of the Cash on Delivery Confirmation.
         */
        WAIT_FOR_PAYMENT_CONTRACT_CONFIRMATION("Warten auf Bestätigung der Nachnahmebedingungen",
                "Wir warten auf den Kunden, daß dieser die Nachnahmebedingungen akzeptiert."),
        /**
         * Wait for the confirmation of the Cash on Delivery Confirmation, reminded.
         */
        WAIT_FOR_PAYMENT_CONTRACT_CONFIRMATION_REMINDED("Warten auf Bestätigung der Nachnahmebedingungen. Erinnert!",
                "Wir warten auf den Kunden, daß dieser die Nachnamebedingungen akzeptiert. Er wurde mindestens einmal erinnert."),
        /**
         * Wait for the confirmation of Order.
         */
        WAIT_FOR_ORDER_CONFIRMATION("Warten auf Bestätigung des Auftrags",
                "Wir warten auf den Kunden, daß dieser den Auftrag akzeptiert."),
        /**
         * Wait for the confirmation of Order, reminded.
         */
        WAIT_FOR_ORDER_CONFIRMATION_REMINDED("Warten auf Bestätigung des Auftrags. Erinnert!",
                "Wir warten auf den Kunden, daß dieser den Auftrag akzeptiert. Er wurde mindestens einmal erinnert."),
        /**
         * Wait for complaint completion.
         */
        WAIT_FOR_COMPLAINT_COMPLETION("Warten auf abschliessende Bearbeitung",
                "Es wird auf eine abschliessende Bearbeitung gewartet, entweder fehlen Informationen/Geräte vom Kunden oder eine Entscheidung im Haus."),
        /**
         * Wait for complaint completion.
         */
        CREATE_CREDIT_MEMO_OR_ANNULATION_INVOICE("Stornorechnung oder Gutschrift erzeugten",
                "Die Reklamation wurde akzeptiert, es muss ein Stornorechung oder eine Gutschrift erzeugt werden."),
        /**
         * Balance the CreditMemo.
         */
        BALANCE_REPAYMENT("Gutschrift/Stornorechnung ausgleichen.",
                "Es wurde eine Gutschrift/Stornorechung erzeugt, diese muss jetzt ausgeglichen werden. Der Kunde muss sein Geld zurück erhalten.");

        private final String name;

        private final String description;

        private Directive(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Flags for the Document.
     * Flags have absolutely nothing in common, but the Set, they are associated to.
     * The Handling and Impact of each Flag differs individually. Each Flag has a unique documentation at which point in
     * the lifecycle of a Dossier it can be set, removed or else.
     */
    public static enum Flag {

        /**
         * This Flag indicates, that at least on Document of the actual Type has been published to the Customers.
         * <p/>
         * Conditions for add:
         * <ul>
         * <li>Customer has been briefed about the document, either printed or send via email</li>
         * </ul>
         * Conditions for removal:
         * <ul>
         * <li>Document Type changed</li>
         * </ul>
         */
        CUSTOMER_BRIEFED,
        /**
         * This Flag indicates, that exactly this Document was published to the Customer.
         * <p/>
         * Conditions for add:
         * <ul>
         * <li>Customer has been briefed about the document, either printed or send via email</li>
         * </ul>
         * Changes in Document or Dossier which indicate a removal:
         * <ul>
         * <li>Document Type</li>
         * <li>A Position</li>
         * <li>Address</li>
         * <li>PaymentMethod</li>
         * <li>Dispatch state</li>
         * </ul>
         */
        CUSTOMER_EXACTLY_BRIEFED
    }

    /**
     * Possible receipts of Payment.
     * Represents the way, the customer balanced the receipt of a Payment.<br />
     * Settlement may have restrictions according to {@link PaymentMethod} of the {@link Document#dossier}
     */
    public enum Settlement {

        /**
         * Direct physical transfer of money.
         * Only possible for:
         * <ul>
         * <li>{@link PaymentMethod#ADVANCE_PAYMENT}</li>
         * <li>{@link PaymentMethod#INVOICE}</li>
         * </ul>
         */
        CASH("Barzahlung"),
        /**
         * Direct electronic transfer of money.
         * Only possible for:
         * <ul>
         * <li>{@link PaymentMethod#ADVANCE_PAYMENT}</li>
         * <li>{@link PaymentMethod#INVOICE}</li>
         * </ul>
         */
        E_CASH("EC-Zahlung"),
        /**
         * Transfer of money provided by the customer.
         * Only possible for:
         * <ul>
         * <li>{@link PaymentMethod#ADVANCE_PAYMENT}</li>
         * <li>{@link PaymentMethod#INVOICE}</li>
         * </ul>
         */
        REMITTANCE("Bank");

        private String name;

        private Settlement(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Id
    @GeneratedValue
    private long id;

    @Version
    private short optLock = 0;

    @Enumerated
    private DocumentType type;

    @OneToMany(cascade = ALL, mappedBy = "document", fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    @MapKey(name = "id")
    @Valid
    Map<Integer, Position> positions = new TreeMap<>();

    private boolean active;

    @Valid
    @NotNull // May be removed if UI Validation problem
    @Embedded
    private DocumentHistory history;

    @OneToOne(cascade = {DETACH})
    private Document predecessor;

    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private Dossier dossier;

    @Enumerated
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Flag> flags = EnumSet.noneOf(Flag.class);

    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private Address invoiceAddress;

    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    private Address shippingAddress;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Condition> conditions = EnumSet.noneOf(Condition.class);

    @NotNull
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Settlement> settlements = EnumSet.noneOf(Settlement.class);

    @Enumerated
    @NotNull
    private Directive directive;

    /**
     * Represents this document as closed.
     * Only changes in changesAllowed are still possible.
     */
    private boolean closed;

    /**
     * The identifier, i.e. Invoice.
     */
    private String identifier;

    /**
     * The actual Date of the Document, only the day part is relevant.
     *
     * This Date should be set to the actual value on every new Type of Document.
     */
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date actual;

    public Document() {
        actual = new Date();
    }

    /**
     * Constructor useful for test, has all mandatory parameters.
     *
     * @param type      the type
     * @param directive the directive
     * @param history   the history
     */
    public Document(DocumentType type, Directive directive, DocumentHistory history) {
        this();
        this.type = type;
        this.history = history;
        this.directive = directive;
    }

    /**
     * Returns a partial clone of the Document, without some fields (nearly same goes for {@link Document#equalsContent(Document) }.
     * <p/>
     * The following properties are not cloned:
     * <ul>
     * <li>active</li>
     * <li>dossier</li>
     * <li>history</li>
     * <li>id</li>
     * <li>optLock</li>
     * <li>predecessor</li>
     * </ul>
     * <p/>
     * @return the partial clone
     */
    public Document partialClone() {
        Document clone = new Document();
        clone.setType(type);
        clone.setIdentifier(identifier);
        clone.setActual(actual);
        clone.setInvoiceAddress(invoiceAddress);
        clone.setShippingAddress(shippingAddress);
        clone.setDirective(directive);
        clone.setClosed(closed);
        for (Settlement settlement : settlements) clone.add(settlement);
        for (Condition condition : conditions) clone.add(condition);
        for (Flag flag : flags) clone.add(flag);
        // TODO: I assume a valid Document, meaning there are no holes and no negative values in the position ids and starting from 1.
        for (Integer pos : new TreeSet<>(this.positions.keySet())) {
            clone.append(this.positions.get(pos).partialClone());
        }
        return clone;
    }

    @Override
    public long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public Document setActive(boolean isNewest) {
        this.active = isNewest;
        return this;
    }

    public Date getActual() {
        return actual;
    }

    public void setActual(Date actual) {
        this.actual = actual;
    }

    public Directive getDirective() {
        return directive;
    }

    public void setDirective(Directive directive) {
        this.directive = directive;
    }

    public Dossier getDossier() {
        return dossier;
    }

    public void setDossier(Dossier dossier) {
        if ( this.dossier == dossier ) return; // Implies both null
        if ( this.dossier != null ) this.dossier.documents.remove(this);
        if ( dossier != null ) dossier.documents.add(this);
        this.dossier = dossier;
    }

    public Address getInvoiceAddress() {
        return invoiceAddress;
    }

    public void setInvoiceAddress(Address invoiceAddress) {
        this.invoiceAddress = invoiceAddress;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public DocumentType getType() {
        return type;
    }

    public short getOptLock() {
        return optLock;
    }

    public Document setType(DocumentType type) {
        this.type = type;
        return this;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public DocumentHistory getHistory() {
        return history;
    }

    public void setHistory(DocumentHistory history) {
        this.history = history;
    }

    public Document getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Document predecessor) {
        this.predecessor = predecessor;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public Document add(Condition condition) {
        if ( condition == null ) return this;
        conditions.add(condition);
        return this;
    }

    public Set<Condition> getConditions() {
        return Collections.unmodifiableSet(conditions);
    }

    public void appendAll(Collection<Position> positionsToAdd) {
        for (Position position : positionsToAdd) {
            append(position);
        }
    }

    public void appendAll(Position... positionsToAdd) {
        if ( positionsToAdd != null ) appendAll(Arrays.asList(positionsToAdd));
    }

    /**
     * Appends a position at the and of the document.
     * <p/>
     * @param position the position to be appended.
     * @return the added position;
     * @throws IllegalArgumentException if the supplied position has a document other than null..
     */
    public Position append(Position position) throws IllegalArgumentException {
        if ( position == null ) return null;
        if ( position.document != null ) throw new IllegalArgumentException("Position has a document other than null: + " + position);
        position.document = this;
        if ( positions.keySet().isEmpty() ) position.id = 1;
        else position.id = Collections.max(positions.keySet()) + 1;
        positions.put(position.id, position);
        return position;
    }

    /**
     * Appends a position at an exlplizit place, workaround for ReceiptUnitOperation.executeOperation.
     * <p/>
     * @param id
     * @param position the position to be appended.
     * @return the added position;
     * @throws IllegalArgumentException if the supplied position has a document other than null..
     */
    public Position append(int id, Position position) throws IllegalArgumentException {
        if ( position == null ) return null;
        if ( position.document != null ) throw new IllegalArgumentException("Position has a document other than null: + " + position);
        position.document = this;
        position.id = id;
        positions.put(position.id, position);
        return position;
    }

    /**
     * Moves the position on id up, swapping the id with it's predecessor.
     * <p/>
     * @param position the position to be moved.
     * @return true if successful.
     */
    public boolean moveUp(Position position) {
        if ( position.id == 1 ) return false;
        positions.remove(position.id);
        Position tmp = positions.remove(position.id - 1);
        tmp.id = position.id;
        position.id -= 1;
        positions.put(position.id, position);
        positions.put(tmp.id, tmp);
        return true;
    }

    /**
     * Moves the position on id down, swapping the id with it's successor.
     * <p/>
     * @param position the position to be moved.
     * @return true if successful.
     */
    public boolean moveDown(Position position) {
        if ( position.id == Collections.max(positions.keySet()) ) return false;
        positions.remove(position.id);
        Position tmp = positions.remove(position.id + 1);
        tmp.id = position.id;
        position.id += 1;
        positions.put(position.id, position);
        positions.put(tmp.id, tmp);
        return true;
    }

    /**
     * Clears all Positions.
     * <p/>
     * @return all Positions, which are no longer in the List. (For possible removal)
     */
    public List<Position> removeAllPositions() {
        List<Position> result = new ArrayList<>(positions.values());
        for (Position position : result) position.document = null;
        positions.clear();
        return result;
    }

    /**
     * Removes the supplied position, and the reverse mapped document.
     * If the position is null, nothing happens.
     * If the position is not in the map positions, nothing happens.
     * <p/>
     * @param position the position to be removed.
     * @return the position.
     */
    public Position remove(final Position position) {
        if ( position == null ) return null;
        if ( !positions.containsValue(position) ) return null;
        return removeAt(position.getId());
    }

    /**
     * Removes if existing a position, which is of Type Unit and has the supplied uniqueUnitId.
     *
     * @param uniqueUnitId the uniqueUnitId
     * @return the remove position or null if none found.
     */
    public Position removeByUniqueUnitId(int uniqueUnitId) {
        for (Position position : new ArrayList<>(positions.values())) {
            if ( position.getType() == PositionType.UNIT && position.getUniqueUnitId() == uniqueUnitId ) {
                removeAt(position.getId());
                return position;
            }
        }
        return null;
    }

    /**
     * Removes the position at the id.
     * <p/>
     * @param id the id of the position to be removed.
     * @return the removed position.
     */
    public Position removeAt(final int id) {
        if ( !positions.containsKey(id) ) return null;
        Position position = positions.remove(id);
        position.document = null;
        position.id = 0;
        // If we have positions which are at the upper end, we want to change there ids.
        if ( positions.containsKey(id + 1) ) {
            for (int i = (id + 1); i <= Collections.max(positions.keySet()); i++) {
                Position shift = positions.remove(i);
                shift.id = i - 1;
                positions.put(i - 1, shift);
            }
        }
        return position;
    }

    /**
     * Returns all Positions.
     * <p/>
     * @return all Positions.
     */
    public SortedMap<Integer, Position> getPositions() {
        return new TreeMap<>(positions);
    }

    /**
     * Returns all UniqueUnitIds of all Positions of Type Unit.
     *
     * @return all UniqueUnitIds of all Positions of Type Unit, result is never null;
     */
    public Set<Integer> getPositionsUniqueUnitIds() {
        Set<Integer> result = new HashSet<>();
        for (Position position : positions.values()) {
            if ( position.getType() == PositionType.UNIT ) result.add(position.getUniqueUnitId());
        }
        return result;
    }

    /**
     * Returns all Positions with supplied Type.
     *
     * @param type the type
     * @return all Positions with supplied Type.
     */
    public SortedMap<Integer, Position> getPositions(PositionType type) {
        SortedMap<Integer, Position> result = new TreeMap<>();
        for (int pos : positions.keySet()) {
            if ( positions.get(pos).getType() == type ) result.put(pos, positions.get(pos));
        }
        return result;
    }

    /**
     * Returns the position associated with the id.
     * <p/>
     * @param id the id of the position
     * @return the position with the id or null.
     */
    public Position getPosition(int id) {
        return positions.get(id);
    }

    /**
     * Returns a position of type Unit matching the supplied uniqueUnitId, or null if not existent.
     *
     * @param uniqueUnitId the uniqueUnitId
     * @return a position of type Unit matching the supplied uniqueUnitId, or null if not existent.
     */
    public Position getPositionByUniqueUnitId(int uniqueUnitId) {
        for (Position position : positions.values()) {
            if ( position.getType() == PositionType.UNIT && position.getUniqueUnitId() == uniqueUnitId ) return position;
        }
        return null;
    }

    public Set<Flag> getFlags() {
        return new HashSet<>(flags);
    }

    public void add(Flag flag) {
        this.flags.add(flag);
    }

    public void remove(Flag flag) {
        this.flags.remove(flag);
    }

    public Set<Settlement> getSettlements() {
        return Collections.unmodifiableSet(settlements);
    }

    public void add(Settlement settlement) {
        this.settlements.add(settlement);
    }

    public void remove(Settlement settlement) {
        this.settlements.remove(settlement);
    }

    public double getPrice() {
        double price = 0.;
        for (Position position : positions.values()) {
            price += (position.getAmount() * position.getPrice());
        }
        return price;
    }

    public double getAfterTaxPrice() {
        double afterTax = 0.;
        for (Position position : positions.values()) {
            afterTax += (position.getAmount() * position.getAfterTaxPrice());
        }
        return afterTax;
    }

    /**
     * Returns true if and only if at least one Position is from a given Type.
     * <p/>
     * @param type The Type
     * @return true if at least one Position is from a given Type.
     */
    public boolean containsPositionType(PositionType type) {
        for (Position position : positions.values()) {
            if ( position.getType() == type ) return true;
        }
        return false;
    }

    /**
     * Returns true if any of the condition is at the document.
     * <p>
     * @param filter the condition to test against.
     * @return true if any of the condition is at the document.
     */
    public boolean containsAny(Condition... filter) {
        return !containsNone(filter);
    }

    /**
     * Returns true if none of the condition is at the document.
     * <p>
     * @param filter the condition to test against.
     * @return true if none of the condition is at the document.
     */
    public boolean containsNone(Condition... filter) {
        if ( filter == null || filter.length == 0 ) throw new RuntimeException("The filter for contains any must not be null or empty");
        Set<Condition> toRetain = new HashSet<>(conditions);
        toRetain.retainAll(Arrays.asList(filter));
        return toRetain.isEmpty();
    }

    /**
     * Equals the content of the Document, not evaluating all parameters (nearly same goes for {@link Document#partialClone() }.
     *
     * The following parameters are ignored:
     * <ul>
     * <li>id</li>
     * <li>optLock</li>
     * <li>active</li>
     * <li>history</li>
     * <li>predecessor : Should be impossible, that it changes and create some exception</li>
     * </ul>
     *
     * @param other the other Document
     * @return true if content is equal, otherwise false.
     */
    public boolean equalsContent(Document other) {
        return new DocumentEquals()
                .ignore(ID, ACTIVE, HISTORY, PREDECESSOR)
                .equals(this, other);
    }

    /**
     * Verifies if the difference between this and other have no impact on {@link Flag#CUSTOMER_EXACTLY_BRIEFED}
     *
     * @param other the other document to difference against.
     * @return true if the difference has no impact.
     */
    public boolean isStillExactlyBriefed(Document other) {
        if ( other == null ) throw new NullPointerException("The other Document must not be null");
        if ( this.getDossier().isDispatch() != other.getDossier().isDispatch() ) return false;
        if ( this.getDossier().getPaymentMethod() != other.getDossier().getPaymentMethod() ) return false;
        if ( this.type != other.type ) return false;
        if ( !Objects.equals(this.invoiceAddress, other.invoiceAddress) ) return false;
        if ( !Objects.equals(this.shippingAddress, other.shippingAddress) ) return false;
        if ( this.positions.size() != other.positions.size() ) return false;
        Iterator<Position> p1 = new TreeSet<>(this.positions.values()).iterator();
        Iterator<Position> p2 = new TreeSet<>(other.positions.values()).iterator();
        while (p1.hasNext()) {
            Position p1p = p1.next();
            Position p2p = p2.next();
            if ( !p1p.equalsContent(p2p) ) return false;
        }
        return true;
    }

    @Override
    public int compareTo(Document o) {
        if ( o == null ) return -1;
        if ( this.type != o.type ) return this.type.compareTo(o.type);
        return this.hashCode() - o.hashCode();
    }

    public String toTypeConditions() {
        return (StringUtils.isBlank(identifier) ? "id=" + id : identifier) + ", " + type.getName() + ", " + DocumentFormater.toConditions(this);
    }

    @Override
    public String toString() {
        return "Document{" + "id=" + id + ", type=" + type + ", closed=" + closed + ",actual=" + actual + ", conditions=" + conditions
                + ", directive=" + directive + ", positions=" + positions + "settlements=" + settlements
                + ", active=" + active + ", history=" + history + ", predecessor.id=" + (predecessor == null ? null : predecessor.getId())
                + ", dossier.id=" + (dossier == null ? null : dossier.getId()) + ", flags=" + flags + ", invoiceAddress=" + invoiceAddress
                + ", shippingAddress=" + shippingAddress + ", identifier=" + identifier + '}';
    }

    public String toSimpleLine() {
        return this.getClass().getSimpleName() + "{"
                + "id=" + id
                + (identifier == null ? "" : ",idenifier=" + identifier)
                + ",type=" + type
                + (active ? ",active" : "")
                + (closed ? ",closed" : "")
                + "}";
    }

}
