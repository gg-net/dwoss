/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.price.engine;

import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.PriceHistory;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.price.engine.support.TraceCollector;
import eu.ggnet.dwoss.uniqueunit.format.ProductFormater;
import eu.ggnet.dwoss.uniqueunit.format.UniqueUnitFormater;

import lombok.Data;

import static eu.ggnet.dwoss.price.api.EngineTracer.Status.*;

/**
 * ValueObject for Import and Export of PriceEngine and the Database
 *
 * @author oliver.guenther
 */
@Data
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

    public void consumeLog(TraceCollector collector) {
        rulesLog = collector.getMessages();
        if ( collector.getStatus() == WARNING ) warning = true;
        if ( collector.getStatus() == ERROR ) error = true;
    }

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
}
