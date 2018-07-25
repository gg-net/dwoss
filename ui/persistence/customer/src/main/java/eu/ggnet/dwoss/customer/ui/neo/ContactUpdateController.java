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
import javafx.util.StringConverter;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.AlertType;

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
        //only get valid object out
        if ( contact.getViolationMessage() != null ) {
            Ui.exec(() -> {
                Ui.build().alert().message("Kontakt ist inkompatibel: " + contact.getViolationMessage()).show(AlertType.WARNING);
            });
        }
    }

    @FXML
    private void saveAndCloseButtonHandling() {
        contact = getContact();
        if ( contact.getViolationMessage() != null ) {
            Ui.exec(() -> {
                Ui.build().alert().message("Kontakt ist inkompatibel: " + contact.getViolationMessage()).show(AlertType.WARNING);
            });
            return;
        }
        Ui.closeWindowOf(lastNameTextField);
    }

    @FXML
    private void cancelButtonHandling() {
        contact = null;
        Ui.closeWindowOf(lastNameTextField);
    }

    @FXML
    private void handleEditAddressButton() {
        Address selectedItem = addressListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> selectedItem, AddressUpdateController.class)
                    .cf()
                    .thenApply(add -> CustomerConnectorFascade.updateAddressOnContact(contact.getId(), add))
                    .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                    .handle(Ui.handler());
        }
    }

    @FXML
    private void handleAddAddressButton() {
            Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> new Address(), AddressUpdateController.class)
                    .cf()
                    .thenApply(add -> CustomerConnectorFascade.createAddressOnContact(contact.getId(), add))
                    .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                    .handle(Ui.handler());
    }

    @FXML
    private void handleDelAddressButton() {
        if ( addressListView.getSelectionModel().getSelectedItem() == null ) return;
        // Hier machen wir morgen weiter.
        // Yes/no alert ob wirklich löschen.
        
        
        int selectedIndex = addressListView.getSelectionModel().getSelectedIndex();
        if ( addressListView.getSelectionModel().getSelectedItems() != null ) {
            addressList.remove(selectedIndex);
            addressListView.refresh();
        }
    }

    @FXML
    private void handleEditComButton() {
        Communication selectedItem = communicationTableView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            Ui.exec(() -> {
                Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> selectedItem, CommunicationUpdateController.class)
                        .opt()
                        .filter(a -> a != null)
                        .ifPresent(a -> Platform.runLater(() -> communicationsList.set(communicationTableView.getSelectionModel().getSelectedIndex(), a)));
            });
        }
    }

    @FXML
    private void handleAddComButton() {
        Communication communication = new Communication();
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> communication, CommunicationUpdateController.class)
                    .opt()
                    .filter(a -> a != null)
                    .ifPresent(a -> Platform.runLater(() -> communicationsList.add(a)));
        });
    }

    @FXML
    private void handleDelComButton() {
        int selectedIndex = communicationTableView.getSelectionModel().getSelectedIndex();
        if ( communicationTableView.getSelectionModel().getSelectedItems() != null ) {
            communicationsList.remove(selectedIndex);
            communicationTableView.refresh();
        }
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        //button behavior
        delAddressButton.disableProperty().bind(addressListView.getSelectionModel().selectedIndexProperty().lessThan(0));
        delComButton.disableProperty().bind(communicationTableView.getSelectionModel().selectedIndexProperty().lessThan(0));

        //get overwriten in accept()
        lastNameTextField.setText("");

        //enable the save and "saveAndClose" button only on filled TextFields
        saveButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> lastNameTextField.getText().trim().isEmpty(), lastNameTextField.textProperty()
                )
        );
        saveAndCloseButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> lastNameTextField.getText().trim().isEmpty(), lastNameTextField.textProperty()
                )
        );

        //fill the UI with default values
        genderBox.setConverter(new StringConverter<Sex>() {
            @Override
            public Sex fromString(String string) {
                throw new UnsupportedOperationException("Invalid operation for Convert a String into a Sex.");
            }

            @Override
            public String toString(Sex myClassinstance) {
                return myClassinstance.getSign();
            }
        });
        genderBox.getItems().addAll(Contact.Sex.values());

        //Address HORIZONTAL CellFactory
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

        //adding a CellFactory for every Colum
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
        prefColumn.setCellValueFactory(new PropertyValueFactory<>("preferred"));
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

        //fill the listViews
        addressListView.setItems(addressList);
        communicationTableView.setItems(communicationsList);
        communicationTableView.getColumns().addAll(typeColumn, idColumn, prefColumn);
    }

    @Override
    public void accept(Contact cont) {
        if ( cont != null ) {
            setContact(cont);
        } else {
            Ui.exec(() -> {
                Ui.build().alert().message("Kontakt ist null").show(AlertType.ERROR);
            });
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
     * Set the Contact for the Editor
     *
     * @param comp the Contact
     */
    private void setContact(Contact cont) {
        titleTextField.setText(cont.getTitle());
        firstNameTextField.setText(cont.getFirstName());
        if ( cont.getLastName() != null ) {
            lastNameTextField.setText(cont.getLastName());
        }

        if ( cont.getSex() != null ) {
            genderBox.getSelectionModel().select(cont.getSex());
        } else {
            genderBox.getSelectionModel().selectFirst();
        }

        addressList.addAll(cont.getAddresses());
        communicationsList.addAll(cont.getCommunications());

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
