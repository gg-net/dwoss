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
package eu.ggnet.dwoss.customer.ui.neo;

import eu.ggnet.dwoss.customer.ui.neo.customListCell.AdditionalCustomerIdListCell;

import java.net.URL;
import java.util.Map.Entry;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.saft.Ui;

import lombok.*;

/**
 *
 * @author jacob.weinhold
 */
public class AdditionalCustomerIdList implements Initializable {

    @FXML
    @Getter
    private VBox vbox;

    @FXML
    private ListView<Map.Entry<ExternalSystem, String>> listView;

    @FXML
    private ImageView addImage;

    @FXML
    private Label titleLabel;

    @Getter
    @Setter
    private ObservableMap<ExternalSystem, String> observableMap;

    private Map.Entry<ExternalSystem, String> emptyEntry = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vbox = new VBox();

        Separator separator = new Separator();

        HBox headerBox = new HBox();
        headerBox.setSpacing(5.0);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMinHeight(24.0);

        titleLabel.setText("Externe Kunden Nummern");

        Region headerFillregion = new Region();
        headerFillregion.setMinHeight(24.0);
        headerFillregion.setMinWidth(10.0);

        addImage.setFitHeight(24.0);
        addImage.setFitWidth(24.0);
        addImage.setImage(new Image(getClass().getResourceAsStream("../../add_black_24dp.png")));
        addImage.setPickOnBounds(true);
        addImage.setPreserveRatio(true);
        Tooltip.install(addImage, new Tooltip("Hinzufügen"));
        addImage.setOnMousePressed(add(emptyEntry));

        headerBox.getChildren().addAll(titleLabel, headerFillregion, addImage);

        observableMap = FXCollections.observableMap(new HashMap());

        listView.getItems().addAll(FXCollections.observableSet(observableMap.entrySet()));
        listView.setCellFactory((element) -> {
            AdditionalCustomerIdListCell listCell = new AdditionalCustomerIdListCell();
            listCell.setDeleteHandler(del((Map.Entry<ExternalSystem, String>)listView.getSelectionModel().getSelectedItem()));
            listCell.setEditHandler(edit((Map.Entry<ExternalSystem, String>)listView.getSelectionModel().getSelectedItem()));

            return listCell;
        });
        
        vbox.getChildren().addAll(separator, headerBox, listView);

    }

    public EventHandler<? super MouseEvent> add(Entry<ExternalSystem, String> entry) {
        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            Ui.exec(() -> {
                Ui.fxml().parent(titleLabel).eval(() -> entry, AdditionalCustomerIdUpdateController.class)
                        .ifPresent((java.util.Map.Entry<ExternalSystem, String> entry1) -> observableMap.put(entry1.getKey(), entry1.getValue()));
            });
        };

        return editHandler;
    }

    public EventHandler<? super MouseEvent> edit(Entry<ExternalSystem, String> entry) {
        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            Ui.exec(() -> {
                Ui.fxml().parent(titleLabel).eval(() -> entry, AdditionalCustomerIdUpdateController.class)
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
        this.observableMap = FXCollections.observableMap(map);
    }

}
