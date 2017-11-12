package eu.ggnet.saft.core.fx;

import eu.ggnet.saft.UiUtil;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import eu.ggnet.saft.api.ui.OnOk;

/**
 *
 * @author oliver.guenther
 * @param <T>
 */
public class OkCancelStage<T extends Node> extends Stage {

    private boolean ok = false;

    private T payload;

    public OkCancelStage(String title, T payload) {

        this.payload = payload;
        BorderPane pane = new BorderPane();
        pane.setCenter(payload);

        Button okButton = new Button("Ok");
        okButton.defaultButtonProperty();

        Button cancelButton = new Button("Abbrechen");
        cancelButton.setCancelButton(true);

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10));
        bottom.getChildren().addAll(okButton, cancelButton);
        pane.setBottom(bottom);
        if (!UiUtil.isBlank(title)) setTitle(title);
        setScene(new Scene(pane));

        okButton.setOnAction((ActionEvent t) -> {
            boolean close = true;
            if (payload instanceof OnOk) {
                close = ((OnOk) payload).onOk();
            }
            if (!close) return;
            OkCancelStage.this.ok = true;
            close();
        });
        cancelButton.setOnAction((ActionEvent t) -> {
            close();
        });
    }

    public boolean isOk() {
        return ok;
    }

    public boolean isCancel() {
        return !ok;
    }

    public T getPayload() {
        return payload;
    }

}
