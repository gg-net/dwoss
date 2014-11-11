/* 
 * Copyright (C) 2014 pascal.perau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.util;

import org.apache.commons.lang3.StringUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
        if ( !StringUtils.isBlank(title) ) setTitle(title);
        setScene(new Scene(pane));

        okButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                OkCancelStage.this.ok = true;
                close();
            }
        });
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                close();
            }
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
