package eu.ggnet.saft.sample;

import eu.ggnet.saft.sample.support.MainPanel;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 * A Simple Exception handling Example.
 *
 * @author oliver.guenther
 */
public class SimpleException {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        Ui.exec(Ui.call(() -> {
                    throw new IllegalAccessException("Sinnlos");
                })
        );
    }

}
