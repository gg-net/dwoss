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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.customer.entity.Address;
import eu.ggnet.dwoss.customer.ui.neo.listView.customListCell.AddressListCell;
import eu.ggnet.dwoss.customer.ui.neo.listView.popup.AddressEditorController;
import eu.ggnet.saft.Ui;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author jacob.weinhold
 */
public class AddressListedViewController implements Initializable {

    @FXML
    private VBox rootVbox;

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView addImage;

    @FXML
    private ListView<Address> listView;

    @Getter
    @Setter
    private ObservableList<Address> observableList;

    public AddressListedViewController() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleLabel.setText("Adressen");

        addImage.setFitHeight(24.0);
        addImage.setFitWidth(24.0);
        addImage.setImage(new Image(getClass().getResourceAsStream("../../add_black_24dp.png")));
        addImage.setPickOnBounds(true);
        addImage.setPreserveRatio(true);
        Tooltip.install(addImage, new Tooltip("Hinzufügen"));
        addImage.setOnMousePressed(add(new Address()));

        observableList = FXCollections.observableArrayList();

        listView.setCellFactory((element) -> {
            AddressListCell listCell = new AddressListCell();
            listCell.setDeleteHandler(del((Address)listView.getSelectionModel().getSelectedItem()));
            listCell.setEditHandler(edit((Address)listView.getSelectionModel().getSelectedItem()));

            return listCell;
        });
    }

    public void fillList() {
        listView.setItems(observableList);
    }

    public EventHandler<? super MouseEvent> add(Address entry) {
        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            Ui.exec(() -> {
                Ui.fxml().parent(titleLabel).eval(() -> entry, AddressEditorController.class)
                        .ifPresent(address -> observableList.add(address));
            });
        };

        return editHandler;
    }

    public EventHandler<? super MouseEvent> edit(Address entry) {
        EventHandler<? super MouseEvent> editHandler = (MouseEvent event) -> {
            Ui.exec(() -> {
                Ui.fxml().parent(titleLabel).eval(() -> entry, AddressEditorController.class);
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
