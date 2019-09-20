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
package eu.ggnet.dwoss.price.ee.engine;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.price.ee.engine.support.TraceCollector;
import eu.ggnet.dwoss.uniqueunit.ee.entity.*;
import eu.ggnet.dwoss.uniqueunit.ee.format.ProductFormater;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;

import static eu.ggnet.dwoss.price.ee.EngineTracer.Status.ERROR;
import static eu.ggnet.dwoss.price.ee.EngineTracer.Status.WARNING;

/**
 * ValueObject for Import and Export of PriceEngine and the Database
 *
 * @author oliver.guenther
 */
// TODO: This is just a bad bad class. Do not optimize, the price engine will probably be dropt in the future.
public class PriceEngineResult implements Comparable<PriceEngineResult>, Serializable {

    public static enum Change {

        SET, UNSET, NO_CHANGE
    }

    public final static String PROP_REFURBISHED_ID = "refurbishedId";

    public final static String PROP_COMMODITY_GROUP = "commodityGroup";

    public final static String PROP_MANUFACTURER_PART_NO = "manufacturerPartNo";

    public final static String PROP_PRODUCT_NAME = "productName";

    public final static String PROP_PRODUCT_DESCRIPTION = "productDescription";

    public final static String PROP_COMMENT = "comment";

    public final static String PROP_INTERNAL_COMMENT = "internalComment";

    public final static String PROP_COST_PRICE = "costPrice";

    public final static String PROP_CONTRACTOR_REFERENCE_PRICE = "contractorReferencePrice";

    public final static String PROP_REFERENCE_PRICE = "referencePrice";

    public final static String PROP_RETAILER_PRICE = "retailerPrice";

    public final static String PROP_CUSTOMER_PRICE = "customerPrice";

    public final static String PROP_WARRANTY_ID = "warrantyId";

    public final static String PROP_MFG_DATE = "mfgDate";

    public final static String PROP_INPUT_DATE = "inputDate";

    public final static String PROP_EOL = "eol";

    public final static String PROP_CONDITION_LEVEL = "conditionLevel";

    public final static String PROP_RULES_LOG = "rulesLog";

    public final static String PROP_ERROR = "error";

    public final static String PROP_WARNING = "warning";

    public final static String PROP_MANUFACTURER_PART_PRICE_FIXED = "manufacturerPartPriceFixed";

    public final static String PROP_UNIT_PRICE_FIXED = "unitPriceFixed";

    public final static String PROP_DATE_FIRST_PRICED = "dateFirstPriced";

    public final static String PROP_SPECIAL = "special";

    public final static String PROP_SALES_CHANNEL = "salesChannel";

    public final static String PROP_TAX = "tax";

    public final static String PROP_RETAILER_TO_CUSTOMER_PRICE_PERCENTAGE = "retailerToCustomerPricePercentage";

    public final static String PROP_LAST_RETAILER_PRICE = "lastRetailerPrice";

    public final static String PROP_LAST_CUSTOMER_PRICE = "lastCustomerPrice";

    public final static String PROP_WARRENTYVALID = "warrentyValid";

    public final static String PROP_STOCK = "stock";

    private String refurbishedId;

    private String commodityGroup;

    private String manufacturerPartNo;

    private String productName;

    private String productDescription;

    private String comment;

    private String internalComment;

    private double costPrice;

    private double contractorReferencePrice;

    private double referencePrice;

    private double retailerPrice;

    private double customerPrice;

    private double retailerToCustomerPricePercentage;

    private int warrantyId;

    private Date mfgDate;

    private Date inputDate;

    private Date eol;

    private String conditionLevel;

    private String rulesLog;

    private boolean error;

    private boolean warning;

    private Change manufacturerPartPriceFixed;

    private Change unitPriceFixed;

    private Date dateFirstPriced;

    private String special;

    private String salesChannel;

    private double tax;

    private double lastRetailerPrice;

    private double lastCustomerPrice;

    private Date warrentyValid;
    
    private String stock;

