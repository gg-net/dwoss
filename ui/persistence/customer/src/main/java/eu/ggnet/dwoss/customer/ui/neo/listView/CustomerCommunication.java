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


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Communication;

import lombok.*;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerCommunication extends VBox implements CustomerListViewCommand<Communication> {

    @FXML
    @Getter
    private VBox vbox = new VBox();

    public CustomerCommunication() {
    }

    /**
     * fill a VBox for a List of Communication
     * select the Prefered Communication
     * <p>
     * @param observableList
     */
    @Override
    public void fillList(ObservableList<?> observableList) {
        Separator separator = new Separator();

        HBox headerBox = new HBox();
        headerBox.setSpacing(5.0);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMinHeight(24.0);

        Label headerLable = new Label("Kommunikationswege:");

        Region headerFillregion = new Region();
        headerFillregion.setMinHeight(24.0);
        headerFillregion.setMinWidth(10.0);

        ImageView addImg = new CustomerListViewUtil().addButton();
        addImg.setOnMousePressed((EventHandler<? super MouseEvent>)add(new Communication()));

        headerBox.getChildren().addAll(headerLable, headerFillregion, addImg);

        vbox.getChildren().addAll(separator, headerBox);

        if ( !observableList.isEmpty() ) {
            //the Togglegroup for this VBox
            ToggleGroup togglegroup = new ToggleGroup();

            for (Communication communication : (ObservableList<Communication>)observableList) {
                //buildup the HBox
                HBox hbox = new HBox();
                hbox.setSpacing(5.0);
                hbox.setAlignment(Pos.CENTER);
                hbox.setMinHeight(24.0);

                RadioButton commButton = new RadioButton();
                commButton.setToggleGroup(togglegroup);
                if ( communication.isPrefered() ) {
                    commButton.setSelected(true);
                }

                Label commtype = new Label(communication.getType().name());
                commtype.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                Label commfield = new Label(communication.getIdentifier());

                Region fillregion = new Region();
                fillregion.setMinHeight(24.0);
                fillregion.setMinWidth(10.0);

                ImageView editImg = new CustomerListViewUtil().editButton();
                editImg.setOnMousePressed((EventHandler<? super MouseEvent>)edit(communication));

                ImageView delImg = new CustomerListViewUtil().deleteButton();
                //disable the click on the prefered entry
                if ( communication.isPrefered() ) {
                    delImg.setDisable(true);
                }
                delImg.setOnMousePressed((EventHandler<? super MouseEvent>)del(communication));
                Tooltip.install(delImg, new Tooltip("Löschen"));

                //fill the HBox
                hbox.getChildren().addAll(commButton, commtype, commfield, fillregion, editImg, delImg);
                HBox.setHgrow(fillregion, Priority.ALWAYS);

                //add the first entrie
                vbox.getChildren().add(hbox);
            }
        }
    }

    //TODO
    @Override
    public ActionEvent edit(Object entry) {
        return null;
    }

    //TODO
    @Override
    public ActionEvent del(Object entry) {
        return null;
    }

    //TODO
    @Override
    public ActionEvent add(Object entry) {
        return null;
    }
}
