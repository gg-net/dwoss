/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.entity.dto;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import java.io.Serializable;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import lombok.Data;

/**
 *
 * @author jacob.weinhold
 */
@Data
public class ProductDto implements Serializable {

    private long id;

    private short optLock;

    private String name;

    private String description;

    private TradeName tradeName;

    private ProductGroup group;

    private String partNo;

    private Date eol;

    private Map<TradeName, String> additionalPartNo = new EnumMap<>(TradeName.class);

    private int imageId;

    private long gtin;

    private Map<PriceType, Double> prices = new EnumMap<>(PriceType.class);

    private SalesChannel salesChannel = SalesChannel.UNKNOWN;

}