    public PriceEngineResult() {
        manufacturerPartPriceFixed = Change.NO_CHANGE;
        unitPriceFixed = Change.NO_CHANGE;
    }

    /**
     * Special Construtor for Imports.
     * <p/>
     * @param refurbishedId      the refurbishId
     * @param manufacturerPartNo the manufacturerParNo
     * @param retailerPrice      the retailerPrice
     * @param customerPrice      the customerPrice
     * @param unitFixPrice       is the unitPriceFixed
     * @param partFixPrice       is the productPriceFixed
     * @param warrantyId         the WarrantyId
     */
    public PriceEngineResult(String refurbishedId, String manufacturerPartNo, Double retailerPrice, Double customerPrice, Integer unitFixPrice, Integer partFixPrice, Integer warrantyId) {
        this();
        this.refurbishedId = refurbishedId;
        this.manufacturerPartNo = manufacturerPartNo;
        this.retailerPrice = retailerPrice;
        this.customerPrice = customerPrice;
        this.warrantyId = warrantyId;
        if ( unitFixPrice < 0 ) this.unitPriceFixed = Change.UNSET;
        else if ( unitFixPrice > 0 ) this.unitPriceFixed = Change.SET;
        if ( partFixPrice < 0 ) this.manufacturerPartPriceFixed = Change.UNSET;
        else if ( partFixPrice > 0 ) this.manufacturerPartPriceFixed = Change.SET;
    }

    public PriceEngineResult(UniqueUnit uu) {
        this();
        Product p = uu.getProduct();

        this.refurbishedId = uu.getRefurbishId();
        this.comment = UniqueUnitFormater.toSingleLineEquipmentAndComment(uu);
        this.manufacturerPartNo = p.getPartNo();
        this.conditionLevel = uu.getCondition().getNote();
        this.inputDate = uu.getInputDate();
        this.internalComment = UniqueUnitFormater.toSingleLineInternalComment(uu);
        this.mfgDate = uu.getMfgDate();
        this.warrantyId = uu.getWarranty().ordinal();
        Date firstPriced = null;
        for (PriceHistory priceHistory : uu.getPriceHistory()) {
            if ( firstPriced == null || firstPriced.after(priceHistory.getDate()) ) firstPriced = priceHistory.getDate();
        }
        this.dateFirstPriced = firstPriced;
        this.salesChannel = uu.getSalesChannel().getName();

        this.commodityGroup = p.getGroup().getNote();
        this.costPrice = p.getPrice(PriceType.MANUFACTURER_COST);
        this.eol = p.getEol();
        this.contractorReferencePrice = p.getPrice(PriceType.CONTRACTOR_REFERENCE);
        this.productDescription = p.getDescription();
        this.productName = ProductFormater.toName(p);
        this.referencePrice = 0;
        this.lastRetailerPrice = uu.getPrice(PriceType.RETAILER);
        this.lastCustomerPrice = uu.getPrice(PriceType.CUSTOMER);
    }
    
    public PriceEngineResult(UniqueUnit uu, String stock) {
        this(uu);
        this.stock = stock;
    }

    public void consumeLog(TraceCollector collector) {
        rulesLog = collector.getMessages();
        if ( collector.getStatus() == WARNING ) warning = true;
        if ( collector.getStatus() == ERROR ) error = true;
    }

    //<editor-fold defaultstate="collapsed" desc="getter/setter">
    public double getTax() {
        return tax;
    }
    
    public void setTax(double tax) {
        this.tax = tax;
    }
    
    /** m
     * Get the value of dateFirstPriced
     *
     * @return the value of dateFirstPriced
     */
    public Date getDateFirstPriced() {
        return dateFirstPriced;
    }
    
    public double getRetailerToCustomerPricePercentage() {
        return retailerToCustomerPricePercentage;
    }
    
    public void setRetailerToCustomerPricePercentage(double retailerToCustomerPricePercentage) {
        this.retailerToCustomerPricePercentage = retailerToCustomerPricePercentage;
    }
    
