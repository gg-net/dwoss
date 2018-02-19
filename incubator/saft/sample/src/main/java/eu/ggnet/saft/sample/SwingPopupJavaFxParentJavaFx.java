package eu.ggnet.saft.sample;

import java.util.concurrent.ExecutionException;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.sample.support.MainPanel;
import eu.ggnet.saft.sample.support.RevenueReportSelectorPane;

import static javafx.scene.text.Font.font;

/**
 * Opening a JavaFX Pane as popup Dialog, blocking the hole application.
 *
 * @author oliver.guenther
 */
public class SwingPopupJavaFxParentJavaFx {

    public static class TestPane extends BorderPane implements ResultProducer<String> {

        private boolean ok = false;

        public TestPane() {
            Label l = new Label("Ein JavaFX Dialog");
            l.setFont(font(50));
            Button one = new Button("Open another Dialog");
            one.setOnAction((e) -> Ui.exec(() -> {
                Ui.build(l).fx().eval(() -> new RevenueReportSelectorPane()).opt().ifPresent(System.out::println);
            }));

            Button two = new Button("Ok (Close this dialog)");
            two.setOnAction(e -> {
                ok = true;
                Ui.closeWindowOf(this);
            });

            setCenter(l);
            setBottom(new FlowPane(one, two));
        }

        @Override
        public String getResult() {
            if ( ok ) return this.toString();
            return null;
        }

    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        UiCore.startSwing(() -> new MainPanel());

        // JavaFX Pane in Swing Dialog.
        Ui.exec(() -> {
            Ui.build().fx().eval(() -> new TestPane()).opt().ifPresent(System.out::println);
        });
    }

}
