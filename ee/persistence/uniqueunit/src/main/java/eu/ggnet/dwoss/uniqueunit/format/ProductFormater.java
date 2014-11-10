package eu.ggnet.dwoss.uniqueunit.format;

import java.util.Iterator;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.entity.PriceType;
import eu.ggnet.dwoss.uniqueunit.entity.Product;

/**
 * The ProductFormater.
 *
 * @author pascal.perau
 */
public class ProductFormater {

    /**
     * Returns a String like Acer Aspire 5473 (LX.AAAAA.012).
     *
     * @param product the product to format
     * @return the formated String.
     */
    public static String toNameWithPartNo(Product product) {
        if ( product == null ) return "ProductSpecification ist null";
        return product.getTradeName().getName() + " " + product.getName() + " (" + product.getPartNo() + ")";
    }

    public static String toName(Product product) {
        if ( product == null ) return "ProductSpecification ist null";
        return product.getTradeName().getName() + " " + product.getName();
    }

    public static String toDetailedName(Product product) {
        if ( product == null ) return "ProductSpecification ist null";
        return product.getGroup().getNote() + " - " + toName(product) + " (" + product.getPartNo() + ")";
    }

    public static String toSource(Product product) {
        String var = randomVar();
        String re = product.getClass().getSimpleName() + " " + var + " = new " + product.getClass().getName() + "();\n";
        for (TradeName id : product.getAdditionalPartNos().keySet()) {
            re += var + ".setAdditionalPartNo(TradeName." + id + ",\"" + product.getAdditionalPartNo(id) + "\");\n";
        }
        for (PriceType id : product.getPrices().keySet()) {
            re += var + ".setPrice(PriceType." + id + "," + product.getPrice(id) + ",\"formToSource\");\n";
        }
        if ( !product.getFlags().isEmpty() ) {
            re += var + ".setFlags(EnumSet.of(";
            for (Iterator<Product.Flag> it = product.getFlags().iterator(); it.hasNext();) {
                Product.Flag flag = it.next();
                re += "Product.Flag." + flag;
                if ( it.hasNext() ) re += ",";
            }
            re += "));\n";
        }
        if ( product.getName() != null ) re += var + ".setName(\"" + product.getName() + "\");\n";
        if ( product.getDescription() != null ) re += var + ".setDescription(\"" + product.getDescription() + "\");\n";
        if ( product.getPartNo() != null ) re += var + ".setPartNo(\"" + product.getPartNo() + "\");\n";
        re += var + ".setGroup(ProductGroup." + product.getGroup() + ");\n";
        re += var + ".setTradeName(TradeName." + product.getTradeName() + ");\n";
        return re;
    }

    private static String randomVar() {
        return "" + (char)(97 + (Math.random() * ((122 - 97) + 1)))
                + (char)(97 + (Math.random() * ((122 - 97) + 1)))
                + (char)(97 + (Math.random() * ((122 - 97) + 1)));
    }
}
