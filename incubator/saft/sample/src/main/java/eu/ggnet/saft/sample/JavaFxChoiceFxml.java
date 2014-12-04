package eu.ggnet.saft.sample;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.sample.aux.*;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Opening a JavaFX Pane, build from Fxml and Controller as popup Dialog, blocking the hole application.
 *
 * @author oliver.guenther
 */
public class JavaFxChoiceFxml extends Application {

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        UiCore.startJavaFx(primaryStage, () -> new SimplePane());

        // JavaFX Pane in Swing Dialog.
        Ui.exec(Ui.choiceFxml(SimpleFxmlController.class)
                .onOk(v -> {
                    System.out.println(v);
                    return null;
                })
        );
    }

}
