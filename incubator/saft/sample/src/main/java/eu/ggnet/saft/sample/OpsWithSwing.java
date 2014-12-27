package eu.ggnet.saft.sample;

import javax.swing.*;

import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.ops.Ops;
import eu.ggnet.saft.sample.search.*;
import eu.ggnet.saft.sample.support.*;

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
            b.addActionListener((e) -> Ui.exec(Ui.openFx(SearchViewWithDefault.class)));
            menu.add(b);

            b = new JMenuItem("Open Search with Context");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(SearchViewWithSimpleContext.class)));
            menu.add(b);

            b = new JMenuItem("Open Search with Everything");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(SearchViewWithEverything.class)));
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

        Ops.register(new UnitDetailViewAction());
        Ops.register(new ExtraUnitDetailViewAction());
        Ops.register(new UnitDependentActionFactory());
        Ops.register(new DossierAction());

    }
}
