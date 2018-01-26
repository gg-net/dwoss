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

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.entity.MandatorMetadata;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 *
 * @author jacob.weinhold
 */
public class MandatorMetaDataController implements Initializable, FxController, Consumer<MandatorMetadata>, ResultProducer<MandatorMetadata> {

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<ShippingCondition> shippingConditionComboBox;

    @FXML
    private ComboBox<PaymentCondition> paymentConditionComboBox;

    @FXML
    private ComboBox<PaymentMethod> paymentMethodComboBox;

    @FXML
    private VBox allowedSalesChannelsVBox;

    @FXML
    private TextField defaultshippingConditionTextField;

    @FXML
    private TextField defaultpaymentConditionTextField;

    @FXML
    private TextField defaultpaymentMethodTextField;

    @FXML
    private VBox defaultAllowedSalesChannelsVBox;

    private ObservableList<CheckBox> allowedSalesChannelCheckBoxList = FXCollections.observableArrayList();

    private ObservableList<CheckBox> defaultAllowedSalesChannelCheckBoxList = FXCollections.observableArrayList();

    private MandatorMetadata mandatorMetaData;

    private DefaultCustomerSalesdata defaultCustomerSalesdata;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shippingConditionComboBox.getItems().setAll(ShippingCondition.values());
        paymentConditionComboBox.getItems().setAll(PaymentCondition.values());

        paymentConditionComboBox.setCellFactory(new Callback<ListView<PaymentCondition>, ListCell<PaymentCondition>>() {
            @Override
            public ListCell<PaymentCondition> call(ListView<PaymentCondition> l) {
                return new ListCell<PaymentCondition>() {
                    @Override
                    protected void updateItem(PaymentCondition item, boolean empty) {
                        super.updateItem(item, empty);
                        if ( item == null || empty ) {
                            setGraphic(null);
                        } else {
                            setText(item.getNote());
                        }
                    }
                };
            }
        });

        paymentMethodComboBox.setCellFactory(new Callback<ListView<PaymentMethod>, ListCell<PaymentMethod>>() {
            @Override
            public ListCell<PaymentMethod> call(ListView<PaymentMethod> l) {
                return new ListCell<PaymentMethod>() {
                    @Override
                    protected void updateItem(PaymentMethod item, boolean empty) {
                        super.updateItem(item, empty);
                        if ( item == null || empty ) {
                            setGraphic(null);
                        } else {
                            setText(item.getNote());
                        }
                    }
                };
            }
        });

        paymentMethodComboBox.getItems().setAll(PaymentMethod.values());

        defaultshippingConditionTextField.setText(ShippingCondition.DEFAULT.name());
        defaultpaymentConditionTextField.setText(PaymentCondition.DEALER.getNote());
        defaultpaymentMethodTextField.setText(PaymentMethod.ADVANCE_PAYMENT.getNote());

        CustomerGenerator gen = new CustomerGenerator();
        MandatorMetadata m = gen.makeMandatorMetadata();
        DefaultCustomerSalesdata defaultData = new DefaultCustomerSalesdata(m.getShippingCondition(), m.getPaymentCondition(), m.getPaymentMethod(), m.getAllowedSalesChannels(), new ArrayList(m.getAllowedSalesChannels()));
        this.setDefaults(defaultData);

    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {

        if ( shippingConditionComboBox.getValue() == null
                || paymentConditionComboBox.getValue() == null
                || paymentMethodComboBox.getValue() == null ) {
            UiAlert.title("Fehler!").message("Speichern der Mandantendaten nicht möglich.")
                    .nl("Es müssen Versandkondition, Zahlungskondition und Zahlungsmodalität gesetzt sein.")
                    .parent(saveButton).show(UiAlertBuilder.Type.ERROR);
            return;

        }

        if ( allowedSalesChannelCheckBoxList.stream().noneMatch(CheckBox::isSelected) ) {
            UiAlert.title("Fehler!").message("Speichern der Mandantendaten nicht möglich.")
                    .nl("Es ist kein Verkaufskanal angegeben.")
                    .parent(saveButton).show(UiAlertBuilder.Type.ERROR);
            return;
        }

        mandatorMetaData.setShippingCondition(shippingConditionComboBox.getValue());
        mandatorMetaData.setPaymentCondition(paymentConditionComboBox.getValue());
        mandatorMetaData.setPaymentMethod(paymentMethodComboBox.getValue());

        allowedSalesChannelCheckBoxList.stream().filter((checkBox) -> (checkBox.isSelected())).forEach((checkBox) -> {
            mandatorMetaData.add(SalesChannel.valueOf(checkBox.getText()));
        });

    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {

    }

    @Override
    public void accept(MandatorMetadata consumable) {
        this.mandatorMetaData = consumable;
    }

    @Override
    public MandatorMetadata getResult() {
        return this.mandatorMetaData;
    }

    private void setSalesChannelBoxesUp() {

        for (SalesChannel salesChannel : SalesChannel.values()) {

            CheckBox checkBox = new CheckBox(salesChannel.getName());
            allowedSalesChannelCheckBoxList.add(checkBox);
        }

        for (SalesChannel allowedSalesChannel : defaultCustomerSalesdata.getAllowedSalesChannels()) {
            CheckBox checkBox = new CheckBox(allowedSalesChannel.getName());
            checkBox.setSelected(true);
            checkBox.setDisable(true);
            defaultAllowedSalesChannelCheckBoxList.add(checkBox);
        }

        allowedSalesChannelsVBox.getChildren().setAll(allowedSalesChannelCheckBoxList);
        defaultAllowedSalesChannelsVBox.getChildren().setAll(defaultAllowedSalesChannelCheckBoxList);

    }

    private void setDefaults(DefaultCustomerSalesdata defaultCustomerSalesdata) {
        this.defaultCustomerSalesdata = defaultCustomerSalesdata;
        setSalesChannelBoxesUp();

    }

}
