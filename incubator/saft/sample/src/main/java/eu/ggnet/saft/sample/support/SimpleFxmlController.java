package eu.ggnet.saft.sample.support;

import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.FxController;

import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import eu.ggnet.saft.core.Ui;

/**
 * FXML Controller class
 *
 * @author oliver.guenther
 */
@Title("This is a simple Title")
public class SimpleFxmlController implements FxController, Consumer<String>, ResultProducer<String> {

    private boolean okPressed = false;

    @FXML
    private TextField input;

    @FXML
    private GridPane root;

    @FXML
    public void ok() {
        okPressed = true;
        Ui.closeWindowOf(root);
    }

    @FXML
    public void cancel() {
        Ui.closeWindowOf(root);
    }

    @Override
    public String toString() {
        return "SimpleFxmlController{" + "input=" + input.getText() + '}';
    }

    @Override
    public void accept(String t) {
        input.setText(t);
    }

    @Override
    public String getResult() {
        if ( okPressed ) return input.getText();
        return null;
    }

}
