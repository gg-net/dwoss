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
package eu.ggnet.dwoss.redtape.ee.entity;

import java.io.Serializable;
import java.lang.ProcessBuilder.Redirect.Type;
import java.util.*;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PositionType;
import eu.ggnet.dwoss.mandator.api.value.Ledger;
import eu.ggnet.dwoss.redtape.ee.entity.Position.Key;
import eu.ggnet.dwoss.core.system.util.TwoDigits;

import static jakarta.persistence.CascadeType.*;

/**
 * A Position of a Document. A Position has an id and a document associated, but all changes to these values are all done
 * from {@link Document}, e.g., {@link Document#append(Position) } or {@link Document#remove(Position) }
 * <p>
 * Valid Positions are:
 * <table>
 * <tr><td>Type</td><td>Needed Values</td></tr>
 * <tr><td>Unit</td><td>name, price, amount, tax, description, unitId, document, uniqueUnitProductId;</td></tr>
 * <tr><td>Service</td><td>name, price, amount, tax, description, document;</td></tr>
 * <tr><td>Product_Batch</td><td>name, price, amount, tax, description, document, uniqueUnitProductId;</td></tr>
 * <tr><td>Comment</td><td>name, description, document;</td></tr>
 * <tr><td>Temporary_Comment</td><td>name, description, document;</td></tr>
 * <tr><td>Transportation_Cost</td><td>name, price, amount, tax, description, document;</td></tr>
 * </table>
 * <p>
 * @has 1 - 1 Position.Key
 * @has 1 - n Position.Type
 * @author bastian.venz, oliver.guenther
 */
@Entity
@IdClass(Key.class)
@NamedQuery(name = "Position.findByDocumentId", query = "SELECT p FROM Position p WHERE p.document.id = ?1")
@NamedQuery(name = "Position.countByDocumentId", query = "SELECT COUNT(p) FROM Position p WHERE p.document.id = ?1")
@NamedQuery(name = "Position.findByUniqueUnitId", query = "SELECT p FROM Position p WHERE p.uniqueUnitId = ?1")
@SuppressWarnings("PersistenceUnitPresent")
@Table(name = "DocumentPosition",indexes = @Index(name = "PositionUniqueUnitId", columnList = "uniqueUnitId"))
public class Position implements Serializable, Comparable<Position> {

    /**
     * Validation Group, for all Positions at all default Document Types (not Returns or Blocks), which must hold in the UI.
     */
    public static interface DefaultUi {
    };

    /**
     * Validation Group, for all Positions at Documents of {@link Type#RETURNS}, which must hold in the UI.
     */
    public static interface Returns {
    };

    /**
     * Validation Group, for all Positions at Documents of {@link Type#BLOCK}, which must hold in the UI.
     */
    public static interface Blocks {
    };

    public static class Key implements Serializable {

        private long document;

        private int id;

        public Key(long document, int id) {
            this.document = document;
            this.id = id;
        }

        public Key() {
            this(0, 0);
        }

        //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + (int)(this.document ^ (this.document >>> 32));
            hash = 83 * hash + this.id;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final Key other = (Key)obj;
            if ( this.document != other.document ) return false;
            if ( this.id != other.id ) return false;
            return true;
        }
        //</editor-fold>

