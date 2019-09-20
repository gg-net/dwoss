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
package eu.ggnet.dwoss.stock.ee.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import eu.ggnet.dwoss.common.api.values.SalesChannel;
import eu.ggnet.dwoss.stock.ee.entity.Stock;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/**
 *
 * @author oliver.guenther
 */
/*
TODO: Convert this class to freebuilder or something completely different
As u can see in the swing Ui components, the some setters are used. So we still need a pojo.
*/
public class SalesChannelLine implements Serializable {

    // Refactor to stockUnitId;
    private final int unitId;

    private final String refurbishedId;

    private String description;

    private String stockName;

    private double retailerPrice;

    private double customerPrice;

    private final SalesChannel originalSalesChannel;

    private SalesChannel salesChannel;

    private int stockId;

    private Stock destination;

    private String comment;

    public SalesChannelLine(int unitId, String refurbishedId, String description, String stockName, double retailerPrice, double customerPrice, SalesChannel originalSalesChannel, SalesChannel salesChannel, int stockId, Stock destinationStock, String comment) {
        this.unitId = unitId;
        this.refurbishedId = refurbishedId;
        this.description = description;
        this.stockName = stockName;
        this.retailerPrice = retailerPrice;
        this.customerPrice = customerPrice;
        this.originalSalesChannel = originalSalesChannel;
        this.salesChannel = salesChannel;
        this.stockId = stockId;
        this.destination = destinationStock;
        this.comment = comment;
    }

    public SalesChannelLine(int unitId, String refurbishedId, String description, String comment, double retailerPrice, double customerPrice, String stockName, SalesChannel salesChanel, int stockId) {
        this.unitId = unitId;
        this.refurbishedId = refurbishedId;
        this.description = description;
        if ( salesChanel == null ) this.salesChannel = SalesChannel.UNKNOWN;
        else this.salesChannel = salesChanel;
        this.originalSalesChannel = this.salesChannel;
        this.stockId = stockId;
        this.stockName = stockName;
        this.retailerPrice = retailerPrice;
        this.customerPrice = customerPrice;
        this.comment = comment;
    }

    public boolean hasChanged() {
        return originalSalesChannel != salesChannel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public double getRetailerPrice() {
        return retailerPrice;
    }

    public void setRetailerPrice(double retailerPrice) {
        this.retailerPrice = retailerPrice;
    }

    public double getCustomerPrice() {
        return customerPrice;
    }

    public void setCustomerPrice(double customerPrice) {
        this.customerPrice = customerPrice;
    }

    public SalesChannel getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(SalesChannel salesChannel) {
        this.salesChannel = salesChannel;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public Stock getDestination() {
        return destination;
    }

    public void setDestination(Stock destination) {
        this.destination = destination;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getUnitId() {
        return unitId;
    }

    public String getRefurbishedId() {
        return refurbishedId;
    }

    public SalesChannel getOriginalSalesChannel() {
        return originalSalesChannel;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,SHORT_PREFIX_STYLE);
    }
    
}
