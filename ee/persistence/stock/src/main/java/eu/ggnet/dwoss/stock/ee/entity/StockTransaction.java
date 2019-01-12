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
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import eu.ggnet.dwoss.util.persistence.EagerAble;

import lombok.*;

import static eu.ggnet.dwoss.stock.ee.entity.StockTransactionStatusType.*;

/**
 * This class represents the transaction between stocks or in special cases the in and out of stocks.
 * See {@link StockTransactionType} for more details about the course of events.
 * <p/>
 * @has 1 - 2 Stock
 * @has 1 - n StockTransactionStatus
 * @has 1 - n StockTransactionPosition
 * @has n - 1 StockTransactionType
 */
//TODO: The StockTransaction must have a validationViolation():String method to check the validity of a transaction
@Entity
@EqualsAndHashCode(of = "id")
@NamedQueries({
    @NamedQuery(name = "StockTransaction.byTypeAndStatus", query = "select s from StockTransaction s where s.type = ?1 and s.status.type = ?2 ORDER BY s.id DESC")
    ,
    @NamedQuery(name = "StockTransaction.bySourceTypesComment", query = "select s from StockTransaction s where s.source.id = ?1 and s.type = ?2 and s.status.type = ?3 and s.comment = ?4")
    ,
    @NamedQuery(name = "StockTransaction.byDestinationTypesComment", query = "select s from StockTransaction s where s.destination.id = ?1 and s.type = ?2 and s.status.type = ?3 and s.comment = ?4")
    ,
    @NamedQuery(name = "StockTransaction.byDestinationTypes", query = "select s from StockTransaction s where s.destination.id = ?1 and s.type = ?2 and s.status.type = ?3")
})
public class StockTransaction implements Serializable, EagerAble {

    private static final List<List<StockTransactionStatusType>> POSSIBLE_STATUS_TYPES = new ArrayList<>();

    static {
        List<StockTransactionStatusType> possibleStatusType = new ArrayList<>();
        possibleStatusType.add(PREPARED);
        POSSIBLE_STATUS_TYPES.add(possibleStatusType);

        possibleStatusType = new ArrayList<>();
        possibleStatusType.add(PREPARED);
        possibleStatusType.add(CANCELLED);
        POSSIBLE_STATUS_TYPES.add(possibleStatusType);

        possibleStatusType = new ArrayList<>();
        possibleStatusType.add(PREPARED);
        possibleStatusType.add(COMPLETED);
        POSSIBLE_STATUS_TYPES.add(possibleStatusType);

        possibleStatusType = new ArrayList<>();
        possibleStatusType.add(PREPARED);
        possibleStatusType.add(COMMISSIONED);
        POSSIBLE_STATUS_TYPES.add(possibleStatusType);

        possibleStatusType = new ArrayList<>();
        possibleStatusType.add(PREPARED);
        possibleStatusType.add(COMMISSIONED);
        possibleStatusType.add(IN_TRANSFER);
        POSSIBLE_STATUS_TYPES.add(possibleStatusType);
        possibleStatusType = new ArrayList<>();
        possibleStatusType.add(PREPARED);
        possibleStatusType.add(COMMISSIONED);
        possibleStatusType.add(IN_TRANSFER);
        possibleStatusType.add(RECEIVED);
        POSSIBLE_STATUS_TYPES.add(possibleStatusType);

        possibleStatusType = new ArrayList<>();
        possibleStatusType.add(PREPARED);
        possibleStatusType.add(COMMISSIONED);
        possibleStatusType.add(IN_TRANSFER);
        possibleStatusType.add(RECEIVED);
        possibleStatusType.add(COMPLETED);
        POSSIBLE_STATUS_TYPES.add(possibleStatusType);

        possibleStatusType = new ArrayList<>();
        possibleStatusType.add(COMPLETED);
        POSSIBLE_STATUS_TYPES.add(possibleStatusType);
    }

    @Getter
    @Id
    @GeneratedValue
    private int id;

    @Version
    private short optLock;

    /**
     * The source of the transaction
     */
    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Stock source;

    /**
     * The destination of the transaction
     */
    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Stock destination;

    /**
     * The Type of transaction
     */
    @Getter
    @Setter
    @NotNull
    @Basic(optional = false)
    private StockTransactionType type;

    @Getter
    @Setter
    @Lob
    @Column(length = 65536)
    private String comment;

    @OneToMany(cascade = CascadeType.ALL)
    private List<StockTransactionStatus> statusHistory = new ArrayList<>();

