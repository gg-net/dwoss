package eu.ggnet.saft.sample;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.Ops;
import eu.ggnet.saft.sample.search.*;
import eu.ggnet.saft.sample.support.MainPanelAddButtons;

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

            JMenuItem b = new JMenuItem("Open Search with Default");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> new SearchViewWithDefault());
            }));
            menu.add(b);

            b = new JMenuItem("Open Search with Context");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> new SearchViewWithSimpleContext());
            }));
            menu.add(b);

            b = new JMenuItem("Open Search with Everything");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> new SearchViewWithEverything());
            }));
            menu.add(b);

            main.getMenuBar().add(menu);

            menu = new JMenu("Viewers");

            b = new JMenuItem("Reactive Unit Viewer");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> new UnitDetailSelectivView());
            }));
            menu.add(b);

            b = new JMenuItem("Reactive Dossier Viewer");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> new DossierDetailSelectivView());
            }));
            menu.add(b);

            main.getMenuBar().add(menu);

            return main;
        });

        Ops.registerAction(new UnitDetailViewAction());
        Ops.registerAction(new ExtraUnitDetailViewAction());
        Ops.registerActionFactory(new UnitDependentActionFactory());
        Ops.registerAction(new DossierAction());

    }
}
