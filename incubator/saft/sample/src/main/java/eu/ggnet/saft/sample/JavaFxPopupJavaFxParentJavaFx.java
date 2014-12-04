package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.fx.FxSaft;
import eu.ggnet.saft.sample.aux.RevenueReportSelectorPane;
import eu.ggnet.saft.sample.aux.SimplePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import static javafx.scene.text.Font.font;

/**
 * Opening a JavaFX Pane as popup Dialog, blocking the hole application.
 *
 * @author oliver.guenther
 */
public class JavaFxPopupJavaFxParentJavaFx extends Application {

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        UiCore.startJavaFx(primaryStage, () -> new SimplePane());

        final Label l = new Label("Ein JavaFX Dialog");
        l.setFont(font(50));

        BorderPane p = new BorderPane(l);

        final Stage stage = FxSaft.dispatch(() -> {
            Stage s = new Stage();
            s.setTitle("Second MainStage");
            s.setX(400);
            s.setY(600);
            s.setScene(new Scene(p));
            s.sizeToScene();
            s.show();
            return s;
        });

        // JavaFX Pane in Swing Dialog.
        Ui.exec(Ui
                .parent(p)
                .choiceFx(RevenueReportSelectorPane.class)
                .onOk(v -> {
                    System.out.println(v);
                    return null;
                })
        );
    }

}
