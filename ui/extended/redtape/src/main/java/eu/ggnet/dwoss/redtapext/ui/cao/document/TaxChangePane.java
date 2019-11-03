/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.ui.cao.document;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.core.common.values.TaxType;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.Title;

/**
 *
 * @author oliver.guenther
 */
@Title("Steuer anpassen")
public class TaxChangePane extends StackPane implements ResultProducer<TaxType> {

    private TaxType result = null;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public TaxChangePane() {
        VBox p = new VBox(10);
        p.setAlignment(Pos.CENTER);
        for (TaxType taxType : TaxType.values()) {
            Button button = new Button(taxType.description);
            button.setOnAction(e -> {
                result = taxType;
                close();
            });
            button.setTooltip(new Tooltip(taxType.detailedDescription));
            p.getChildren().add(button);

        }
        Button cancel = new Button("Abbrechen");
        cancel.setOnAction(e -> close());
        p.getChildren().add(cancel);
        setPrefHeight(120);
        setPrefWidth(300);
        getChildren().add(p);
    }

    @Override
    public TaxType getResult() {
        return result;
    }

    private void close() {
        Ui.closeWindowOf(this);
    }

}
