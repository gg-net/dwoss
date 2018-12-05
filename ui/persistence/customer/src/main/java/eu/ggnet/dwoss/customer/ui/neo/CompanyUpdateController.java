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

import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.FxController;

import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.application.Platform;
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
    private Button deleteAddressButton;

    @FXML
    private Button deleteCommunicationButton;

    @FXML
    private Button deleteContactButton;

    @FXML
    private Button editAddressButton;

    @FXML
    private Button editCommunicationButton;

    @FXML
    private Button editContactButton;

    @FXML
    private Button saveButton;

    private Company company;

    private TableColumn<Communication, Type> typeColumn;

    private TableColumn<Communication, String> idColumn;

    private ObservableList<Contact> contactsList;

    private ObservableList<Address> addressList;

    private ObservableList<Communication> communicationsList;

    private boolean isCanceled = true;

    @FXML
    private void clickSaveButton() {
        //only get valid object out
        if ( company.getViolationMessage() != null ) {
            Ui.build().alert().message("Firma ist invalid: " + company.getViolationMessage()).show(AlertType.ERROR);
        } else {
            updateCompany();
            isCanceled = false;
            Ui.closeWindowOf(taxIdTextField);
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
        if ( selectedItem == null ) return;
        Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> selectedItem, AddressUpdateController.class)
                .cf()
                .thenApply(add -> CustomerConnectorFascade.updateAddressOnCompany(company.getId(), add))
                .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                .handle(Ui.handler());
    }

    @FXML
    private void clickAddAddressButton() {
        Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> new Address(), AddressUpdateController.class)
                .cf()
                .thenApply(add -> CustomerConnectorFascade.createAddressOnCompany(company.getId(), add))
                .thenAcceptAsync(c -> accept(c), Platform::runLater)
                .handle(Ui.handler());
    }

    @FXML
    private void clickDeleteAddressButton() {
        if ( addressListView.getSelectionModel().getSelectedItem() == null ) return;

        Ui.build(addressListView).dialog().eval(() -> {
            Dialog<Address> dialog = new Dialog<>();
            dialog.setTitle("Löschen bestätigen");
            dialog.setHeaderText("Möchten sie die Addresse wirklich löschen ?");
            dialog.setContentText(addressListView.getSelectionModel().getSelectedItem().toHtml());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            dialog.setResultConverter((bt) -> {
                if ( bt == ButtonType.YES ) return addressListView.getSelectionModel().getSelectedItem();
                return null;
            });
            return dialog;
        })
                .cf()
                .thenApply(add -> CustomerConnectorFascade.deleteAddressOnCompany(company.getId(), add))
                .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                .handle(Ui.handler());
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
    private void clickDeleteCommunicationButton() {
        if ( communicationTableView.getSelectionModel().getSelectedItem() == null ) return;

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
        if ( selectedItem == null ) return;
        Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(() -> selectedItem, CompanyContactUpdateController.class)
                .cf()
                .thenApply(add -> CustomerConnectorFascade.updateContactOnCompany(company.getId(), add))
                .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                .handle(Ui.handler())
                .thenApply(x -> CustomerConnectorFascade.reload(company)) // Allways reload, no mater what. Changes may have happend even if cancel is pressed
                .thenAcceptAsync(c -> accept(c), Platform::runLater);
    }

    @FXML
    private void clickAddContactButton() {
        Ui.build().modality(WINDOW_MODAL).parent(companyNameTextField).fxml().eval(ContactAddController.class)
                .cf()
                .thenApply(add -> CustomerConnectorFascade.createContactOnCompany(company.getId(), add))
                .thenAcceptAsync(c -> accept(c), Platform::runLater)
                .handle(Ui.handler());
    }

    @FXML
    private void clickDeleteContactButton() {

        if ( contactListView.getSelectionModel().getSelectedItem() == null ) return;

        Ui.build(contactListView).dialog().eval(() -> {
            Dialog<Contact> dialog = new Dialog<>();
            dialog.setTitle("Löschen bestätigen");
            dialog.setHeaderText("Möchten sie diesen Kontakt wirklich löschen ?");
            dialog.setContentText(contactListView.getSelectionModel().getSelectedItem().toString());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
            dialog.setResultConverter((bt) -> {
                if ( bt == ButtonType.YES ) return contactListView.getSelectionModel().getSelectedItem();
                return null;
            });
            return dialog;
        })
                .cf()
                .thenApply(add -> CustomerConnectorFascade.deleteContactOnCompany(company.getId(), add))
                .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                .handle(Ui.handler());
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        addressList = FXCollections.observableArrayList();
        addressListView.setItems(addressList);
        communicationsList = FXCollections.observableArrayList();
        communicationTableView.setItems(communicationsList);
        contactsList = FXCollections.observableArrayList();
        contactListView.setItems(contactsList);

        //button behavior
        deleteAddressButton.disableProperty().bind(addressListView.getSelectionModel().selectedItemProperty().isNull());
        deleteCommunicationButton.disableProperty().bind(communicationTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteContactButton.disableProperty().bind(contactListView.getSelectionModel().selectedItemProperty().isNull());
        editAddressButton.disableProperty().bind(addressListView.getSelectionModel().selectedItemProperty().isNull());
        editCommunicationButton.disableProperty().bind(communicationTableView.getSelectionModel().selectedItemProperty().isNull());
        editContactButton.disableProperty().bind(contactListView.getSelectionModel().selectedItemProperty().isNull());

        //get overwriten in accept()
        companyNameTextField.setText("");

        //enable the save button only on filled companyName TextField
        saveButton.disableProperty().bind(companyNameTextField.textProperty().isEmpty());

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
        typeColumn = new TableColumn("Typ");
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
        idColumn = new TableColumn("Wert");
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

        //adding all columns to the communicationTable
        communicationTableView.getColumns().setAll(typeColumn, idColumn);
        communicationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
                        StringBuilder text = new StringBuilder("");
                        //add pronoun
                        if ( item.getSex() == Sex.FEMALE ) {
                            text.append("Frau ");
                        }
                        if ( item.getSex() == Sex.MALE ) {
                            text.append("Herr ");
                        }

                        //add full name
                        text.append(item.toFullName());
                        
                        //add communication types to the line
                        if ( !item.getCommunications().isEmpty() ) {
                            text.append(" | ");
                            text.append(item.getCommunications().stream().map(com -> com.getType().name()).collect(Collectors.joining(", ")));
                        }
                        setText(text.toString());
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
        this.company = comp;
        companyNameTextField.setText(comp.getName());
        taxIdTextField.setText(comp.getTaxId());
        ledgerTextField.setText(comp.getLedger() + "");

        addressList.clear();
        addressList.addAll(comp.getAddresses());
        communicationsList.clear();
        communicationsList.addAll(comp.getCommunications());
        contactsList.clear();
        contactsList.addAll(comp.getContacts());

    }

    /**
     * Get the Company back
     */
    private void updateCompany() {

        company.setName(companyNameTextField.getText());
        company.setTaxId(taxIdTextField.getText());
        company.setLedger(Integer.parseInt(ledgerTextField.getText()));

    }

}
