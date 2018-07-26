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

import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.FxController;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.saft.core.Ui;

import lombok.*;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Controller class for the editor view of a Customer. Allows the user to
 * change all values of the Customer.
 *
 * @author jens.papenhagen
 */
@Title("Erweiterte Kunden bearbeiten")
public class CustomerEnhanceController implements Initializable, FxController, Consumer<Customer>, ResultProducer<Customer> {

    @FXML
    private ChoiceBox<Source> sourceChoiceBox;

    @FXML
    private Label customerIdLabel;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label customerTypeLabel;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private ListView<CustomerFlagWithSelect> flagListView;

    @FXML
    private HBox showHBox;

    @FXML
    private Label contactOrCompanyLabel;

    @FXML
    private TextField keyAccounterTextField;

    @FXML
    private Label nameOrCompanyLabel;

    // Used in business show box.
    private ListView<Company> companyListView;

    // Used in non business customer
    private ListView<Contact> contactListView;

    private ObservableList<CustomerFlagWithSelect> customerFlagsWithSelect;

    @FXML
    private ListView<AdditionalCustomerId> additionalCustomerIdsListView;

    @FXML
    private Button addAdditionalCustomerIdButton;

    @FXML
    private Button editdditionalCustomerIdButton;

    @FXML
    private Button deletedditionalCustomerIdButton;

    private ObservableList<AdditionalCustomerId> additionalCustomerIds;

    private ObservableList<Company> companyList = FXCollections.observableArrayList();

    private ObservableList<Contact> contactList = FXCollections.observableArrayList();

    private ObservableList<MandatorMetadata> mandatorMetadata = FXCollections.observableArrayList();

    private boolean isBusinessCustomer = false;

    private Customer customer;

    private boolean isCanceled = true;

    @FXML
    private void clickSaveButton(ActionEvent event) {
        isCanceled = false;
        Ui.closeWindowOf(customerIdLabel);
    }

    @FXML
    private void clickCancelButton(ActionEvent event) {
        isCanceled = true;
        Ui.closeWindowOf(customerIdLabel);
    }

    @FXML
    private void clickSelectPreferedAddressLabelsButton(ActionEvent event) {
        Ui.build(commentTextArea).fxml().eval(() -> getCustomer(), PreferedAddressLabelsController.class)
                .cf()
                .thenApply(ui -> CustomerConnectorFascade.updateAddressLabels(customer.getId(), ui.getInvoiceLabel(), ui.getShippingLabel()))
                .thenAcceptAsync(c -> accept(c), Platform::runLater)
                .handle(Ui.handler());
    }

