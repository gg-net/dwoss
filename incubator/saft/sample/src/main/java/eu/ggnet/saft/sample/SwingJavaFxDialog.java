package eu.ggnet.saft.sample;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import eu.ggnet.saft.core.*;
import eu.ggnet.saft.sample.support.MainPanel;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

/**
 * Opening a JavaFX Pane as popup Dialog, blocking the hole application.
 *
 * @author oliver.guenther
 */
public class SwingJavaFxDialog {

    public static void main(String[] args) {
        UiCore.startSwing(() -> new MainPanel());

        new JFXPanel();

//        dialog.showAndWait().ifPresent(System.out::println);

        Ui.dialog().parent(SwingCore.mainFrame()).eval(() -> {
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
        }).ifPresent(System.out::println);

    }

}
