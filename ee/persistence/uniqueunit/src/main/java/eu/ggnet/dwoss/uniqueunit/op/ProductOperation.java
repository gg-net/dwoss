package eu.ggnet.dwoss.uniqueunit.op;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitSupport;
import eu.ggnet.dwoss.uniqueunit.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

/**
 * ProductOperation.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class ProductOperation {

    @Inject
    private UniqueUnitSupport uus;

    /**
     * Update a price of a product by id.
     * <p/>
     * @param productId the product id
     * @param priceType the type of price to update
     * @param price     the price
     * @param comment   the comment for the price history
     * @return the updated product
     */
    public Product updatePrice(long productId, PriceType priceType, double price, String comment) {
        Product product = new ProductEao(uus.getEntityManager()).findById(productId);
        if ( product == null ) return null;
        product.setPrice(priceType, price, comment);
        return product;
    }
}
