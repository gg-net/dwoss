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

import eu.ggnet.dwoss.customer.ui.neo.listView.customListCell.AdditionalCustomerIdListCell;

import java.net.URL;
import java.util.Map.Entry;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ui.neo.listView.popup.AdditionalCustomerIdEditorController;
import eu.ggnet.saft.Ui;

import lombok.Getter;

/**
 *
 * @author jacob.weinhold
 */
public class AdditionalCustomerIdListViewController implements Initializable {

    @FXML
    private ListView<Map.Entry<ExternalSystem, String>> listView;

    @FXML
    private ImageView addImage;

    @FXML
    private Label titleLabel;

    @Getter
    private ObservableMap<ExternalSystem, String> observableMap;

    private Map.Entry<ExternalSystem, String> emptyEntry = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText("Externe Kunden Nummern");

        addImage.setFitHeight(24.0);
        addImage.setFitWidth(24.0);
        addImage.setImage(new Image(getClass().getResourceAsStream("../../add_black_24dp.png")));
        addImage.setPickOnBounds(true);
        addImage.setPreserveRatio(true);
        Tooltip.install(addImage, new Tooltip("Hinzufügen"));
        addImage.setOnMousePressed(add(emptyEntry));

//        listView.setItems(FXCollections.observableArrayList(FXCollections.observableSet(observableMap.entrySet())));
        observableMap = FXCollections.observableMap(new HashMap());

        System.out.println("ini " + observableMap.hashCode());

        listView.getItems().addAll(FXCollections.observableSet(observableMap.entrySet()));
        listView.setCellFactory((element) -> {

            AdditionalCustomerIdListCell listCell = new AdditionalCustomerIdListCell();
            listCell.setDeleteHandler(del((Map.Entry<ExternalSystem, String>)listView.getSelectionModel().getSelectedItem()));
            listCell.setEditHandler(edit((Map.Entry<ExternalSystem, String>)listView.getSelectionModel().getSelectedItem()));

            return listCell;
        });

    }

    public EventHandler<? super MouseEvent> add(Entry<ExternalSystem, String> entry) {
        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            Ui.exec(() -> {
                Ui.fxml().parent(titleLabel).eval(() -> entry, AdditionalCustomerIdEditorController.class)
                        .ifPresent((java.util.Map.Entry<ExternalSystem, String> entry1) -> observableMap.put(entry1.getKey(), entry1.getValue()));
            });
        };

        return editHandler;
    }

    public EventHandler<? super MouseEvent> edit(Entry<ExternalSystem, String> entry) {
        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            Ui.exec(() -> {
                Ui.fxml().parent(titleLabel).eval(() -> entry, AdditionalCustomerIdEditorController.class)
                        .ifPresent((java.util.Map.Entry<ExternalSystem, String> entry1) -> observableMap.put(entry1.getKey(), entry1.getValue()));
            });
        };

        return editHandler;
    }

    public EventHandler<? super MouseEvent> del(Entry<ExternalSystem, String> entry) {

        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            observableMap.remove(entry.getKey());
        };

        return editHandler;

    }

    public void setObservableMap(ObservableMap<ExternalSystem, String> map) {
        System.out.println("setObservableMap" + map.hashCode());
        this.observableMap = FXCollections.observableMap(map);
    }

}
