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

import eu.ggnet.dwoss.customer.ee.entity.Contact;
import eu.ggnet.dwoss.customer.ee.entity.Address;
import eu.ggnet.dwoss.customer.ee.entity.Communication;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Controller class for the editor view of a Contact. Allows the user to
 * change all values of the Contact.
 * <p>
 * import static javafx.stage.Modality.WINDOW_MODAL;
 *
 * @author jens.papenhagen
 */
@Title("Kontakt eintragen")
public class ContactUpdateController implements Initializable, FxController, Consumer<Contact>, ResultProducer<Contact> {

    @FXML
    private ListView<Address> addressListView;

    @FXML
    private TableView<Communication> communicationTableView;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField titleTextField;

    @FXML
    private ChoiceBox<Sex> genderBox;

    private Contact contact;

    private TableColumn<Communication, Type> typeColumn = new TableColumn("Type");

    private TableColumn<Communication, String> idColumn = new TableColumn("Identifier");

    private TableColumn<Communication, Boolean> prefColumn = new TableColumn("prefered");

    private ObservableList<Address> addressList = FXCollections.observableArrayList();

    private ObservableList<Communication> communicationsList = FXCollections.observableArrayList();

    private ToggleGroup prefGroup = new ToggleGroup();

    @FXML
    private Button delAddressButton;

    @FXML
    private Button delComButton;

    @FXML
    private Button saveAndCloseButton;

    @FXML
    private Button saveButton;

    @FXML
    private void saveButtonHandling() {
        contact = getContact();
    }

    @FXML
    private void saveAndCloseButtonHandling() {
        contact = getContact();
        Ui.closeWindowOf(lastNameTextField);
    }

    @FXML
    private void cancelButtonHandling() {
        Ui.closeWindowOf(lastNameTextField);
    }

