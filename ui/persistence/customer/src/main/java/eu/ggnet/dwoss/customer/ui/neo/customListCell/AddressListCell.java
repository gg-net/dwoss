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

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import eu.ggnet.dwoss.customer.entity.Address;
import eu.ggnet.dwoss.customer.ui.neo.ListedViewUtil;

import lombok.Setter;

/**
 *
 * @author jacob.weinhold
 */
public class AddressListCell extends ListCell<Address> {

    @Setter
    private EventHandler<? super MouseEvent> editHandler;

    @Setter
    private EventHandler<? super MouseEvent> deleteHandler;

    private HBox hbox;

    private Label preferdType;

    private Label street;

    private Label zipcode;

    private Label city;

    public AddressListCell() {
        hbox = new HBox();
        hbox.setSpacing(5.0);
        hbox.setAlignment(Pos.CENTER);
        hbox.setMinHeight(24.0);

        preferdType = new Label();
        preferdType.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        street = new Label();
        zipcode = new Label();
        city = new Label();

        Region fillregion = new Region();
        fillregion.setMinHeight(24.0);
        fillregion.setMinWidth(10.0);

        ImageView editImg = new ListedViewUtil().editButton();
        editImg.setOnMousePressed(editHandler);

        ImageView delImg = new ListedViewUtil().deleteButton();
        delImg.setOnMousePressed(deleteHandler);

        //fill the HBox
        hbox.getChildren().addAll(preferdType, street, zipcode, city, fillregion, editImg, delImg);
        HBox.setHgrow(fillregion, Priority.ALWAYS);

    }

    @Override
    protected void updateItem(Address item, boolean empty) {
        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.

        if ( empty || item == null ) {
            setGraphic(null);
            return;
        }

        if ( item.getPreferedType() == null ) {
            preferdType.setText("");
        } else {
            preferdType.setText(item.getPreferedType().getName());
        }
        street.setText(item.getStreet());
        zipcode.setText(item.getZipCode());
        city.setText(item.getCity());

        setGraphic(hbox);
    }

}
