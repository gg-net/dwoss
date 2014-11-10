package eu.ggnet.dwoss.spec.entity;

import javax.swing.JOptionPane;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;



/**
 * Creates Offline Elements.
 *
 * @author oliver.guenther
 */
public class SpecOfflineConstructor {

    private static SpecOfflineConstructor instance;

    public long counter;

    private SpecOfflineConstructor() {
        String messages= "Warning: The " + this.getClass().getSimpleName() + " is in use.\n In a Prodcutive Environment, this will very probably cause Datalose!";
        JOptionPane.showMessageDialog(null, messages,"Warning, OfflineConstructor in use",JOptionPane.WARNING_MESSAGE);
        counter = 0;
    }

    public static SpecOfflineConstructor getInstance() {
        if (instance == null) {
            instance = new SpecOfflineConstructor();
        }
        return instance;
    }

    public BasicSpec newBasicSpec() {
        return new BasicSpec(counter++);
    }

    public Monitor newMonitor() {
      return new Monitor(counter++);
    }

    public ProductFamily newProductFamily() {
        return new ProductFamily(counter++);
    }

    public ProductModel newProductModel() {
        return new ProductModel(counter++);
    }

    public ProductSeries newProductSeries() {
        return new ProductSeries(counter++);
    }

    public ProductSeries newProductSeries(TradeName brand, ProductGroup group, String name) {
        ProductSeries series = new ProductSeries(counter++);
        series.setBrand(brand);
        series.setGroup(group);
        series.setName(name);
        return series;
    }

}
