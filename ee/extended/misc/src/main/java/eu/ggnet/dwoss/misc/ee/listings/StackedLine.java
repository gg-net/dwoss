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

import java.net.URL;
import java.util.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.api.values.TradeName;

/**
 * Stacked Line for the Report.
 * 
 * @author oliver.guenther
 */
// TODO: As this Model is used in the Jasperreports, we cannot change it easyly for a Pojo to Imutable or Freebuilder. Do slowly.
public class StackedLine implements Comparable<StackedLine> {

    private boolean used = true;

    private String name;

    private int amount;

    private String description;

    private String manufacturerName;

    private String manufacturerPartNo;

    private String commodityGroupName;

    private ProductGroup group;

    private TradeName brand;

    private URL imageUrl;

    private String customerPriceLabel;

    private List<StackedLineUnit> units;

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getManufacturerName() {
        return manufacturerName;
    }
    
    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
    
    public String getManufacturerPartNo() {
        return manufacturerPartNo;
    }
    
    public void setManufacturerPartNo(String manufacturerPartNo) {
        this.manufacturerPartNo = manufacturerPartNo;
    }
    
    public String getCommodityGroupName() {
        return commodityGroupName;
    }
    
    public void setCommodityGroupName(String commodityGroupName) {
        this.commodityGroupName = commodityGroupName;
    }
    
    public ProductGroup getGroup() {
        return group;
    }
    
    public void setGroup(ProductGroup group) {
        this.group = group;
    }
    
    public TradeName getBrand() {
        return brand;
    }
    
    public void setBrand(TradeName brand) {
        this.brand = brand;
    }
    
    public URL getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getCustomerPriceLabel() {
        return customerPriceLabel;
    }
    
    public void setCustomerPriceLabel(String customerPriceLabel) {
        this.customerPriceLabel = customerPriceLabel;
    }
    
    public List<StackedLineUnit> getUnits() {
        return units;
    }
    
    public void setUnits(List<StackedLineUnit> units) {
        this.units = units;
    }
    //</editor-fold>
   
    public boolean isNew() {
        return !used;
    }

    public void add(StackedLineUnit u) {
        if ( units == null ) units = new ArrayList<>();
        units.add(u);
    }

    @Override
    public int compareTo(StackedLine other) {
        if ( other == null ) return 1;
        if ( !this.getManufacturerName().equals(other.getManufacturerName()) ) return this.getManufacturerName().compareTo(other.getManufacturerName());
        if ( !this.getCommodityGroupName().equals(other.getCommodityGroupName()) ) return this.getCommodityGroupName().compareTo(other.getCommodityGroupName());
        if ( !this.getName().equals(other.getName()) ) return this.getName().compareTo(other.getName());
        if ( !this.getManufacturerPartNo().equals(other.getManufacturerPartNo()) ) return this.getManufacturerPartNo().compareTo(other.getManufacturerPartNo());
        return 0;
    }

    public void normaize() {
        if ( description != null ) description = description.replaceAll("&", "&amp;");
        if ( manufacturerName != null ) manufacturerName = manufacturerName.replaceAll("&", "&amp;");
        if ( manufacturerPartNo != null ) manufacturerPartNo = manufacturerPartNo.replaceAll("&", "&amp;");
        if ( commodityGroupName != null ) commodityGroupName = commodityGroupName.replaceAll("&", "&amp;");
    }

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
    // TODO: Not sure if needed.
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.used ? 1 : 0);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + this.amount;
        hash = 53 * hash + Objects.hashCode(this.description);
        hash = 53 * hash + Objects.hashCode(this.manufacturerName);
        hash = 53 * hash + Objects.hashCode(this.manufacturerPartNo);
        hash = 53 * hash + Objects.hashCode(this.commodityGroupName);
        hash = 53 * hash + Objects.hashCode(this.group);
        hash = 53 * hash + Objects.hashCode(this.brand);
        hash = 53 * hash + Objects.hashCode(this.imageUrl);
        hash = 53 * hash + Objects.hashCode(this.customerPriceLabel);
        hash = 53 * hash + Objects.hashCode(this.units);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final StackedLine other = (StackedLine)obj;
        if ( this.used != other.used ) return false;
        if ( this.amount != other.amount ) return false;
        if ( !Objects.equals(this.name, other.name) ) return false;
        if ( !Objects.equals(this.description, other.description) ) return false;
        if ( !Objects.equals(this.manufacturerName, other.manufacturerName) ) return false;
        if ( !Objects.equals(this.manufacturerPartNo, other.manufacturerPartNo) ) return false;
        if ( !Objects.equals(this.commodityGroupName, other.commodityGroupName) ) return false;
        if ( !Objects.equals(this.customerPriceLabel, other.customerPriceLabel) ) return false;
        if ( this.group != other.group ) return false;
        if ( this.brand != other.brand ) return false;
        if ( !Objects.equals(this.imageUrl, other.imageUrl) ) return false;
        if ( !Objects.equals(this.units, other.units) ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
