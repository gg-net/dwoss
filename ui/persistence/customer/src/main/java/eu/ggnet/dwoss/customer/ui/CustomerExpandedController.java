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
package eu.ggnet.dwoss.customer.ui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Communication;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class CustomerExpandedController implements Initializable {

    @FXML
    VBox comm;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private VBox fillCommunicationsBox(ObservableList<Communication> list, VBox vbox) {
        if ( !list.isEmpty() ) {
            for (Communication communication : list) {
                //the Togglegroup for this VBox
                ToggleGroup togglegroup = new ToggleGroup();

                //buildup the HBox
                HBox firstEntry = new HBox();
                firstEntry.setSpacing(5.0);
                firstEntry.setAlignment(Pos.CENTER);
                firstEntry.setMinHeight(24.0);

                RadioButton firstCommTyp = new RadioButton();
                firstCommTyp.setToggleGroup(togglegroup);
                if ( communication.isPrefered() ) {
                    firstCommTyp.setSelected(true);
                }

                Label commtype = new Label();
                commtype.setText(communication.getType().name());
                commtype.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                Label commfield = new Label();
                commfield.setText(communication.getIdentifier());

                Region fillregion = new Region();
                fillregion.setPickOnBounds(true);

                ImageView editImg = new ImageView();
                editImg.setFitHeight(24.0);
                editImg.setFitWidth(24.0);
                editImg.setImage(new Image("edit_black_24dp.png"));
                editImg.setPickOnBounds(true);
                editImg.setPreserveRatio(true);
                editImg.setOnMousePressed((EventHandler<? super MouseEvent>)openCommunicationView(communication.getId()));
                Tooltip.install(editImg, new Tooltip("Bearbeiten"));

                ImageView delImg = new ImageView();
                delImg.setFitHeight(24.0);
                delImg.setFitWidth(24.0);
                delImg.setImage(new Image("del_black_24dp.png"));
                delImg.setPickOnBounds(true);
                delImg.setPreserveRatio(true);
                delImg.setOnMousePressed((EventHandler<? super MouseEvent>)delCommunication(communication.getId()));
                Tooltip.install(delImg, new Tooltip("Löschen"));

                //fill the HBox
                firstEntry.getChildren().addAll(firstCommTyp, commtype, commfield, fillregion, editImg, delImg);
                HBox.setHgrow(fillregion, Priority.ALWAYS);

                //add the first entrie
                vbox.getChildren().add(firstEntry);
            }
        }

        return vbox;
    }

    //TODO
    public ActionEvent openCommunicationView(long id) {
        return null;

    }
    //TODO
    public ActionEvent delCommunication(long id) {
        return null;

    }

}
