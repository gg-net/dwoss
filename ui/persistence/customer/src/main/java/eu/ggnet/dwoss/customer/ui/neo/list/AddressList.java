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
package eu.ggnet.dwoss.customer.ui.neo.list;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Address;
import eu.ggnet.dwoss.customer.ui.neo.AddressUpdateController;
import eu.ggnet.dwoss.customer.ui.neo.customListCell.AddressListCell;
import eu.ggnet.saft.Ui;


/**
 *
 * @author jacob.weinhold
 */
public class AddressList {

    private VBox vbox;

    private Label titleLabel;

    private ImageView addImage;

    private ListView<Address> listView;

    private ObservableList<Address> observableList;

    public AddressList(ObservableList<Address> ol) {
        this.observableList = ol;
        vbox = new VBox();

        Separator separator = new Separator();

        HBox headerBox = new HBox();
        headerBox.setSpacing(5.0);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMinHeight(24.0);

        titleLabel.setText("Adressen");

        Region headerFillregion = new Region();
        headerFillregion.setMinHeight(24.0);
        headerFillregion.setMinWidth(10.0);

        addImage.setFitHeight(24.0);
        addImage.setFitWidth(24.0);
        addImage.setImage(new Image(getClass().getResourceAsStream("../../add_black_24dp.png")));
        addImage.setPickOnBounds(true);
        addImage.setPreserveRatio(true);
        Tooltip.install(addImage, new Tooltip("Hinzufügen"));
        addImage.setOnMousePressed(add(new Address()));

        headerBox.getChildren().addAll(titleLabel, headerFillregion, addImage);

        observableList = FXCollections.observableArrayList();

        listView.getItems().addAll(observableList);
        listView.setCellFactory((element) -> {
            AddressListCell listCell = new AddressListCell();
            listCell.setDeleteHandler(del((Address)listView.getSelectionModel().getSelectedItem()));
            listCell.setEditHandler(edit((Address)listView.getSelectionModel().getSelectedItem()));

            return listCell;
        });

        vbox.getChildren().addAll(separator, headerBox, listView);

    }

    public VBox getList() {
        return vbox;
    }

    public EventHandler<? super MouseEvent> add(Address entry) {
        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            Ui.exec(() -> {
                Ui.fxml().parent(titleLabel).eval(() -> entry, AddressUpdateController.class)
                        .ifPresent(address -> observableList.add(address));
            });
        };

        return editHandler;
    }

    public EventHandler<? super MouseEvent> edit(Address entry) {
        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            Ui.exec(() -> {
                Ui.fxml().parent(titleLabel).eval(() -> entry, AddressUpdateController.class);
            });
        };

        return editHandler;
    }

    public EventHandler<? super MouseEvent> del(Address entry) {

        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            observableList.remove(entry);
        };

        return editHandler;

    }
}