    /**
     * open a new window of an MandatorMetaData editor
     *
     * @todo select the proper MandatorMetaData instance by MatchCode
     * @param event
     */
    @FXML
    private void clickMandatorMetaDataButton(ActionEvent event) {

        Ui.exec(() -> {
            Ui.build(commentTextArea).fxml().eval(() -> getCustomer().getMandatorMetadata().get(0), MandatorMetaDataController.class
            )
                    .opt()
                    .ifPresent(newMandatorMetaData -> {
                        MandatorMetadata oldMandatorMetadata = getCustomer().getMandatorMetadata().get(0);
                        oldMandatorMetadata.setMandatorMatchcode(newMandatorMetaData.getMandatorMatchcode());
                        oldMandatorMetadata.setPaymentCondition(newMandatorMetaData.getPaymentCondition());
                        oldMandatorMetadata.setPaymentMethod(newMandatorMetaData.getPaymentMethod());
                        oldMandatorMetadata.setShippingCondition(newMandatorMetaData.getShippingCondition());
                    });
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO add button behavior see the RULES on getViolationMessage() in Customer, enable only on vaild customer

        customerFlagsWithSelect = FXCollections.observableArrayList();

        //for each CustomerFlag create a CustomerFlagWithSelect. If the customer contains the flag it is selected.
        for (CustomerFlag flag : CustomerFlag.values()) {
            customerFlagsWithSelect.add(new CustomerFlagWithSelect(flag));
        }

        flagListView.setItems(customerFlagsWithSelect);

        flagListView.setCellFactory(CheckBoxListCell.forListView(CustomerFlagWithSelect::selectedProperty, new StringConverter<CustomerFlagWithSelect>() {
            @Override
            public String toString(CustomerFlagWithSelect object) {
                return object.getFlag().getName();
            }

            @Override
            public CustomerFlagWithSelect fromString(String string) {
                return null;
            }
        }));

        sourceChoiceBox.getItems().addAll(Source.values());
        sourceChoiceBox.setConverter(new StringConverter<Source>() {
            @Override
            public String toString(Source object) {
                return object.getName();
            }

            @Override
            public Source fromString(String string) {
                throw new UnsupportedOperationException("fromString is not supported"); //
            }
        });

        additionalCustomerIds = FXCollections.observableArrayList();
        additionalCustomerIdsListView.setItems(additionalCustomerIds);
        additionalCustomerIdsListView.setCellFactory((ListView<AdditionalCustomerId> p) -> {
            ListCell<AdditionalCustomerId> cell = new ListCell<AdditionalCustomerId>() {
                @Override
                protected void updateItem(AdditionalCustomerId item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) {
                        setGraphic(null);
                        setText("");
                    } else {
                        HBox flagbox = new HBox();
                        Label flagLabel = new Label(item.type.name() + ":");
                        flagLabel.setPrefWidth(65.0);
                        flagLabel.setStyle("-fx-font-weight: bold");

                        Label customerIdLabel = new Label();
                        customerIdLabel.textProperty().bind(item.valueProperty());

                        flagbox.getChildren().addAll(flagLabel, customerIdLabel);
                        flagbox.setSpacing(2.0);

                        setText(null);
                        setGraphic(flagbox);

                    }
                }
            };
            return cell;
        });

        addAdditionalCustomerIdButton.setOnAction(new AdditionalCustomerIDsDialogHandler());
        // TODO: try bindings
        additionalCustomerIdsListView.getItems().addListener((javafx.beans.Observable observable) -> {
            addAdditionalCustomerIdButton.setDisable(additionalCustomerIdsListView.getItems().stream()
                    .map(additionalCustomerID -> additionalCustomerID.type)
                    .collect(Collectors.toList())
                    .containsAll(Arrays.asList(ExternalSystem.values())));
        });

        deletedditionalCustomerIdButton.setOnAction((event) -> {
            Ui.build(deletedditionalCustomerIdButton).dialog().eval(() -> {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Externe Kundennummer löschen");
                alert.setHeaderText("Bestätigen der Löschung einer Kundennummer");
                alert.setContentText("Wollen sie die Kundennummer wirklich löschen?");
                return alert;
            })
                    .cf()
                    .thenAcceptAsync(b -> {
                        if ( b == ButtonType.OK ) additionalCustomerIds.remove(additionalCustomerIdsListView.getSelectionModel().getSelectedItem());
                    }, Platform::runLater)
                    .handle(Ui.handler());
        });

        editdditionalCustomerIdButton.setOnAction(new AdditionalCustomerIDsDialogHandler(additionalCustomerIdsListView.getSelectionModel()));
        // TODO: try bindigns
        editdditionalCustomerIdButton.setDisable(true);
        deletedditionalCustomerIdButton.setDisable(true);

        additionalCustomerIdsListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            editdditionalCustomerIdButton.setDisable(newValue.intValue() < 0);
            deletedditionalCustomerIdButton.setDisable(newValue.intValue() < 0);
        });

    }

    /**
     * Sets the customer to edit.
     * This ui allways expects to edit an allready persisted customer.
     *
     * @param customer must not be null by definition.
     */
    @Override
    public void accept(@NonNull Customer customer) {
        if ( !customer.isValid() ) throw new IllegalArgumentException("Invalid Customer: " + customer.getViolationMessage());
        isBusinessCustomer = customer.isBusiness();
        setCustomer(customer);
    }

    @Override
    public Customer getResult() {
        if ( isCanceled ) return null;
        return customer;
    }

    // consider merge with accept
    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerNameLabel.setText(customer.toName());
        if ( customer.isBusiness() ) {
            customerTypeLabel.setText("Geschäftskunde");
            nameOrCompanyLabel.setText("Firma: ");
            contactOrCompanyLabel.setText("Firmen: ");
            companyList.setAll(customer.getCompanies());
            isBusinessCustomer = true;
        } else {
            customerTypeLabel.setText("Endkunde");
            nameOrCompanyLabel.setText("Name: ");
            contactOrCompanyLabel.setText("Kontakte: ");
            contactList.setAll(customer.getContacts());
        }

        customerIdLabel.setText("" + customer.getId());
        keyAccounterTextField.setText(customer.getKeyAccounter());

        customerFlagsWithSelect.forEach((cfws) -> {
            if ( customer.getFlags().contains(cfws.getFlag()) ) cfws.setSelected(true);
            else cfws.setSelected(false);
        });

        sourceChoiceBox.getSelectionModel().select(customer.getSource());

        // Possibly not right here
        mandatorMetadata.addAll(customer.getMandatorMetadata());

        //transfer the map into a List
        additionalCustomerIds.clear();
        additionalCustomerIds.addAll(
                customer.getAdditionalCustomerIds().entrySet().stream()
                        .map(e -> new AdditionalCustomerId(e.getKey(), e.getValue()))
                        .collect(Collectors.toList()));

        commentTextArea.setText(customer.getComment());

        //build the showbox
        buildShowBox();
    }

    @Deprecated // vermutlich
    public Customer getCustomer() {
        // consider updating all fields on event, not at the end.

        customer.setKeyAccounter(keyAccounterTextField.getText());

        customer.setSource(sourceChoiceBox.getSelectionModel().getSelectedItem());

        // possibly wrong here
        customer.getMandatorMetadata().clear();
        mandatorMetadata.forEach(m -> customer.getMandatorMetadata().add(m));

        customer.getFlags().clear();
        customer.getFlags().addAll(customerFlagsWithSelect.stream().filter(c -> c.isSelected()).map(c -> c.getFlag()).collect(Collectors.toSet()));

        //transfer List back to a Map
        customer.getAdditionalCustomerIds().clear();
        customer.getAdditionalCustomerIds().putAll(additionalCustomerIds.stream().collect(Collectors.toMap(AdditionalCustomerId::getType, AdditionalCustomerId::getValue)));

        customer.setComment(commentTextArea.getText());

        return customer;
    }

    /**
     * build the main show box
     * for business Customer with Companies
     * for consumer Customer with Contacts
     */
    private void buildShowBox() {
        //build up the Buttons
        VBox buttonVBox = new VBox();
        Button editButton = new Button("Ändern");
        Button addButton = new Button("Hinzufügen");
        Button delButton = new Button("Löschen");
        editButton.setMinWidth(80.0);
        addButton.setMinWidth(80.0);
        delButton.setMinWidth(80.0);

        //set the right actions for the buttons
        if ( isBusinessCustomer ) {
            companyListView = new ListView<>();
            companyListView.setItems(companyList);

            //cellcaftory for Company
            companyListView.setCellFactory((ListView<Company> p) -> {
                ListCell<Company> cell = new ListCell<Company>() {
                    @Override
                    protected void updateItem(Company item, boolean empty) {
                        super.updateItem(item, empty);
                        if ( item == null || empty ) {
                            setGraphic(null);
                            setText("");
                        } else {
                            setText(item.getName());
                        }
                    }
                };
                return cell;
            });
            companyListView.setMinWidth(450.0);
            HBox.setHgrow(companyListView, Priority.ALWAYS);

            editButton.setOnAction((ActionEvent e) -> {
                Company selectedItem = companyListView.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    editCompany(selectedItem);
                }

            });
            addButton.setOnAction((ActionEvent e) -> {
                Company c = new Company();
                c.setName("");
                Address a = new Address();
                a.setCity("");
                a.setStreet("");
                a.setZipCode("");
                c.getAddresses().add(a);

                addCompany(c);
            });
            delButton.setOnAction((ActionEvent e) -> {
                Company selectedItem = companyListView.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    companyList.remove(selectedItem);
                }
            });
            delButton.disableProperty().bind(companyListView.getSelectionModel().selectedIndexProperty().lessThan(0));
        } else {
            contactListView = new ListView<>();
            contactListView.setItems(contactList);
            //cellcaftory for Contacts
            contactListView.setCellFactory((ListView<Contact> p) -> {
                ListCell<Contact> cell = new ListCell<Contact>() {
                    @Override
                    protected void updateItem(Contact item, boolean empty) {
                        super.updateItem(item, empty);
                        if ( item == null || empty ) {
                            setGraphic(null);
                            setText("");
                        } else {
                            String anrede = "";
                            if ( item.getSex() == Sex.FEMALE ) {
                                anrede = "Frau ";
                            }
                            if ( item.getSex() == Sex.MALE ) {
                                anrede = "Herr ";
                            }
                            setText(anrede + item.toFullName());
                        }
                    }
                };
                return cell;
            });
            contactListView.setMinWidth(450.0);
            HBox.setHgrow(contactListView, Priority.ALWAYS);

            editButton.setOnAction((ActionEvent e) -> {
                Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
                if ( selectedItem == null ) return;
                Ui.build(commentTextArea).modality(WINDOW_MODAL).fxml().eval(() -> selectedItem, ContactUpdateController.class)
                        .cf()
                        .thenApply(c -> CustomerConnectorFascade.updateContactOnCustomer(customer.getId(), c))
                        .thenAcceptAsync(c -> accept(c), Platform::runLater)
                        .handle(Ui.handler());
            });
            addButton.setOnAction((ActionEvent e) -> {
                Ui.build(commentTextArea).modality(WINDOW_MODAL).fxml().eval(ContactAddController.class)
                        .cf()
                        .thenApply(c -> CustomerConnectorFascade.createContactOnCustomer(customer.getId(), c))
                        .thenAcceptAsync(c -> accept(c), Platform::runLater)
                        .handle(Ui.handler());
            });
            delButton.setOnAction((ActionEvent e) -> {
                Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
                if ( selectedItem == null ) return;

// TODO: Disallow deletion of contacts in an addresslabel.
                Ui.build(commentTextArea).dialog().eval(() -> {
                    Dialog<Contact> dialog = new Dialog<>();
                    dialog.setTitle("Löschen bestätigen");
                    dialog.setHeaderText("Möchten sie diese Kontakt wirklich löschen ?");
                    dialog.setContentText(contactListView.getSelectionModel().getSelectedItem().toString());
                    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
                    dialog.setResultConverter((bt) -> {
                        if ( bt == ButtonType.YES ) return contactListView.getSelectionModel().getSelectedItem();
                        return null;
                    });
                    return dialog;
                })
                        .cf()
                        .thenApply(add -> CustomerConnectorFascade.deleteContactOnCustomer(customer.getId(), add))
                        .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                        .handle(Ui.handler());
            });

            // diable if it is the last contact.
            delButton.disableProperty().bind(contactListView.getSelectionModel().selectedIndexProperty().lessThan(1));
        }

        buttonVBox.getChildren().addAll(editButton, addButton, delButton);
        buttonVBox.setSpacing(3.0);

        //build up the showBox
        showHBox.getChildren().clear();
        if ( isBusinessCustomer ) {
            showHBox.getChildren().addAll(companyListView, buttonVBox);
        } else {
            showHBox.getChildren().addAll(contactListView, buttonVBox);
        }
    }

    private void editCompany(Company company) {
        Ui.exec(() -> {
            Ui.build(commentTextArea).modality(WINDOW_MODAL).parent(customerNameLabel).fxml().eval(() -> company, CompanyUpdateController.class
            )
                    .opt()
                    .filter(a -> a != null)
                    .ifPresent(a -> Platform.runLater(() -> companyList.set(companyListView.getSelectionModel().getSelectedIndex(), a)));
        });

    }

    private void addCompany(Company company) {
        Ui.exec(() -> {
            Ui.build(commentTextArea).modality(WINDOW_MODAL).parent(customerNameLabel).fxml().eval(() -> company, CompanyUpdateController.class
            )
                    .opt()
                    .filter(a -> a != null)
                    .ifPresent(a -> Platform.runLater(() -> companyList.add(a)));
        });
    }

    class AdditionalCustomerId {

        @Getter
        @Setter
        private ExternalSystem type;

        private StringProperty valueProperty = new SimpleStringProperty(this, "value");

        public AdditionalCustomerId(ExternalSystem type, String value) {
            this.type = type;
            this.valueProperty.set(value);
        }

        public final String getValue() {
            return valueProperty.get();
        }

        public final void setValue(String value) {
            valueProperty.set(value);
        }

        public StringProperty valueProperty() {
            return valueProperty;
        }
        
        @Override
        public String toString() {
            return "System: " + type + " Kundennummer: " + valueProperty.get();
        }

    }

