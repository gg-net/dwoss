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
package eu.ggnet.dwoss.misc.op.listings;

import java.util.Date;

import lombok.Data;

/**
 *
 * @author oliver.guenther
 */
@Data
public class StackedLineUnit implements IStackedLineUnit, Comparable<IStackedLineUnit> {

    private String warranty;

    private String refurbishedId;

    // remove, unused
    private double retailerPrice;

    private double customerPrice;

    private double roundedTaxedCustomerPrice;

    private String accessories;

    private String comment;

    private String conditionLevelDescription;

    private Date mfgDate;

    private String serial;

    private Date warrentyTill;

    @Override
    public int compareTo(IStackedLineUnit other) {
        if ( other == null ) return 1;
        if ( !this.getConditionLevelDescription().equals(other.getConditionLevelDescription()) )
            return this.getConditionLevelDescription().compareTo(other.getConditionLevelDescription());
        if ( this.getCustomerPrice() != other.getCustomerPrice() ) return (int)(this.getCustomerPrice() - other.getCustomerPrice());
        return 0;
    }
}
