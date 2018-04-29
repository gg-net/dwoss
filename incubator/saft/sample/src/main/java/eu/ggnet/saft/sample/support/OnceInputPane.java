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

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.Once;
import eu.ggnet.saft.core.ui.ResultProducer;

import static javafx.geometry.Pos.CENTER_RIGHT;

/**
 *
 * @author oliver.guenther
 */
@Once
public class OnceInputPane extends BorderPane implements ResultProducer<String>, Consumer<String> {

    private TextField tf;

    private String result = null;

    public OnceInputPane() {
        tf = new TextField();
        tf.setPromptText("Eingabe");
        setCenter(tf);

        Button ok = new Button("Ok");
        ok.setOnAction((ActionEvent event) -> {
            if ( StringUtils.isBlank(tf.getText()) ) {
                Ui.build(OnceInputPane.this).alert("Eingabe ist leer");
                return;
            }
            result = tf.getText();
            Ui.closeWindowOf(OnceInputPane.this);
        });

        Button cancel = new Button("Cancel");
        cancel.setOnAction((ActionEvent event) -> {
            Ui.closeWindowOf(OnceInputPane.this);
        });

        FlowPane fp = new FlowPane(ok, cancel);
        fp.setAlignment(CENTER_RIGHT);
        setBottom(fp);
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void accept(String t) {
        tf.setText(t);
    }

}
