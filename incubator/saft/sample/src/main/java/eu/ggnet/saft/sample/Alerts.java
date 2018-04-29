package eu.ggnet.saft.sample;

import javax.swing.*;

import eu.ggnet.saft.core.Ui;

import static eu.ggnet.saft.core.ui.AlertType.*;

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
        Ui.build().alert().title("Nachricht").message("Eine Info").show(INFO);

        Ui.build().alert().title("Nachricht").message("Eine Warnung").show(WARNING);

        Ui.build().alert().title("Nachricht").message("Ein Fehler").show(ERROR);
        // Sehr lange Nachricht
        Ui.build().alert().title("Lange Nachricth")
                .message("Erste Zeile")
                .nl("Zeite Zeile")
                .nl("Dritte Zeile")
                .show(INFO);

        JFrame f = new JFrame();
        f.getContentPane().add(new JLabel("Ein JFrame"));
        f.setLocationByPlatform(true);
        f.setSize(400, 400);
        f.setVisible(true);

        Ui.build(f).alert().title("Title").message("relative Nachricht").show();

        f.setVisible(false);
        f.dispose();
    }

}
