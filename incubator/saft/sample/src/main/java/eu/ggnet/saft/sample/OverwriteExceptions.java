package eu.ggnet.saft.sample;

import javax.swing.JOptionPane;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.sample.support.MainPanel;

/**
 * Example of overwriting the UI Exception handling.
 *
 * @author oliver.guenther
 */
public class OverwriteExceptions {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        UiCore.registerExceptionConsumer(IllegalArgumentException.class, (t) -> {
            JOptionPane.showMessageDialog(null, "Important:" + t.getClass().getSimpleName() + " : " + t.getMessage());
        });

        Ui.exec(() -> {
            throw new IllegalArgumentException("Sinnlos");
        });
    }

}
