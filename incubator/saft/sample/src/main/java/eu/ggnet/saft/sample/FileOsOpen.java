package eu.ggnet.saft.sample;

import eu.ggnet.saft.sample.aux.MainPanel;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

/**
 * Shows a file handling.
 *
 * @author oliver.guenther
 */
public class FileOsOpen {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        Ui.exec(Ui.openFileChooser("Bitte Datei auswählen, die das Betriebsystem öffnen kann")
                .onOk(f -> f) // Push value though.
                .osOpen()
        );

    }

}
