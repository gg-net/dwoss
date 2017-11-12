package eu.ggnet.saft.sample;

import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.Ui;

import java.util.Random;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import eu.ggnet.saft.api.ui.IdSupplier;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.experimental.SwingBuilder;
import eu.ggnet.saft.sample.support.*;

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
            b.addActionListener((e) -> Ui.exec(() -> {
                SwingBuilder swing = Ui.swing();
                swing.show(() -> new PanelOnceDialog());
            }));
            menu.add(b);

            b = new JMenuItem("Multiple : 1");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.swing().id("1").show(() -> new UnitViewer());
            }));
            menu.add(b);

            b = new JMenuItem("Multiple : 2");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.swing().id("2").show(() -> new UnitViewer());
            }));
            menu.add(b);

            b = new JMenuItem("Multiple : 3 , with precall");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.swing().once(false).show(() -> "Das ist der Riesentext für Unit 3", () -> new UnitViewer());
            }));
            menu.add(b);

            b = new JMenuItem("SelfCloser");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.swing().show(() -> new PanelWithSelfCloser());
            }));
            menu.add(b);

            main.getMenuBar().add(menu);

            menu = new JMenu("SwingFrames");

            b = new JMenuItem("Once Frame with ClosedListener");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.swing().show(() -> new PanelAsFrame());
            }));
            menu.add(b);

            main.getMenuBar().add(menu);

            menu = new JMenu("JavaFxDialogs");

            b = new JMenuItem("Once");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> new SimplePane());
            }));
            menu.add(b);

            b = new JMenuItem("Mutiple 1 with Title");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().id("1").show(() -> new SimplePane());
            }));
            menu.add(b);

            b = new JMenuItem("Mutiple 2 with Title");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().id("2").show(() -> new SimplePane());
            }));
            menu.add(b);

            b = new JMenuItem("HtmlPane");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> "<h1>Ueberschrift</h1>", () -> new HtmlPane());
            }));
            menu.add(b);

            b = new JMenuItem("Once InputPane via Fxml ");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fxml().eval(SimpleFxmlController.class).ifPresent(t -> Alert.show("Ok pressed with Input: " + t));
            }));
            menu.add(b);
            main.getMenuBar().add(menu);

            b = new JMenuItem("Consumer with Random Id Supplier");
            b.addActionListener((e) -> {
                Ui.exec(() -> {
                    Ui.fx().show(() -> new S(NAMES[R.nextInt(NAMES.length)]), () -> new PaneConsumesIdSupplier());
                });
            });
            menu.add(b);

            menu = new JMenu("JavaFxFrames");

            b = new JMenuItem("Once");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> new PaneAsFrame());
            }));
            menu.add(b);

            b = new JMenuItem("Once With Self Closer");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fx().show(() -> new PaneAsFrameWithSelfCloser());
            }));
            menu.add(b);

            b = new JMenuItem("Once Fxml");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.fxml().show(BasicApplicationController.class);
            }));
            menu.add(b);
            main.getMenuBar().add(menu);

            return main;
        });

    }

}
