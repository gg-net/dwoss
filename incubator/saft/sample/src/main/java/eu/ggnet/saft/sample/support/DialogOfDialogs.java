/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.saft.sample.support;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;

import eu.ggnet.saft.Ui;

/**
 *
 * @author oliver.guenther
 */
public class DialogOfDialogs extends FlowPane {

    public DialogOfDialogs() {
        for (Modality modality : Modality.values()) {
            getChildren().add(dialog(modality, true));
            getChildren().add(dialog(modality, false));
        }
    }

    private Button dialog(Modality modality, boolean frame) {
        String title = "[" + modality + ",frame=" + frame + "]";
        Button b1 = new Button(title);
        b1.setOnAction((ActionEvent event) -> {
            Ui.build(DialogOfDialogs.this).title(title).modality(modality).frame(frame).fx().show(() -> {
                Label label = new Label(title);
                label.setFont(new Font("Arial", 48));
                return new BorderPane(label);
            });
        });
        return b1;
    }


}
