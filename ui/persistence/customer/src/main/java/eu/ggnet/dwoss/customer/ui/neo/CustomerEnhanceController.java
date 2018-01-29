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

import eu.ggnet.dwoss.customer.ee.entity.MandatorMetadata;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.Company;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

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
    private Label shoboxLabel;

    @FXML
    private Button PreferedAddressLabelsButton;

    @FXML
    private Button mandatorInfoButton;

    @FXML
    private Button okButton;


    @Data
    @AllArgsConstructor
    class ExternalId {

        private ExternalSystem type;

        private String value;
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

    @FXML
    private Label CustomerKindLabel;

    @FXML
    private Label kid;

    @FXML
    private Label kundenname;

    @FXML
    private ChoiceBox<Source> source;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private TextField keyAccount;

    @FXML
    private VBox flagVBox = new VBox();

    @FXML
    private VBox externalSysremIds = new VBox();

    @FXML
    private HBox showHBox = new HBox();

    private ListView<Company> companyListView = new ListView<>();

    private ListView<Contact> contactListView = new ListView<>();

    private ListView<ExternalId> addExternalIdsListView = new ListView<>();

    private ObservableList<Company> companyList = FXCollections.observableArrayList();

    private ObservableList<Contact> contactList = FXCollections.observableArrayList();

    private ObservableList<MandatorMetadata> mandatorMetadata = FXCollections.observableArrayList();

    private ObservableSet<CustomerFlag> flagsSet = FXCollections.observableSet();

    private ObservableList<CustomerFlag> outputFlagslist = FXCollections.observableArrayList();

    private ObservableMap<ExternalSystem, String> additionalCustomerIds = FXCollections.observableHashMap();

    private boolean bussines = false;

    private Customer customer;

    @FXML
    private void saveButtonHandling(ActionEvent event) {
        customer = getCustomer();
    }

    @FXML
    private void handelPreferedAddressLabelsButton(ActionEvent event) {
        //TODO better work with the entity AddressLable
        
        Ui.exec(() -> {
            Ui.build().fxml().eval(() -> customer, PreferedAddressLabelsController.class);
        });
    }

    @FXML
    private void cancelButtonHandling(ActionEvent event) {
        Ui.closeWindowOf(kid);
    }

    @FXML
    private void handleMandatorInfoButton(ActionEvent event) {
//        
//        Ui.exec(() -> {
//            Ui.build().parent(kundenname).fxml().eval(() -> customer.getMandatorMetadata(), MandatorMetaDataController.class).ifPresent(a -> {
//                companyList.set(companyListView.getSelectionModel().getSelectedIndex(), a);
//                
//            });
//        });

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO add button behavior see the RULES on getViolationMessage() in Customer, enable only on vaild customer
        
        /**
        //button behavior            
        okButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> keyAccount.getText().trim().isEmpty(), keyAccount.textProperty()
                )
        );
          */
        
        source.getItems().addAll(Source.values());
    }

    @Override
    public void accept(Customer cust) {
        if ( cust != null || cust.getViolationMessage() != null ) {
            if ( cust.isBussines() ) {
                bussines = true;
            }
            setCustomer(cust);
        } else {
            UiAlert.message("Kunde ist inkompatibel: " + cust.getViolationMessage()).show(UiAlertBuilder.Type.WARNING);
        }
    }

    @Override
    public Customer getResult() {
        if ( customer == null ) {
            return null;
        }
        return customer;
    }

    public void setCustomer(Customer c) {
        if ( c.isBussines() ) {
            CustomerKindLabel.setText("Geschäftskunde");
            kundenname.setText(c.getCompanies().get(0).getName());
            companyList.setAll(c.getCompanies());
            companyListView.setItems(companyList);
            shoboxLabel.setText("Firmen: ");

            bussines = true;
        } else {
            CustomerKindLabel.setText("Endkunde");
            kundenname.setText(c.getContacts().get(0).toFullName());
            contactList.setAll(c.getContacts());
            contactListView.setItems(contactList);
            shoboxLabel.setText("Kontakte: ");
        }
        kid.setText("" + c.getId());
        keyAccount.setText(c.getKeyAccounter());

        flagsSet.addAll(c.getFlags());

        source.getSelectionModel().select(c.getSource());
        mandatorMetadata.addAll(c.getMandatorMetadata());
        additionalCustomerIds.putAll(c.getAdditionalCustomerIds());

        //transfer the map into a List
        if ( additionalCustomerIds == null ) {
            FXCollections.emptyObservableList();
        } else {
            ObservableList<ExternalId> observableArrayList = FXCollections.observableArrayList(
                    additionalCustomerIds.entrySet().stream()
                            .map(e -> new ExternalId(e.getKey(), e.getValue()))
                            .collect(Collectors.toList()));
            addExternalIdsListView.setItems(observableArrayList);
        }

        commentTextArea.setText(c.getComment());

        //build the Flags Box
        buildFlagBox();

        //build the external System Id´s box
        buildExternalSystemIdBox();

        //build the showbox
        buildShowBox();
    }

    public Customer getCustomer() {
        Customer cust = new Customer();
        if ( bussines ) {
            cust.getCompanies().clear();
            companyList.forEach(c -> cust.add(c));
        } else {
            cust.getContacts().clear();
            contactList.forEach(c -> cust.add(c));
        }
        cust.setKeyAccounter(keyAccount.getText());

        cust.getFlags().clear();
        flagsSet.forEach(f -> cust.add(f));

        cust.setSource(source.getSelectionModel().getSelectedItem());

        cust.getMandatorMetadata().clear();
        mandatorMetadata.forEach(m -> cust.add(m));

        //tansfer the List of Flags back to Set (remove duplicates) 
        HashSet<CustomerFlag> tempSet = new HashSet<>(outputFlagslist);
        outputFlagslist.clear();
        outputFlagslist.addAll(tempSet);
        outputFlagslist.forEach((flag) -> {
            cust.getFlags().add(flag);
        });

        //transfer List back to a Map
        ObservableList<ExternalId> items = addExternalIdsListView.getItems();
        cust.getAdditionalCustomerIds().clear();
        cust.getAdditionalCustomerIds().putAll(items.stream().collect(Collectors.toMap(ExternalId::getType, ExternalId::getValue)));

        cust.setComment(commentTextArea.getText());

        return cust;
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

    /**
     * build a small list of all ExternalId
     */
    private void buildExternalSystemIdBox() {
        addExternalIdsListView.setCellFactory((ListView<ExternalId> p) -> {
            ListCell<ExternalId> cell = new ListCell<ExternalId>() {
                @Override
                protected void updateItem(ExternalId item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) {
                        setGraphic(null);
                        setText("");
                    } else {
                        HBox flagbox = new HBox();
                        Label flagLable = new Label(item.type.name() + ":");
                        flagLable.setStyle("-fx-font-weight: bold");

                        TextField textfield = new TextField();
                        textfield.setText(item.getValue());

                        flagbox.getChildren().addAll(flagLable, textfield);
                        flagbox.setSpacing(2.0);

                        setText(null);
                        setGraphic(flagbox);

                    }
                }
            };
            return cell;
        });
        Label ExternalSystemIdsLable = new Label("Extra Kunden Nummer: ");
        externalSysremIds.getChildren().addAll(ExternalSystemIdsLable, addExternalIdsListView);
        externalSysremIds.setMinWidth(120.0);
    }

    /**
     * build the main show box
     * for bussnis Customer with Companies
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
        if ( bussines ) {
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
                c.add(a);

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
            addButton.setOnAction((ActionEvent e) -> {
                Contact c = new Contact();
                c.setLastName("");
                addContact(c);
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
        if ( bussines ) {
            showHBox.getChildren().addAll(companyListView, buttonVBox);
        } else {
            showHBox.getChildren().addAll(contactListView, buttonVBox);

        }
    }

    private void editContact(Contact contact) {
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(kundenname).fxml().eval(() -> contact, ContactUpdateController.class).ifPresent(
                    a -> {
                        Platform.runLater(() -> {
                            contactList.set(contactListView.getSelectionModel().getSelectedIndex(), a);
                        });
                    });
        });
    }

    private void addContact(Contact contact) {
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(kundenname).fxml().eval(() -> contact, ContactUpdateController.class).ifPresent(
                    a -> {
                        Platform.runLater(() -> {
                            contactList.add(a);
                        });
                    });
        });
    }

    private void editCompany(Company company) {
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(kundenname).fxml().eval(() -> company, CompanyUpdateController.class).ifPresent(
                    a -> {
                        Platform.runLater(() -> {
                            companyList.set(companyListView.getSelectionModel().getSelectedIndex(), a);
                        });
                    });
        });
    }

    private void addCompany(Company company) {
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(kundenname).fxml().eval(() -> company, CompanyUpdateController.class).ifPresent(
                    a -> {
                        Platform.runLater(() -> {
                            companyList.add(a);
                        });
                    });
        });
    }

}
