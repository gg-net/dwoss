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

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.stock.api.PicoStock;

import static eu.ggnet.dwoss.core.common.values.SalesChannel.UNKNOWN;

/**
 * Represents a physical stock, i.e. a location that is used as a stock.
 *
 * @has n - 1 Stock.ID
 */
@Entity
@SuppressWarnings("PersistenceUnitPresent")
public class Stock implements Serializable {

    @Id
    private int id;

    @Version
    private short optLock;

    @NotNull
    @Size(min = 5)
    private String name;

    @NotNull
    private SalesChannel primaryChannel = UNKNOWN;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "stock")
    List<StockUnit> units = new ArrayList<>();

    public Stock() {
    }

    public Stock(int id) {
        this.id = id;
    }

    public Stock(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SalesChannel getPrimaryChannel() {
        return primaryChannel;
    }

    public void setPrimaryChannel(SalesChannel primaryChannel) {
        this.primaryChannel = primaryChannel;
    }

    public int getId() {
        return id;
    }

    public short getOptLock() {
        return optLock;
    }

    /**
     * Adds the unit to the stock if unit is not null, bidirectional handling implemented.
     *
     * @param unit the unit to be added, may not be null
     */
    public void addUnit(StockUnit unit) {
        if ( unit == null ) return;
        unit.setStock(this);
    }

    public void removeUnit(StockUnit unit) {
        if ( unit == null ) return;
        unit.setStock(null);
    }

    public List<StockUnit> getUnits() {
        return Collections.unmodifiableList(units);
    }

    public PicoStock toPicoStock() {
        return new PicoStock(id, name);
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of if">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Stock other = (Stock)obj;
        if ( this.id != other.id ) return false;
        return true;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "Stock{" + "id=" + id + ", optLock=" + optLock + ", name=" + name + ", primaryChannel=" + primaryChannel + '}';
    }

}

