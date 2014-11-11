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

/**
 * Someone (a real person) which participates in the transaction at a defined point
 * <p/>
 * @author oliver.guenther
 * @has n - 1 StockTransactionParticipationType
 */
@Entity
public class StockTransactionParticipation implements Serializable {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private short optLock;

    @Basic(optional = false)
    private StockTransactionParticipationType type;

    @Basic(optional = false)
    private String participantName;

    @Basic(optional = false)
    @Column(columnDefinition = "bit(1)")
    private boolean signed = false;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    StockTransactionStatus status;

    public StockTransactionParticipation() {
    }

    public StockTransactionParticipation(StockTransactionParticipationType type, String participantName) {
        this.type = type;
        this.participantName = participantName;
    }

    public StockTransactionParticipation(StockTransactionParticipationType type, String participantName, boolean signed) {
        this.type = type;
        this.participantName = participantName;
        this.signed = signed;
    }

    public String getPraticipantName() {
        return participantName;
    }

    public void setPraticipantName(String praticipantName) {
        this.participantName = praticipantName;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public StockTransactionParticipationType getType() {
        return type;
    }

    public void setType(StockTransactionParticipationType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public StockTransactionStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "StockTransactionParticipation{" + "id=" + id + ", type=" + type + ", participantName=" + participantName + ", signed=" + signed + '}';
    }
}
