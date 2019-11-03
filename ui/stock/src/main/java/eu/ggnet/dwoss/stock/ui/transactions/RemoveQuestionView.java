/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.ui.transactions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.Title;

/**
 *
 * @author oliver.guenther
 */
@Title("Einzelnes Gerät aus Transaktion entfernen")
public class RemoveQuestionView extends VBox implements ResultProducer<RemoveQuestionView> {

    private final TextField refurbishIdField;

    private final TextField commentField;

    private boolean ok = false;

    public RemoveQuestionView() {
        setPadding(new Insets(5));
        refurbishIdField = new TextField();
        commentField = new TextField();

        Button cancel = new Button("Abbrechen");
        cancel.setOnAction((e) -> Ui.closeWindowOf(this));
        Button okButton = new Button("Ok");
        okButton.setOnAction((e) -> {
            ok = true;
            Ui.closeWindowOf(this);
        });

        FlowPane flowPane = new FlowPane(10, 10, okButton, cancel);
        flowPane.setAlignment(Pos.BOTTOM_RIGHT);

        getChildren().addAll(
                new HBox(5, new Label("SopoNr:"), refurbishIdField),
                new HBox(5, new Label("Kommentar:"), commentField),
                flowPane
        );
    }

    public String refurbishId() {
        return refurbishIdField.getText();
    }

    public String comment() {
        return commentField.getText();
    }

    @Override
    public RemoveQuestionView getResult() {
        if ( ok ) return this;
        return null;
    }

}