    /**
     * Set the value of dateFirstPriced
     *
     * @param dateFirstPriced new value of dateFirstPriced
     */
    public void setDateFirstPriced(Date dateFirstPriced) {
        this.dateFirstPriced = dateFirstPriced;
    }
    
    public Change getManufacturerPartPriceFixed() {
        return manufacturerPartPriceFixed;
    }
    
    public void setManufacturerPartPriceFixed(Change manufacturerPartPriceFixed) {
        this.manufacturerPartPriceFixed = manufacturerPartPriceFixed;
    }
    
    public double getCostPrice() {
        return roundTo2Decimals(costPrice);
    }
    
    public double getCustomerPrice() {
        return roundTo2Decimals(customerPrice);
    }
    
    public double getContractorReferencePrice() {
        return roundTo2Decimals(contractorReferencePrice);
    }
    
    public double getReferencePrice() {
        return roundTo2Decimals(referencePrice);
    }
    
    public double getRetailerPrice() {
        return roundTo2Decimals(retailerPrice);
    }
    
    public String getRulesLog() {
        return rulesLog;
    }
    
    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }
    
    public void setContractorReferencePrice(double contractorReferencePrice) {
        this.contractorReferencePrice = contractorReferencePrice;
    }
    
    public void setReferencePrice(double referencePrice) {
        this.referencePrice = referencePrice;
    }
    
    public void setRetailerPrice(double retailerPrice) {
        this.retailerPrice = retailerPrice;
    }
    
    public void setCustomerPrice(double customerPrice) {
        this.customerPrice = customerPrice;
    }
    
    public void setRulesLog(String rulesLog) {
        this.rulesLog = rulesLog;
    }
    
    public String getRefurbishedId() {
        return refurbishedId;
    }
    
    public void setRefurbishedId(String refurbishedId) {
        this.refurbishedId = refurbishedId;
    }
    
    public String getCommodityGroup() {
        return commodityGroup;
    }
    
    public void setCommodityGroup(String commodityGroup) {
        this.commodityGroup = commodityGroup;
    }
    
    public String getManufacturerPartNo() {
        return manufacturerPartNo;
    }
    
    public void setManufacturerPartNo(String manufacturerPartNo) {
        this.manufacturerPartNo = manufacturerPartNo;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductDescription() {
        return productDescription;
    }
    
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getInternalComment() {
        return internalComment;
    }
    
    public void setInternalComment(String internalComment) {
        this.internalComment = internalComment;
    }
    
    public int getWarrantyId() {
        return warrantyId;
    }
    
    public void setWarrantyId(int warrantyId) {
        this.warrantyId = warrantyId;
    }
    
    public Date getMfgDate() {
        return mfgDate;
    }
    
    public void setMfgDate(Date mfgDate) {
        this.mfgDate = mfgDate;
    }
    
    public Date getInputDate() {
        return inputDate;
    }
    
    public void setInputDate(Date inputDate) {
        this.inputDate = inputDate;
    }
    
    public Date getEol() {
        return eol;
    }
    
    public void setEol(Date eol) {
        this.eol = eol;
    }
    
    public String getConditionLevel() {
        return conditionLevel;
    }
    
    public void setConditionLevel(String conditionLevel) {
        this.conditionLevel = conditionLevel;
    }
    
    public boolean isError() {
        return error;
    }
    
    public void setError(boolean error) {
        this.error = error;
    }
    
    public boolean isWarning() {
        return warning;
    }
    
    public void setWarning(boolean warning) {
        this.warning = warning;
    }
    
    public Change getUnitPriceFixed() {
        return unitPriceFixed;
    }
    
    public void setUnitPriceFixed(Change unitPriceFixed) {
        this.unitPriceFixed = unitPriceFixed;
    }
    
    public String getSpecial() {
        return special;
    }
    
    public void setSpecial(String special) {
        this.special = special;
    }
    
    public String getSalesChannel() {
        return salesChannel;
    }
    
    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }
    
    public double getLastRetailerPrice() {
        return lastRetailerPrice;
    }
    
    public void setLastRetailerPrice(double lastRetailerPrice) {
        this.lastRetailerPrice = lastRetailerPrice;
    }
    
    public double getLastCustomerPrice() {
        return lastCustomerPrice;
    }
    
    public void setLastCustomerPrice(double lastCustomerPrice) {
        this.lastCustomerPrice = lastCustomerPrice;
    }
    
    public Date getWarrentyValid() {
        return warrentyValid;
    }
    
    public void setWarrentyValid(Date warrentyValid) {
        this.warrentyValid = warrentyValid;
    }
    
    public String getStock() {
        return stock;
    }
    
    public void setStock(String stock) {
        this.stock = stock;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="equals and hashCode of all">
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.refurbishedId);
        hash = 83 * hash + Objects.hashCode(this.commodityGroup);
        hash = 83 * hash + Objects.hashCode(this.manufacturerPartNo);
        hash = 83 * hash + Objects.hashCode(this.productName);
        hash = 83 * hash + Objects.hashCode(this.productDescription);
        hash = 83 * hash + Objects.hashCode(this.comment);
        hash = 83 * hash + Objects.hashCode(this.internalComment);
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.costPrice) ^ (Double.doubleToLongBits(this.costPrice) >>> 32));
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.contractorReferencePrice) ^ (Double.doubleToLongBits(this.contractorReferencePrice) >>> 32));
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.referencePrice) ^ (Double.doubleToLongBits(this.referencePrice) >>> 32));
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.retailerPrice) ^ (Double.doubleToLongBits(this.retailerPrice) >>> 32));
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.customerPrice) ^ (Double.doubleToLongBits(this.customerPrice) >>> 32));
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.retailerToCustomerPricePercentage) ^ (Double.doubleToLongBits(this.retailerToCustomerPricePercentage) >>> 32));
        hash = 83 * hash + this.warrantyId;
        hash = 83 * hash + Objects.hashCode(this.mfgDate);
        hash = 83 * hash + Objects.hashCode(this.inputDate);
        hash = 83 * hash + Objects.hashCode(this.eol);
        hash = 83 * hash + Objects.hashCode(this.conditionLevel);
        hash = 83 * hash + Objects.hashCode(this.rulesLog);
        hash = 83 * hash + (this.error ? 1 : 0);
        hash = 83 * hash + (this.warning ? 1 : 0);
        hash = 83 * hash + Objects.hashCode(this.manufacturerPartPriceFixed);
        hash = 83 * hash + Objects.hashCode(this.unitPriceFixed);
        hash = 83 * hash + Objects.hashCode(this.dateFirstPriced);
        hash = 83 * hash + Objects.hashCode(this.special);
        hash = 83 * hash + Objects.hashCode(this.salesChannel);
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.tax) ^ (Double.doubleToLongBits(this.tax) >>> 32));
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.lastRetailerPrice) ^ (Double.doubleToLongBits(this.lastRetailerPrice) >>> 32));
        hash = 83 * hash + (int)(Double.doubleToLongBits(this.lastCustomerPrice) ^ (Double.doubleToLongBits(this.lastCustomerPrice) >>> 32));
        hash = 83 * hash + Objects.hashCode(this.warrentyValid);
        hash = 83 * hash + Objects.hashCode(this.stock);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final PriceEngineResult other = (PriceEngineResult)obj;
        if ( Double.doubleToLongBits(this.costPrice) != Double.doubleToLongBits(other.costPrice) ) return false;
        if ( Double.doubleToLongBits(this.contractorReferencePrice) != Double.doubleToLongBits(other.contractorReferencePrice) ) return false;
        if ( Double.doubleToLongBits(this.referencePrice) != Double.doubleToLongBits(other.referencePrice) ) return false;
        if ( Double.doubleToLongBits(this.retailerPrice) != Double.doubleToLongBits(other.retailerPrice) ) return false;
        if ( Double.doubleToLongBits(this.customerPrice) != Double.doubleToLongBits(other.customerPrice) ) return false;
        if ( Double.doubleToLongBits(this.retailerToCustomerPricePercentage) != Double.doubleToLongBits(other.retailerToCustomerPricePercentage) ) return false;
        if ( this.warrantyId != other.warrantyId ) return false;
        if ( this.error != other.error ) return false;
        if ( this.warning != other.warning ) return false;
        if ( Double.doubleToLongBits(this.tax) != Double.doubleToLongBits(other.tax) ) return false;
        if ( Double.doubleToLongBits(this.lastRetailerPrice) != Double.doubleToLongBits(other.lastRetailerPrice) ) return false;
        if ( Double.doubleToLongBits(this.lastCustomerPrice) != Double.doubleToLongBits(other.lastCustomerPrice) ) return false;
        if ( !Objects.equals(this.refurbishedId, other.refurbishedId) ) return false;
        if ( !Objects.equals(this.commodityGroup, other.commodityGroup) ) return false;
        if ( !Objects.equals(this.manufacturerPartNo, other.manufacturerPartNo) ) return false;
        if ( !Objects.equals(this.productName, other.productName) ) return false;
        if ( !Objects.equals(this.productDescription, other.productDescription) ) return false;
        if ( !Objects.equals(this.comment, other.comment) ) return false;
        if ( !Objects.equals(this.internalComment, other.internalComment) ) return false;
        if ( !Objects.equals(this.conditionLevel, other.conditionLevel) ) return false;
        if ( !Objects.equals(this.rulesLog, other.rulesLog) ) return false;
        if ( !Objects.equals(this.special, other.special) ) return false;
        if ( !Objects.equals(this.salesChannel, other.salesChannel) ) return false;
        if ( !Objects.equals(this.stock, other.stock) ) return false;
        if ( !Objects.equals(this.mfgDate, other.mfgDate) ) return false;
        if ( !Objects.equals(this.inputDate, other.inputDate) ) return false;
        if ( !Objects.equals(this.eol, other.eol) ) return false;
        if ( this.manufacturerPartPriceFixed != other.manufacturerPartPriceFixed ) return false;
        if ( this.unitPriceFixed != other.unitPriceFixed ) return false;
        if ( !Objects.equals(this.dateFirstPriced, other.dateFirstPriced) ) return false;
        if ( !Objects.equals(this.warrentyValid, other.warrentyValid) ) return false;
        return true;
    }
    //</editor-fold>
    
    @Override
    public int compareTo(PriceEngineResult o) {
        if ( o == null ) return 1;
        if ( this.productDescription == null && o.productDescription == null ) return 0;
        if ( this.productDescription == null ) return -1;
        if ( o.productDescription == null ) return 1;

        boolean price1Bad = false;
        boolean price2Bad = false;
        int result = 0;
        if ( this.retailerPrice <= 0 || this.retailerPrice > 10000 ) price1Bad = true;
        if ( o.retailerPrice <= 0 || o.retailerPrice > 10000 ) price2Bad = true;

        if ( price1Bad && price2Bad ) result = 0;
        else if ( price1Bad ) result = 1;
        else if ( price2Bad ) result = -1;
        if ( result == 0 ) result = this.commodityGroup.compareTo(o.commodityGroup);
        if ( result == 0 ) result = this.productName.compareTo(o.productName);
        if ( result == 0 ) result = this.manufacturerPartNo.compareTo(o.manufacturerPartNo);
        return result;
    }

    private double roundTo2Decimals(double price) {
        return Math.round(price * 100.0) / 100.0;
    }

    public static NavigableSet<String> toRefurbishIds(Collection<PriceEngineResult> pers) {
        NavigableSet<String> result = new TreeSet<>();
        for (PriceEngineResult per : pers) {
            result.add(per.getRefurbishedId());
        }
        return result;
    }

    public static NavigableSet<String> toPartNos(Collection<PriceEngineResult> pers) {
        NavigableSet<String> result = new TreeSet<>();
        for (PriceEngineResult per : pers) {
            result.add(per.getManufacturerPartNo());
        }
        return result;
    }
    
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
