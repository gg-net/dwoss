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
import java.util.Map.Entry;

import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ui.neo.listView.popup.AdditionalCustomerIdEditorController;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;

import lombok.Getter;

/**
 *
 * @author jens.papenhagen
 */
public class AdditionalCustomerIdListedView extends VBox implements ListedViewCommandable<Map.Entry<ExternalSystem, String>>, FxController {

    private ObservableMap<ExternalSystem, String> map;

    @Getter
    private VBox vbox = new VBox();

    private Map.Entry<ExternalSystem, String> emptyEntry = null;

    /**
     * fill a VBox for a List of Contact
     * select the Prefered Contact
     * <p>
     * @param observableList
     */
    @Override
    public void fillList(ObservableList<?> observableList) {

        //only use the ObservableList for "transfering" the map here.
        Map<ExternalSystem, String> hashmap = new HashMap<>();
        map = FXCollections.observableMap(hashmap);
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

        ImageView addImg = new ListedViewUtil().addButton();
        addImg.setOnMousePressed(add(emptyEntry));

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

                ImageView editImg = new ListedViewUtil().editButton();
                editImg.setOnMousePressed(edit(entry));

                ImageView delImg = new ListedViewUtil().deleteButton();
                delImg.setOnMousePressed(del(entry));

                //fill the HBox
                hbox.getChildren().addAll(externalSystem, idFormExternalSystem, fillregion, editImg, delImg);
                HBox.setHgrow(fillregion, Priority.ALWAYS);

                //add the first entrie
                vbox.getChildren().add(hbox);
            }
        }
    }

    //TODO
    @Override
    public EventHandler<? super MouseEvent> add(Entry<ExternalSystem, String> entry) {
        EventHandler<? super MouseEvent> editHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                // @todo
                // how to get the selected entry instance
                Ui.exec(() -> {
                    Ui.fxml().parent(vbox).eval(() -> entry, AdditionalCustomerIdEditorController.class)
                            .ifPresent((entry) -> map.put(entry.getKey(), entry.getValue()));

                });
                vbox.getChildren().clear();
                fillList(FXCollections.observableArrayList(map));
            }
        };

        return editHandler;
    }

    //TODO
    @Override
    public EventHandler<? super MouseEvent> edit(Entry<ExternalSystem, String> entry) {

        EventHandler<? super MouseEvent> editHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                // @todo
                // how to get the selected entry instance
                Ui.exec(() -> {
                    Ui.fxml().parent(vbox).eval(() -> entry, AdditionalCustomerIdEditorController.class)
                            .ifPresent((entry) -> map.put(entry.getKey(), entry.getValue()));
                    vbox.getChildren().clear();
                    fillList(FXCollections.observableArrayList(map));

                });

            }
        };

        return editHandler;
    }

    //TODO
    @Override
    public EventHandler<? super MouseEvent> del(Entry<ExternalSystem, String> entry) {

        EventHandler<? super MouseEvent> editHandler = new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                map.remove(entry.getKey());
                vbox.getChildren().clear();
                fillList(FXCollections.observableArrayList(map));
            }

        };

        return editHandler;

    }

}
