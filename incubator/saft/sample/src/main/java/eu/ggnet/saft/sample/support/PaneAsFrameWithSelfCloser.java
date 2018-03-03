package eu.ggnet.saft.sample.support;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.Frame;
import eu.ggnet.saft.api.ui.Once;

import static javafx.scene.text.Font.font;

/**
 *
 * @author oliver.guenther
 */
@Frame
@Once
public class PaneAsFrameWithSelfCloser extends BorderPane {

    public PaneAsFrameWithSelfCloser() {
        Label l = new Label("Pane As Frame with Selfcloser");
        l.setFont(font(50));
        setCenter(l);
        final Button b = new Button("Close");
        b.setOnAction(e -> {
            Ui.closeWindowOf(b);
        });
        setBottom(b);
    }

}
