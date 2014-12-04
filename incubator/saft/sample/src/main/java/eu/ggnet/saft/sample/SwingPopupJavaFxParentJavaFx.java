package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.sample.aux.MainPanel;
import eu.ggnet.saft.sample.aux.RevenueReportSelectorPane;

import java.util.concurrent.ExecutionException;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import static javafx.scene.text.Font.font;

/**
 * Opening a JavaFX Pane as popup Dialog, blocking the hole application.
 *
 * @author oliver.guenther
 */
public class SwingPopupJavaFxParentJavaFx {

    public static class TestPane extends BorderPane {

        public TestPane() {
            Label l = new Label("Ein JavaFX Dialog");
            l.setFont(font(50));
            Button b = new Button("Open another Dialog");
            b.setOnAction((e) -> {
                Ui.exec(Ui
                        .parent(l)
                        .choiceFx(RevenueReportSelectorPane.class)
                        .onOk(v -> {
                            System.out.println(v);
                            return null;
                        })
                );
            });

            BorderPane p = new BorderPane(l);
            p.setBottom(b);
        }

    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        UiCore.startSwing(() -> new MainPanel());

        // JavaFX Pane in Swing Dialog.
        Ui.exec(Ui
                .choiceFx(TestPane.class)
                .onOk(v -> {
                    System.out.println(v.getId());
                    return null;
                })
        );

    }

}
