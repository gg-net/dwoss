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
package eu.ggnet.dwoss.stock.ee.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Represents one position on a transaction.
 * <p/>
 * @has 1 - 1 StockUnit
 */
@Entity
public class StockTransactionPosition implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private short optLock;

    /**
     * Short description, what this position represents. Normaly genererated from the StockUnit on creation or setting.
     */
    @Basic(optional = false)
    private String description;

    /**
     * The Transaction of the position
     */
    @NotNull
    @Valid
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    private StockTransaction transaction;

    /**
     * The StockUnit, may be null if the transaction is completed
     */
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private StockUnit stockUnit;

    /**
     * Reference to an optional UniqueUnit. May only be set implicit through the StockUnit
     */
    private Integer uniqueUnitId;

    public StockTransactionPosition() {
    }

    public StockTransactionPosition(StockUnit stockUnit) {
        setStockUnit(stockUnit);
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StockUnit getStockUnit() {
        return stockUnit;
    }

    static void internalSetUnitPosition(StockUnit unit, StockTransactionPosition position) {
        if ( unit == null && position == null ) return;
        if ( unit != null && position != null && position.equals(unit.getPosition()) ) return;
        if ( unit != null ) {
            if ( unit.position != null ) unit.position.stockUnit = null;
            unit.position = null;
        }
        if ( position != null ) {
            if ( position.stockUnit != null ) position.stockUnit.position = null;
            position.stockUnit = null;
        }
        if ( unit != null && position != null ) {
            position.stockUnit = unit;
            unit.position = position;
            // The special handling, also setting the refernece
            position.uniqueUnitId = unit.getUniqueUnitId();
            position.updateDescription();
        }
    }

    /**
     * Sets a StockUnit to be identified by the position. If the StockUnit is not null,
     * also the UniqueUnitReference is set an the description of this position is regenerated.
     *
     * @param stockUnit the StockUnit to be set.
     */
    public void setStockUnit(StockUnit stockUnit) {
        internalSetUnitPosition(stockUnit, this);
    }

    public StockTransaction getTransaction() {
        return transaction;
    }

    /**
     * Sets the Transaction, bidirectional handling is implemented.
     *
     * @param transaction the Transaction to be set.
     */
    public void setTransaction(StockTransaction transaction) {
        if ( transaction == null && this.transaction == null ) return;
        if ( this.transaction != null && this.transaction.equals(transaction) ) return;
        if ( this.transaction != null ) {
            this.transaction.positions.remove(this);
        }
        if ( transaction != null ) {
            transaction.positions.add(this);
        }
        this.transaction = transaction;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final StockTransactionPosition other = (StockTransactionPosition)obj;
        if ( this.id != other.id ) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + this.id;
        return hash;
    }

    /**
     * Updates the description of the position from the set StockUnit, if not null.
     */
    public void updateDescription() {
        if ( stockUnit != null ) {
            description = stockUnit.getRefurbishId() + " - " + stockUnit.getName();
        }
    }

    @PreRemove
    private void preRemove() {
        setStockUnit(null);
        setTransaction(null);
    }

    @Override
    public String toString() {
        return "StockTransactionPosition{id=" + id + ",description=" + description + ",transactionId="
                + (transaction == null ? "null" : transaction.getId()) + ",stockUnit=" + stockUnit + '}';
    }

    public Integer getUniqueUnitId() {
        return uniqueUnitId;
    }

    public void setUniqueUnitId(Integer uniqueUnitId) {
        this.uniqueUnitId = uniqueUnitId;
    }
}