        @Override
        public String toString() {
            return "Document.Key{" + "document=" + document + ", id=" + id + '}';
        }

    }

    @Version
    private Short optLock = 0;

    @Id
    int id;

    @Id
    @NotNull
    @Valid
    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST}, optional = false)
    Document document;

    @NotNull(groups = {Default.class, Returns.class, DefaultUi.class, Blocks.class})
    @Basic(optional = false)
    @Enumerated
    private PositionType type;

    @NotNull(groups = {Default.class, Returns.class, DefaultUi.class, Blocks.class})
    @Size(min = 1, max = 255, groups = {Default.class, Returns.class, DefaultUi.class, Blocks.class})
    private String name;

    private double price;

    private double amount;

    private double tax;

    @NotNull(groups = {Default.class, Returns.class, DefaultUi.class, Blocks.class})
    @Size(min = 1, groups = {Default.class, Returns.class, DefaultUi.class, Blocks.class})
    @Lob
    @Column(length = 65536)
    private String description;

    /**
     * Bookingaccounts below or equal zero represents non accountancy relevant data.
     */
    private int bookingAccount;

    /**
     * The optional uniqueUnitId.
     * Possible values:
     * <ul>
     * <li>-1 and Position.Type == Unit &rarr; a old SopoUnit whitout a UniqueUnit</li>
     * <li>0 and Position.Type != Unit &rarr; a non unit based position</li>
     * <li>bigger than 0 and Position.Type == Unit &rarr; a unit based position with the matching UniqueUnit</li>
     * </ul>
     */
    private int uniqueUnitId;

    private long uniqueUnitProductId;

    /**
     * The refurbished id associated with the position.
     */
    private String refurbishedId;

    /**
     * Serialnumber of the associated unit.
     */
    private String serial;

    public Position() {
    }

    Position(PositionType type, String name, double price, double amount, double tax, String description, Ledger bookingAccount, int uniqueUnitId, long uniqueUnitProductId, String refurbishedId, String serialNumber) {
        this.type = type;
        this.name = name;
        this.price = TwoDigits.round(price);
        this.amount = amount;
        this.tax = tax;
        this.description = description;
        this.bookingAccount = Optional.ofNullable(bookingAccount).map(Ledger::getValue).orElse(-1);
        this.uniqueUnitId = uniqueUnitId;
        this.uniqueUnitProductId = uniqueUnitProductId;
        this.serial = serialNumber;
        this.refurbishedId = refurbishedId;
    }

    /**
     * Returns a new Builder for the Position.
     *
     * @return a new Builder.
     */
    public static PositionBuilder builder() {
        return new PositionBuilder();
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRefurbishedId() {
        return refurbishedId;
    }
    
    public void setRefurbishedId(String refurbishedId) {
        this.refurbishedId = refurbishedId;
    }
    
    public String getSerial() {
        return serial;
    }
    
    public void setSerial(String serial) {
        this.serial = serial;
    }
    
    public Short getOptLock() {
        return optLock;
    }
    
    public double getPrice() {
        return price;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public double getTax() {
        return tax;
    }
    
    public void setTax(double tax) {
        this.tax = tax;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public PositionType getType() {
        return type;
    }
    
    public void setType(PositionType type) {
        this.type = type;
    }
    
    public Document getDocument() {
        return document;
    }
    
    public int getUniqueUnitId() {
        return uniqueUnitId;
    }
    
    public void setUniqueUnitId(int uniqueUnitId) {
        this.uniqueUnitId = uniqueUnitId;
    }
    
    public long getUniqueUnitProductId() {
        return uniqueUnitProductId;
    }
    
    public void setUniqueUnitProductId(long uniqueUnitProductId) {
        this.uniqueUnitProductId = uniqueUnitProductId;
    }
    //</editor-fold>
    
    /**
     * Returns price * tax optimized to 0.00 look. (+0.01 and .99 are changed)
     *
     * @return price * tax.
     */
    public double toAfterTaxPrice() {
        return TwoDigits.roundedApply(getPrice(), getTax(), 0);
    }

    public void setPrice(double nettoPrice) {
        this.price = TwoDigits.round(nettoPrice);
    }

    /**
     * Returns a Ledger instance, for now never Null, may chenge in the future.
     *
     * @return a Ledger instance, never Null.
     */
    public Optional<Ledger> getBookingAccount() {
        if ( bookingAccount <= 0 ) return Optional.empty();
        return Optional.of(new Ledger(bookingAccount, "From Position(id=" + id + ")"));
    }

    public void setBookingAccount(Ledger bookingAccount) {
        this.bookingAccount = Optional.ofNullable(bookingAccount).map(Ledger::getValue).orElse(-1);
    }
    /**
     * Creates a partial clone of the position, but without the document.
     * <p/>
     * @return a copy with document == null
     */
    public Position partialClone() {
        return new Position(type, name, price, amount, tax, description, new Ledger(bookingAccount, "fromPartialClone"), uniqueUnitId, uniqueUnitProductId, refurbishedId, serial);
    }
    
    @Override
    public int compareTo(Position other) {
        if ( other == null ) return 1;
        return this.id - other.id;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Position other = (Position)obj;
        if ( this.id != other.id ) return false;
        if ( !Objects.equals(this.document, other.document) ) return false;
        return true;
    }

    public boolean equalsContentWithoutId(Position p) {
        // EqualsContent should also work with unpersitend objects.
        if ( this.document == null && p.document != null ) return false;
        if ( this.document != null && p.document == null ) return false;
        if ( this.document != null && p.document != null && this.document.getId() != p.document.getId() ) return false;
        if ( this.type != p.type ) return false;
        if ( !Objects.equals(this.name, p.name) ) return false;
        if ( !Objects.equals(this.description, p.description) ) return false;
        if ( Math.abs(this.price - p.price) > 0.00001 ) return false;
        if ( Math.abs(this.tax - p.tax) > 0.00001 ) return false;
        if ( Math.abs(this.amount - p.amount) > 0.00001 ) return false;
        if ( !Objects.equals(this.bookingAccount, p.bookingAccount) ) return false;
        if ( this.uniqueUnitId != p.uniqueUnitId ) return false;
        return true;
    }

    public boolean equalsContent(Position p) {
        if ( this.id != p.id ) return false;
        // EqualsContent should also work with unpersitend objects.
        return equalsContentWithoutId(p);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.id;
        hash = 41 * hash + Objects.hashCode(this.document);
        return hash;
    }

    @Override
    public String toString() {
        return "Position{" + "id=" + id + ", document.id=" + (document == null ? "null" : document.getId()) + ", type=" + type + ", name=" + name + ", price=" + price + ", amount=" + amount + ", tax=" + tax + ", description=" + description + ", bookingAccount=" + bookingAccount + ", uniqueUnitId=" + uniqueUnitId + '}';
    }

    @Null(groups = Blocks.class, message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getBlocksValidationViolations() {
        if ( type == null ) return null;
        ArrayList<String> violations = new ArrayList<>();
        switch (type) {
            case UNIT: // Differs
                if ( amount != 1 ) violations.add("Die Menge darf nicht kleiner oder größer als 1 sein.");
                if ( uniqueUnitId == 0 ) violations.add("UniqueUnitId ist nicht gesetzt!");
                if ( uniqueUnitProductId == 0 ) violations.add("UniqueUnitProductId ist nicht gesetzt!");
                break;
            case SERVICE: // Default
                if ( amount < 1 ) violations.add("Die Menge muss größer als 0 sein.");
                break;
            case PRODUCT_BATCH: // Differs
                return "Artikel Positionen sind für Blocker nicht erlaubt.";
            case SHIPPING_COST:
                return "Versandkosten sind für Blocker nicht erlaubt.";
            case COMMENT:
                if ( price != 0 ) violations.add("Preis muss 0 sein, ist aber " + price);
        }
        if ( violations.isEmpty() ) return null;
        violations.add(0, "type=" + type);
        return violations.toString();
    }

    @Null(groups = Returns.class, message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getReturnsValidationViolations() {
        if ( type == null ) return null;
        ArrayList<String> violations = new ArrayList<>();
        switch (type) {
            case UNIT: // Differs
                if ( price != 0 ) violations.add("Preis muss 0 sein, ist aber " + price);
                if ( amount != 1 ) violations.add("Die Menge darf nicht kleiner oder größer als 1 sein.");
                if ( uniqueUnitId == 0 ) violations.add("UniqueUnitId ist nicht gesetzt!");
                if ( uniqueUnitProductId == 0 ) violations.add("UniqueUnitProductId ist nicht gesetzt!");
                break;
            case SERVICE: // Default
                if ( price == 0 ) violations.add("Preis ist nicht gesetzt!");
                if ( tax == 0 ) violations.add("Mwst nicht gesetzt!");
                if ( amount < 1 ) violations.add("Die Menge muss größer als 0 sein.");
                break;
            case PRODUCT_BATCH: // Differs
                return "Artikel Positionen sind für Rückläufer nicht erlaubt.";
            case SHIPPING_COST:
                return "Versandkosten sind für Rückläufer nicht erlaubt.";
            case COMMENT:
                if ( price != 0 ) violations.add("Preis muss 0 sein, ist aber " + price);
        }
        if ( violations.isEmpty() ) return null;
        violations.add(0, "type=" + type);
        return violations.toString();
    }

    @Null(groups = DefaultUi.class, message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getDefaultUiValidationViolations() {
        // if this is InPersistence, we could also discover the Type of Document and get more detailed.
        if ( type == null ) return null;
        ArrayList<String> violations = new ArrayList<>();
        switch (type) {
            case UNIT:
                if ( price == 0 ) violations.add("Preis ist nicht gesetzt!");
                if ( amount != 1 ) violations.add("Die Menge darf nicht kleiner oder größer als 1 sein.");
                if ( uniqueUnitId == 0 ) violations.add("UniqueUnitId ist nicht gesetzt!");
                if ( uniqueUnitProductId == 0 ) violations.add("UniqueUnitProductId ist nicht gesetzt!");
                break;
            case SERVICE:
                if ( price == 0 ) violations.add("Preis ist nicht gesetzt!");
                if ( amount <= 0 ) violations.add("Die Menge muss größer als 0 sein.");
                break;
            case PRODUCT_BATCH:
                if ( price == 0 ) violations.add("Preis ist nicht gesetzt!");
                if ( amount < 1 ) violations.add("Die Menge muss größer als 0 sein.");
                if ( uniqueUnitProductId == 0 ) violations.add("UniqueUnitProductId ist nicht gesetzt!");
                break;
            case SHIPPING_COST:
                if ( price == 0 ) violations.add("Preis ist nicht gesetzt!");
                if ( amount != 1 ) violations.add("Die Menge darf nicht kleiner oder größer als 1 sein.");
                break;
            case COMMENT:
                if ( price != 0 ) violations.add("Preis muss 0 sein, ist aber " + price);
        }
        if ( violations.isEmpty() ) return null;
        violations.add(0, "type=" + type);
        return violations.toString();
    }

    /**
     * Validation that must hold on Persistence Time.
     * <p/>
     * @return a String if the Validation failed
     */
    @Null(message = "ViolationMessage is not null, but '${validatedValue}'")
    public String getPersistenceValidationViolations() {
        if ( type == null ) return null;
        if ( document == null ) return null;
        if ( document.getType() == null ) return null;
        String violation;
        if ( document.getType() == DocumentType.RETURNS ) {
            violation = getReturnsValidationViolations();
        } else if ( document.getType() == DocumentType.BLOCK ) {
            violation = getBlocksValidationViolations();
        } else {
            violation = getDefaultUiValidationViolations();
        }
        if ( violation == null ) return null;
        return "[document.Type=" + document.getType() + "]" + violation;
    }

    public String toSimpleLine() {
        return this.getClass().getSimpleName() + "{" + (amount == 1 ? "" : amount + " * ") + "(" + type + ") " + name + "}";
    }
}
