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

import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.SalesChannel;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;

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

        CustomerGenerator gen = new CustomerGenerator();
        MandatorMetadata m = gen.makeMandatorMetadata();
        defaultCustomerSalesdata = new DefaultCustomerSalesdata(m.getShippingCondition(), m.getPaymentCondition(), m.getPaymentMethod(), m.getAllowedSalesChannels(), new ArrayList(m.getAllowedSalesChannels()));
        this.setDefaultValues(defaultCustomerSalesdata);

        saveButton.setDisable(true);

        shippingConditionComboBox.getItems().setAll(ShippingCondition.values());
        paymentConditionComboBox.getItems().setAll(PaymentCondition.values());
        paymentMethodComboBox.getItems().setAll(PaymentMethod.values());

        shippingConditionComboBox.setConverter(new StringConverter<ShippingCondition>() {
            @Override
            public ShippingCondition fromString(String string) {
                throw new UnsupportedOperationException("fromString is not supported");

            }

            @Override
            public String toString(ShippingCondition myClassinstance) {
                return myClassinstance.toString();
            }
        });

        paymentConditionComboBox.setConverter(new StringConverter<PaymentCondition>() {
            @Override
            public PaymentCondition fromString(String string) {
                throw new UnsupportedOperationException("fromString is not supported");
            }

            @Override
            public String toString(PaymentCondition myClassinstance) {
                return myClassinstance.getNote();
            }
        });
        paymentMethodComboBox.setConverter(new StringConverter<PaymentMethod>() {
            @Override
            public PaymentMethod fromString(String string) {
                throw new UnsupportedOperationException("fromString is not supported");
            }

            @Override
            public String toString(PaymentMethod myClassinstance) {
                return myClassinstance.getNote();
            }
        });
        paymentConditionComboBox.setCellFactory(new Callback<ListView<PaymentCondition>, ListCell<PaymentCondition>>() {
            @Override
            public ListCell<PaymentCondition> call(ListView<PaymentCondition> l) {
                return new ListCell<PaymentCondition>() {
                    @Override
                    public String toString() {
                        return this.toString();
                    }

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

        paymentMethodComboBox.setCellFactory((ListView<PaymentMethod> l) -> new ListCell<PaymentMethod>() {
            @Override
            protected void updateItem(PaymentMethod item, boolean empty) {
                super.updateItem(item, empty);
                if ( item == null || empty ) {
                    setGraphic(null);
                } else {
                    setText(item.getNote());
                }
            }
        });

        InvalidationListener saveButtonDisablingListener = new InvalidationListener() {

            @Override
            public void invalidated(javafx.beans.Observable observable) {

                if ( shippingConditionComboBox.getSelectionModel().isEmpty() && paymentConditionComboBox.getSelectionModel().isEmpty()
                        && paymentMethodComboBox.getSelectionModel().isEmpty()
                        && allowedSalesChannelCheckBoxList.stream().noneMatch(CheckBox::isSelected) )
                    saveButton.setDisable(true);

                else
                    saveButton.setDisable(false);

            }

        };

        shippingConditionComboBox.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        paymentConditionComboBox.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        paymentMethodComboBox.getSelectionModel().selectedItemProperty().addListener(saveButtonDisablingListener);
        allowedSalesChannelCheckBoxList.forEach(e -> e.selectedProperty().addListener(saveButtonDisablingListener));

    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {

        mandatorMetaData.clearSalesChannels();
        mandatorMetaData.setShippingCondition(shippingConditionComboBox.getValue());
        mandatorMetaData.setPaymentCondition(paymentConditionComboBox.getValue());
        mandatorMetaData.setPaymentMethod(paymentMethodComboBox.getValue());

        allowedSalesChannelCheckBoxList.stream()
                .filter((checkBox) -> (checkBox.isSelected()))
                .map(checkBox -> {
                    return Arrays.stream(SalesChannel.values())
                            .filter(salesChannel -> salesChannel.getName().equals(checkBox.getText()))
                            .findFirst()
                            .get();
                })
                .forEach(salesChannel -> mandatorMetaData.add(salesChannel));
        Ui.closeWindowOf(saveButton);
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        mandatorMetaData = null;
        Ui.closeWindowOf(saveButton);
    }

    @Override
    public void accept(final MandatorMetadata consumable) {
        this.mandatorMetaData = consumable;

        this.paymentConditionComboBox.getSelectionModel().select(mandatorMetaData.getPaymentCondition());
        this.paymentMethodComboBox.getSelectionModel().select(mandatorMetaData.getPaymentMethod());
        this.shippingConditionComboBox.getSelectionModel().select(mandatorMetaData.getShippingCondition());

        mandatorMetaData.getAllowedSalesChannels().forEach(salesChannel -> {
            this.allowedSalesChannelCheckBoxList.forEach(checkBox -> {
                if ( checkBox.getText().equals(salesChannel.getName()) )
                    checkBox.setSelected(true);

            });
        });

    }

    @Override
    public MandatorMetadata getResult() {
        return this.mandatorMetaData;
    }

    private void setSalesChannelBoxesUp() {
        Arrays.stream(SalesChannel.values())
                .filter(salesChannel -> salesChannel != SalesChannel.UNKNOWN)
                .map(salesChannel -> new CheckBox(salesChannel.getName()))
                .forEach(checkBox -> allowedSalesChannelCheckBoxList.add(checkBox));

        defaultCustomerSalesdata.getAllowedSalesChannels().stream()
                .filter(salesChannel -> salesChannel != SalesChannel.UNKNOWN)
                .map(salesChannel -> new CheckBox(salesChannel.getName()))
                .forEach(checkBox -> {
                    checkBox.setSelected(true);
                    checkBox.setDisable(true);
                    defaultAllowedSalesChannelCheckBoxList.add(checkBox);
                });

        allowedSalesChannelsVBox.getChildren().setAll(allowedSalesChannelCheckBoxList);
        defaultAllowedSalesChannelsVBox.getChildren().setAll(defaultAllowedSalesChannelCheckBoxList);
    }

    private void setDefaultValues(DefaultCustomerSalesdata defaultCustomerSalesdata) {
        this.defaultCustomerSalesdata = defaultCustomerSalesdata;
        setSalesChannelBoxesUp();

        defaultshippingConditionTextField.setText(defaultCustomerSalesdata.getShippingCondition().name());
        defaultpaymentConditionTextField.setText(defaultCustomerSalesdata.getPaymentCondition().getNote());
        defaultpaymentMethodTextField.setText(defaultCustomerSalesdata.getPaymentMethod().getNote());
    }

}
