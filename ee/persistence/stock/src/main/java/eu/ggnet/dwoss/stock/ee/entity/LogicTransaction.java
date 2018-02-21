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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import eu.ggnet.dwoss.util.persistence.entity.IdentifiableEntity;

/**
 * Represents a pool to Block {@link StockUnit}s.
 * Blocked StockUnits are unavailable for any other request to participate in another transaction until they are removes from the current transaction.
 *
 * @author oliver.guenther
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "LogicTransaction.findByUniqueUnitId", query = "select u.logicTransaction from StockUnit u where u.uniqueUnitId = ?1"),
    @NamedQuery(name = "LogicTransaction.findByDossierId", query = "select l from LogicTransaction l where l.dossierId = ?1"),
    @NamedQuery(name = "LogicTransaction.findByDossierIds", query = "select l from LogicTransaction l where l.dossierId in (?1)")
})
public class LogicTransaction extends IdentifiableEntity implements Serializable {

    @GeneratedValue
    @Id
    private long id;

    @Min(1)
    private long dossierId;

    @Version
    private short optLock;

    @NotNull
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "logicTransaction", fetch = FetchType.EAGER)
    Set<StockUnit> units = new HashSet<>();

    public LogicTransaction() {
    }

    @Override
    public long getId() {
        return id;
    }

    public long getDossierId() {
        return dossierId;
    }

    public void setDossierId(long dossierId) {
        this.dossierId = dossierId;
    }

    public void add(StockUnit stockUnit) {
        if ( stockUnit == null ) return;
        stockUnit.setLogicTransaction(this);
    }

    public void remove(StockUnit stockUnit) {
        if ( stockUnit == null ) return;
        stockUnit.setLogicTransaction(null);
    }

    public Set<StockUnit> getUnits() {
        return units;
    }

    @Override
    public String toString() {
        StringBuilder us = new StringBuilder();
        for (Iterator<StockUnit> it = units.iterator(); it.hasNext();) {
            StockUnit su = it.next();
            us.append("StockUnit(id=").append(su.getId()).append(",unitId=").append(su.getRefurbishId()).append(") ").append(su.getName());
            if ( it.hasNext() ) us.append(", ");
        }
        return "LogicTransaction{" + "id=" + id + ", dossierId=" + dossierId + ", units=[" + us.toString() + "]}";
    }
}