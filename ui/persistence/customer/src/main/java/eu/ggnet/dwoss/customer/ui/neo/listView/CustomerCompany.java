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

import eu.ggnet.dwoss.customer.entity.Company;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerCompany extends VBox implements CustomerListViewCommand<Company> {

    @FXML
    @Getter
    @Setter
    VBox vbox = new VBox();

    public CustomerCompany() {
    }

    /**
     * fill a VBox for a List of Company
     * <p>
     * @param observableList
     */
    @Override
    public VBox fillList(ObservableList<?> observableList) {
        Separator separator = new Separator();

        HBox headerBox = new HBox();
        headerBox.setSpacing(5.0);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMinHeight(24.0);

        Label headerLable = new Label("Firmen:");

        Region headerFillregion = new Region();
        headerFillregion.setMinHeight(24.0);
        headerFillregion.setMinWidth(10.0);

        ImageView addImg = new CustomerListViewUtil().addButton();
        addImg.setOnMousePressed((EventHandler<? super MouseEvent>)add(new Company()));

        headerBox.getChildren().addAll(headerLable, headerFillregion, addImg);

        vbox.getChildren().addAll(separator, headerBox);

        if ( !observableList.isEmpty() ) {
            //the Togglegroup for this VBox
            ToggleGroup togglegroup = new ToggleGroup();

            for (Company company : (ObservableList<Company>) observableList) {
                //buildup the HBox
                HBox hbox = new HBox();
                hbox.setSpacing(5.0);
                hbox.setAlignment(Pos.CENTER);
                hbox.setMinHeight(24.0);

                RadioButton companyButton = new RadioButton();
                companyButton.setToggleGroup(togglegroup);
                if ( company.isPrefered() ) {
                    companyButton.setSelected(true);
                }

                Label name = new Label(company.getName());
                name.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                Region fillregion = new Region();
                fillregion.setMinHeight(24.0);
                fillregion.setMinWidth(10.0);

                ImageView editImg = new CustomerListViewUtil().editButton();
                editImg.setOnMousePressed((EventHandler<? super MouseEvent>)edit(company.getId()));


                ImageView delImg = new CustomerListViewUtil().deleteButton();
                delImg.setOnMousePressed((EventHandler<? super MouseEvent>)del(company.getId()));

                //fill the HBox
                hbox.getChildren().addAll(companyButton, name, fillregion, editImg, delImg);
                HBox.setHgrow(fillregion, Priority.ALWAYS);

                //add the first entry
                vbox.getChildren().add(hbox);

            }

        }

        return vbox;
    }

    //TODO
    @Override
    public ActionEvent edit(long id) {
        return null;
    }

    //TODO
    @Override
    public ActionEvent del(long id) {
        return null;
    }

    //TODO
    @Override
    public ActionEvent add(Object entry) {
        return null;
    }

}