    @FXML
    private void handleEditAddressButton() {
        Address selectedItem = addressListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            editAddress(selectedItem);
        }
    }

    @FXML
    private void handleAddAddressButton() {
        Address a = new Address();
        a.setCity("");
        a.setStreet("");
        a.setZipCode("");

        addAddress(a);
    }

    @FXML
    private void handleDelAddressButton() {
        Address selectedItem = addressListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            addressList.remove(selectedItem);
        }
    }

    @FXML
    private void handleEditComButton() {
        Communication selectedItem = communicationTableView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            editCommunication(selectedItem);
        }
    }

    @FXML
    private void handleAddComButton() {
        Communication c = new Communication();
        c.setIdentifier("");
        addCommunication(c);
    }

    @FXML
    private void handleDelComButton() {
        Communication selectedItem = communicationTableView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            communicationsList.remove(selectedItem);
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //button behavior        
        delAddressButton.disableProperty().bind(addressListView.getSelectionModel().selectedIndexProperty().lessThan(0));
        delComButton.disableProperty().bind(communicationTableView.getSelectionModel().selectedIndexProperty().lessThan(0));

        saveAndCloseButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> lastNameTextField.getText().trim().isEmpty(), lastNameTextField.textProperty()
                )
        );

        saveButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> lastNameTextField.getText().trim().isEmpty(), lastNameTextField.textProperty()
                )
        );

        //fill the UI with default values
        genderBox.getItems().addAll(Contact.Sex.values());

        //Address CellFactory
        addressListView.setCellFactory((ListView<Address> p) -> {
            ListCell<Address> cell = new ListCell<Address>() {
                @Override
                protected void updateItem(Address item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) {
                        setGraphic(null);
                        setText("");
                    } else {
                        VBox anschriftbox = new VBox();
                        Label street = new Label(item.getStreet());

                        Label zipCode = new Label(item.getZipCode());
                        Label city = new Label(item.getCity());
                        HBox postBox = new HBox();
                        postBox.getChildren().addAll(zipCode, city);
                        postBox.setSpacing(2.0);

                        Label country = new Label(new Locale("", item.getIsoCountry()).getDisplayCountry());

                        anschriftbox.getChildren().addAll(street, postBox, country);
                        anschriftbox.setSpacing(2.0);

                        setText(null);
                        setGraphic(anschriftbox);

                    }
                }
            };
            return cell;
        });
        addressListView.setOrientation(Orientation.HORIZONTAL);

        //Communication CellFactory
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeColumn.setCellFactory(column -> {
            return new TableCell<Communication, Type>() {
                @Override
                protected void updateItem(Type item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) {
                        setText(null);
                    } else {
                        setText(item.name());
                        setStyle("-fx-font-weight: bold");
                    }
                }
            };
        });
        idColumn.setCellValueFactory(new PropertyValueFactory<>("identifier"));
        idColumn.setCellFactory(column -> {
            return new TableCell<Communication, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) {
                        setText(null);
                    } else {
                        setText(item);
                        setStyle("");
                    }
                }
            };
        });
        prefColumn.setCellValueFactory(new PropertyValueFactory<>("prefered"));
        prefColumn.setCellFactory(column -> {
            return new TableCell<Communication, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        HBox checkHBox = new HBox();
                        RadioButton prefRadioButton = new RadioButton();
                        prefRadioButton.setSelected(item);
                        prefRadioButton.setToggleGroup(prefGroup);

                        checkHBox.getChildren().add(prefRadioButton);
                        checkHBox.setAlignment(Pos.CENTER);

                        setText("");
                        setGraphic(checkHBox);
                    }
                }
            };
        });

    }

    @Override
    public void accept(Contact cont) {
        if ( cont != null || cont.getViolationMessages() != null ) {
            setContact(cont);
        } else {
            UiAlert.message("Kontakt ist inkompatibel: " + cont.getViolationMessages()).show(UiAlertBuilder.Type.WARNING);
        }
    }

    @Override
    public Contact getResult() {
        if ( contact == null ) {
            return null;
        }
        return contact;
    }

    private void editAddress(Address addresse) {
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> addresse, AddressUpdateController.class).ifPresent(
                    a -> {
                        Platform.runLater(() -> {
                            addressList.set(addressListView.getSelectionModel().getSelectedIndex(), a);
                        });

                    });
        });
    }

    private void addAddress(Address addresse) {
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> addresse, AddressUpdateController.class).ifPresent(
                    a -> {
                        Platform.runLater(() -> {
                            addressList.add(a);
                        });

                    });
        });
    }

    private void editCommunication(Communication communication) {
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> communication, CommunicationUpdateController.class).ifPresent(
                    a -> {

                        Platform.runLater(() -> {
                            communicationsList.set(communicationTableView.getSelectionModel().getSelectedIndex(), a);
                        });

                    });
        });
    }

    private void addCommunication(Communication communication) {
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> communication, CommunicationUpdateController.class).ifPresent(
                    a -> {

                        Platform.runLater(() -> {
                            communicationsList.set(communicationTableView.getSelectionModel().getSelectedIndex(), a);
                        });

                    });
        });
    }

    /**
     * Set the Contact for the Editor
     *
     * @param comp the Contact
     */
    private void setContact(Contact cont) {
        titleTextField.setText(cont.getTitle());
        firstNameTextField.setText(cont.getFirstName());
        lastNameTextField.setText(cont.getLastName());

        if ( cont.getSex() != null ) {
            genderBox.getSelectionModel().select(cont.getSex());
        } else {
            genderBox.getSelectionModel().selectFirst();
        }

        addressList.addAll(cont.getAddresses());
        communicationsList.addAll(cont.getCommunications());

        //fill the listViews
        addressListView.setItems(addressList);
        communicationTableView.setItems(communicationsList);
        communicationTableView.getColumns().addAll(typeColumn, idColumn, prefColumn);
    }

    /**
     * Get the Contact back
     */
    private Contact getContact() {
        Contact c = new Contact();
        c.setTitle(titleTextField.getText());
        c.setFirstName(firstNameTextField.getText());
        c.setLastName(lastNameTextField.getText());
        c.setSex(genderBox.getSelectionModel().getSelectedItem());

        c.getAddresses().addAll(addressList);
        c.getCommunications().addAll(communicationsList);

        return c;
    }

}
