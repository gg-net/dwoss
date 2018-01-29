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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiAlert;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.UiAlertBuilder;

import static javafx.stage.Modality.WINDOW_MODAL;

/**
 * Controller class for the editor view of a SimpleCustomer. Allows the user to
 * change all values of the SimpleCustomer.
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

    private TextField companyNameTextFiled = new TextField();

    private TextField ustIdTextField = new TextField();

    @FXML
    private Button saveAndCloseButton;

    @FXML
    private Button saveAndEnhanceUIButton;

    @FXML
    private Button cancelButton;

    public CustomerSimpleController() {
    }

    /**
     * Initializes the controller class.
     *
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

        saveAndCloseButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> lastNameTextField.getText().trim().isEmpty(), lastNameTextField.textProperty()
                )
        );

        saveAndEnhanceUIButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> lastNameTextField.getText().trim().isEmpty(), lastNameTextField.textProperty()
                )
        );

    }

    @Override
    public void accept(Customer c) {
        if ( c != null || c.isSimple() ) {
            if ( c.isBussines() ) {
                bussines = true;
            }
            setSimpleCustomer(c.toSimple().get());
        } else {
            UiAlert.message("Kunde ist nicht in SimpleCustomer umwandelbar" + c.getSimpleViolationMessage()).show(UiAlertBuilder.Type.WARNING);
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
        simpleCustomer = getSimpleCustomer();
        Ui.closeWindowOf(kid);
    }

    @FXML
    private void saveAndEnhanceUIButtonHandling(ActionEvent event) {
        simpleCustomer = getSimpleCustomer();
        
        //convert the SimpleCustomer to a Customer
        Customer c = new Customer();

        Contact cont = new Contact();
        cont.setFirstName(simpleCustomer.getFirstName());
        cont.setLastName(simpleCustomer.getLastName());
        cont.setSex(simpleCustomer.getSex());
        cont.setTitle(simpleCustomer.getTitle());

        //Contact with only one Address
        Address a = new Address();
        a.setCity(simpleCustomer.getCity());
        a.setIsoCountry(new Locale(simpleCustomer.getIsoCountry().toLowerCase(), simpleCustomer.getIsoCountry().toUpperCase()));
        a.setStreet(simpleCustomer.getStreet());
        a.setZipCode(simpleCustomer.getZipCode());
        cont.add(a);

        //one Communication form eatch type email, phone, mobile allowed
        Communication comm = new Communication();
        if ( !emailTextField.getText().trim().isEmpty() ) {
            comm.setType(Type.EMAIL);
            comm.setIdentifier(simpleCustomer.getEmail());
        }
        if ( !landLineTextField.getText().trim().isEmpty() ) {
            comm.setType(Type.PHONE);
            comm.setIdentifier(simpleCustomer.getLandlinePhone());
        }
        if ( !mobileTextField.getText().trim().isEmpty() ) {
            comm.setType(Type.MOBILE);
            comm.setIdentifier(simpleCustomer.getMobilePhone());
        }
        
        //check if the Communication is valid with the right pattern
        if(comm.getViolationMessages() != null){
            cont.add(comm);
        }else{
            UiAlert.message("Eingabefehler in einem der Kommunikationswege. Bitte überprüfen Sie Diese.").show(UiAlertBuilder.Type.WARNING);
        }
        

        if ( bussines ) {
            //Either a Contact or a Company are set.
            //Contains only one Contact or one Company.
            c.getContacts().clear();

            Company comp = new Company();
            comp.setName(simpleCustomer.getCompanyName());
            comp.setTaxId(simpleCustomer.getTaxId());

            //The Address of the Company Contact has to match the Company Address
            comp.add(a);
            comp.add(cont);

            c.add(comp);

        } else {
            //Contains only one Contact or one Company.
            c.getCompanies().clear();
            c.add(cont);
        }

        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(kid).fxml().eval(() -> c, CustomerEnhanceController.class);
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

        simpleCustomer = getSimpleCustomer();
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

    private SimpleCustomer getSimpleCustomer() {
        SimpleCustomer sc = new SimpleCustomer();

        sc.setTitle(titleTextField.getText());
        sc.setFirstName(firstNameTextField.getText());
        sc.setLastName(lastNameTextField.getText());
        sc.setStreet(streetTextField.getText());
        sc.setZipCode(zipcodeTextField.getText());
        sc.setCity(cityTextField.getText());
        sc.setIsoCountry(countryTextField.getText());
        sc.setMobilePhone(mobileTextField.getText());
        sc.setLandlinePhone(landLineTextField.getText());
        sc.setEmail(emailTextField.getText());
        sc.setSex(genderChoiseBox.getSelectionModel().getSelectedItem());
        sc.setSource(sourceChoiseBox.getSelectionModel().getSelectedItem());

        sc.setComment(commentTextArea.getText());
        sc.setCompanyName(companyNameTextFiled.getText());

        sc.setTaxId(ustIdTextField.getText());

        return sc;
    }

}
