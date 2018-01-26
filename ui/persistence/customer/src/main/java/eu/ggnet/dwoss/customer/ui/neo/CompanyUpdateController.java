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
import java.util.regex.Pattern;

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
@Title("Firmen Editieren")
public class CompanyUpdateController implements Initializable, FxController, Consumer<Company>, ResultProducer<Company> {

    private final Pattern decimalPattern = Pattern.compile("\\d+");

    @FXML
    private TextField companyNameTextField;

    @FXML
    private TextField taxIdTextField;

    @FXML
    private TextField ledgerTextField;

    @FXML
    private ListView<Contact> contactListView;

    @FXML
    private ListView<Address> addressListView;

    @FXML
    private TableView<Communication> communicationTableView;

    private Company company;

    private final TableColumn<Communication, Type> typeColumn = new TableColumn("Type");

    private final TableColumn<Communication, String> idColumn = new TableColumn("Identifier");

    private final TableColumn<Communication, Boolean> prefColumn = new TableColumn("prefered");

    private final ObservableList<Contact> contactsList = FXCollections.observableArrayList();

    private final ObservableList<Address> addressList = FXCollections.observableArrayList();

    private final ObservableList<Communication> communicationsList = FXCollections.observableArrayList();

    private final ToggleGroup prefGroup = new ToggleGroup();

    @FXML
    private void saveAndCloseButtonHandling(ActionEvent event) {
        if ( StringUtils.isBlank(companyNameTextField.getText()) ) {
            UiAlert.message("Es muss ein Firmen Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }
        getCompany();
        Ui.closeWindowOf(taxIdTextField);
    }

    @FXML
    private void saveButtonHandling(ActionEvent event) {
        if ( StringUtils.isBlank(companyNameTextField.getText()) ) {
            UiAlert.message("Es muss ein Firmen Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }
        getCompany();
    }

    @FXML
    private void cancelButtonHandling(ActionEvent event) {
        Ui.closeWindowOf(taxIdTextField);
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

    @FXML
    private void handleEditContactButton(ActionEvent event) {
        Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            openContact(selectedItem);
        }
    }

    @FXML
    private void handleAddContactButton(ActionEvent event) {
        openContact(new Contact());
    }

    @FXML
    private void handleDelContactButton(ActionEvent event) {
        Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            contactsList.remove(selectedItem);
        }

    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

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

        //Contact CellFactory
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

        // force the field to be numeric only
        ledgerTextField.textFormatterProperty().set(new TextFormatter<>(changeed -> {
            if ( decimalPattern.matcher(changeed.getControlNewText()).matches() ) {
                return changeed;
            } else {
                return null;
            }
        }));

    }

    @Override
    public void accept(Company comp) {
        if ( comp != null ) {
            company = comp;
            setCompany(company);
        } else {
            UiAlert.message("Firma ist inkompatibel").show(UiAlertBuilder.Type.WARNING);
        }
    }

    @Override
    public Company getResult() {
        if ( company == null ) {
            return null;
        }
        return company;
    }

    /**
     * open the Address Editor
     *
     * @param addresse is the Address
     */
    private void openAddress(Address addresse) {
        Ui.exec(() -> {
            Ui.build().parent(companyNameTextField).fxml().eval(() -> addresse, AddressUpdateController.class).ifPresent(a -> {
                addressList.add(a);
            });
        });
    }

    /**
     * open the Communication Editor
     *
     * @param communication is the Communication
     */
    private void openCommunication(Communication communication) {
        Ui.exec(() -> {
            Ui.build().parent(companyNameTextField).fxml().eval(() -> communication, CommunicationUpdateController.class).ifPresent(a -> {
                communicationsList.add(a);
            });
        });
    }

    /**
     * open the Contact Editor
     *
     * @param contact is the Contact
     */
    private void openContact(Contact contact) {
        Ui.exec(() -> {
            Ui.build().parent(companyNameTextField).fxml().eval(() -> contact, ContactUpdateController.class).ifPresent(a -> {
                contactsList.add(a);
            });
        });
    }

    /**
     * Set the Company for the Editor
     *
     * @param comp the Company
     */
    private void setCompany(Company comp) {
        contactsList.addAll(comp.getContacts());
        addressList.addAll(comp.getAddresses());
        communicationsList.addAll(comp.getCommunications());

        //fill the listViews
        addressListView.setItems(addressList);
        communicationTableView.setItems(communicationsList);
        communicationTableView.getColumns().addAll(typeColumn, idColumn, prefColumn);

        contactListView.setItems(contactsList);

        companyNameTextField.setText(comp.getName());
        taxIdTextField.setText(comp.getTaxId());
        ledgerTextField.setText("" + comp.getLedger());

    }

    /**
     * Get the Company back
     */
    private void getCompany() {
        company.setName(companyNameTextField.getText());
        company.setTaxId(taxIdTextField.getText());
        company.setLedger(Integer.parseInt(ledgerTextField.getText()));

        company.getAddresses().addAll(addressList);
        company.getCommunications().addAll(communicationsList);
        company.getContacts().addAll(contactsList);
    }

}
