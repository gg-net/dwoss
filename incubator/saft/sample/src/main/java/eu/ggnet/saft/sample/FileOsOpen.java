package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.sample.support.MainPanel;

/**
 * Shows a file handling.
 *
 * @author oliver.guenther
 */
public class FileOsOpen {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        // New Stype
        Ui.exec(() -> {
            Ui.fileChooser().title("Bitte Datei auswählen, die das Betriebsystem öffnen kann").open()
                    .ifPresent(file -> Ui.osOpen(file));
        });

        // Old style
//        Ui.exec(Ui.openFileChooser("Bitte Datei auswählen, die das Betriebsystem öffnen kann")
//                .onOk(f -> f) // Push value though.
//                .osOpen()
//        );
    }

}
