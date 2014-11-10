package eu.ggnet.dwoss.stock.entity;

import javax.swing.JOptionPane;

/**
 * Creates Offline Elements.
 *
 * @author oliver.guenther
 */
public class StockOfflineConstructor {

    private static StockOfflineConstructor instance;

    public int counter;

    private StockOfflineConstructor() {
        String messages= "Warning: The " + this.getClass().getSimpleName() + " is in use.\n In a Prodcutive Environment, this will very probably cause Datalose!";
        JOptionPane.showMessageDialog(null, messages,"Warning, OfflineConstructor in use",JOptionPane.WARNING_MESSAGE);
        counter = 0;
    }

    public static StockOfflineConstructor getInstance() {
        if (instance == null) {
            instance = new StockOfflineConstructor();
        }
        return instance;
    }

    public StockUnit newStockUnit(String unitId, String name) {
        StockUnit stockUnit = new StockUnit(counter++);
        stockUnit.setName(name);
        stockUnit.setRefurbishId(unitId);
        return stockUnit;
    }

}
