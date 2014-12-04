package eu.ggnet.saft.sample.aux;

import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import eu.ggnet.saft.api.ui.IdSupplier;
import eu.ggnet.saft.api.ui.Title;

import static javafx.scene.text.Font.font;

/**
 *
 * @author oliver.guenther
 */
@Title("Id is {id}")
public class PaneConsumesIdSupplier extends BorderPane implements Consumer<IdSupplier> {

    private Label b;

    public PaneConsumesIdSupplier() {
        Label l = new Label("Pane As Cosumer of IdSupplier");
        l.setFont(font(50));
        setCenter(l);
        b = new Label("No Id Consumed");
        b.setFont(font(20));
        setBottom(b);
    }

    @Override
    public void accept(IdSupplier t) {
        b.setText(t.id());
    }

}
