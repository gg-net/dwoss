package eu.ggnet.saft.sample;

import eu.ggnet.saft.sample.support.SimplePane;
import eu.ggnet.saft.sample.support.BasicApplicationController;
import eu.ggnet.saft.sample.support.PanelWithSelfCloser;
import eu.ggnet.saft.sample.support.UnitViewer;
import eu.ggnet.saft.sample.support.MainPanelAddButtons;
import eu.ggnet.saft.sample.support.PanelAsFrame;
import eu.ggnet.saft.sample.support.PaneAsFrameWithSelfCloser;
import eu.ggnet.saft.sample.support.PaneConsumesIdSupplier;
import eu.ggnet.saft.sample.support.SimpleFxmlController;
import eu.ggnet.saft.sample.support.PanelOnceDialog;
import eu.ggnet.saft.sample.support.PaneAsFrame;

import java.util.Random;

import javax.swing.*;

import eu.ggnet.saft.api.ui.IdSupplier;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

import lombok.Value;

/**
 * Opening a JavaFX Pane as popup Dialog, blocking the hole application.
 *
 * @author oliver.guenther
 */
public class OpenWithSwing {

    private final static String[] NAMES = {"Hans", "Klaus", "Horst", "Charlotte", "Caroline", "Ivet"};

    private final static Random R = new Random();

    @Value
    public final static class S implements IdSupplier {

        private final String id;

        @Override
        public String id() {
            return id;
        }

    }

    public static void main(String[] args) {
        UiCore.startSwing(() -> {
            MainPanelAddButtons main = new MainPanelAddButtons();

            JMenu menu = new JMenu("SwingDialogs");

            JMenuItem b = new JMenuItem("Once");
            b.addActionListener((e) -> Ui.exec(Ui.openSwing(PanelOnceDialog.class)));
            menu.add(b);

            b = new JMenuItem("Multiple : 1");
            b.addActionListener((e) -> Ui.exec(Ui.openSwing(UnitViewer.class, "1")));
            menu.add(b);

            b = new JMenuItem("Multiple : 2");
            b.addActionListener((e) -> Ui.exec(Ui.openSwing(UnitViewer.class, " 2")));
            menu.add(b);

            b = new JMenuItem("Multiple : 3 , with precall");
            b.addActionListener((e) -> Ui.exec(Ui
                    .call(() -> "Das ist der Riesentext für Unit 3")
                    .openSwing(UnitViewer.class, "3")
            ));
            menu.add(b);

            b = new JMenuItem("SelfCloser");
            b.addActionListener((e) -> Ui.openSwing(PanelWithSelfCloser.class).exec());
            menu.add(b);

            main.getMenuBar().add(menu);

            menu = new JMenu("SwingFrames");

            b = new JMenuItem("Once");
            b.addActionListener((e) -> Ui.exec(Ui.openSwing(PanelAsFrame.class)));
            menu.add(b);

            main.getMenuBar().add(menu);

            menu = new JMenu("JavaFxDialogs");

            b = new JMenuItem("Once");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(SimplePane.class)));
            menu.add(b);

            b = new JMenuItem("Mutiple 1 with Title");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(SimplePane.class, "1")));
            menu.add(b);

            b = new JMenuItem("Mutiple 2 with Title");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(SimplePane.class, "2")));
            menu.add(b);

            b = new JMenuItem("Once Fxml");
            b.addActionListener((e) -> Ui.exec(Ui.openFxml(SimpleFxmlController.class)));
            menu.add(b);
            main.getMenuBar().add(menu);

            b = new JMenuItem("Consumer with Random Id Supplier");
            b.addActionListener((e) -> {
                Ui.call(() -> new S(NAMES[R.nextInt(NAMES.length)])).openFx(PaneConsumesIdSupplier.class).exec();
            });
            menu.add(b);

            menu = new JMenu("JavaFxFrames");

            b = new JMenuItem("Once");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(PaneAsFrame.class)));
            menu.add(b);

            b = new JMenuItem("Once With Self Closer");
            b.addActionListener((e) -> Ui.exec(Ui.openFx(PaneAsFrameWithSelfCloser.class)));
            menu.add(b);

            b = new JMenuItem("Once Fxml");
            b.addActionListener((e) -> Ui.exec(Ui.openFxml(BasicApplicationController.class)));
            menu.add(b);
            main.getMenuBar().add(menu);

            return main;
        });

        // Ui.openSwing(UnitView.class,"12345").exec();
        // ui.openSwing(UnitView.class,"12345").prepare((UnitView v) -> v.setValue("lannnger String")).exec();
        // JavaFX Pane in Swing Dialog.
    }

}
