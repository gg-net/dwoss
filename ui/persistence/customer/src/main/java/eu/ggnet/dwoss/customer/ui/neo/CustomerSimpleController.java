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
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.saft.*;
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
    private void saveAndCloseButtonHandling() {
        simpleCustomer = getSimpleCustomer();

        Customer tempCustomer = Dl.remote().lookup(CustomerAgent.class).store(simpleCustomer);

        //only get valid object out
        if ( tempCustomer.getViolationMessage() != null ) {
            UiAlert.message("Kunde ist inkompatibel: " + tempCustomer.getViolationMessage()).show(UiAlertBuilder.Type.WARNING);
            return;
        }

        Ui.closeWindowOf(kid);
    }

    @FXML
    private void saveAndEnhanceUIButtonHandling() {
        simpleCustomer = getSimpleCustomer();

        Customer tempCustomer = Dl.remote().lookup(CustomerAgent.class).store(simpleCustomer);

        //only get valid object out
        if ( tempCustomer.getViolationMessage() != null ) {
            UiAlert.message("Kunde ist inkompatibel: " + tempCustomer.getViolationMessage()).show(UiAlertBuilder.Type.WARNING);
            return;
        }

        Ui.exec(() -> {
            Ui.build().modality(WINDOW_MODAL).parent(kid).fxml().eval(() -> tempCustomer, CustomerEnhanceController.class);
        });
        //close this window
        simpleCustomer = null;
        Ui.closeWindowOf(kid);
    }

    @FXML
    private void cancelButtonHandling() {
        simpleCustomer = null;
        Ui.closeWindowOf(kid);
    }

    @FXML
    private void changeUI() {
        bussines ^= true; //tournaround of the boolean

        simpleCustomer = getSimpleCustomer();
        setSimpleCustomer(simpleCustomer);
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
        genderChoiseBox.setConverter(new StringConverter<Sex>() {
            @Override
            public Sex fromString(String string) {
                throw new UnsupportedOperationException("Invalid operation for Convert a String into a Sex.");
            }

            @Override
            public String toString(Sex myClassinstance) {
                return myClassinstance.getSign();
            }
        });
        genderChoiseBox.getItems().addAll(Sex.values());
        sourceChoiseBox.setConverter(new StringConverter<Source>() {
            @Override
            public Source fromString(String personString) {
                throw new UnsupportedOperationException("Invalid operation for Convert a String into a Source.");
            }

            @Override
            public String toString(Source sour) {
                if ( sour == null ) {
                    return null;
                } else {
                    return sour.getName();
                }
            }
        });
        sourceChoiseBox.getItems().addAll(Source.values());
        
        //get overwriten in accept()
        lastNameTextField.setText("");
        streetTextField.setText("");       

        //button behavior
        //enable the save and "saveAndEnhanceUI" button only on filled TextFields
        saveAndCloseButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> lastNameTextField.getText().trim().isEmpty(), lastNameTextField.textProperty()
                ).or(
                        Bindings.createBooleanBinding(()
                                -> streetTextField.getText().trim().isEmpty(), streetTextField.textProperty()
                        )
                ));

        saveAndEnhanceUIButton.disableProperty().bind(
                Bindings.createBooleanBinding(()
                        -> lastNameTextField.getText().trim().isEmpty(), lastNameTextField.textProperty()
                ).or(
                        Bindings.createBooleanBinding(()
                                -> streetTextField.getText().trim().isEmpty(), streetTextField.textProperty()
                        )
                ));

        // force the zipcode field to be numeric only, becuase the ledger get saved as an int
        zipcodeTextField.textFormatterProperty().set(
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
    public void accept(Customer c) {
        if ( c != null ) {
            if ( c.isBussines() ) {
                bussines = true;
            }
            setSimpleCustomer(c.toSimple().get());
        } else {
            UiAlert.message("Kunde ist nicht in SimpleCustomer umwandelbar " + c.getSimpleViolationMessage()).show(UiAlertBuilder.Type.WARNING);
        }

    }

    @Override
    public SimpleCustomer getResult() {
        if ( simpleCustomer == null ) {
            return null;
        }
        return simpleCustomer;
    }

    public void setSimpleCustomer(SimpleCustomer simpleCustomer) {
        //the button and the header
        if ( bussines ) {
            headerLabel.setText("Geschäftskunde");
            changeUIButton.setText("Endkunde");
            companyNameTextFiled.setText(simpleCustomer.getCompanyName());
            ustIdTextField.setText(simpleCustomer.getTaxId());
            //fill the HBox for Company
            Label companyNameLable = new Label("Firma:");
            Label ustIdLable = new Label("ustID:");

            companyHBox.getChildren().addAll(companyNameLable, companyNameTextFiled, ustIdLable, ustIdTextField);
            companyHBox.setSpacing(5.0);
        } else {
            headerLabel.setText("Endkunde");
            changeUIButton.setText("Geschäftskunde");
            companyHBox.getChildren().clear();
        }

        kid.setText("" + simpleCustomer.getId());
        titleTextField.setText(simpleCustomer.getTitle());
        firstNameTextField.setText(simpleCustomer.getFirstName());
        lastNameTextField.setText(simpleCustomer.getLastName());
        streetTextField.setText(simpleCustomer.getStreet());
        zipcodeTextField.setText(simpleCustomer.getZipCode());
        cityTextField.setText(simpleCustomer.getCity());
        countryTextField.setText(new Locale("", simpleCustomer.getIsoCountry()).getDisplayName());
        landLineTextField.setText(simpleCustomer.getLandlinePhone());
        mobileTextField.setText(simpleCustomer.getMobilePhone());
        emailTextField.setText(simpleCustomer.getEmail());

        commentTextArea.setText(simpleCustomer.getComment());

        //select the choicebox
        if ( simpleCustomer.getSex() != null ) {
            genderChoiseBox.getSelectionModel().select(simpleCustomer.getSex());
        } else {
            genderChoiseBox.getSelectionModel().selectFirst();
        }

        if ( simpleCustomer.getSource() != null ) {
            sourceChoiseBox.getSelectionModel().select(simpleCustomer.getSource());
        } else {
            sourceChoiseBox.getSelectionModel().selectFirst();
        }

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

        sc.setCompanyName(companyNameTextFiled.getText());
        sc.setTaxId(ustIdTextField.getText());

        sc.setComment(commentTextArea.getText());

        return sc;
    }

}
