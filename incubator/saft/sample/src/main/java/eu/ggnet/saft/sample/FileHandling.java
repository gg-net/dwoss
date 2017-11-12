package eu.ggnet.saft.sample;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.sample.support.MainPanel;

/**
 * Shows a file handling.
 *
 * @author oliver.guenther
 */
public class FileHandling {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        Ui.exec(() -> {
            Ui.fileChooser().open().ifPresent(f -> System.out.println("Ok pressed, File: " + f.getAbsolutePath()));
        });

    }

}
