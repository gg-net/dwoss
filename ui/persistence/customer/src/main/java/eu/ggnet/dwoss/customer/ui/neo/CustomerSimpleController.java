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
import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.FxController;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import org.apache.commons.lang3.StringUtils;

import eu.ggnet.dwoss.customer.ee.CustomerAgent;
import eu.ggnet.dwoss.customer.ee.entity.Country;
import eu.ggnet.dwoss.customer.ee.entity.Communication;
import eu.ggnet.dwoss.customer.ee.entity.Communication.Type;
import eu.ggnet.dwoss.customer.ee.entity.Contact.Sex;
import eu.ggnet.dwoss.customer.ee.entity.Customer;
import eu.ggnet.dwoss.customer.ee.entity.Customer.Source;
import eu.ggnet.dwoss.customer.ee.entity.dto.SimpleCustomer;
import eu.ggnet.saft.core.Dl;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Controller class for the editor view of a SimpleCustomer. Allows the user to
 * change all values of the SimpleCustomer.
 *
 * @author jens.papenhagen
 */
@Title("Kunden Anlegen und Bearbeiten")
public class CustomerSimpleController implements Initializable, FxController, Consumer<Customer>, ResultProducer<CustomerCommand>, ClosedListener {

    private CustomerCommand result = null;

    private boolean bussines = false;

    private final InvalidationListener searchListener = (Observable observable) -> CustomerSimpleController.this.updateSearch();

    @FXML
    private BorderPane mainPane;

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
    private TextField landLineTextField;

    @FXML
    private TextField mobileTextField;

    @FXML
    private TextField emailTextField;

    private final TextField companyNameTextField = new TextField();

    private final TextField ustIdTextField = new TextField();

    @FXML
    private Button saveAndCloseButton;

    @FXML
    private Button saveAndEnhanceUIButton;

    @FXML
    private ComboBox<Country> countryComboBox;

    private ObservableList<Customer> quickSearchList;

    private Timer timer = new Timer();

    private final ExecutorService ES = Executors.newSingleThreadExecutor();

    @FXML
    private void saveAndCloseButtonHandling() {
        try {
            result = CustomerCommand.store(getSimpleCustomer());
            Ui.closeWindowOf(kid);
        } catch (IllegalStateException e) {
            Ui.build(saveAndCloseButton).alert(e.getMessage());
        }

    }

    @FXML
    private void saveAndEnhanceUIButtonHandling() {
        try {
            result = CustomerCommand.storeAndEnhance(getSimpleCustomer());
            Ui.closeWindowOf(kid);
        } catch (IllegalStateException e) {
            Ui.build(saveAndCloseButton).alert(e.getMessage());
        }
    }

    @FXML
    private void cancelButtonHandling() {
        Ui.closeWindowOf(kid);
    }

