/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ee.assist;

import eu.ggnet.dwoss.core.common.values.ShipmentStatus;

import com.querydsl.core.annotations.QueryProjection;

/**
 * Result holder.
 * 
 * @author oliver.guenther
 */
public class ShipmentCount {
    
    private final ShipmentStatus status;
    
    private final long amount;
    
    private final int amountOfUnits;

    @QueryProjection
    public ShipmentCount(ShipmentStatus status, long amount, int amountOfUnits) {
        this.status = status;
        this.amount = amount;
        this.amountOfUnits = amountOfUnits;
    }

    public ShipmentStatus status() {
        return status;
    }

    public long amount() {
        return amount;
    }

    public int amountOfUnits() {
        return amountOfUnits;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + "status=" + status + ", amount=" + amount + ", amountOfUnits=" + amountOfUnits + '}';
    }
    
}