    /**
     * the actual/last status
     */
    @Getter
    @NotNull
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, optional = false)
    private StockTransactionStatus status;

    @Deprecated // Nie implementiert.
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<StockTransaction> alternativeTransactions = new ArrayList<>();

    /**
     * Positions of the Transaction
     */
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "transaction")
    List<StockTransactionPosition> positions = new ArrayList<>();

    public StockTransaction() {
    }

    public StockTransaction(StockTransactionType type) {
        this.type = type;
    }

    public void addUnit(StockUnit stockUnit) {
        addPosition(new StockTransactionPosition(stockUnit));
    }

    public void addPosition(StockTransactionPosition position) {
        if ( position == null ) return;
        position.setTransaction(this);
    }

    public void removePosition(StockTransactionPosition position) {
        if ( position == null ) return;
        position.setTransaction(null);
    }

    /**
     * Returns all positions
     * <p/>
     * @return all positions
     */
    public List<StockTransactionPosition> getPositions() {
        return Collections.unmodifiableList(positions);
    }

    /**
     * Returns all stock units in this transaction.
     * This method iterates over all positions and collects only {@link StockTransactionPosition#getStockUnit()} != null
     *
     * @return all stock units in this transaction.
     */
    public List<StockUnit> getUnits() {
        List<StockUnit> result = new ArrayList<>();
        for (StockTransactionPosition position : positions) {
            if ( position.getStockUnit() == null ) continue;
            result.add(position.getStockUnit());
        }
        return result;
    }

    public List<StockTransactionStatus> getStatusHistory() {
        return statusHistory;
    }

    public Date addStatus(StockTransactionStatus status) {
        statusHistory.add(status);
        this.status = status;
        return status.getOccurence();
    }

    public Date addStatus(StockTransactionStatusType statusType,
                          StockTransactionParticipationType participationType1,
                          String participant1,
                          StockTransactionParticipationType participationType2,
                          String participant2) {
        StockTransactionStatus newStatus = new StockTransactionStatus(statusType, new Date());
        newStatus.addParticipation(new StockTransactionParticipation(participationType1, participant1, true));
        newStatus.addParticipation(new StockTransactionParticipation(participationType2, participant2, true));
        return addStatus(newStatus);
    }

    public Date addStatus(StockTransactionStatusType statusType,
                          StockTransactionParticipationType participationType1,
                          String participant1) {
        StockTransactionStatus newStatus = new StockTransactionStatus(statusType, new Date());
        newStatus.addParticipation(new StockTransactionParticipation(participationType1, participant1, true));
        return addStatus(newStatus);
    }

    public Date addStatus(Date ocourence, StockTransactionStatusType statusType,
                          StockTransactionParticipationType participationType1,
                          String participant1) {
        StockTransactionStatus newStatus = new StockTransactionStatus(statusType, ocourence);
        newStatus.addParticipation(new StockTransactionParticipation(participationType1, participant1, true));
        return addStatus(newStatus);
    }

    public List<StockTransactionParticipation> getParticipations() {
        List<StockTransactionParticipation> result = new ArrayList<>();
        for (StockTransactionStatus s : statusHistory) {
            for (StockTransactionParticipation stp : s.getParticipations()) {
                result.add(stp);
            }
        }
        return result;
    }

    @Override
    public void fetchEager() {
        status.getParticipations().size();
        for (StockTransactionPosition position : positions) {
            if ( position.getStockUnit() != null ) position.getStockUnit().getId();
        }
        for (StockTransactionStatus stockTransactionStatus : getStatusHistory()) stockTransactionStatus.getParticipations().size();
    }

    @Null(message = "ValidationViolation must be null. ValidationMessage: ${validatedValue}")
    public String getValidationViolations() {
        List<StockTransactionStatus> sortedList = new ArrayList<>(getStatusHistory());
        Collections.sort(sortedList);
        List<StockTransactionStatusType> sortedTypeList = new ArrayList<>();
        for (StockTransactionStatus stockTransactionStatus : sortedList) {
            sortedTypeList.add(stockTransactionStatus.getType());
        }

        if ( POSSIBLE_STATUS_TYPES.contains(sortedTypeList) ) return null;
        return "TransactionStatusHistory is in invalid. Statuses=" + sortedList + " AllowedTypes=" + POSSIBLE_STATUS_TYPES;
    }

    @Override
    public String toString() {
        return "StockTransaction{id=" + id + ",source=" + source
                + ",destination=" + destination
                + ",type=" + type + ",status=" + status + ",positions=" + positions + '}';
    }

    public String toSimpleLine() {
        return "StockTransaction{id=" + id + ",source=" + source
                + ",destination=" + destination
                + ",type=" + type + '}';
    }
}
