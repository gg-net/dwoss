package eu.ggnet.saft.sample;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.sample.support.MainPanel;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

/**
 * Opening a JavaFX Pane as popup Dialog, blocking the hole application.
 * See the javafx Dialog for more details.
 *
 * @author oliver.guenther
 */
public class SwingJavaFxDialog {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        Ui.build().dialog().eval(() -> {
            Dialog<String> dialog = new Dialog<>();
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField username = new TextField();
            username.setPromptText("Username");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);

            dialog.setResultConverter(buttonType -> {
                if ( buttonType.equals(OK) ) return username.getText();
                return null;
            });

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(OK, CANCEL);
            return dialog;
        }).opt().ifPresent(System.out::println);

    }

}
