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
package eu.ggnet.dwoss.customer.ui.neo.listView;

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

import eu.ggnet.dwoss.customer.entity.Address;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerAddressListController extends VBox implements Initializable {

    @FXML
    @Getter
    @Setter
    ObservableList<Address> list;

    @FXML
    @Getter
    @Setter
    VBox vbox = new VBox();

    public CustomerAddressListController(ObservableList<Address> list) {
        this.list = list;
        start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    private void start() {
        /**
         * fill a VBox for a List of Address
         */
        Separator separator = new Separator();

        HBox headerBox = new HBox();
        headerBox.setSpacing(5.0);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMinHeight(24.0);

        Label headerLable = new Label("Adressen:");

        Region headerFillregion = new Region();
        headerFillregion.setMinHeight(24.0);
        headerFillregion.setMinWidth(10.0);

        ImageView addImg = new ImageView();
        addImg.setFitHeight(24.0);
        addImg.setFitWidth(24.0);
        addImg.setImage(new Image(getClass().getResourceAsStream("../../add_black_24dp.png") ));
        addImg.setPickOnBounds(true);
        addImg.setPreserveRatio(true);
        addImg.setOnMousePressed((EventHandler<? super MouseEvent>)addAddress(new Address()));
        Tooltip.install(addImg, new Tooltip("Hinzufügen"));

        headerBox.getChildren().addAll(headerLable, headerFillregion, addImg);

        vbox.getChildren().addAll(separator, headerBox);

        if ( !list.isEmpty() ) {
            for (Address address : list) {

                //buildup the HBox
                HBox hbox = new HBox();
                hbox.setSpacing(5.0);
                hbox.setAlignment(Pos.CENTER);
                hbox.setMinHeight(24.0);

                Label preferdType = new Label(address.getPreferedType().getName());
                preferdType.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                Label street = new Label(address.getStreet());
                Label zipcode = new Label(address.getZipCode());
                Label city = new Label(address.getCity());

                Region fillregion = new Region();
                fillregion.setMinHeight(24.0);
                fillregion.setMinWidth(10.0);

                ImageView editImg = new ImageView();
                editImg.setFitHeight(24.0);
                editImg.setFitWidth(24.0);
                editImg.setImage(new Image(getClass().getResourceAsStream("../../edit_black_24dp.png")));
                editImg.setPickOnBounds(true);
                editImg.setPreserveRatio(true);
                editImg.setOnMousePressed((EventHandler<? super MouseEvent>)editAddress(address.getId()));
                Tooltip.install(editImg, new Tooltip("Bearbeiten"));

                ImageView delImg = new ImageView();
                delImg.setFitHeight(24.0);
                delImg.setFitWidth(24.0);
                delImg.setImage(new Image(getClass().getResourceAsStream("../../del_black_24dp.png")));
                delImg.setPickOnBounds(true);
                delImg.setPreserveRatio(true);
                delImg.setOnMousePressed((EventHandler<? super MouseEvent>)delAddress(address.getId()));
                Tooltip.install(delImg, new Tooltip("Löschen"));

                //fill the HBox
                hbox.getChildren().addAll(preferdType, street, zipcode, city, fillregion, editImg, delImg);
                HBox.setHgrow(fillregion, Priority.ALWAYS);

                //add the first entrie
                vbox.getChildren().add(hbox);

            }

        }

    }

    //TODO
    public ActionEvent addAddress(Address a) {
        return null;
    }

    //TODO
    public ActionEvent editAddress(long id) {
        return null;
    }

    //TODO
    public ActionEvent delAddress(long id) {
        return null;
    }

}
