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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Communication;
import eu.ggnet.dwoss.customer.ui.neo.customListCell.CommunicationListCell;

import lombok.Getter;

/**
 *
 * @author jens.papenhagen
 */
public class CommunicationList {

    @FXML
    private VBox vbox;

    @FXML
    private Label titleLabel;

    @FXML
    private ImageView addImage;

    @FXML
    private ListView<Communication> listView;

    @Getter
    private ToggleGroup togglegroup;

    private ObservableList<Communication> observableList;

    public CommunicationList(ObservableList<Communication> observableList) {
        this.observableList = observableList;
        vbox = new VBox();
        Separator separator = new Separator();

        HBox headerBox = new HBox();
        headerBox.setSpacing(5.0);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setMinHeight(24.0);

        titleLabel = new Label("Kommunikationswege:");

        Region headerFillregion = new Region();
        headerFillregion.setMinHeight(24.0);
        headerFillregion.setMinWidth(10.0);

        addImage.setFitHeight(24.0);
        addImage.setFitWidth(24.0);
        addImage.setImage(new Image(getClass().getResourceAsStream("../../add_black_24dp.png")));
        addImage.setPickOnBounds(true);
        addImage.setPreserveRatio(true);
        Tooltip.install(addImage, new Tooltip("Hinzufügen"));
        addImage.setOnMousePressed(add(new Communication()));

        headerBox.getChildren().addAll(titleLabel, headerFillregion, addImage);

        togglegroup = new ToggleGroup();

        this.observableList = FXCollections.observableArrayList();

        listView.getItems().addAll(this.observableList);
        listView.setCellFactory((element) -> {
            CommunicationListCell listCell = new CommunicationListCell();
            listCell.setDeleteHandler(del((Communication)listView.getSelectionModel().getSelectedItem()));
            listCell.setEditHandler(edit((Communication)listView.getSelectionModel().getSelectedItem()));

            return listCell;
        });
        System.out.println("in constr before add " + vbox.getChildren().size());
        vbox.getChildren().addAll(separator, headerBox, listView);
        System.out.println("in constr after add " + vbox.getChildren().size());
    }

    public VBox getList() {
        System.out.println("in getList()" + vbox.getChildren().size());
        return vbox;
    }

    //TODO
    public EventHandler<? super MouseEvent> add(Communication entry) {
        return null;
    }

    //TODO
    public EventHandler<? super MouseEvent> edit(Communication entry) {
        return null;
    }

    //TODO
    public EventHandler<? super MouseEvent> del(Communication entry) {
        return null;
    }
}
