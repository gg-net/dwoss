package eu.ggnet.saft.sample;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.sample.support.MainPanel;

/**
 * A Simple Exception handling Example, Exception is shown.
 *
 * @author oliver.guenther
 */
public class SimpleException {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        Ui.exec(() -> {
            throw new IllegalAccessException("Sinnlos");
        });
    }

}
