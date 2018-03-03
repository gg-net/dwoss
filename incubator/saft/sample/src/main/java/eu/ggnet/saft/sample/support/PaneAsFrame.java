package eu.ggnet.saft.sample.support;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import eu.ggnet.saft.api.ui.Frame;
import eu.ggnet.saft.api.ui.Once;

import static javafx.scene.text.Font.font;

/**
 *
 * @author oliver.guenther
 */
@Frame
@Once
public class PaneAsFrame extends BorderPane {

    public PaneAsFrame() {
        Label l = new Label("Pane As Frame");
        l.setFont(font(50));
        setCenter(l);
    }

}
