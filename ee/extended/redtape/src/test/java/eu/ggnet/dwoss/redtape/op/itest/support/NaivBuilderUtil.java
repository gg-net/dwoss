package eu.ggnet.dwoss.redtape.op.itest.support;

import java.util.Random;

import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.TaxType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.util.TwoDigits;

import static eu.ggnet.dwoss.rules.PositionType.UNIT;
import static eu.ggnet.dwoss.uniqueunit.entity.PriceType.CUSTOMER;

/**
 *
 * @author oliver.guenther
 */
public class NaivBuilderUtil {

    private final static Random R = new Random();

    private static TaxType taxType = TaxType.GENERAL_SALES_TAX_DE_SINCE_2007;

    public static void overwriteTax(TaxType taxType) {
        NaivBuilderUtil.taxType = taxType;
    }

    /**
     * A navi way to create a position from a unit
     * <p>
     * @param uu the unit
     * @return a position
     */
    public static Position unit(UniqueUnit uu) {
        return Position.builder()
                .type(UNIT)
                .amount(1)
                .uniqueUnitId(uu.getId())
                .uniqueUnitProductId(uu.getProduct().getId())
                .price(uu.getPrice(CUSTOMER))
                .tax(taxType.getTax())
                .name(uu.getProduct().getName() + " | SN:" + uu.getIdentifier(UniqueUnit.Identifier.SERIAL))
                .description(uu.getProduct().getDescription())
                .build();
    }

    public static Position comment() {
        return comment("TestKommentar", "Ein Test Kommentar");
    }

    public static Position comment(String title, String description) {
        return Position.builder()
                .type(PositionType.COMMENT).tax(taxType.getTax()).name(title).description(description).build();
    }

    /**
     * Returns a serice position with generated price and amount.
     * <p>
     * @return a serice position with generated price and amount.
     */
    public static Position service() {
        return service((R.nextInt(100) + 1) * 0.25, TwoDigits.roundedApply(R.nextInt(10000) * R.nextDouble(), 0, 0));
    }

    public static Position service(double amount, double price) {
        return Position.builder()
                .type(PositionType.SERVICE)
                .name("Service")
                .amount(amount)
                .price(price)
                .tax(taxType.getTax())
                .description("Service Description")
                .build();
    }

    /**
     * Creates a Product Batch Position with generated price and amount.
     * <p>
     * @param product the product
     * @return a Product Batch Position with generated price and amount.
     */
    public static Position batch(Product product) {
        return batch(R.nextInt(20) + 1, TwoDigits.roundedApply(R.nextInt(1000) * R.nextDouble(), 0, 0), product);
    }

    public static Position batch(int amount, double price, Product product) {
        return Position.builder()
                .type(PositionType.PRODUCT_BATCH)
                .uniqueUnitProductId(product.getId())
                .amount(amount)
                .price(price)
                .tax(taxType.getTax())
                .description(product.getDescription())
                .name(product.getName())
                .build();
    }

    /**
     * Creates a Shipping Cost Position with generated price
     * <p>
     * @return a Shipping Cost Position with generated price
     */
    public static Position shippingcost() {
        return shippingcost(TwoDigits.roundedApply(R.nextInt(1000) * R.nextDouble(), 0, 0));
    }

    public static Position shippingcost(double price) {
        return Position.builder()
                .type(PositionType.SHIPPING_COST)
                .amount(1)
                .price(price)
                .tax(taxType.getTax())
                .description("Shipping costs")
                .name("Shipping costs")
                .build();
    }

}
