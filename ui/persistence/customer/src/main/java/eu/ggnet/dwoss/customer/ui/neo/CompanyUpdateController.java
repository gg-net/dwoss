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

import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.FxController;

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
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.AlertType;

import lombok.NonNull;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Controller class for the editor view of a Company. Allows the user to
 * change all values of the Company.
 *
 * @author jens.papenhagen
 */
@Title("Firmen Editieren")
public class CompanyUpdateController implements Initializable, FxController, Consumer<Company>, ResultProducer<Company> {

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

    @FXML
    private Button delAddressButton;

    @FXML
    private Button delCommunicationButton;

    @FXML
    private Button delContactButton;

    @FXML
    private Button saveButton;

    private Company company;

    private TableColumn<Communication, Type> typeColumn;

    private TableColumn<Communication, String> idColumn;

    private TableColumn<Communication, Boolean> prefColumn;

    private ObservableList<Contact> contactsList;

    private ObservableList<Address> addressList;

    private ObservableList<Communication> communicationsList;

    private ToggleGroup prefGroup;

    private boolean isCanceled = true;

    @FXML
    private void clickSaveButton() {
        //only get valid object out
        if ( company.getViolationMessage() != null ) {
            Ui.exec(() -> {
                Ui.build().alert().message("Firma ist null: " + company.getViolationMessage()).show(AlertType.ERROR);
            });
            Ui.closeWindowOf(taxIdTextField);
            isCanceled = false;
        }
    }

    @FXML
    private void clickCancelButton() {
        isCanceled = true;
        Ui.closeWindowOf(taxIdTextField);
    }

    @FXML
    private void clickEditAddressButton() {
        Address selectedItem = addressListView.getSelectionModel().getSelectedItem();
        if ( selectedItem != null ) {
            Ui.exec(() -> {
                Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> selectedItem, AddressUpdateController.class)
                        .opt()
                        .filter(a -> a != null)
                        .ifPresent(a -> Platform.runLater(() -> addressList.set(addressListView.getSelectionModel().getSelectedIndex(), a)));
            });
        }
    }

    @FXML
    private void clickAddAddressButton() {
        Address addresse = new Address();
        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> addresse, AddressUpdateController.class)
                    .opt()
                    .filter(a -> a != null)
                    .ifPresent(a -> Platform.runLater(() -> addressList.add(a)));
        });
    }

    @FXML
    private void clickDelAddressButton() {
        int selectedIndex = addressListView.getSelectionModel().getSelectedIndex();
        if ( addressListView.getSelectionModel().getSelectedItems() != null ) {
            addressList.remove(selectedIndex);
            addressListView.refresh();
        }
    }

    @FXML
    private void clickEditCommunicationButton() {
        Communication selectedItem = communicationTableView.getSelectionModel().getSelectedItem();
        if ( selectedItem == null ) return;
        Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> selectedItem, CommunicationUpdateController.class)
                .cf()
                .thenApply(comm -> CustomerConnectorFascade.updateCommunicationOnCompany(company.getId(), comm))
                .thenAcceptAsync(c -> accept(c), Platform::runLater)
                .handle(Ui.handler());
    }

    @FXML
    private void clickAddCommunicationButton() {
        Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(CommunicationUpdateController.class)
                .cf()
                .thenApply(communication -> CustomerConnectorFascade.createCommunicationOnCompany(company.getId(), communication))
                .thenAcceptAsync(c -> accept(c), Platform::runLater)
                .handle(Ui.handler());
    }

    @FXML
    private void clickDelCommunicationButton() {
        if ( communicationTableView.getSelectionModel().getSelectedItem() == null ) return;

        System.out.println("Company: " + company);

        Ui.build(addressListView).dialog().eval(() -> {
            Dialog<Communication> dialog = new Dialog<>();
            dialog.setTitle("Löschen bestätigen");
            dialog.setHeaderText("Möchten sie diese Kommunikation wirklich löschen ?");
            dialog.setContentText(communicationTableView.getSelectionModel().getSelectedItem().toString());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            dialog.setResultConverter((bt) -> {
                if ( bt == ButtonType.YES ) return communicationTableView.getSelectionModel().getSelectedItem();
                return null;
            });
            return dialog;
        })
                .cf()
                .thenApply(add -> CustomerConnectorFascade.deleteCommunicationOnCompany(company.getId(), add))
                .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                .handle(Ui.handler());
    }

    @FXML
    private void clickEditContactButton() {
        Contact selectedItem = contactListView.getSelectionModel().getSelectedItem();

        if ( selectedItem != null ) {
            Ui.exec(() -> {
                Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> selectedItem, ContactUpdateController.class
                )
                        .opt()
                        .filter(a -> a != null)
                        .ifPresent(a -> Platform.runLater(() -> contactsList.set(contactListView.getSelectionModel().getSelectedIndex(), a)));

            });
        }
    }

    @FXML
    private void clickAddContactButton() {
        Contact contact = new Contact();
        Ui
                .exec(() -> {
                    Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> contact, ContactUpdateController.class
                    )
                            .opt()
                            .filter(a -> a != null)
                            .ifPresent(a -> Platform.runLater(() -> contactsList.add(a)));
                });
    }

    @FXML
    private void clickDelContactButton() {
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
        addressList = FXCollections.observableArrayList();
        delAddressButton.disableProperty().bind(addressListView.getSelectionModel().selectedIndexProperty().lessThan(0));
        delCommunicationButton.disableProperty().bind(communicationTableView.getSelectionModel().selectedIndexProperty().lessThan(0));
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
        typeColumn = new TableColumn("Type");
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
        idColumn = new TableColumn("Identifier");
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
        prefColumn = new TableColumn("prefered");
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
                        prefGroup = new ToggleGroup();
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
    public void accept(@NonNull Company company) {
        setCompany(company);
    }

    @Override
    public Company getResult() {
        if ( isCanceled ) return null;
        return company;
    }

    /**
     * Set the Company for the Editor
     *
     * @param comp the Company
     */
    private void setCompany(Company comp) {
        contactsList = FXCollections.observableArrayList();
        contactsList.addAll(comp.getContacts());
        if ( comp.getAddresses() != null ) {
            addressList.addAll(comp.getAddresses());
        }
        communicationsList = FXCollections.observableArrayList();
        communicationsList.addAll(comp.getCommunications());

        //fill the listViews
        addressListView.setItems(addressList);
        addressListView.getSelectionModel().selectFirst();

        communicationTableView.setItems(communicationsList);
        communicationTableView.getColumns().addAll(typeColumn, idColumn, prefColumn);
        communicationTableView.getSelectionModel().selectFirst();

        contactListView.setItems(contactsList);
        contactListView.getSelectionModel().selectFirst();

        if ( comp.getName() != null ) {
            companyNameTextField.setText(comp.getName());
        }

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
