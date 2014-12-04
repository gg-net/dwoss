package eu.ggnet.saft.sample;

import javax.swing.JOptionPane;
import eu.ggnet.saft.sample.aux.MainPanel;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 * A Simple Exception handling Example.
 *
 * @author oliver.guenther
 */
public class OverwriteExceptions {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());
        UiCore.registerExceptionConsumer(IllegalArgumentException.class, (t) -> {
            JOptionPane.showMessageDialog(null, "Important:" + t.getClass().getSimpleName() + " : " + t.getMessage());
        });

        Ui.exec(Ui.call(() -> {
                    throw new IllegalArgumentException("Sinnlos");
                })
        );
    }

}
