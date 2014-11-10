package eu.ggnet.dwoss.stock.model;

import eu.ggnet.dwoss.rules.SalesChannel;

import java.io.Serializable;

import eu.ggnet.dwoss.stock.entity.Stock;

import lombok.*;
import lombok.experimental.Builder;

/**
 *
 * @author oliver.guenther
 */
@Getter
@Setter
@ToString
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

    @Builder
    private SalesChannelLine(int unitId, String refurbishedId, String description, String stockName, double retailerPrice, double customerPrice, SalesChannel originalSalesChannel, SalesChannel salesChannel, int stockId, Stock destinationStock, String comment) {
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

}
