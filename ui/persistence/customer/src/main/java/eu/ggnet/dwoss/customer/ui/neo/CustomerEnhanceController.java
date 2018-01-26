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
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Customer.Source;
import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.entity.Customer.ExternalSystem;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
@Title("Erweiterte Kunden bearbeiten")
public class CustomerEnhanceController implements Initializable, FxController, Consumer<Customer>, ResultProducer<Customer> {

    @Data
    @AllArgsConstructor
    public static class ExternalId {

        private ExternalSystem type;

        private String value;

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

    private final ListView<Company> companyListView = new ListView<>();

    private final ListView<Contact> contactListView = new ListView<>();

    private final ListView<ExternalId> addExternalIdsListView = new ListView<>();

    private final ObservableList<Company> companyList = FXCollections.observableArrayList();

    private final ObservableList<Contact> contactList = FXCollections.observableArrayList();

    private final ObservableList<MandatorMetadata> mandatorMetadata = FXCollections.observableArrayList();

    private final ObservableSet<CustomerFlag> flagsSet = FXCollections.emptyObservableSet();

    private final ObservableMap<ExternalSystem, String> additionalCustomerIds = FXCollections.observableHashMap();

    private boolean bussines = false;

    private Customer customer;

    @FXML
    private void saveButtonHandling(ActionEvent event) {
        if ( StringUtils.isBlank(kid.getText()) ) {
            UiAlert.message("Es muss ein Firmen Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }
        getCustomer();
    }

    @FXML
    private void handelPreferedAddressLabelsButton(ActionEvent event) {
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
        //TODO MandatorMetadataUpdateController is missing
        new Thread(() -> {
            //          Ui.fxml().eval(() -> customer.getMandatorMetadata(), MandatorMetadataUpdateController.class);
        }).start();
    }

    @Override
        public void initialize(URL url, ResourceBundle rb) {
        source.getItems().addAll(Source.values());

        //build the Flags Box
        buildFlagBox();

        //build the external System Id´s box
        buildExternalSystemIdBox();

        //build the showbox
        buildShowBox();

    }

    @Override
        public void accept(Customer cust) {
        if ( cust != null ) {
            if ( cust.isBussines() ) {
                bussines = true;
            }
            customer = cust;
            setCustomer(customer);
        } else {
            UiAlert.message("Kunde ist inkompatibel").show(UiAlertBuilder.Type.WARNING);
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
            companyList.addAll(c.getCompanies());
            companyListView.setItems(companyList);

            bussines = true;
        } else {
            CustomerKindLabel.setText("Endkunde");
            kundenname.setText(c.getContacts().get(0).toFullName());
            contactList.addAll(c.getContacts());
            contactListView.setItems(contactList);
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
    }

    public void getCustomer() {
        if ( bussines ) {
            customer.getCompanies().clear();
            companyList.forEach(c -> customer.add(c));
        } else {
            customer.getContacts().clear();
            contactList.forEach(c -> customer.add(c));
        }
        customer.setKeyAccounter(keyAccount.getText());

        customer.getFlags().clear();
        flagsSet.forEach(f -> customer.add(f));

        customer.setSource(source.getSelectionModel().getSelectedItem());

        customer.getMandatorMetadata().clear();
        mandatorMetadata.forEach(m -> customer.add(m));

        //transfer List back to a Map
        ObservableList<ExternalId> items = addExternalIdsListView.getItems();
        customer.getAdditionalCustomerIds().clear();
        customer.getAdditionalCustomerIds().putAll(items.stream().collect(Collectors.toMap(ExternalId::getType, ExternalId::getValue)));

        customer.setComment(commentTextArea.getText());
    }

    private void buildFlagBox() {
        //transform a Set to a ObservableList of CustomerFlag
        List<CustomerFlag> templist = new ArrayList<>();
        flagsSet.forEach(f -> templist.add(f));
        ObservableList<CustomerFlag> allFlagsFromTheCustomer = FXCollections.observableArrayList(templist);

        //fill with all posibile flags
        ObservableList<CustomerFlag> observableArrayListOfAllFlags = FXCollections.observableArrayList(CustomerFlag.values());
        VBox checkVBox = new VBox();
        for (CustomerFlag oFlag : observableArrayListOfAllFlags) {
            CheckBox checkBox = new CheckBox(oFlag.getName());
            checkBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
                if(checkBox.isSelected()){
                    //TODO get the lable back
                    checkBox.getText();
                }
            });
            if ( allFlagsFromTheCustomer.contains(oFlag) ) {
                checkBox.setSelected(true);
            }
            checkVBox.getChildren().add(checkBox);
        }

        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
            checkBox.isSelected();
        });

        Label flagLable = new Label("Flags: ");
        flagVBox.getChildren().addAll(flagLable, checkBox);
    }

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

                        flagbox.getChildren().addAll(textfield, flagLable);
                        flagbox.setSpacing(2.0);

                        setText(null);
                        setGraphic(flagbox);

                    }
                }
            };
            return cell;
        });
        Label ExternalSystemIdsLable = new Label("Flags: ");
        externalSysremIds.getChildren().addAll(ExternalSystemIdsLable, addExternalIdsListView);
    }

    private void buildShowBox() {
        //build up the Buttons
        VBox buttonVBox = new VBox();
        Button editButton = new Button("Ändern");
        Button addButton = new Button("Hinzufügen");
        Button delButton = new Button("Löschen");
        editButton.setOnAction((ActionEvent e) -> {
            if ( bussines ) {
                Company selectedItem = companyListView.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    openCompany(selectedItem);
                }
            } else {
                Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    openContact(selectedItem);
                }
            }
        });
        addButton.setOnAction((ActionEvent e) -> {
            if ( bussines ) {
                Company selectedItem = new Company();
                openCompany(selectedItem);

            } else {
                Contact selectedItem = new Contact();
                openContact(selectedItem);
            }
        });
        delButton.setOnAction((ActionEvent e) -> {
            if ( bussines ) {
                Company selectedItem = companyListView.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    companyList.remove(selectedItem);
                }
            } else {
                Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
                if ( selectedItem != null ) {
                    contactList.remove(selectedItem);
                }
            }
        });
        buttonVBox.getChildren().addAll(editButton, addButton, delButton);
        buttonVBox.setSpacing(2.0);

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
        //build up the showBox
        if ( bussines ) {
            showHBox.getChildren().addAll(companyListView, buttonVBox);
        } else {
            showHBox.getChildren().addAll(contactListView, buttonVBox);
        



}
    }

    /**
     * open the Contact Editor
     *
     * @param contact is the Contact
     */
    private void openContact(Contact contact) {
         Ui.exec(() -> {
            Ui.build().fxml().eval(() -> contact, ContactUpdateController.class

).ifPresent(a -> {
                contactList.add(a);
            });
        });

    



}

    /**
     * open the Company Editor
     *
     * @param company is the Company
     */
    private void openCompany(Company company) {
         Ui.exec(() -> {
            Ui.build().fxml().eval(() -> company, CompanyUpdateController.class

).ifPresent(a -> {
                companyList.add(a);
            });
        });
    }

}
