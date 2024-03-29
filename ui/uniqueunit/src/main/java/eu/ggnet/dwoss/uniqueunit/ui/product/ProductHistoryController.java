/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.product;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

/**
 * Controller class for a dialog to enter a product number.
 *
 * @author mirko.schulze
 */
//TODO: naming : 
@Dependent
@Title("Eingabe Artikelnummer")
public class ProductHistoryController implements FxController, ResultProducer<String>, Initializable {

    @Bind(SHOWING)
    private final BooleanProperty showingProperty = new SimpleBooleanProperty();

    private boolean ok;

    @FXML
    private TextField partNoTextField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        okButton.setOnAction(e -> {
            ok = true;
            showingProperty.set(false);
        });
        cancelButton.setOnAction(e -> showingProperty.set(false));
    }

    @Override
    public String getResult() {
        return ok == true ? partNoTextField.getText() : null;
    }

}
