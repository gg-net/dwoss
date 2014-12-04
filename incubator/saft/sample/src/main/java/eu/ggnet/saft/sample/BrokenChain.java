package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.Ui;

/**
 * Shows a chain,that is broken through a null result.
 *
 * @author oliver.guenther
 */
public class BrokenChain {

    public static void main(String[] args) throws Exception {
        Ui.call(() -> {
            System.out.println("One");
            return "X";
        }).call(() -> {
            System.out.println("Two");
            return "Y";
        }).call(() -> {
            System.out.println("Three");
            return null;
        }).call(() -> {
            System.out.println("Will never be shown");
            return "X";
        }).call();
    }

}
