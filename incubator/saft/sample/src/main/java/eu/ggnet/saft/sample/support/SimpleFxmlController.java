package eu.ggnet.saft.sample.support;

import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.Title;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

/**
 * FXML Controller class
 *
 * @author oliver.guenther
 */
@Title("This is a simple Title")
public class SimpleFxmlController implements FxController, Consumer<String> {

    @FXML
    private TextField input;

    @Override
    public String toString() {
        return "SimpleFxmlController{" + "input=" + input.getText() + '}';
    }

    @Override
    public void accept(String t) {
        input.setText(t);
    }

}
