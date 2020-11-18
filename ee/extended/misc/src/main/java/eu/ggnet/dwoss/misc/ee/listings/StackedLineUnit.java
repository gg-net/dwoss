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
package eu.ggnet.dwoss.misc.ee.listings;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 *
 * @author oliver.guenther
 */
public class StackedLineUnit implements Comparable<StackedLineUnit> {

    private String warranty;

    private String refurbishedId;

    private double customerPrice;

    private double roundedTaxedCustomerPrice;

    private String accessories;

    private String comment;

    private String conditionLevelDescription;

    private Date mfgDate;

    private String serial;

    private Date warrentyTill;

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public String getWarranty() {
        return warranty;
    }
    
    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }
    
    public String getRefurbishedId() {
        return refurbishedId;
    }
    
    public void setRefurbishedId(String refurbishedId) {
        this.refurbishedId = refurbishedId;
    }
    
    public double getCustomerPrice() {
        return customerPrice;
    }
    
    public void setCustomerPrice(double customerPrice) {
        this.customerPrice = customerPrice;
    }
    
    public double getRoundedTaxedCustomerPrice() {
        return roundedTaxedCustomerPrice;
    }
    
    public void setRoundedTaxedCustomerPrice(double roundedTaxedCustomerPrice) {
        this.roundedTaxedCustomerPrice = roundedTaxedCustomerPrice;
    }
    
    public String getAccessories() {
        return accessories;
    }
    
    public void setAccessories(String accessories) {
        this.accessories = accessories;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getConditionLevelDescription() {
        return conditionLevelDescription;
    }
    
    public void setConditionLevelDescription(String conditionLevelDescription) {
        this.conditionLevelDescription = conditionLevelDescription;
    }
    
    public Date getMfgDate() {
        return mfgDate;
    }
    
    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }
    
    public String getSerial() {
        return serial;
    }
    
    public void setSerial(String serial) {
        this.serial = serial;
    }
    
    public Date getWarrentyTill() {
        return warrentyTill;
    }
    
    public void setWarrentyTill(Date warrentyTill) {
        this.warrentyTill = warrentyTill;
    }
    //</editor-fold>

    @Override
    public int compareTo(StackedLineUnit other) {
        if ( other == null ) return 1;
        if ( !this.getConditionLevelDescription().equals(other.getConditionLevelDescription()) )
            return this.getConditionLevelDescription().compareTo(other.getConditionLevelDescription());
        if ( this.getCustomerPrice() != other.getCustomerPrice() ) return (int)(this.getCustomerPrice() - other.getCustomerPrice());
        return 0;
    }

    public void normaize() {
        if ( warranty != null ) warranty = warranty.replaceAll("&", "&amp;");
        if ( accessories != null ) accessories = accessories.replaceAll("&", "&amp;");
        if ( comment != null ) comment = comment.replaceAll("&", "&amp;");
        if ( conditionLevelDescription != null ) conditionLevelDescription = conditionLevelDescription.replaceAll("&", "&amp;");
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
    // TODO: Not sure if needed, here cause of the lombok removal.
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.warranty);
        hash = 89 * hash + Objects.hashCode(this.refurbishedId);
        hash = 89 * hash + (int)(Double.doubleToLongBits(this.customerPrice) ^ (Double.doubleToLongBits(this.customerPrice) >>> 32));
        hash = 89 * hash + (int)(Double.doubleToLongBits(this.roundedTaxedCustomerPrice) ^ (Double.doubleToLongBits(this.roundedTaxedCustomerPrice) >>> 32));
        hash = 89 * hash + Objects.hashCode(this.accessories);
        hash = 89 * hash + Objects.hashCode(this.comment);
        hash = 89 * hash + Objects.hashCode(this.conditionLevelDescription);
        hash = 89 * hash + Objects.hashCode(this.mfgDate);
        hash = 89 * hash + Objects.hashCode(this.serial);
        hash = 89 * hash + Objects.hashCode(this.warrentyTill);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final StackedLineUnit other = (StackedLineUnit)obj;
        if ( Double.doubleToLongBits(this.customerPrice) != Double.doubleToLongBits(other.customerPrice) ) return false;
        if ( Double.doubleToLongBits(this.roundedTaxedCustomerPrice) != Double.doubleToLongBits(other.roundedTaxedCustomerPrice) ) return false;
        if ( !Objects.equals(this.warranty, other.warranty) ) return false;
        if ( !Objects.equals(this.refurbishedId, other.refurbishedId) ) return false;
        if ( !Objects.equals(this.accessories, other.accessories) ) return false;
        if ( !Objects.equals(this.comment, other.comment) ) return false;
        if ( !Objects.equals(this.conditionLevelDescription, other.conditionLevelDescription) ) return false;
        if ( !Objects.equals(this.serial, other.serial) ) return false;
        if ( !Objects.equals(this.mfgDate, other.mfgDate) ) return false;
        if ( !Objects.equals(this.warrentyTill, other.warrentyTill) ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
