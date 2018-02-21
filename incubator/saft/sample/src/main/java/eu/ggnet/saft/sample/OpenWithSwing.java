package eu.ggnet.saft.sample;

import java.util.Random;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import javafx.scene.control.Alert;
import javafx.stage.Modality;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.api.ui.IdSupplier;
import eu.ggnet.saft.api.ui.StoreLocation;
import eu.ggnet.saft.core.ui.builder.SwingBuilder;
import eu.ggnet.saft.sample.support.*;

import lombok.Value;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

/**
 * Opening a JavaFX Pane import static javafx.stage.Modality.APPLICATION_MODAL;
 * as popup Dialog
 * import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
 * , blocking the hole application.
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
                Ui.build().swing().show(() -> new PanelOnceDialog());
            }));
            menu.add(b);

            b = new JMenuItem("Multiple : 1");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().once(true).id("1").swing().show(() -> new UnitViewer());
            }));
            menu.add(b);

            b = new JMenuItem("Multiple : 2");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().once(true).id("2").swing().show(() -> new UnitViewer());
            }));
            menu.add(b);

            b = new JMenuItem("Multiple : 3 , with precall");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().swing().show(() -> "Das ist der Riesentext für Unit 3", () -> new UnitViewer());
            }));
            menu.add(b);

            b = new JMenuItem("SelfCloser");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().swing().show(() -> new PanelWithSelfCloser());
            }));
            menu.add(b);

            main.getMenuBar().add(menu);

            menu = new JMenu("SwingFrames");

            b = new JMenuItem("Once Frame with ClosedListener");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().swing().show(() -> new PanelAsFrame());
            }));
            menu.add(b);

            main.getMenuBar().add(menu);

            menu = new JMenu("JavaFxDialogs");

            b = new JMenuItem("Once + Store Location");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().fx().show(() -> new SimplePane());
            }));
            menu.add(b);

            b = new JMenuItem("Mutiple 1 with Title + Store Location");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().id("1").fx().show(() -> new SimplePane());
            }));
            menu.add(b);

            b = new JMenuItem("Mutiple 2 with Title + Store Location");
            b.addActionListener((e) -> Ui.exec(() -> {

                SimplePane pane = new SimplePane();
                System.out.println("test2 = " + pane.getClass().getAnnotation(StoreLocation.class) != null);

                Ui.build().id("2").fx().show(() -> pane);
            }));
            menu.add(b);

            b = new JMenuItem("HtmlPane");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().fx().show(() -> "<h1>Ueberschrift</h1>", () -> new HtmlPane());
            }));
            menu.add(b);

            b = new JMenuItem("Once InputPane via Fxml ");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().fxml().eval(SimpleFxmlController.class).opt().ifPresent(t -> Ui.build().alert().message("Ok pressed with Input: " + t).show());
            }));
            menu.add(b);

            b = new JMenuItem("Ui.build.dialog with javfx Alert");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().dialog().eval(() -> new Alert(CONFIRMATION, "Bitte eine Knopf drücken")).opt().ifPresent(t -> Ui.build().alert().message("Result: " + t).show());
            }));
            menu.add(b);

            b = new JMenuItem("Ui.build.dialog with javfx Alert (window modal)");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().modality(Modality.WINDOW_MODAL).dialog().eval(() -> new Alert(CONFIRMATION, "Bitte eine Knopf drücken")).opt().ifPresent(t -> Ui.build().alert().message("Ok pressed with Input: " + t).show());
            }));
            menu.add(b);

            main.getMenuBar().add(menu);

            b = new JMenuItem("Consumer with Random Id Supplier");
            b.addActionListener((e) -> {
                Ui.exec(() -> {
                    Ui.build().fx().show(() -> new S(NAMES[R.nextInt(NAMES.length)]), () -> new PaneConsumesIdSupplier());
                });
            });
            menu.add(b);

            menu = new JMenu("JavaFxFrames");

            b = new JMenuItem("Once");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().fx().show(() -> new PaneAsFrame());
            }));
            menu.add(b);

            b = new JMenuItem("Once With Self Closer");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().fx().show(() -> new PaneAsFrameWithSelfCloser());
            }));
            menu.add(b);

            b = new JMenuItem("Once Fxml");
            b.addActionListener((e) -> Ui.exec(() -> {
                Ui.build().fxml().show(BasicApplicationController.class);
            }));
            menu.add(b);
            main.getMenuBar().add(menu);

            return main;
        });

    }

}
