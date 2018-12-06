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
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;

import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.upi.CachedMandators;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.experimental.auth.Guardian;

import lombok.NonNull;

/**
 *
 * @author jacob.weinhold
 */
public class MandatorMetaDataController implements Initializable, FxController, Consumer<MandatorMetadata>, ResultProducer<MandatorMetadata> {

    public static class SelectableSalesChannel {

        private final ReadOnlyObjectWrapper<SalesChannel> salesChannelProperty = new ReadOnlyObjectWrapper<>(this, "salesChannel");

        private final BooleanProperty selected = new SimpleBooleanProperty(this, "selected", false);

        public SelectableSalesChannel(SalesChannel s) {
            salesChannelProperty.set(s);
        }

        public final SalesChannel getSalesChannel() {
            return salesChannelProperty.get();
        }

        public ReadOnlyObjectProperty<SalesChannel> salesChannelProperty() {
            return salesChannelProperty.getReadOnlyProperty();
        }

        public final boolean isSelected() {
            return selected.get();
        }

        public final void setSelected(boolean value) {
            selected.set(value);
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

    }

    private static class ToWayOnlyConverter<T> extends StringConverter<T> {

        private final Function<T, String> toFunction;

        public ToWayOnlyConverter(Function<T, String> toFunction) {
            this.toFunction = toFunction;
        }

        @Override
        public String toString(T object) {
            return toFunction.apply(object);
        }

        @Override
        public T fromString(String string) {
            throw new UnsupportedOperationException("From not implemented");
        }

    }

    @FXML
    private ComboBox<ShippingCondition> shippingConditionComboBox;

    @FXML
    private ComboBox<PaymentCondition> paymentConditionComboBox;

    @FXML
    private ComboBox<PaymentMethod> paymentMethodComboBox;

    @FXML
    private ListView<SelectableSalesChannel> defaultSalesChannelsListView;

    @FXML
    private ListView<SelectableSalesChannel> allowedSalesChannelsListView;

    @FXML
    private TextField defaultshippingConditionTextField;

    @FXML
    private TextField defaultpaymentConditionTextField;

    @FXML
    private TextField defaultpaymentMethodTextField;

    private MandatorMetadata mandatorMetaData;

    private DefaultCustomerSalesdata defaultCsd;

    private boolean isCanceled = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        EnumSet<SalesChannel> visibleSalseChannels = EnumSet.complementOf(EnumSet.of(SalesChannel.UNKNOWN));

        defaultSalesChannelsListView.setItems(visibleSalseChannels.stream()
                .map(s -> new SelectableSalesChannel(s))
                .collect(Collectors.toCollection(() -> FXCollections.observableArrayList())));
        defaultSalesChannelsListView.setCellFactory(CheckBoxListCell.forListView(SelectableSalesChannel::selectedProperty, new ToWayOnlyConverter<>(s -> s.getSalesChannel().getName())));

        allowedSalesChannelsListView.setItems(visibleSalseChannels.stream()
                .map(s -> new SelectableSalesChannel(s))
                .collect(Collectors.toCollection(() -> FXCollections.observableArrayList())));
        allowedSalesChannelsListView.setCellFactory(CheckBoxListCell.forListView(SelectableSalesChannel::selectedProperty, new ToWayOnlyConverter<>(s -> s.getSalesChannel().getName())));

        shippingConditionComboBox.getItems().setAll(ShippingCondition.values());
        paymentConditionComboBox.getItems().setAll(PaymentCondition.values());
        paymentMethodComboBox.getItems().setAll(PaymentMethod.values());

        shippingConditionComboBox.setConverter(new ToWayOnlyConverter<>(s -> s.getName()));
        paymentConditionComboBox.setConverter(new ToWayOnlyConverter<>(s -> s.getNote()));
        paymentMethodComboBox.setConverter(new ToWayOnlyConverter<>(s -> s.getNote()));

        paymentConditionComboBox.setCellFactory((ListView<PaymentCondition> l) -> new ListCell<PaymentCondition>() {

            @Override
            protected void updateItem(PaymentCondition item, boolean empty) {
                super.updateItem(item, empty);
                if ( item == null || empty ) {
                    setGraphic(null);
                } else {
                    setText(item.getNote());
                }
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

        defaultCsd = Dl.local().lookup(CachedMandators.class).loadSalesdata();
        defaultshippingConditionTextField.setText(defaultCsd.getShippingCondition().getName());
        defaultpaymentConditionTextField.setText(defaultCsd.getPaymentCondition().getNote());
        defaultpaymentMethodTextField.setText(defaultCsd.getPaymentMethod().getNote());
        defaultSalesChannelsListView.getItems().forEach(i -> {
            if ( defaultCsd.getAllowedSalesChannels().contains(i.getSalesChannel()) ) i.setSelected(true);
        });

        Guardian guardian = Dl.local().lookup(Guardian.class);
        guardian.add(new NodeEnabler(AtomicRight.UPDATE_CUSTOMER_PAYMENT_CONDITION, paymentConditionComboBox));
        guardian.add(new NodeEnabler(AtomicRight.UPDATE_CUSTOMER_PAYMENT_METHOD, paymentMethodComboBox));
        guardian.add(new NodeEnabler(AtomicRight.UPDATE_CUSTOMER_SHIPPING_CONDITION, shippingConditionComboBox));

    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        mandatorMetaData.setShippingCondition(shippingConditionComboBox.getValue());
        mandatorMetaData.setPaymentCondition(paymentConditionComboBox.getValue());
        mandatorMetaData.setPaymentMethod(paymentMethodComboBox.getValue());

        mandatorMetaData.getAllowedSalesChannels().clear();
        mandatorMetaData.getAllowedSalesChannels().addAll(allowedSalesChannelsListView.getItems()
                .stream()
                .filter(s -> s.isSelected())
                .map(s -> s.getSalesChannel())
                .collect(Collectors.toList()));
        isCanceled = false;
        Ui.closeWindowOf(defaultSalesChannelsListView);
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        isCanceled = true;
        Ui.closeWindowOf(defaultSalesChannelsListView);
    }

    @FXML
    private void handleResetButtonAction(ActionEvent event) {
        allowedSalesChannelsListView.getItems()
                .forEach(s -> s.setSelected(defaultCsd.getAllowedSalesChannels().contains(s.getSalesChannel())));
        shippingConditionComboBox.getSelectionModel().select(defaultCsd.getShippingCondition());
        paymentConditionComboBox.getSelectionModel().select(defaultCsd.getPaymentCondition());
        paymentMethodComboBox.getSelectionModel().select(defaultCsd.getPaymentMethod());
    }

    @Override
    public void accept(@NonNull MandatorMetadata consumable) {
        this.mandatorMetaData = consumable;

        this.paymentConditionComboBox.getSelectionModel().select(mandatorMetaData.getPaymentCondition());
        this.paymentMethodComboBox.getSelectionModel().select(mandatorMetaData.getPaymentMethod());
        this.shippingConditionComboBox.getSelectionModel().select(mandatorMetaData.getShippingCondition());

        if (!mandatorMetaData.getAllowedSalesChannels().isEmpty()) {// Empty = default
        allowedSalesChannelsListView.getItems()
                .forEach(s -> s.setSelected(mandatorMetaData.getAllowedSalesChannels().contains(s.getSalesChannel())));
        }
    }

    @Override
    public MandatorMetadata getResult() {
        if ( isCanceled ) return null;
        return mandatorMetaData;
    }
}
