/*
 * Copyright (C) 2015 GG-Net GmbH
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
package eu.ggnet.dwoss.mobile.ui;

import java.util.ArrayList;

import javax.ws.rs.core.MediaType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.apache.cxf.jaxrs.client.WebClient;

import eu.ggnet.dwoss.uniqueunit.api.UnitShard;
import eu.ggnet.saft.api.ui.FxController;

/**
 * FXML Controller class
 *
 * @author oliver.guenther
 */
public class UnitAvailabilityController implements FxController {

    @FXML
    private ListView<UnitShard> searchResultView;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    private ObservableList<UnitShard> searchResult;

    public String url;

    @FXML
    void initialize() {
        searchResult = FXCollections.observableList(new ArrayList<UnitShard>());
        searchResultView.setItems(searchResult);
        searchResultView.setCellFactory(new HtmlCell.Factory());
        searchButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if ( searchField.getText() != null ) return;
                UnitShard unit = WebClient.create(url).type(MediaType.APPLICATION_XML).path(searchField.getText()).get(UnitShard.class);
                searchResult.add(0, unit);
                System.out.println(searchResult.size());
            }
        });
    }

}
