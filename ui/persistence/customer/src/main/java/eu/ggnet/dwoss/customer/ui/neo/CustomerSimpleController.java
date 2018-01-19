package eu.ggnet.dwoss.customer.ui.neo;

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
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.RowConstraints;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.entity.Customer.Source;
import eu.ggnet.dwoss.customer.entity.dto.SimpleCustomer;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class CustomerSimpleController implements Initializable, FxController, Consumer<Customer>, ResultProducer<SimpleCustomer> {

    private SimpleCustomer simpleCustomer;

    @FXML
    private Button saveAndCloseButton;

    @FXML
    private Button saveAndEnhanceUIButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label headerLabel;

    @FXML
    private Label kid;

    @FXML
    private TextField ustIdTextField;

    @FXML
    private ChoiceBox<Source> sourceChoiseBox;

    @FXML
    private Button changeUIButton;

    @FXML
    private TextField companyNameTextFiled;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private ChoiceBox<Sex> genderChoiseBox;

    @FXML
    private TextField streetTextField;

    @FXML
    private TextField zipcodeTextField;

    @FXML
    private TextField cityTextField;

    @FXML
    private TextField countryTextField;

    @FXML
    private TextField landLineTextField;

    @FXML
    private TextField mobileTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextArea commentTextArea;

    private boolean bussines = false;

    @FXML
    private RowConstraints companyRow;

    public CustomerSimpleController() {
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //disable the TextField
        companyNameTextFiled.setDisable(true);
        ustIdTextField.setDisable(true);
        //"hidde" the row
        companyRow.setMinHeight(0);

        //fill the choiseBoxs
        genderChoiseBox.getItems().addAll(Sex.values());
        sourceChoiseBox.getItems().addAll(Source.values());

    }

    @Override
    public void accept(Customer customer) {
        if ( customer != null || customer.isSimple() ) {
            if ( customer.isBussines() ) {
                bussines = true;
            }
            simpleCustomer = customer.toSimple().get();
            setSimpleCustomer(simpleCustomer);
        } else {
            UiAlert.message("Kunde ist nicht in SimpleCustomer umwandelbar").show(UiAlertBuilder.Type.WARNING);
        }

    }

    @Override
    public SimpleCustomer getResult() {
        if ( simpleCustomer == null ) {
            return null;
        }
        return simpleCustomer;
    }

    @FXML
    private void saveAndCloseButtonHandling(ActionEvent event) {

        if ( StringUtils.isBlank(lastNameTextField.getText()) ) {
            UiAlert.message("Es muss ein Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }

        Ui.closeWindowOf(kid);
    }

    @FXML
    private void saveAndEnhanceUIButtonHandling(ActionEvent event) {
        //TODO
    }

    @FXML
    private void cancelButtonHandling(ActionEvent event) {
        Ui.closeWindowOf(kid);
    }

    @FXML
    private void changeUI(ActionEvent event) {      
        bussines ^= true; //tournaround of the boolean
        setSimpleCustomer(simpleCustomer);
    }

    public void setSimpleCustomer(SimpleCustomer simpleCustomer) {
        //the button and the header
        if ( bussines ) {
            headerLabel.setText("Endkunde");
            changeUIButton.setText("Geschäftskunde");

            companyNameTextFiled.setDisable(false);
            ustIdTextField.setDisable(false);

            companyNameTextFiled.setText(simpleCustomer.getCompanyName());
            ustIdTextField.setText(simpleCustomer.getTaxId());

            companyRow.setMinHeight(25.0);
        } else {
            headerLabel.setText("Geschäftskunde");
            changeUIButton.setText("Endkunde");
        }

        titleTextField.setText(simpleCustomer.getTitle());
        firstNameTextField.setText(simpleCustomer.getFirstName());
        lastNameTextField.setText(simpleCustomer.getLastName());
        streetTextField.setText(simpleCustomer.getStreet());
        zipcodeTextField.setText(simpleCustomer.getZipCode());
        cityTextField.setText(simpleCustomer.getCity());
        countryTextField.setText(simpleCustomer.getIsoCountry());
        landLineTextField.setText(simpleCustomer.getLandlinePhone());
        mobileTextField.setText(simpleCustomer.getMobilePhone());
        emailTextField.setText(simpleCustomer.getEmail());

        commentTextArea.setText(simpleCustomer.getComment());

    }

}
