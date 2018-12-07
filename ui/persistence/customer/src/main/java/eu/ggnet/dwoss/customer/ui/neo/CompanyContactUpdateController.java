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
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import lombok.NonNull;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Controller class for the editor view of a company contact.
 * Allows the user to change all values of the Contact.
 * <p>
 * import static javafx.stage.Modality.WINDOW_MODAL;
 *
 * @author pascal.perau
 */
public class CompanyContactUpdateController implements Initializable, FxController, Consumer<Contact>, ResultProducer<Contact> {

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

    private ObservableList<Communication> communicationsList = FXCollections.observableArrayList();

    private ToggleGroup prefGroup = new ToggleGroup();

    @FXML
    private Button editCommunicationButton;

    @FXML
    private Button deleteCommunicationButton;

    @FXML
    private Button saveButton;

    private boolean isCanceled = true;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        editCommunicationButton.disableProperty().bind(communicationTableView.getSelectionModel().selectedItemProperty().isNull());
        editCommunicationButton.setOnAction(e -> {
            Communication selectedItem = communicationTableView.getSelectionModel().getSelectedItem();
            if ( selectedItem == null ) return;
            Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(() -> selectedItem, CommunicationUpdateController.class)
                    .cf()
                    .thenApply(comm -> CustomerConnectorFascade.updateCommunicationOnContact(contact.getId(), comm))
                    .thenAcceptAsync(c -> accept(c), Platform::runLater)
                    .handle(Ui.handler());
        });

        deleteCommunicationButton.disableProperty().bind(communicationTableView.getSelectionModel().selectedItemProperty().isNull());
        deleteCommunicationButton.setOnAction(e -> {
            if ( communicationTableView.getSelectionModel().getSelectedItem() == null ) return;

            Ui.build(communicationTableView).dialog().eval(() -> {
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
                    .thenApply(add -> CustomerConnectorFascade.deleteCommunicationOnContact(contact.getId(), add))
                    .thenAcceptAsync(cont -> accept(cont), Platform::runLater)
                    .handle(Ui.handler());
        });

        //get overwriten in accept()
        lastNameTextField.setText("");

        //enable the save and "saveAndClose" button only on filled TextFields
        saveButton.disableProperty().bind(lastNameTextField.textProperty().isEmpty());

        saveButton.setOnAction((e) -> {
            updateContact();
            if ( contact.getViolationMessage() != null ) {
                Ui.build().alert().message("Kontakt ist invalid: " + contact.getViolationMessage()).show(AlertType.ERROR);
            } else {
                isCanceled = false;
                Ui.closeWindowOf(lastNameTextField);
            }
        });

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

        //fill the listViews
        communicationTableView.setItems(communicationsList);
        communicationTableView.getColumns().setAll(typeColumn, idColumn);
        communicationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    private void clickCancelButton() {
        isCanceled = true;
        Ui.closeWindowOf(lastNameTextField);
    }

    @FXML
    private void clickAddCommunicationButton() {
        Ui.build().modality(WINDOW_MODAL).parent(firstNameTextField).fxml().eval(CommunicationUpdateController.class)
                .cf()
                .thenApply(communication -> CustomerConnectorFascade.createCommunicationOnContact(contact.getId(), communication))
                .thenAcceptAsync(c -> accept(c), Platform::runLater)
                .handle(Ui.handler());
    }

    // For now i define, that a contact must be set, may change in the future
    @Override
    public void accept(@NonNull Contact cont) {
        setContact(cont);
    }

    @Override
    public Contact getResult() {
        if ( isCanceled ) return null;
        return contact;
    }

    /**
     * Set the Contact for the Editor
     *
     * @param comp the Contact
     */
    private void setContact(Contact contact) {
        this.contact = contact;
        titleTextField.setText(contact.getTitle());
        firstNameTextField.setText(contact.getFirstName());
        lastNameTextField.setText(contact.getLastName());

        genderBox.getSelectionModel().select(contact.getSex());

        communicationsList.setAll(contact.getCommunications());
    }

    /**
     * update the contact before, consider doing this on element change.
     */
    private void updateContact() {
        contact.setTitle(titleTextField.getText());
        contact.setFirstName(firstNameTextField.getText());
        contact.setLastName(lastNameTextField.getText());
        contact.setSex(genderBox.getSelectionModel().getSelectedItem());
    }

}