    @FXML
    private void changeUI() {
        bussines ^= true; //tournaround of the boolean
        setSimpleCustomer(getSimpleCustomer());
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
        genderChoiseBox.getSelectionModel().selectFirst();

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
        sourceChoiseBox.getSelectionModel().selectFirst();

        quickSearchList = FXCollections.observableArrayList();
        ListView<Customer> listView = new ListView<>(quickSearchList);
        listView.setCellFactory(l -> {
            return new ListCell<Customer>() {
                @Override
                protected void updateItem(Customer item, boolean empty) {
                    super.updateItem(item, empty);
                    if ( item == null || empty ) setText("");
                    else if ( item.isSimple() ) setText(item.toName());
                    else {
                        setText("#Complex# - " + item.toName());
                        setTooltip(new Tooltip("Complex Customer, Doppelclick öffnet neues Fenster"));
                    }
                }
            };
        });

        //build context menu
        ContextMenu menu = new ContextMenu();
        MenuItem editItem = new MenuItem("Bearbeiten");
        editItem.setOnAction((event) -> {
            
            Customer current = listView.getSelectionModel()
                    .getSelectedItem();
            
            if ( current.isSimple() ) accept(current);
            else {
                result = CustomerCommand.enhance(current);
                Ui.closeWindowOf(kid);
            }
        });
        MenuItem selectItem = new MenuItem("Auswählen");
        selectItem.setOnAction((event) -> {

            result = CustomerCommand.select(
                    listView.getSelectionModel().getSelectedItem()
            );

            Ui.closeWindowOf(kid);
        });
        menu.getItems().addAll(editItem, selectItem);

        //show context menu on right click
        listView.setOnContextMenuRequested((event) -> {
            menu.show(listView, event.getScreenX(), event.getScreenY());
        });

        listView.setOnMouseClicked((MouseEvent event) -> {
            if ( event.getClickCount() == 2 ) {

                result = CustomerCommand.select(
                        listView.getSelectionModel().getSelectedItem()
                );
                Ui.closeWindowOf(kid);
            }
        });

        mainPane.setRight(listView);

        //get overwriten in accept()
        lastNameTextField.setText("");
        streetTextField.setText("");
        zipcodeTextField.setText("");
        cityTextField.setText("");
        lastNameTextField.textProperty().addListener(searchListener);
        firstNameTextField.textProperty().addListener(searchListener);
        emailTextField.textProperty().addListener(searchListener);
        companyNameTextField.textProperty().addListener(searchListener);

        countryComboBox.getItems().addAll(Country.values());
        countryComboBox.setButtonCell(new CountryListCell());
        countryComboBox.setCellFactory((p) -> new CountryListCell());
        countryComboBox.getSelectionModel().selectFirst();

        //button behavior
        //enable the save and "saveAndEnhanceUI" button only on filled TextFields
        saveAndCloseButton.disableProperty().bind(
                cityTextField.textProperty().isEmpty()
                        .or(lastNameTextField.textProperty().isEmpty())
                        .or(streetTextField.textProperty().isEmpty())
                        .or(zipcodeTextField.textProperty().isEmpty())
                        .or((landLineTextField.textProperty().isEmpty()
                             .and(mobileTextField.textProperty().isEmpty())
                             .and(emailTextField.textProperty().isEmpty()))
                        ));

        saveAndEnhanceUIButton.disableProperty().bind(cityTextField.textProperty().isEmpty()
                .or(lastNameTextField.textProperty().isEmpty())
                .or(streetTextField.textProperty().isEmpty())
                .or(zipcodeTextField.textProperty().isEmpty())
                .or((landLineTextField.textProperty().isEmpty()
                     .and(mobileTextField.textProperty().isEmpty())
                     .and(emailTextField.textProperty().isEmpty()))
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
        if ( c == null ) return;
        if ( !c.isSimple() ) {
            Ui.exec(() -> {
                Ui.build().alert().message("Kunde ist nicht als SimpleCustomer darstellbar " + c.getSimpleViolationMessage()).show(AlertType.WARNING);
            });
            return;
        }
        bussines = c.isBusiness();
        setSimpleCustomer(c.toSimple().get());
    }

    public void setSimpleCustomer(SimpleCustomer simpleCustomer) {
        disableSearch();

        //the button and the header
        if ( bussines ) {
            headerLabel.setText("Geschäftskunde");
            changeUIButton.setText("Endkunde");
            companyNameTextField.setText(simpleCustomer.getCompanyName());
            ustIdTextField.setText(simpleCustomer.getTaxId());
            //fill the HBox for Company
            Label companyNameLable = new Label("Firma:");
            Label ustIdLable = new Label("ustID:");

            companyHBox.getChildren().addAll(companyNameLable, companyNameTextField, ustIdLable, ustIdTextField);
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
        countryComboBox.getSelectionModel().select(simpleCustomer.getCountry());

        landLineTextField.setText(simpleCustomer.getLandlinePhone());
        mobileTextField.setText(simpleCustomer.getMobilePhone());
        emailTextField.setText(simpleCustomer.getEmail());

        commentTextArea.setText(simpleCustomer.getComment());

        //select the choicebox
        if ( simpleCustomer.getSex() != null ) {
            genderChoiseBox.getSelectionModel().select(simpleCustomer.getSex());
        }

        if ( simpleCustomer.getSource() != null ) {
            sourceChoiseBox.getSelectionModel().select(simpleCustomer.getSource());
        }
        if ( simpleCustomer.getId() > 0 ) changeUIButton.setDisable(true); // Disable UI Change on allready pesistend Customer.
    }

    private SimpleCustomer getSimpleCustomer() throws IllegalStateException {
        SimpleCustomer sc = new SimpleCustomer();

        sc.setTitle(titleTextField.getText());
        sc.setFirstName(firstNameTextField.getText());
        sc.setLastName(lastNameTextField.getText());
        sc.setStreet(streetTextField.getText());
        sc.setZipCode(zipcodeTextField.getText());
        sc.setCity(cityTextField.getText());
        sc.setCountry(countryComboBox.getValue());
        if ( StringUtils.isNotBlank(mobileTextField.getText()) ) {

            Communication communication = new Communication(Type.MOBILE, mobileTextField.getText());

            if ( communication.getViolationMessage() != null )
                throw new IllegalStateException(communication.getViolationMessage());

            sc.setMobilePhone(mobileTextField.getText());
        }
        if ( StringUtils.isNotBlank(landLineTextField.getText()) ) {

            Communication communication = new Communication(Type.PHONE, landLineTextField.getText());
            if ( communication.getViolationMessage() != null )
                throw new IllegalStateException(communication.getViolationMessage());

            sc.setLandlinePhone(landLineTextField.getText());

        }
        if ( StringUtils.isNotBlank(emailTextField.getText()) ) {

            Communication communication = new Communication(Type.EMAIL, emailTextField.getText());

            if ( communication.getViolationMessage() != null )
                throw new IllegalStateException(communication.getViolationMessage());

            sc.setEmail(emailTextField.getText());
        }

        sc.setSex(genderChoiseBox.getSelectionModel().getSelectedItem());
        sc.setSource(sourceChoiseBox.getSelectionModel().getSelectedItem());

        sc.setCompanyName(companyNameTextField.getText());
        sc.setTaxId(ustIdTextField.getText());

        sc.setComment(commentTextArea.getText());

        return sc;
    }

    @Override
    public CustomerCommand getResult() {
        return result;
    }

    /**
     * Can be called to inform of a change on parameters.
     */
    private void updateSearch() {
        timer.cancel();
        // special case, we don't wont intelligent search if everything becomes empty
        if ( isEmpty(companyNameTextField.getText()) && isEmpty(firstNameTextField.getText()) && isEmpty(lastNameTextField.getText()) && isEmpty(emailTextField.getText()) )
            return;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // This ensures correct output of search results.
                ES.submit(() -> {
                    List<Customer> result1 = Dl.remote().lookup(CustomerAgent.class)
                            .search(companyNameTextField.getText(), firstNameTextField.getText(), lastNameTextField.getText(), emailTextField.getText(), true);
                    Platform.runLater(() -> {
                        quickSearchList.clear();
                        quickSearchList.addAll(result1);
                    });
                });
            }
        }, 1000);
    }

    /**
     * Disables any search.
     */
    private void disableSearch() {
        timer.cancel();
        mainPane.setRight(null); // remove the search panel.
        firstNameTextField.textProperty().removeListener(searchListener);
        lastNameTextField.textProperty().removeListener(searchListener);
        emailTextField.textProperty().removeListener(searchListener);
        companyNameTextField.textProperty().removeListener(searchListener);
    }

    @Override
    public void closed() {
        timer.cancel();
        ES.shutdown();
    }

}
