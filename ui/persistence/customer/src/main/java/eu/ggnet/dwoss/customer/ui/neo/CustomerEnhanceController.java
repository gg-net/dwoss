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
import eu.ggnet.dwoss.customer.ee.entity.projection.AddressLabel;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.saft.core.Ui;

import lombok.AllArgsConstructor;
import lombok.Data;

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
    private VBox flagVBox = new VBox();

    @FXML
    private HBox showHBox = new HBox();

    @FXML
    private VBox additionalCustomerIdsVBox;

    @FXML
    private Label contactOrCompanyLabel;

    @FXML
    private TextField keyAccounterTextField;

    private ListView<Company> companyListView = new ListView<>();

    private ListView<Contact> contactListView = new ListView<>();

    private ListView<AdditionalCustomerID> additionalCustomerIdsListView = new ListView<>();

    private ObservableList<Company> companyList = FXCollections.observableArrayList();

    private ObservableList<Contact> contactList = FXCollections.observableArrayList();

    private ObservableList<MandatorMetadata> mandatorMetadata = FXCollections.observableArrayList();

    private ObservableSet<CustomerFlag> flagsSet = FXCollections.observableSet();

    private ObservableList<CustomerFlag> outputFlagslist = FXCollections.observableArrayList();

    private ObservableMap<ExternalSystem, String> additionalCustomerIds = FXCollections.observableHashMap();

    private ObservableList<AddressLabel> addressLabels = FXCollections.observableArrayList();

    private boolean isBusinessCustomer = false;

    private Customer customer;

    @FXML
    private Label nameOrCompanyLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO add button behavior see the RULES on getViolationMessage() in Customer, enable only on vaild customer

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
    }

    @Override
    public void accept(Customer cust) {
        if ( cust != null && cust.isValid() ) {
            if ( cust.isBusiness() ) {
                isBusinessCustomer = true;
            }
            setCustomer(cust);
        } else {
            Ui.build(commentTextArea).alert().message("Kunde ist inkompatibel: " + cust.getViolationMessage()).show(eu.ggnet.saft.core.ui.AlertType.WARNING);
        }
    }

    @Override
    public Customer getResult() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        addressLabels.addAll(customer.getAddressLabels());
        customerNameLabel.setText(customer.toName());
        if ( customer.isBusiness() ) {
            customerTypeLabel.setText("Geschäftskunde");
            nameOrCompanyLabel.setText("Firma: ");
            companyList.setAll(customer.getCompanies());
            companyListView.setItems(companyList);
            contactOrCompanyLabel.setText("Firmen: ");

            isBusinessCustomer = true;
        } else {

            customerTypeLabel.setText("Endkunde");
            nameOrCompanyLabel.setText("Name: ");
            contactList.setAll(customer.getContacts());
            contactListView.setItems(contactList);
            contactOrCompanyLabel.setText("Kontakte: ");
        }

        customerIdLabel.setText("" + customer.getId());
        keyAccounterTextField.setText(customer.getKeyAccounter());

        flagsSet.addAll(customer.getFlags());

        sourceChoiceBox.getSelectionModel().select(customer.getSource());
        mandatorMetadata.addAll(customer.getMandatorMetadata());
        additionalCustomerIds.putAll(customer.getAdditionalCustomerIds());

        //transfer the map into a List
        if ( additionalCustomerIds == null ) {
            FXCollections.emptyObservableList();
        } else {
            ObservableList<AdditionalCustomerID> observableArrayList = FXCollections.observableArrayList(
                    additionalCustomerIds.entrySet().stream()
                            .map(e -> new AdditionalCustomerID(e.getKey(), e.getValue()))
                            .collect(Collectors.toList()));
            additionalCustomerIdsListView.setItems(observableArrayList);
        }

        commentTextArea.setText(customer.getComment());

        //build the Flags Box
        buildFlagBox();

        //build the external System Id´s box
        buildExternalSystemIdBox();

        //build the showbox
        buildShowBox();
    }

    public Customer getCustomer() {
        Customer cust = new Customer();
        cust.getAddressLabels().addAll(addressLabels);

        if ( isBusinessCustomer ) {
            cust.getCompanies().clear();
            companyList.forEach(c -> cust.getCompanies().add(c));
        } else {
            cust.getContacts().clear();
            contactList.forEach(c -> cust.getContacts().add(c));
        }
        cust.setKeyAccounter(keyAccounterTextField.getText());

        cust.getFlags().clear();
        flagsSet.forEach(f -> cust.getFlags().add(f));

        cust.setSource(sourceChoiceBox.getSelectionModel().getSelectedItem());

        cust.getMandatorMetadata().clear();
        mandatorMetadata.forEach(m -> cust.getMandatorMetadata().add(m));

        //tansfer the List of Flags back to Set (remove duplicates)
        HashSet<CustomerFlag> tempSet = new HashSet<>(outputFlagslist);
        outputFlagslist.clear();
        outputFlagslist.addAll(tempSet);
        outputFlagslist.forEach((flag) -> {
            cust.getFlags().add(flag);
        });

        //transfer List back to a Map
        ObservableList<AdditionalCustomerID> items = additionalCustomerIdsListView.getItems();
        cust.getAdditionalCustomerIds().clear();
        cust.getAdditionalCustomerIds().putAll(items.stream().collect(Collectors.toMap(AdditionalCustomerID::getType, AdditionalCustomerID::getValue)));

        cust.setComment(commentTextArea.getText());

        return cust;
    }

    @FXML
    private void clickSaveButton(ActionEvent event) {
        customer = getCustomer();
        Ui.closeWindowOf(customerIdLabel);
    }

    @FXML
    private void clickCancelButton(ActionEvent event) {
        Ui.closeWindowOf(customerIdLabel);
    }

    @FXML
    private void clickSelectPreferedAddressLabelsButton(ActionEvent event) {
        Ui.build(commentTextArea).fxml().eval(() -> getCustomer(), PreferedAddressLabelsController.class)
                .cf()
                .thenAccept(ui -> accept(CustomerConnectorFascade.updateAddressLabels(customer.getId(), ui.getInvoiceLabel(), ui.getShippingLabel())))
                .handle(Ui.handler());
    }

    /**
     * Build up a ListView with CheckBoxes for the Set of CunstomerFlags
     */
    private void buildFlagBox() {
        //transform a Set to a ObservableList of CustomerFlag
        List<CustomerFlag> templist = new ArrayList<>();
        flagsSet.forEach(f -> templist.add(f));
        ObservableList<CustomerFlag> allFlagsFromTheCustomer = FXCollections.observableArrayList(templist);

        //fill with all posibile flags
        ObservableList<CustomerFlag> observableArrayListOfAllFlags = FXCollections.observableArrayList(CustomerFlag.values());

        ObservableList<CustomerFlagWithSelect> listForTheView = FXCollections.observableArrayList();

        //fill the CustomerFlagWithSelect List
        observableArrayListOfAllFlags.stream().map((ovall) -> {
            CustomerFlagWithSelect cfs = new CustomerFlagWithSelect(ovall);
            if ( allFlagsFromTheCustomer.contains(ovall) ) {
                cfs.setSelected(true);
            }
            return cfs;
        }).forEachOrdered((cfs) -> {
            listForTheView.add(cfs);
        });

        listForTheView.forEach(flag -> flag.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
            if ( isSelected ) {
                outputFlagslist.add(flag.getFlag());
            }
        }));

        ListView<CustomerFlagWithSelect> checklist = new ListView<>();
        checklist.setItems(listForTheView);
        checklist.setMinWidth(150.0);
        checklist.setCellFactory(CheckBoxListCell.forListView(CustomerFlagWithSelect::selectedProperty, new StringConverter<CustomerFlagWithSelect>() {
            @Override
            public String toString(CustomerFlagWithSelect object) {
                return object.getFlag().getName();
            }

            @Override
            public CustomerFlagWithSelect fromString(String string) {
                return null;
            }
        }));

        Label flagLable = new Label("Flags: ");
        flagVBox.getChildren().addAll(flagLable, checklist);
    }

    private void buildExternalSystemIdBox() {
        additionalCustomerIdsListView.setCellFactory((ListView<AdditionalCustomerID> p) -> {
            ListCell<AdditionalCustomerID> cell = new ListCell<AdditionalCustomerID>() {
                @Override
                protected void updateItem(AdditionalCustomerID item, boolean empty) {
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
                        customerIdLabel.setText(item.getValue());

                        flagbox.getChildren().addAll(flagLabel, customerIdLabel);
                        flagbox.setSpacing(2.0);

                        setText(null);
                        setGraphic(flagbox);

                    }
                }
            };
            return cell;
        });
        Label ExternalSystemIDsLabel = new Label("Zusätzliche Kundennummern: ");

        //create a dialog to add AdditionalCustomerId instances to the additionalCustomerIDsListView
        Button addButton = new Button("Hinzufügen");
        addButton.setMinWidth(80.0);
        addButton.setOnAction(new AdditionalCustomerIDsDialogHandler());

        Button deleteButton = new Button("Löschen");
        deleteButton.setMinWidth(80.0);
        deleteButton.setOnAction((event) -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Externe Kundennummer löschen");
            alert.setHeaderText("Bestätigen der Löschung einer Kundennummer");
            alert.setContentText("Wollen sie die Kundennummer wirklich löschen?");

            Optional<ButtonType> result = alert.showAndWait(); // TODO: JACOB
            if ( result.get() == ButtonType.OK ) {
                additionalCustomerIdsListView.getItems().remove(additionalCustomerIdsListView.getSelectionModel().getSelectedItem());
            }
        });
        Button editButton = new Button("Bearbeiten");
        editButton.setOnAction(new AdditionalCustomerIDsDialogHandler(additionalCustomerIdsListView.getSelectionModel()));

        // disable the add button if every type of ExternalSystem enum is already contained in the listView
        additionalCustomerIdsListView.getItems().addListener((javafx.beans.Observable observable) -> {
            addButton.setDisable(additionalCustomerIdsListView.getItems().stream()
                    .map(additionalCustomerID -> additionalCustomerID.type)
                    .collect(Collectors.toList())
                    .containsAll(Arrays.asList(ExternalSystem.values())));
        });
        editButton.setDisable(true);
        deleteButton.setDisable(true);
        additionalCustomerIdsListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            editButton.setDisable(newValue.intValue() < 0);
            deleteButton.setDisable(newValue.intValue() < 0);
        });

        HBox buttonsHBox = new HBox();
        buttonsHBox.getChildren().addAll(addButton, editButton, deleteButton);
        buttonsHBox.setSpacing(3.0);
        additionalCustomerIdsVBox.getChildren().addAll(ExternalSystemIDsLabel, buttonsHBox, additionalCustomerIdsListView);
        additionalCustomerIdsVBox.setMinWidth(120.0);

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
            editButton.setOnAction((ActionEvent e) -> {
                Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    editContact(selectedItem);
                }
            });
            addButton
                    .setOnAction((ActionEvent e) -> {

                        Ui.exec(() -> {
                            Ui.build(commentTextArea).modality(WINDOW_MODAL).parent(customerNameLabel).fxml().eval(ContactUpdateController.class
                            )
                                    .opt()
                                    .ifPresent(a -> Platform.runLater(() -> contactList.add(a)));
                        });

                    });
            delButton.setOnAction((ActionEvent e) -> {
                Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    contactList.remove(selectedItem);
                }
            });
            delButton.disableProperty().bind(contactListView.getSelectionModel().selectedIndexProperty().lessThan(0));
        }

        buttonVBox.getChildren().addAll(editButton, addButton, delButton);
        buttonVBox.setSpacing(3.0);

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

        //build up the showBox
        if ( isBusinessCustomer ) {
            showHBox.getChildren().addAll(companyListView, buttonVBox);
        } else {
            showHBox.getChildren().addAll(contactListView, buttonVBox);

        }
    }

    private void editContact(Contact contact) {
        Ui.exec(() -> {
            Ui.build(commentTextArea).modality(WINDOW_MODAL).parent(customerNameLabel).fxml().eval(() -> contact, ContactUpdateController.class
            )
                    .opt()
                    .ifPresent(a -> Platform.runLater(() -> contactList.set(contactListView.getSelectionModel().getSelectedIndex(), a)));
        });

    }

    private void addContact(Contact contact) {
        Ui.exec(() -> {
            Ui.build(commentTextArea).modality(WINDOW_MODAL).parent(customerNameLabel).fxml().eval(() -> contact, ContactUpdateController.class
            )
                    .opt()
                    .filter(a -> a != null)
                    .ifPresent(a -> Platform.runLater(() -> contactList.add(a)));
        });

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

    @Data
    @AllArgsConstructor
    class AdditionalCustomerID {

        private ExternalSystem type;

        private String value;

        @Override
        public String toString() {
            return "System: " + type + " Kundennummer: " + value;
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

        private MultipleSelectionModel<AdditionalCustomerID> selectionModel;

        public AdditionalCustomerIDsDialogHandler() {
        }

        public AdditionalCustomerIDsDialogHandler(MultipleSelectionModel<AdditionalCustomerID> selectionModel) {
            this.selectionModel = selectionModel;
        }

        @Override
        public void handle(ActionEvent event) {

            Dialog<AdditionalCustomerID> dialog = new Dialog<>();
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
                    return new AdditionalCustomerID((ExternalSystem)externalSystemChoiceBox.getSelectionModel().selectedItemProperty().get(), customerId.getText());
                }
                return null;
            });
            Ui.exec(() -> {

                Optional<AdditionalCustomerID> result = Ui.build(additionalCustomerIdsListView).modality(WINDOW_MODAL).dialog().eval(() -> dialog).opt();

                if ( selectionModel == null )
                    result.ifPresent(additionalId -> {
                        Platform.runLater(()
                                -> {
                            additionalCustomerIdsListView.getItems().add(additionalId);
                            additionalCustomerIdsListView.refresh();
                        });
                    });
                else
                    result.ifPresent(additionalId -> {
                        Platform.runLater(()
                                -> {
                            selectionModel.getSelectedItem().setValue(additionalId.getValue());
                            additionalCustomerIdsListView.refresh();
                        });
                    });
            });

        }

    }

}
