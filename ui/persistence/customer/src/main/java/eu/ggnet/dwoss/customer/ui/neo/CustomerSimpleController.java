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
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.entity.Customer;
import eu.ggnet.dwoss.customer.entity.Customer.Source;
import eu.ggnet.dwoss.customer.entity.dto.SimpleCustomer;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

/**
 * FXML Controller for CustomerSimple Editor
 *
 * @author jens.papenhagen
 */
@Title("Kunden Editieren")
public class CustomerSimpleController implements Initializable, FxController, Consumer<Customer>, ResultProducer<SimpleCustomer> {

    private SimpleCustomer simpleCustomer;

    private boolean bussines = false;

    @FXML
    private HBox companyHBox;

    @FXML
    private Label headerLabel;

    @FXML
    private Label kid;

    @FXML
    private TextArea commentTextArea;

    @FXML
    private ChoiceBox<Source> sourceChoiseBox;

    @FXML
    private Button changeUIButton;

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

    private final TextField companyNameTextFiled = new TextField();

    private final TextField ustIdTextField = new TextField();

    public CustomerSimpleController() {
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //"hidde" the companyHBox
        companyHBox.setDisable(bussines);

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

        getSimpleCustomer();
        Ui.closeWindowOf(kid);
    }

    @FXML
    private void saveAndEnhanceUIButtonHandling(ActionEvent event) {
        if ( StringUtils.isBlank(lastNameTextField.getText()) ) {
            UiAlert.message("Es muss ein Name gesetzt werden").show(UiAlertBuilder.Type.WARNING);
            return;
        }
        getSimpleCustomer();
        
        //TODO convert the simpleCustomer to a Customer
        
        Ui.exec(() -> {
       //     Ui.build().fxml().eval(() -> company, CustomerEnhanceController.class);
        });

        Ui.closeWindowOf(kid);
    }

    @FXML
    private void cancelButtonHandling(ActionEvent event) {
        Ui.closeWindowOf(kid);
    }

    @FXML
    private void changeUI(ActionEvent event) {
        bussines ^= true; //tournaround of the boolean

        getSimpleCustomer();
        setSimpleCustomer(simpleCustomer);
    }

    public void showCompanyHBox() {
        Label companyNameLable = new Label("Firma:");
        Label ustIdLable = new Label("ustID:");

        companyNameTextFiled.setText(simpleCustomer.getCompanyName());
        ustIdTextField.setText(simpleCustomer.getTaxId());
        companyHBox.setSpacing(5.0);

        companyHBox.getChildren().addAll(companyNameLable, companyNameTextFiled, ustIdLable, ustIdTextField);
    }

    public void hiddeCompanyHBox() {
        companyHBox.getChildren().clear();
    }

    public void setSimpleCustomer(SimpleCustomer simpleCustomer) {
        //the button and the header
        if ( bussines ) {
            headerLabel.setText("Geschäftskunde");
            changeUIButton.setText("Endkunde");
            showCompanyHBox();
        } else {
            headerLabel.setText("Endkunde");
            changeUIButton.setText("Geschäftskunde");
            hiddeCompanyHBox();
        }

        kid.setText("" + simpleCustomer.getId());
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

        //select the choicebox
        genderChoiseBox.getSelectionModel().select(simpleCustomer.getSex());
        sourceChoiseBox.getSelectionModel().select(simpleCustomer.getSource());

    }

    private void getSimpleCustomer() {
        simpleCustomer.setTitle(titleTextField.getText());
        simpleCustomer.setFirstName(firstNameTextField.getText());
        simpleCustomer.setLastName(lastNameTextField.getText());
        simpleCustomer.setStreet(streetTextField.getText());
        simpleCustomer.setZipCode(zipcodeTextField.getText());
        simpleCustomer.setCity(cityTextField.getText());
        simpleCustomer.setIsoCountry(countryTextField.getText());
        simpleCustomer.setMobilePhone(mobileTextField.getText());
        simpleCustomer.setLandlinePhone(landLineTextField.getText());
        simpleCustomer.setEmail(emailTextField.getText());
        simpleCustomer.setSex(genderChoiseBox.getSelectionModel().getSelectedItem());
        simpleCustomer.setSource(sourceChoiseBox.getSelectionModel().getSelectedItem());
        simpleCustomer.setComment(commentTextArea.getText());
        simpleCustomer.setCompanyName(companyNameTextFiled.getText());
        simpleCustomer.setTaxId(ustIdTextField.getText());
    }

}
