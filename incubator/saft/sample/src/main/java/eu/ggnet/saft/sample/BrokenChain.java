package eu.ggnet.saft.sample;

import java.util.Optional;

import eu.ggnet.saft.core.Ui;

/**
 * Shows a chain,that is broken through a null result.
 *
 * @author oliver.guenther
 */
public class BrokenChain {

    public static void main(String[] args) {
        optionalSteam();
    }

    public static void optionalSteam() {

        Optional.of(true).map(v -> {
            System.out.println("One");
            return "X";
        }).map(v -> {
            System.out.println("Two");
            return "Y";
        }).map(v -> {
            System.out.println("Three");
            return null; // Return null, breaks the chain/stream
        }).map(v -> {
            System.out.println("Will never be shown");
            return "X";
        }).ifPresent(v -> System.out.print("Ende"));

    }

    public static void saftChain() throws Exception {
        Ui.call(() -> {
            System.out.println("One");
            return "X";
        }).call(() -> {
            System.out.println("Two");
            return "Y";
        }).call(() -> {
            System.out.println("Three");
            return null; // Return null, breaks the chain/stream
        }).call(() -> {
            System.out.println("Will never be shown");
            return "X";
        }).call();
    }

}
