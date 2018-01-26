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

import eu.ggnet.dwoss.mandator.api.value.Ledger;
import eu.ggnet.dwoss.rules.PositionType;

/**
 * A builder for {@link Position} entities.
 * For further information in usage see {@link Position}.
 * <p/>
 * @author pascal.perau
 * @deprecated Use {@link Position#builder() } and add .amount(1) which was default in position builder.
 */
@Deprecated
public class PositionBuilder {

    private PositionType type;

    private String name;

    private double price;

    private double amount = 1.;

    private double tax;

    private String description;

    private Ledger bookingAccount;

    private int uniqueUnitId;

    private long uniqueUnitProductId;

    private String refurbishedId;

    private String serialNumber;

    public PositionBuilder() {
    }

    public PositionBuilder type(PositionType type) {
        this.type = type;
        return this;
    }

    public PositionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PositionBuilder price(double price) {
        this.price = price;
        return this;
    }

    public PositionBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    public PositionBuilder tax(double tax) {
        this.tax = tax;
        return this;
    }

    public PositionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public PositionBuilder bookingAccount(Ledger bookingAccount) {
        this.bookingAccount = bookingAccount;
        return this;
    }

    public PositionBuilder uniqueUnitId(int uniqueUnitId) {
        this.uniqueUnitId = uniqueUnitId;
        return this;
    }

    public PositionBuilder uniqueUnitProductId(long uniqueUnitProductId) {
        this.uniqueUnitProductId = uniqueUnitProductId;
        return this;
    }

    // build
    public Position build() {
        return new Position(type, name, price, amount, tax, description, bookingAccount, uniqueUnitId, uniqueUnitProductId, refurbishedId, serialNumber);
    }
}
