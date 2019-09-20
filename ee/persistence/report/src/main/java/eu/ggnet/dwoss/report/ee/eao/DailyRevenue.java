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
package eu.ggnet.dwoss.report.ee.eao;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Value holder for revenue reporting.
 * <p>
 * @author pascal.perau
 */
public class DailyRevenue {

    public final Date reportingDate;

    public final String documentTypeName;

    public final double dailySum;

    public final String salesChannelName;

    public DailyRevenue(Date reportingDate, String documentTypeName, double dailySum, String salesChannelName) {
        this.reportingDate = Objects.requireNonNull(reportingDate,"reportingDate must not be null");
        this.documentTypeName = Objects.requireNonNull(documentTypeName,"documentTypeName must not be null");;
        this.dailySum = dailySum;
        this.salesChannelName = salesChannelName;
    }

    // TODO: In the migration away from lombok, I wasn't sure I need that. So for now it's in. Verify, if needed. 
    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.reportingDate);
        hash = 97 * hash + Objects.hashCode(this.documentTypeName);
        hash = 97 * hash + (int)(Double.doubleToLongBits(this.dailySum) ^ (Double.doubleToLongBits(this.dailySum) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.salesChannelName);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final DailyRevenue other = (DailyRevenue)obj;
        if ( Double.doubleToLongBits(this.dailySum) != Double.doubleToLongBits(other.dailySum) ) return false;
        if ( !Objects.equals(this.documentTypeName, other.documentTypeName) ) return false;
        if ( !Objects.equals(this.salesChannelName, other.salesChannelName) ) return false;
        if ( !Objects.equals(this.reportingDate, other.reportingDate) ) return false;
        return true;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
}
