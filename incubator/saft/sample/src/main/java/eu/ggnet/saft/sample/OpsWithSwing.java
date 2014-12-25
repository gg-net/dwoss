package eu.ggnet.saft.sample;

import eu.ggnet.saft.sample.support.SimplePane;
import eu.ggnet.saft.sample.support.MainPanelAddButtons;
import eu.ggnet.saft.sample.support.PanelAsFrame;

import javax.swing.*;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.sample.search.SearchView;


/**
 * Opening a JavaFX Pane as popup Dialog, blocking the hole application.
 *
 * @author oliver.guenther
 */
public class OpsWithSwing {


    public static void main(String[] args) {
        UiCore.startSwing(() -> {
            MainPanelAddButtons main = new MainPanelAddButtons();

            JMenu menu = new JMenu("Search");

            JMenuItem b = new JMenuItem("Open Search");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(SearchView.class)));
            menu.add(b);

            main.getMenuBar().add(menu);

            menu = new JMenu("Viewers");

            b = new JMenuItem("Reactive Unit Viewer");
            b.addActionListener((e) -> Ui.exec(Ui.openSwing(PanelAsFrame.class)));
            menu.add(b);

            b = new JMenuItem("Reactive Dossier Viewer");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(SimplePane.class)));
            menu.add(b);

            main.getMenuBar().add(menu);

            return main;
        });

        // Ui.openSwing(UnitView.class,"12345").exec();
        // ui.openSwing(UnitView.class,"12345").prepare((UnitView v) -> v.setValue("lannnger String")).exec();
        // JavaFX Pane in Swing Dialog.
    }

}
