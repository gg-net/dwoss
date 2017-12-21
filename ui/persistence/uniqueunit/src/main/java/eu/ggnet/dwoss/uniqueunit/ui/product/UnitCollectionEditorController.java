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
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.util.Callback;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.uniqueunit.api.PicoProduct;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.assist.UnitCollectionDto;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ui.ProductTask;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.FxSaft;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 *
 * @author jens.papenhagen
 */
@Title("uniqueUnitCollection Editor")
public class UnitCollectionEditorController implements Initializable, FxController, Consumer<UnitCollection>, ResultProducer<UnitCollectionDto> {

    private final Pattern decimalPattern = Pattern.compile("-?\\d*(\\,\\d{0,2})?");

    public static final DataFormat df = new DataFormat("dw/uniqueUnitCollection");

    private static final Logger L = LoggerFactory.getLogger(UnitCollectionEditorController.class);

    private UnitCollectionFx unitCollectionFx;

    private UnitCollectionDto unitCollectionDto;

    private final ProductTask productsTask = new ProductTask();

    @FXML
    private TextField name;

    @FXML
    private Label producatnamefix;

    @FXML
    private TextArea description;

    @FXML
    private ChoiceBox<SalesChannel> salesChannel;

    @FXML
    private TextField priceInput;

    @FXML
    private TextField partNo;

    @FXML
    private ListView<PicoUnit> listViewUnits;

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void cancel(ActionEvent event) {
        Ui.closeWindowOf(name);
    }

    @FXML
    /**
     * Close the Editor window and discard all changes.
     */
    private void save(ActionEvent event) {

        unitCollectionDto = new UnitCollectionDto();

        if ( StringUtils.isBlank(name.getText()) ) {
            UiAlert.message("Es muss ein Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }

        unitCollectionDto.setId(unitCollectionFx.getId());
        unitCollectionDto.setNameExtension(unitCollectionFx.getNameExtensionProperty().get());
        unitCollectionDto.setDescriptionExtension(unitCollectionFx.getDescriptionExtensionProperty().get());
        unitCollectionDto.setPartNoExtension(unitCollectionFx.getPartNoExtensionProperty().get());
        //product will not be alterable
        unitCollectionDto.setUnits(new ArrayList<>(unitCollectionFx.getUnitsProperty()));
        unitCollectionDto.setPrices(new HashMap<>(unitCollectionFx.getPricesProperty()));        
        unitCollectionDto.setPriceHistories(new ArrayList<>(unitCollectionFx.getPriceHistoriesProperty()));
        unitCollectionDto.setSalesChannel(unitCollectionFx.getSalesChannelProperty().get());


        Ui.closeWindowOf(name);
    }

    @Override
    /**
     * Adding the filters to the combo box. Setting the cell values and the
     * filtered list containing the data.
     */
    public void initialize(URL url, ResourceBundle rb) {
        salesChannel.getItems().addAll(SalesChannel.values());
        salesChannel.getSelectionModel().selectFirst();

        producatnamefix.setText("");

        listViewUnits.setCellFactory(new Callback<ListView<PicoUnit>, ListCell<PicoUnit>>() {
            @Override
            public ListCell<PicoUnit> call(ListView<PicoUnit> p) {
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
            }
        });

        // force the field to be numeric only
        priceInput.textFormatterProperty().set(new TextFormatter<>(changeed -> {
            if ( decimalPattern.matcher(changeed.getControlNewText()).matches() ) {
                return changeed;
            } else {
                return null;
            }
        }));

        Ui.progress().observe(productsTask);
        Ui.exec(productsTask);

    }

    public void closed() {
        FxSaft.dispatch(() -> {
            if ( productsTask.isRunning() ) productsTask.cancel();
            return null;
        });
    }

    /**
     * Create a UnitCollectionFx based on the values from cp. Bind the
     * UnitCollectionFx with the ui components.
     *
     * @param uc received UnitCollection.
     */
    public void setUnitCollection(UnitCollection uc) {
        unitCollectionFx = new UnitCollectionFx(uc.getId(),
                uc.getNameExtension(),
                uc.getDescriptionExtension(),
                uc.getPartNoExtension(),
                new PicoProduct(uc.getProduct().getId(), uc.getProduct().getName()),
                uc.getUnits().stream().map(u -> new PicoUnit(u.getId(), (String)u.getIdentifier(Identifier.SERIAL) + " || " + u.getCondition().getNote()))
                        .collect(Collectors.toList()),
                uc.getPrices(),
                uc.getPriceHistory(),
                uc.getSalesChannel());

        name.textProperty().bindBidirectional(unitCollectionFx.getNameExtensionProperty());
        partNo.textProperty().bindBidirectional(unitCollectionFx.getPartNoExtensionProperty());
        description.textProperty().bindBidirectional(unitCollectionFx.getDescriptionExtensionProperty());
        salesChannel.valueProperty().bindBidirectional(unitCollectionFx.getSalesChannelProperty());

        listViewUnits.setItems(unitCollectionFx.getUnitsProperty());

    }

    @Override
    public void accept(UnitCollection t) {
        setUnitCollection(t);
    }

    @Override
    public UnitCollectionDto getResult() {
        if ( unitCollectionDto == null ) {
            return null;
        }
        return unitCollectionDto;
    }

}
