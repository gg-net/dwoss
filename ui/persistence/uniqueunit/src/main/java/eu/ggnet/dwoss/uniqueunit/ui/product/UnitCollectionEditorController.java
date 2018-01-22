/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.uniqueunit.ui.product;

import eu.ggnet.saft.api.ui.FxController;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.entity.dto.UnitCollectionDto;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.dwoss.uniqueunit.ui.product.UnitCollectionFx.Price;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.FxSaft;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller for the UnitCollection Editor 
 * 
 * @author jens.papenhagen
 */
@Title("Geräte Sammlungen Editieren")
public class UnitCollectionEditorController implements Initializable, FxController, Consumer<UnitCollection>, ResultProducer<UnitCollectionDto> {

    private final Pattern decimalPattern = Pattern.compile("-?\\d*(\\,\\d{0,2})?");

    private static final Logger L = LoggerFactory.getLogger(UnitCollectionEditorController.class);

    private final UnitCollectionFx ucFx = new UnitCollectionFx();

    private UnitCollectionDto result = null;

    private final ProductTask productsTask = new ProductTask();

    @FXML
    private ChoiceBox<SalesChannel> salesChannel;

    @FXML
    private ChoiceBox<PriceType> priceType;

    @FXML
    private TextField priceInput;

    @FXML
    private ListView<Price> listViewPrices;

    @FXML
    private ListView<PicoUnit> listViewUnits;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField nameExtension;

    @FXML
    private TextArea descriptionExtension;

    @FXML
    private TextField partNoExtension;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void cancel(ActionEvent event) {
        Ui.closeWindowOf(nameExtension);
    }

    public void closed() {
        FxSaft.dispatch(() -> {
            if ( productsTask.isRunning() ) productsTask.cancel();
            return null;
        });
    }

    @FXML
    /**
     * Save the UnitCollection.
     */
    private void save(ActionEvent event) {
        if ( StringUtils.isBlank(nameExtension.getText()) ) {
            UiAlert.message("Es muss ein Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }

        result = UnitCollectionFxMapper.INSTANCE.to(ucFx);
        Ui.closeWindowOf(nameExtension);
    }

    @FXML
    /**
     * Add a price to the UnitCollection based on the selected PriceType and
     * the value in priceInput. Both values must be set to be able to add a
     * price.
     */
    private void addPrice(ActionEvent event) {
        if ( priceType.getSelectionModel().getSelectedItem() != null && priceInput.textProperty().isNotEmpty().get() ) {
            //check if the pricetype is in the list
            for (Price items : listViewPrices.getItems()) {
                if ( items.getType() == priceType.getSelectionModel().getSelectedItem() ) {
                    UiAlert.message("Dieser Preistyp wurde schon angelegt").show(UiAlertBuilder.Type.WARNING);
                    return;
                }
            }
            listViewPrices.getItems().add(new Price(priceType.getSelectionModel().getSelectedItem(), Double.parseDouble(priceInput.getText().replace(",", "."))));
            L.info("added Price");
        }
    }

    /**
     * Removes a Price from the UnitCollection. A remove simply means setting
     * the value to 0.
     */
    private void removePrice() {
        if ( listViewPrices.getSelectionModel().getSelectedItem() == null ) return;
        L.info("Removed Price {}", listViewPrices.getSelectionModel().getSelectedItem());

        listViewPrices.getItems().remove(listViewPrices.getSelectionModel().getSelectedItem());

        L.info("All Prices {}", ucFx.getPrices());

    }

    @Override
    /**
     * Fill all ChoiceBoxes adding the Conetex Menu to the price list
     * froce the input in the price input
     */
    public void initialize(URL url, ResourceBundle rb) {
        salesChannel.getItems().addAll(SalesChannel.values());
        salesChannel.getSelectionModel().selectFirst();

        priceType.getItems().addAll(PriceType.values());
        priceType.getSelectionModel().selectFirst();

        //Units CellFactory
        listViewUnits.setCellFactory((ListView<PicoUnit> p) -> {
            ListCell<PicoUnit> cell = new ListCell<PicoUnit>() {
                @Override
                protected void updateItem(PicoUnit t, boolean bln) {
                    super.updateItem(t, bln);
                    if ( t != null ) {
                        setText(t.getShortDescription());
                    } else {
                        setText("");
                    }
                }
            };
            return cell;
        });

        //Price CellFactory
        listViewPrices.setCellFactory((ListView<Price> p) -> {
            ListCell<Price> cell = new ListCell<Price>() {
                @Override
                protected void updateItem(Price p, boolean bln) {
                    super.updateItem(p, bln);
                    if ( p != null ) {
                        setText(p.getType().name() + ": " + p.getValue());
                    } else {
                        setText("");
                    }
                }
            };
            return cell;
        });

        // force the field to be numeric only
        priceInput.textFormatterProperty().set(new TextFormatter<>(changeed -> {
            if ( decimalPattern.matcher(changeed.getControlNewText()).matches() ) {
                return changeed;
            } else {
                return null;
            }
        }));

        //Create a ContextMenu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem delete = new MenuItem("Lösche Preis");
        //actions for the context menu
        delete.setOnAction((ActionEvent event) -> {
            removePrice();
        });

        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(delete);

        //add contextmenu to listview
        listViewPrices.setContextMenu(contextMenu);

        nameExtension.textProperty().bindBidirectional(ucFx.nameExtensionProperty());
        partNoExtension.textProperty().bindBidirectional(ucFx.partNoExtensionProperty());
        descriptionExtension.textProperty().bindBidirectional(ucFx.descriptionExtensionProperty());

        salesChannel.valueProperty().bindBidirectional(ucFx.salesChannelProperty());

        listViewPrices.setItems(ucFx.getPrices());

        //only allow to safe button on input of nameExtension and partNoExtension
        saveButton.disableProperty().bind(nameExtension.textProperty().isEmpty().and(partNoExtension.textProperty().isEmpty()));
    }

    @Override
    public void accept(UnitCollection uc) {
        UnitCollectionFxMapper.INSTANCE.update(ucFx, uc);
        listViewUnits.setItems(FXCollections.observableArrayList(ucFx.getUnits()));
    }

    @Override
    public UnitCollectionDto getResult() {
        return result;
    }

}
