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
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.*;
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
    private ListView<Communication> communicationListView;

    private Company company;

    private ObservableList<Contact> contactsList = FXCollections.observableArrayList();

    private ObservableList<Address> addressList = FXCollections.observableArrayList();

    private ObservableList<Communication> communicationsList = FXCollections.observableArrayList();

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
        Communication selectedItem = communicationListView.getSelectionModel().getSelectedItem();
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
        Communication selectedItem = communicationListView.getSelectionModel().getSelectedItem();
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
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Address CellFactory
        addressListView.setCellFactory((ListView<Address> p) -> {
            ListCell<Address> cell = new ListCell<Address>() {
                @Override
                protected void updateItem(Address t, boolean bln) {
                    super.updateItem(t, bln);
                    if ( t != null ) {
                        setText(t.getStreet() + " " + t.getZipCode() + " " + t.getCity());
                    } else {
                        setText("");
                    }
                }
            };
            return cell;
        });
        //Communication CellFactory
        communicationListView.setCellFactory((ListView<Communication> p) -> {
            ListCell<Communication> cell = new ListCell<Communication>() {
                @Override
                protected void updateItem(Communication t, boolean bln) {
                    super.updateItem(t, bln);
                    if ( t != null ) {
                        setText(t.getType() + ": " + t.getIdentifier());
                    } else {
                        setText("");
                    }
                }
            };
            return cell;
        });
        //Contact CellFactory
        contactListView.setCellFactory((ListView<Contact> p) -> {
            ListCell<Contact> cell = new ListCell<Contact>() {
                @Override
                protected void updateItem(Contact t, boolean bln) {
                    super.updateItem(t, bln);
                    if ( t != null ) {
                        setText(t.toFullName());
                    } else {
                        setText("");
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
            Ui.fxml().eval(() -> addresse, AddressUpdateController.class).ifPresent(a -> { addressList.add(a);} );
        });
    }

    /**
     * open the Communication Editor
     * 
     * @param communication is the Communication
     */
    private void openCommunication(Communication communication) {
        Ui.exec(() -> {
            Ui.fxml().eval(() -> communication, CommunicationUpdateController.class);
        });
    }

    /**
     * open the Contact Editor
     * 
     * @param contact is the Contact
     */
    private void openContact(Contact contact) {
        Ui.exec(() -> {
            Ui.fxml().eval(() -> contact, ContactUpdateController.class);
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
        communicationListView.setItems(communicationsList);
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
