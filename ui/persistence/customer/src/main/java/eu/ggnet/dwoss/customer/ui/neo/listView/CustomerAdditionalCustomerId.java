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

import java.util.*;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Contact;
import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;

import lombok.*;

/**
 *
 * @author jens.papenhagen
 */
public class CustomerAdditionalCustomerId extends VBox implements CustomerListViewCommand<Contact> {

    ObservableMap<ExternalSystem, String> map;

    @FXML
    @Getter
    @Setter
    VBox vbox = new VBox();

    public CustomerAdditionalCustomerId(ObservableMap<ExternalSystem, String> map) {
        this.map = map;
    }

    /**
     * fill a VBox for a List of Contact
     * select the Prefered Contact
     * <p>
     * @param observableList
     */
    @Override
    public VBox fillList(ObservableList<?> observableList) {

        //only use the ObservableList for "transfering" the map here.
        if ( observableList != null ) {
            for (Map<ExternalSystem, String> entymap : (List<Map<ExternalSystem, String>>)observableList) {
                map.putAll(entymap);
            }
        }

        Separator separator = new Separator();

        HBox headerBox = new HBox();
        headerBox.setSpacing(5.0);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMinHeight(24.0);

        Label headerLable = new Label("Externe Kunden Ids:");

        Region headerFillregion = new Region();
        headerFillregion.setMinHeight(24.0);
        headerFillregion.setMinWidth(10.0);

        ImageView addImg = new CustomerListViewUtil().addButton();
        addImg.setOnMousePressed((EventHandler<? super MouseEvent>)add(""));

        headerBox.getChildren().addAll(headerLable, headerFillregion, addImg);

        vbox.getChildren().addAll(separator, headerBox);
        if ( !map.isEmpty() ) {

            for (Map.Entry<ExternalSystem, String> entry : map.entrySet()) {

                //buildup the HBox
                HBox hbox = new HBox();
                hbox.setSpacing(5.0);
                hbox.setAlignment(Pos.CENTER);
                hbox.setMinHeight(24.0);

                Label externalSystem = new Label(entry.getKey().toString());
                Label idFormExternalSystem = new Label(entry.getValue());

                Region fillregion = new Region();
                fillregion.setMinHeight(24.0);
                fillregion.setMinWidth(10.0);

                ImageView editImg = new CustomerListViewUtil().editButton();
                editImg.setOnMousePressed((EventHandler<? super MouseEvent>)edit(entry.getKey()));

                ImageView delImg = new CustomerListViewUtil().deleteButton();
                delImg.setOnMousePressed((EventHandler<? super MouseEvent>)del(entry.getKey()));

                //fill the HBox
                hbox.getChildren().addAll(externalSystem, idFormExternalSystem, fillregion, editImg, delImg);
                HBox.setHgrow(fillregion, Priority.ALWAYS);

                //add the first entrie
                vbox.getChildren().add(hbox);
            }
        }

        return vbox;

    }

    //TODO
    public ActionEvent edit(ExternalSystem id) {
        return null;
    }

    //TODO
    public ActionEvent del(ExternalSystem id) {
        return null;
    }

    //TODO
    @Override
    public ActionEvent add(Object entry) {
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="Unused Methods">
    @Override
    public ActionEvent edit(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ActionEvent del(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //</editor-fold>

}
