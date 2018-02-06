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
import javafx.util.converter.IntegerStringConverter;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.AlertType;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Controller class for the editor view of a Company. Allows the user to
 * change all values of the Company.
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

    private TableColumn<Communication, Type> typeColumn = new TableColumn("Type");

    private TableColumn<Communication, String> idColumn = new TableColumn("Identifier");

    private TableColumn<Communication, Boolean> prefColumn = new TableColumn("prefered");

    private ObservableList<Contact> contactsList = FXCollections.observableArrayList();

    private ObservableList<Address> addressList = FXCollections.observableArrayList();

    private ObservableList<Communication> communicationsList = FXCollections.observableArrayList();

    private ToggleGroup prefGroup = new ToggleGroup();

    @FXML
    private Button delAddressButton;

    @FXML
    private Button delComButton;

    @FXML
    private Button delContactButton;

    @FXML
    private Button saveAndCloseButton;

    @FXML
    private Button saveButton;

    @FXML
    private void saveAndCloseButtonHandling() {
        company = getCompany();
        //only get valid object out
        if ( company.getViolationMessage() != null ) {
            Ui.build().alert().message("Firma ist inkompatibel: " + company.getViolationMessage()).show(AlertType.WARNING);
            return;
        }
        Ui.closeWindowOf(taxIdTextField);
    }

    @FXML
    private void saveButtonHandling() {
        company = getCompany();
        //only get valid object out
        if ( company.getViolationMessage() != null ) {
            Ui.build().alert().message("Firma ist inkompatibel: " + company.getViolationMessage()).show(AlertType.WARNING);
            return;
        }
    }

    @FXML
    private void cancelButtonHandling() {
        company = null;
        Ui.closeWindowOf(taxIdTextField);
    }

    @FXML
    private void handleEditAddressButton() {
        Address selectedItem = addressListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            Ui.exec(() -> {
                Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> selectedItem, AddressUpdateController.class).ifPresent(
                        a -> {
                            if ( a != null ) {
                                Platform.runLater(() -> {
                                    addressList.set(addressListView.getSelectionModel().getSelectedIndex(), a);
                                });
                            }
                        });
            });
        }
    }

    @FXML
    private void handleAddAddressButton() {
        Address addresse = new Address();
        addresse.setCity("Stadt");
        addresse.setStreet("Strasse");
        addresse.setZipCode("123456");
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> addresse, AddressUpdateController.class).ifPresent(
                    a -> {
                        if ( a != null ) {
                            Platform.runLater(() -> {
                                addressList.add(a);
                            });
                        }

                    });
        });
    }

    @FXML
    private void handleDelAddressButton() {
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
                Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> selectedItem, CommunicationUpdateController.class).ifPresent(
                        a -> {
                            if ( a != null ) {
                                Platform.runLater(() -> {
                                    communicationsList.set(communicationTableView.getSelectionModel().getSelectedIndex(), a);
                                });
                            }

                        });
            });
        }
    }

    @FXML
    private void handleAddComButton() {
        Communication communication = new Communication();
        communication.setIdentifier("");
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> communication, CommunicationUpdateController.class).ifPresent(
                    a -> {
                        if ( a != null ) {
                            Platform.runLater(() -> {
                                communicationsList.add(a);
                            });
                        }

                    });
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

    @FXML
    private void handleEditContactButton() {
        Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            Ui.exec(() -> {
                Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> selectedItem, ContactUpdateController.class).ifPresent(
                        a -> {
                            if ( a != null ) {
                                Platform.runLater(() -> {
                                    contactsList.set(contactListView.getSelectionModel().getSelectedIndex(), a);
                                });
                            }
                        });
            });
        }
    }

    @FXML
    private void handleAddContactButton() {
        Contact contact = new Contact();
        contact.setLastName("Nachnahme");
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> contact, ContactUpdateController.class).ifPresent(
                    a -> {
                        if ( a != null ) {
                            Platform.runLater(() -> {
                                contactsList.add(a);
                            });
                        }
                    });
        });
    }

    @FXML
    private void handleDelContactButton() {
        int selectedIndex = contactListView.getSelectionModel().getSelectedIndex();
        if ( contactListView.getSelectionModel().getSelectedItems() != null ) {
            contactsList.remove(selectedIndex);
            contactListView.refresh();
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
        delContactButton.disableProperty().bind(contactListView.getSelectionModel().selectedIndexProperty().lessThan(0));

        //get overwriten in accept()
        companyNameTextField.setText("");
        addressListView.setItems(addressList);

        //enable the save and "saveAndClose" button only on filled TextFields
        saveButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> companyNameTextField.getText().trim().isEmpty(), companyNameTextField.textProperty()
                ).or(
                        addressListView.getSelectionModel().selectedIndexProperty().lessThan(0)
                )
        );

        saveAndCloseButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> companyNameTextField.getText().trim().isEmpty(), companyNameTextField.textProperty()
                ).or(
                        addressListView.getSelectionModel().selectedIndexProperty().lessThan(0)
                )
        );

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

        // force the ledger field to be numeric only, becuase the ledger get saved as an int
        ledgerTextField.textFormatterProperty().set(
                new TextFormatter<>(new IntegerStringConverter(), 0,
                        change -> {
                            String newText = change.getControlNewText();
                            if ( Pattern.compile("-?((\\d*))").matcher(newText).matches() ) {
                                return change;
                            } else {
                                return null;
                            }
                        })
        );

    }

    @Override
    public void accept(Company comp) {
        if ( comp != null ) {
            setCompany(comp);
        } else {
            Ui.build().alert().message("Firma ist inkompatibel").show(AlertType.WARNING);
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
        addressListView.getSelectionModel().selectFirst();

        communicationTableView.setItems(communicationsList);
        communicationTableView.getColumns().addAll(typeColumn, idColumn, prefColumn);
        communicationTableView.getSelectionModel().selectFirst();

        contactListView.setItems(contactsList);
        contactListView.getSelectionModel().selectFirst();

        companyNameTextField.setText(comp.getName());
        taxIdTextField.setText(comp.getTaxId());
        ledgerTextField.setText("" + comp.getLedger());
    }

    /**
     * Get the Company back
     */
    private Company getCompany() {
        Company c = new Company();
        c.setName(companyNameTextField.getText());
        c.setTaxId(taxIdTextField.getText());
        c.setLedger(Integer.parseInt(ledgerTextField.getText()));

        c.getAddresses().addAll(addressList);
        c.getCommunications().addAll(communicationsList);
        c.getContacts().addAll(contactsList);

        return c;
    }

}
