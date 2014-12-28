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
package eu.ggnet.dwoss.stock.transactions;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

import eu.ggnet.saft.api.ui.Title;

/**
 *
 * @author oliver.guenther
 */
@Title("Einzelnes Gerät aus Transaktion entfernen")
public class RemoveQuestionView extends VBox {

    private final TextField refurbishIdField;

    private final TextField commentField;

    public RemoveQuestionView() {
        setPadding(new Insets(5));
        refurbishIdField = new TextField();
        commentField = new TextField();
        getChildren().addAll(
                new HBox(5, new Label("SopoNr:"), refurbishIdField),
                new HBox(5, new Label("Kommentar:"), commentField)
        );
    }

    public String refurbishId() {
        return refurbishIdField.getText();
    }

    public String comment() {
        return commentField.getText();
    }

}
