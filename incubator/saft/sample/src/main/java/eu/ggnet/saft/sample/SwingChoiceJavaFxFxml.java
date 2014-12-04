package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.sample.aux.*;

/**
 * Opening a JavaFX Pane as popup Dialog, blocking the hole application.
 *
 * @author oliver.guenther
 */
public class SwingChoiceJavaFxFxml {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        // JavaFX Pane in Swing Dialog.
        Ui.exec(Ui.choiceFxml(SimpleFxmlController.class)
                .onOk(v -> {
                    System.out.println(v);
                    return null;
                })
        );

    }

}