//extra class for the CheckBox ListView
    class CustomerFlagWithSelect {

        private final ReadOnlyObjectWrapper flag = new ReadOnlyObjectWrapper();

        private final BooleanProperty selected = new SimpleBooleanProperty(false);

        public CustomerFlagWithSelect(CustomerFlag flag) {
            this.flag.set(flag);
        }

        public CustomerFlag getFlag() {
            return (CustomerFlag)flag.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }
    }

    private class AdditionalCustomerIDsDialogHandler implements EventHandler<ActionEvent> {

        private MultipleSelectionModel<AdditionalCustomerId> selectionModel;

        public AdditionalCustomerIDsDialogHandler() {
        }

        public AdditionalCustomerIDsDialogHandler(MultipleSelectionModel<AdditionalCustomerId> selectionModel) {
            this.selectionModel = selectionModel;
        }

        @Override
        public void handle(ActionEvent event) {

            Dialog<AdditionalCustomerId> dialog = new Dialog<>();
            if ( selectionModel == null )
                dialog.setTitle("Zusätzliche Kundennummer hinzufügen.");
            else
                dialog.setTitle("Zusätzliche Kundennummer bearbeiten.");

            ButtonType addButtonType;
            String addButtonTitle = selectionModel == null ? "Hinzufügen" : "Änderungen speichern";
            addButtonType = new ButtonType(addButtonTitle, ButtonData.OK_DONE);

            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
            TextField customerId = new TextField();
            customerId.setPromptText("Kundennummer");
            customerId.setText(selectionModel == null ? "" : selectionModel.getSelectedItem().getValue());

            ChoiceBox externalSystemChoiceBox;
            if ( selectionModel != null ) {
                externalSystemChoiceBox = new ChoiceBox(FXCollections.observableArrayList(Arrays.asList(selectionModel.getSelectedItem().type)));
                externalSystemChoiceBox.getSelectionModel().select(0);

            } else {
                // filter ExternalSystem types which are already contained in the listView
                externalSystemChoiceBox = new ChoiceBox(Arrays.stream(ExternalSystem.values())
                        .filter(externalSystem
                                -> !additionalCustomerIdsListView.getItems()
                                .stream()
                                .map(additionalCustomerID -> additionalCustomerID.type)
                                .collect(Collectors.toList())
                                .contains(externalSystem))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            }
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            grid.add(new Label("ExternalSystem:"), 0, 0);
            grid.add(externalSystemChoiceBox, 1, 0);
            grid.add(new Label("Kundennummer:"), 0, 1);
            grid.add(customerId, 1, 1);
            Node dialogAddButton = dialog.getDialogPane().lookupButton(addButtonType);
            dialogAddButton.setDisable(true);
            InvalidationListener addButtonDisabler = (javafx.beans.Observable observable) -> {
                dialogAddButton.setDisable(externalSystemChoiceBox.getSelectionModel().isEmpty() || customerId.getText().isEmpty());
            };
            externalSystemChoiceBox.getSelectionModel().selectedIndexProperty().addListener(addButtonDisabler);
            customerId.textProperty().addListener(addButtonDisabler);
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(dialogButton -> {
                if ( dialogButton == addButtonType ) {
                    return new AdditionalCustomerId((ExternalSystem)externalSystemChoiceBox.getSelectionModel().selectedItemProperty().get(), customerId.getText());
                }
                return null;
            });

            Ui.build(additionalCustomerIdsListView).modality(WINDOW_MODAL).dialog().eval(() -> dialog)
                    .cf()
                    .thenAcceptAsync(aid -> {
                        if ( selectionModel == null ) {
                            additionalCustomerIds.add(aid);
                        } else {
                            selectionModel.getSelectedItem().setValue(aid.getValue());
                        }
                    }, Platform::runLater)
                    .handle(Ui.handler());

        }

    }

}
