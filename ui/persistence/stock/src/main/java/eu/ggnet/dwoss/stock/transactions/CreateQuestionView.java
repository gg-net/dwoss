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

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.stock.ee.entity.StockUnit;
import eu.ggnet.saft.api.ui.Title;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;

/**
 *
 * @author oliver.guenther
 */
@Title("Umfuhr anlegen")
public class CreateQuestionView extends Dialog<CreateQuestionModel> implements Consumer<CreateQuestionModel> {

    private CreateQuestionModel model;

    private VBox vbox;

    public CreateQuestionView() {
        vbox = new VBox();
        getDialogPane().setContent(vbox);
        getDialogPane().getButtonTypes().addAll(OK, CANCEL);
        setResultConverter(buttonType -> {
            if ( buttonType.equals(OK) ) return model;
            return null;
        });

    }

    @Override
    public void accept(CreateQuestionModel model) {
        this.model = model;
        vbox.setPadding(new Insets(10));
        vbox.getChildren().add(new Label("Umfuhr von " + model.source.getName() + " nach " + model.destination.getName() + " für folgende(s) Gerät(e):"));
        for (StockUnit stockUnit : model.stockUnits) {
            vbox.getChildren().add(new Label("- [" + stockUnit.getRefurbishId() + "] " + stockUnit.getName()));
        }

    }

    public CreateQuestionModel model() {
        return model;
    }

}
