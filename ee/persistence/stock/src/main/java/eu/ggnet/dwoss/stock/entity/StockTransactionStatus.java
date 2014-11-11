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
import java.util.*;

import javax.persistence.*;

/**
 * This is a Status that a {@link StockTransaction} will have.
 * <p/>
 * @has 1 - n StockTransactionParticipation
 * @has n - 1 StockTransactionStatusType
 */
@Entity
public class StockTransactionStatus implements Serializable, Comparable<StockTransactionStatus> {

    @Id
    @GeneratedValue
    private int id;

    @Version
    private short optLock;

    private String comment;

    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "DATETIME")
    private Date occurence;

    @Basic(optional = false)
    private StockTransactionStatusType type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "status", orphanRemoval = true)
    private List<StockTransactionParticipation> participations = new ArrayList<>();

    public StockTransactionStatus() {
    }

    public StockTransactionStatus(StockTransactionStatusType type, Date occurence) {
        this.occurence = occurence;
        this.type = type;
    }

    public StockTransactionStatus(StockTransactionStatusType type, Date occurence, String comment) {
        this.comment = comment;
        this.occurence = occurence;
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getOccurence() {
        return occurence;
    }

    public void setOccurence(Date occurence) {
        this.occurence = occurence;
    }

    public StockTransactionStatusType getType() {
        return type;
    }

    public void setType(StockTransactionStatusType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public List<StockTransactionParticipation> getParticipations() {
        return Collections.unmodifiableList(participations);
    }

    public void addParticipation(StockTransactionParticipation participation) {
        this.participations.add(participation);
        participation.status = this;
    }

    @Override
    public String toString() {
        return "StockTransactionStatus{" + "id=" + id + ",comment=" + comment + ",occurence=" + occurence + ",type=" + type + '}';
    }

    @Override
    public int compareTo(StockTransactionStatus t) {
        if ( !this.occurence.equals(t.occurence) )
            return (this.occurence.after(t.occurence)) ? 1 : -1;
        return this.type.ordinal() - t.type.ordinal();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.occurence);
        hash = 29 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final StockTransactionStatus other = (StockTransactionStatus)obj;
        if ( !Objects.equals(this.occurence, other.occurence) ) return false;
        if ( this.type != other.type ) return false;
        return true;
    }
}
