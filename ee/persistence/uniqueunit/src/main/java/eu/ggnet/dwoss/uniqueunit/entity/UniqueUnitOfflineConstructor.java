package eu.ggnet.dwoss.uniqueunit.entity;

import java.util.Date;

import javax.swing.JOptionPane;

/**
 * Creates Offline Elements.
 *
 * @author oliver.guenther
 */
public class UniqueUnitOfflineConstructor {

    private static UniqueUnitOfflineConstructor instance;

    public int counter;

    private UniqueUnitOfflineConstructor() {
        String messages= "Warning: The " + this.getClass().getSimpleName() + " is in use.\n In a Prodcutive Environment, this will very probably cause Datalose!";
        JOptionPane.showMessageDialog(null, messages,"Warning, OfflineConstructor in use",JOptionPane.WARNING_MESSAGE);
        counter = 0;
    }

    public static UniqueUnitOfflineConstructor getInstance() {
        if (instance == null) {
            instance = new UniqueUnitOfflineConstructor();
        }
        return instance;
    }

    public UniqueUnit newUniqueUnit(Date mfgDate, UniqueUnit.Condition condition) {
        UniqueUnit uu = new UniqueUnit(counter++);
        uu.setMfgDate(mfgDate);
        uu.setCondition(condition);
        return uu;
    }

    public Product newProduct() {
        return new Product();
    }

}
