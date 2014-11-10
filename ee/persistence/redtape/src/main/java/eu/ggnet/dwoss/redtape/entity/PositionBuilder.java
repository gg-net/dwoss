package eu.ggnet.dwoss.redtape.entity;

import eu.ggnet.dwoss.rules.PositionType;

/**
 * A builder for {@link Position} entities.
 * For further information in usage see {@link Position}.
 * <p/>
 * @author pascal.perau
 */
public class PositionBuilder {

    private PositionType type;

    private String name;

    private double afterTaxPrice;

    private double price;

    private double amount = 1.;

    private double tax;

    private String description;

    private int bookingAccount;

    private int uniqueUnitId;

    private long uniqueUnitProductId;

    private String refurbishedId;

    private String serialNumber;

    public PositionBuilder() {
    }

    public PositionBuilder setType(PositionType type) {
        this.type = type;
        return this;
    }

    public PositionBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PositionBuilder setAfterTaxPrice(double afterTaxPrice) {
        this.afterTaxPrice = afterTaxPrice;
        return this;
    }

    public PositionBuilder setPrice(double price) {
        this.price = price;
        return this;
    }

    public PositionBuilder setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public PositionBuilder setTax(double tax) {
        this.tax = tax;
        return this;
    }

    public PositionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public PositionBuilder setBookingAccount(int bookingAccount) {
        this.bookingAccount = bookingAccount;
        return this;
    }

    public PositionBuilder setUniqueUnitId(int uniqueUnitId) {
        this.uniqueUnitId = uniqueUnitId;
        return this;
    }

    public PositionBuilder setUniqueUnitProductId(long uniqueUnitProductId) {
        this.uniqueUnitProductId = uniqueUnitProductId;
        return this;
    }

    public Position createPosition() {
        return new Position(type, name, afterTaxPrice, price, amount, tax, description, bookingAccount, uniqueUnitId, uniqueUnitProductId, refurbishedId, serialNumber);
    }
}
