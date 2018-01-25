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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.*;
import eu.ggnet.dwoss.customer.entity.Communication.Type;
import eu.ggnet.dwoss.customer.entity.Contact.Sex;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class
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

    private final TableColumn<Communication, Type> typeColumn = new TableColumn("Type");

    private final TableColumn<Communication, String> idColumn = new TableColumn("Identifier");

    private final TableColumn<Communication, Boolean> prefColumn = new TableColumn("prefered");

    private final ObservableList<Address> addressList = FXCollections.observableArrayList();

    private final ObservableList<Communication> communicationsList = FXCollections.observableArrayList();

    private final ToggleGroup prefGroup = new ToggleGroup();

    @FXML
    private void saveAndCloseButtonHandling(ActionEvent event) {
        if ( StringUtils.isBlank(lastNameTextField.getText()) ) {
            UiAlert.message("Es muss ein Firmen Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }
        getContact();
        Ui.closeWindowOf(lastNameTextField);
    }

    @FXML
    private void saveButtonHandling(ActionEvent event) {
        if ( StringUtils.isBlank(lastNameTextField.getText()) ) {
            UiAlert.message("Es muss ein Firmen Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }
        getContact();
    }

    @FXML
    private void cancelButtonHandling(ActionEvent event) {
        Ui.closeWindowOf(lastNameTextField);
    }

    @FXML
    private void handleEditAddressButton(ActionEvent event) {
        Address selectedItem = addressListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            openAddress(selectedItem);
        }
    }

    @FXML
    private void handleAddAddressButton(ActionEvent event) {
        openAddress(new Address());
    }

    @FXML
    private void handleDelAddressButton(ActionEvent event) {
        Address selectedItem = addressListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            addressList.remove(selectedItem);
        }
    }

    @FXML
    private void handleEditComButton(ActionEvent event) {
        Communication selectedItem = communicationTableView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            openCommunication(selectedItem);
        }
    }

    @FXML
    private void handleAddComButton(ActionEvent event) {
        openCommunication(new Communication());
    }

    @FXML
    private void handleDelComButton(ActionEvent event) {
        Communication selectedItem = communicationTableView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            communicationsList.remove(selectedItem);
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {       

        //fill the UI with default values
        genderBox.getItems().addAll(Contact.Sex.values());

        //Address CellFactory
        addressListView.setCellFactory((ListView<Address> p) -> {
            ListCell<Address> cell = new ListCell<Address>() {
                @Override
                protected void updateItem(Address t, boolean bln) {
                    super.updateItem(t, bln);
                    if ( t != null ) {
                        VBox anschriftbox = new VBox();
                        Label street = new Label(t.getStreet());

                        Label zipCode = new Label(t.getZipCode());
                        Label city = new Label(t.getCity());
                        HBox postBox = new HBox();
                        postBox.getChildren().addAll(zipCode, city);
                        postBox.setSpacing(2.0);

                        Label country = new Label(new Locale("", t.getIsoCountry()).getDisplayCountry());

                        anschriftbox.getChildren().addAll(street, postBox, country);
                        anschriftbox.setSpacing(2.0);

                        setText(null);
                        setGraphic(anschriftbox);

                    } else {
                        setText("");
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
        if ( cont != null ) {
            contact = cont;
            setContact(contact);
        } else {
            UiAlert.message("Kontakt ist inkompatibel").show(UiAlertBuilder.Type.WARNING);
        }
    }

    @Override
    public Contact getResult() {
        if ( contact == null ) {
            return null;
        }
        return contact;
    }

    /**
     * open the Address Editor
     *
     * @param addresse is the Address
     */
    private void openAddress(Address addresse) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Ui.build().fxml().eval(() -> addresse, AddressUpdateController.class).ifPresent(a -> {
                    addressList.add(a);
                });
            }
        }).start();
        //old code 
//        Ui.exec(() -> {
//            Ui.build().fxml().eval(() -> addresse, AddressUpdateController.class).ifPresent(a -> {
//                addressList.add(a);
//            });
//        });
    }

    /**
     * open the Communication Editor
     *
     * @param communication is the Communication
     */
    private void openCommunication(Communication communication) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Ui.build().fxml().eval(() -> communication, CommunicationUpdateController.class).ifPresent(a -> {
                    communicationsList.add(a);
                });
            }
        }).start();
    }

    /**
     * Set the Contact for the Editor
     *
     * @param comp the Contact
     */
    private void setContact(Contact cont) {
        addressList.addAll(contact.getAddresses());
        communicationsList.addAll(contact.getCommunications());

        //fill the listViews
        addressListView.setItems(addressList);
        communicationTableView.setItems(communicationsList);
        communicationTableView.getColumns().addAll(typeColumn, idColumn, prefColumn);

        titleTextField.setText(contact.getTitle());
        firstNameTextField.setText(contact.getFirstName());
        lastNameTextField.setText(contact.getLastName());

        genderBox.getSelectionModel().select(contact.getSex());
    }

    /**
     * Get the Contact back
     */
    private void getContact() {
        contact.setTitle(titleTextField.getText());
        contact.setFirstName(firstNameTextField.getText());
        contact.setLastName(lastNameTextField.getText());

        if ( genderBox.getSelectionModel().getSelectedItem() != null ) {
            Sex selectedItem = genderBox.getSelectionModel().getSelectedItem();
            contact.setSex(selectedItem);
        }

        contact.getAddresses().addAll(addressList);
        contact.getCommunications().addAll(communicationsList);
    }

}
