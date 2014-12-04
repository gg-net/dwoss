package eu.ggnet.saft.sample;

import javax.swing.*;

import eu.ggnet.saft.core.Alert;

import static eu.ggnet.saft.core.UiAlert.Type.*;

/**
 *
 * @author oliver.guenther
 *
 * import static eu.ggnet.saft.core.UiAlert.Type.INFO;
 */
public class Alerts {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // Normal
        Alert.title("Nachricht").message("Eine Info").show(INFO);

        Alert.title("Nachricht").message("Eine Warnung").show(WARNING);

        Alert.title("Nachricht").message("Ein Fehler").show(ERROR);
        // Sehr lange Nachricht
        Alert.title("Lange Nachricth")
                .message("Erste Zeile")
                .nl("Zeite Zeile")
                .nl("Dritte Zeile")
                .show(INFO);

        // Short form
        Alert.show("Nachricht");

        JFrame f = new JFrame();
        f.getContentPane().add(new JLabel("Ein JFrame"));
        f.setLocationByPlatform(true);
        f.setSize(400, 400);
        f.setVisible(true);

        Alert.show(f, "Title", "relative Nachricht", INFO);

        f.setVisible(false);
        f.dispose();
    }

}
