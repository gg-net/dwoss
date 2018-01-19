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

import org.apache.commons.lang3.StringUtils;


import eu.ggnet.dwoss.customer.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.entity.Customer.Source;
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
public class CustomerSimpleController implements Initializable {

    //private SimpleCustomerDto simpleCustomerDto;

    private SimpleCustomerFx simpleCustomerFx;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //fill the choiseBoxs
        genderChoiseBox.getItems().addAll(Sex.values());
        sourceChoiseBox.getItems().addAll(Source.values());

    }

//    @Override
//    public void accept(SimpleCustomer sc) {
//        setSimpleCustomer(sc);
//    }
//
//    @Override
//    public SimpleCustomerDto getResult() {
//        if ( simpleCustomerDto == null ) {
//            return null;
//        }
//        return simpleCustomerDto;
//    }

    @FXML
    private void saveAndCloseButtonHandling(ActionEvent event) {

        if ( StringUtils.isBlank(lastNameTextField.getText()) ) {
            UiAlert.message("Es muss ein Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }

        //simpleCustomerDto = SimpleCustomerFxMapper.INSTANCE.to(simpleCustomerFx);

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
        
    }

//    public void setSimpleCustomer(SimpleCustomer simpleCustomer) {
//       // simpleCustomerFx = SimpleCustomerFxMapper.INSTANCE.form(simpleCustomer);
//
//        //the button header
//        headerLabel.textProperty().bindBidirectional(simpleCustomer.getModus().text());
//        changeUIButton.textProperty().bindBidirectional(simpleCustomer.getModus().text());
//
//        //bind textfield
//        companyNameTextFiled.textProperty().bindBidirectional(simpleCustomer.getCompanyName());
//        ustIdTextField.textProperty().bindBidirectional(simpleCustomer.getUstId());
//        titleTextField.textProperty().bindBidirectional(simpleCustomer.getTitle());
//        firstNameTextField.textProperty().bindBidirectional(simpleCustomer.getFirstName());
//        lastNameTextField.textProperty().bindBidirectional(simpleCustomer.getLastName());
//        streetTextField.textProperty().bindBidirectional(simpleCustomer.getStreet());
//        zipcodeTextField.textProperty().bindBidirectional(simpleCustomer.getZipCode());
//        cityTextField.textProperty().bindBidirectional(simpleCustomer.getCity());
//        countryTextField.textProperty().bindBidirectional(simpleCustomer.getCountry());
//        landLineTextField.textProperty().bindBidirectional(simpleCustomer.getLandLine());
//        mobileTextField.textProperty().bindBidirectional(simpleCustomer.getMobile());
//        emailTextField.textProperty().bindBidirectional(simpleCustomer.getEmail());
//
//        commentTextArea.textProperty().bindBidirectional(simpleCustomer.getComment());
//
//    }

}
