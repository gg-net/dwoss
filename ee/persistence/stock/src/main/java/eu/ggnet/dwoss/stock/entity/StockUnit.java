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
package eu.ggnet.dwoss.stock.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Null;

import eu.ggnet.dwoss.stock.api.PicoStockUnit;
import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

import static javax.persistence.FetchType.EAGER;

/**
 * Represents a actual unit in the stock.
 * <p/>
 * The implementation captures the following cases:
 * <ul>
 * <li>An existing instance represents one and only one unit</li>
 * <li>There exist special {@link StockTransaction} which allow the creation and deletion of units</li>
 * <li>Units which are
 * <code>{@link StockUnit#isInStock()} == True</code> are physical in the stock</li>
 * <li>Units are either
 * <code>{@link StockUnit#isInStock()} or {@link StockUnit#isInTransaction()}</code> never both, with the one exception, that the
 * transaction is in status prepared.</li>
 * <li>Units which are
 * <code>{@link StockUnit#isInTransaction()} == True</code> not in stock but somewhere else</li>
 * </ul>
 * <p/>
 * @has n - 1 Stock
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "all", query = "Select su from StockUnit su"),
    @NamedQuery(name = "StockUnit.byUniqueUnitId", query = "Select p from StockUnit as p where p.uniqueUnitId = ?1"),
    @NamedQuery(name = "StockUnit.byRefurbishId", query = "Select p from StockUnit as p where p.refurbishId = ?1"),
    @NamedQuery(name = "StockUnit.byRefurbishIds", query = "SELECT p FROM StockUnit AS p WHERE p.refurbishId IN (?1)"),
    @NamedQuery(name = "StockUnit.byStockId", query = "Select p from StockUnit as p where p.stock.id = ?1"),
    @NamedQuery(name = "StockUnit.countByTypeStatusSource", query = "Select count(su) from StockUnit su where su.position.transaction.type = ?1 "
                + "and su.position.transaction.status.type = ?2 and su.position.transaction.source.id = ?3 "),
    @NamedQuery(name = "StockUnit.countByStockNoLogicTransaciton", query = "SELECT COUNT(su) FROM StockUnit su WHERE su.stock.id = ?1 AND su.logicTransaction IS NULL"),
    @NamedQuery(name = "StockUnit.byNoTransaciton", query = "SELECT su FROM StockUnit su WHERE su.logicTransaction IS NULL AND su.position IS NULL"),
    @NamedQuery(name = "StockUnit.byNoLogicTransaciton", query = "SELECT su FROM StockUnit su WHERE su.logicTransaction IS NULL"),
    @NamedQuery(name = "StockUnit.byNoLogicTransacitonAndPresentStock", query = "SELECT su FROM StockUnit su WHERE su.logicTransaction IS NULL AND su.stock IS NOT NULL"),
    @NamedQuery(name = "StockUnit.byNoLogicTransacitonAsUniqueUnitId", query = "SELECT su.uniqueUnitId FROM StockUnit su WHERE su.logicTransaction IS NULL"),
    @NamedQuery(name = "StockUnit.countByStockOnLogicTransaciton", query = "SELECT COUNT(su) FROM StockUnit su WHERE su.stock.id = ?1 AND su.logicTransaction IS NOT NULL"),
    @NamedQuery(name = "StockUnit.findByUniqueUnitIds", query = "SELECT u FROM StockUnit u WHERE u.uniqueUnitId IN (?1)")
})
@EqualsAndHashCode(of = "id")
public class StockUnit implements Serializable, EagerAble {

    @Getter
    @Id
    @GeneratedValue
    private int id;

    @Getter
    @Version
    private short optLock;

    /**
     * The name of the StockUnit.
     */
    @Getter
    @Setter
    private String name;

    /**
     * An id which helps to find the unit in reality. At the moment, this is either the SopoNr or the Serial
     */
    @Getter
    @Setter
    private String refurbishId;

    @Getter
    @Valid
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = EAGER)
    private Stock stock;

    @Getter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = EAGER)
    private StockLocation stockLocation;

    @Getter
    @Valid
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "stockUnit", fetch = EAGER)
    StockTransactionPosition position;

    @Getter
    @Valid
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private LogicTransaction logicTransaction;

    @Getter
    @Setter
    // Todo: convert to long and make it not null.
    private Integer uniqueUnitId;

    /**
     * Default constructor, needed for persistence
     */
    public StockUnit() {
    }

    /**
     * Test constructor, must only be used if this stock unit is never persisted.
     *
     * @param id the database identifier
     */
    StockUnit(int id) {
        this.id = id;
    }

    /**
     * Constructor.
     *
     * @param uniqueUnitId the unique unit reference
     * @param unitId       the unit id for the stock
     */
    public StockUnit(String unitId, Integer uniqueUnitId) {
        this(unitId, null, uniqueUnitId);
    }

    /**
     * Constructor.
     *
     * @param uniqueUnitId the unique unit reference
     * @param unitId       the unit id for the stock
     * @param name         a human understandable name
     */
    public StockUnit(String unitId, String name, Integer uniqueUnitId) {
        this.refurbishId = unitId;
        this.name = name;
        this.uniqueUnitId = uniqueUnitId;
    }

    /**
     * Returns null if Instance is valid, otherwise a message describing the problem
     * <p>
     * Possible cause;
     * <ul>
     * <li>StockUnit is neither on stock nor on transaction</li>
     * <li>StockUnit is on stock and on transaction, and the transaction is not of type status prepared</li>
     * </ul>
     * </p>
     * <p/>
     * @return null if Instance is valid, otherwise a message describing the problem
     */
    @Null
    public String getValidationViolations() {
        if ( isInStock() ^ isInTransaction() ) return null;
        if ( isInStock() && isInTransaction() && getTransaction().getStatus() != null
                && getTransaction().getStatus().getType() == StockTransactionStatusType.PREPARED ) return null;
        if ( isInStock() && isInTransaction() )
            return "StockUnit is in Stock and in Transaction, and the Transaciton is not in status prepared";
        if ( !isInStock() && !isInTransaction() )
            return "StockUnit is neither in Stock nor in Transaction";
        return "StockUnit is invalid, because something mystirious has happend, i don't know, this point should never be reached.";
    }

    /**
     * Sets this StockUnit to be on a StockTransactionPosition, bidirectional handling is implemented.
     * <p/>
     * @param position the position to be set
     */
    public void setPosition(StockTransactionPosition position) {
        StockTransactionPosition.internalSetUnitPosition(this, position);
    }

    /**
     * Returns true, if the unit is in stock.
     *
     * @return true, if the unit is in stock.
     */
    public boolean isInStock() {
        return getStock() != null;
    }

    /**
     * Returns true, if the unit is on a transaction.
     * <p/>
     * @return true, if the unit is on a transaction.
     */
    public boolean isInTransaction() {
        return getPosition() != null;
    }

    /**
     * Returns the transaction, where this unit is located or null if dead or in stock
     *
     * @return the transaction, where this unit is located or null if dead or in stock
     */
    public StockTransaction getTransaction() {
        return position == null ? null : position.getTransaction();
    }

    /**
     * Sets the Stock for the Unit, sets the StockLocation to null, bidirectional handling implemented
     *
     * @param stock the stock to be set
     */
    public void setStock(Stock stock) {
        if ( stock == null && this.stock == null ) return;
        if ( this.stock != null && this.stock.equals(stock) ) return;
        if ( this.stock != null ) {
            this.stock.units.remove(this);
        }
        if ( stock != null ) {
            stock.units.add(this);
        }
        this.stockLocation = null;
        this.stock = stock;
    }

    /**
     * Sets the StockLocation and the Stock
     * <p/>
     * @param stockLocation the StockLocation to be set
     */
    public void setStockLocation(StockLocation stockLocation) {
        setStock(stockLocation == null ? null : stockLocation.getStock());
        this.stockLocation = stockLocation;
    }

    public void setLogicTransaction(LogicTransaction logicTransaction) {
        if ( this.logicTransaction == logicTransaction ) return; // Implies both null
        if ( this.logicTransaction != null ) this.logicTransaction.units.remove(this);
        if ( logicTransaction != null ) logicTransaction.units.add(this);
        this.logicTransaction = logicTransaction;
    }

    @Override
    public void fetchEager() {
        if ( getTransaction() != null ) getTransaction().fetchEager();
    }

    public String toSimple() {
        return "StockUnit{id=" + id + ",refurbishId=" + refurbishId + "}";
    }

    public PicoStockUnit toPico() {
        return new PicoStockUnit(id, uniqueUnitId == null ? 0 : uniqueUnitId, name + " mit refurbishId=" + refurbishId);
    }

    @Override
    public String toString() {
        String location = "unknown";
        if ( isInTransaction() ) {
            location = "Transaction(id=" + position.getTransaction().getId() + ",type=" + position.getTransaction().getType() + ",status=" + position.getTransaction().getStatus().getType() + ")";
        } else if ( isInStock() ) {
            location = "Stock(id=" + stock.getId() + ",name=" + stock.getName() + ")";
        }
        return "StockUnit{" + "id=" + id + ",logicTransaction="
                + (logicTransaction == null ? "null" : "[id=" + logicTransaction.getId() + ",units=" + logicTransaction.getUnits().size() + ",dossierId=" + logicTransaction.getDossierId() + "]")
                + ",location=" + location + ",uniqueUnitId=" + uniqueUnitId + ",refurbishId=" + refurbishId + '}';
    }
}
