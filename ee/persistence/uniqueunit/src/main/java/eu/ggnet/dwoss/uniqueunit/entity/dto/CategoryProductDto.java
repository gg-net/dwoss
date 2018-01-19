/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.entity.dto;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;

import lombok.Data;

/**
 *
 * @author lucas.huelsen
 */
@Data
public class CategoryProductDto implements Serializable {

    private long id;

    private String name;

    private SalesChannel salesChannel;

    private String description;

    private List<PicoProduct> products = new ArrayList<>();

    private Map<PriceType, Double> prices = new HashMap<>();

}
