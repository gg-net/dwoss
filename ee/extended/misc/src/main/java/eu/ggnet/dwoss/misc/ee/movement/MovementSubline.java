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
package eu.ggnet.dwoss.misc.ee.movement;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A sub line for the movement report
 */
// TODO: Model used in jasperreports, optimize with control.
public class MovementSubline {

    private final int amount;

    private final String description;

    private final String refurbishId;

    private final String stock;

    private final boolean wrappedToShip;

    public MovementSubline(int amount, String description, String refurbishId, String stock, boolean wrappedToShip) {
        this.amount = amount;
        this.description = description;
        this.refurbishId = refurbishId;
        this.stock = stock;
        this.wrappedToShip = wrappedToShip;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getRefurbishId() {
        return refurbishId;
    }

    public String getStock() {
        return stock;
    }

    public boolean isWrappedToShip() {
        return wrappedToShip;
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
    // TODO: Not sure if needed, only here thorugh lombok removal.
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.amount;
        hash = 79 * hash + Objects.hashCode(this.description);
        hash = 79 * hash + Objects.hashCode(this.refurbishId);
        hash = 79 * hash + Objects.hashCode(this.stock);
        hash = 79 * hash + (this.wrappedToShip ? 1 : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final MovementSubline other = (MovementSubline)obj;
        if ( this.amount != other.amount ) return false;
        if ( this.wrappedToShip != other.wrappedToShip ) return false;
        if ( !Objects.equals(this.description, other.description) ) return false;
        if ( !Objects.equals(this.refurbishId, other.refurbishId) ) return false;
        if ( !Objects.equals(this.stock, other.stock) ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
