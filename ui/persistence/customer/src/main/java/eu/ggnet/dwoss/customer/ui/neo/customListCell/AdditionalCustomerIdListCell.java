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
package eu.ggnet.dwoss.customer.ui.neo.customListCell;

import java.util.Map;
import java.util.Map.Entry;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ui.neo.ListedViewUtil;

import lombok.Setter;

/**
 *
 * @author jacob.weinhold
 */
public class AdditionalCustomerIdListCell extends ListCell<Map.Entry<ExternalSystem, String>> {

    @Setter
    private EventHandler<? super MouseEvent> editHandler;

    @Setter
    private EventHandler<? super MouseEvent> deleteHandler;

    private HBox hbox;

    private Label externalSystem;

    private Label idFormExternalSystem;

    public AdditionalCustomerIdListCell() {
        setPrefWidth(100);

        hbox = new HBox();
        hbox.setSpacing(5.0);
        hbox.setAlignment(Pos.CENTER);
        hbox.setMinHeight(24.0);

        externalSystem = new Label();
        idFormExternalSystem = new Label();

        Region fillregion = new Region();
        fillregion.setMinHeight(24.0);
        fillregion.setMinWidth(10.0);

        ImageView editImg = new ListedViewUtil().editButton();
        editImg.setOnMousePressed(editHandler);

        ImageView delImg = new ListedViewUtil().deleteButton();
        delImg.setOnMousePressed(deleteHandler);

        //fill the HBox
        hbox.getChildren().addAll(externalSystem, idFormExternalSystem, fillregion, editImg, delImg);
        HBox.setHgrow(fillregion, Priority.ALWAYS);

    }

    @Override
    protected void updateItem(Entry<ExternalSystem, String> item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.

        if ( empty || item == null ) {
            setGraphic(null);
            return;
        }
        externalSystem.setText(item.getKey().toString());
        idFormExternalSystem.setText(item.getValue());

        setGraphic(hbox);
    }

}
