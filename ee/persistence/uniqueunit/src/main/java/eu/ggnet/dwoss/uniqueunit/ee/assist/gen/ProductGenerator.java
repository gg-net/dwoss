/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.ggnet.dwoss.uniqueunit.ee.assist.gen;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.uniqueunit.ee.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;

/**
 * A class that allows to create products with random predefined values.
 *
 * @author lucas.huelsen
 */
public class ProductGenerator {

    public ProductGenerator() {
    }

    private final Random rand = new Random();

    private final LocalDateTime now = LocalDateTime.now();

    private final List<Date> dates = new ArrayList<>(Arrays.asList(Date.from(now.minusDays(1).toInstant(ZoneOffset.UTC)), Date.from(now.plusDays(1).toInstant(ZoneOffset.UTC)), Date.from(now.toInstant(ZoneOffset.UTC))));

    private final List<PriceType> priceTypes = new ArrayList<>(Arrays.asList(PriceType.values()));

    private final List<ProductGroup> productGroups = new ArrayList<>(Arrays.asList(ProductGroup.values()));

    private final List<TradeName> tradeNames = new ArrayList<>(Arrays.asList(TradeName.values()));

    private final List<String> names = new ArrayList<>(Arrays.asList("A41515", "D85412", "B5458", "F65487", "S45656", "P464", "I4445", "V5461", "C4785"));

    /**
     * Generate a specific amount of products with random data.
     *
     * @param amount of products that get created
     * @return list of Product
     */
    public List<Product> generateProduct(int amount) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Product p = new Product(
                    productGroups.get(rand.nextInt(productGroups.size())),
                    tradeNames.get(rand.nextInt(tradeNames.size())),
                    "" + rand.nextInt(15000),
                    names.get(rand.nextInt(names.size())));
            p.setImageId(rand.nextInt(15000));
            p.setGtin(rand.nextInt(15000));
            p.setEol(dates.get(rand.nextInt(dates.size())));
            p.setPrice(priceTypes.get(rand.nextInt(priceTypes.size())), rand.nextInt(9999), "");
            products.add(p);
        }
        return products;
    }

}
