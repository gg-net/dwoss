/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tryout.stub;

import java.util.*;

import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.uniqueunit.assist.gen.ProductGenerator;
import eu.ggnet.dwoss.uniqueunit.entity.CategoryProduct;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;

/**
 * A class that allows to create products with random predefined values.
 *
 * @author lucas.huelsen
 */
public class CategoryProductGenerator {

    private final Random rand = new Random();

    private final List<String> names = new ArrayList<>(Arrays.asList("A41515CP", "D85412CP", "B5458CP", "F65487CP", "S45656CP", "P464CP", "I4445CP", "V5461CP", "C4785CP"));

    private final List<SalesChannel> salesChannels = new ArrayList<>(Arrays.asList(SalesChannel.values()));

    private final List<PriceType> priceTypes = new ArrayList<>(Arrays.asList(PriceType.values()));

    private final List<String> descriptions = new ArrayList<>(Arrays.asList("test", "aaaaaa", "bbbbbb", "ccccccc", "ddddddd"));

    private ProductGenerator pGen = new ProductGenerator();

    public CategoryProductGenerator() {
    }

    /**
     * Generate a specific amount of categoryProducts with random data.
     *
     * @param amount of products that get created
     * @return list of CategoryProducts
     */
    public List<CategoryProduct> generateCategoryProduct(int amount) {
        List<CategoryProduct> categoryProducts = new ArrayList<>();
        for (int i = 0; i < amount; i++) {

            //prepare category product
            CategoryProduct c = new CategoryProduct();
            c.setName(names.get(rand.nextInt(names.size())));
            c.setSalesChannel(salesChannels.get(rand.nextInt(salesChannels.size())));
            c.setPrice(priceTypes.get(rand.nextInt(priceTypes.size())), rand.nextInt(5000), "");
            c.setPrice(priceTypes.get(rand.nextInt(priceTypes.size())), rand.nextInt(5000), "");
            c.setDescription(descriptions.get(rand.nextInt(descriptions.size())));

            c.getProducts().add(pGen.generateProduct(1).get(0));

            categoryProducts.add(c);
        }
        return categoryProducts;
    }

}
