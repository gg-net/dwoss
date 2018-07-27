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

import eu.ggnet.saft.core.ui.Title;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.FxController;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;

import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.saft.core.Ui;

/**
 * Controller class for the editor view of a Company. Allows the user to
 * change all values of the Company.
 *
 * @author jens.papenhagen
 */
@Title("Firma eintragen")
public class CompanyAddController implements Initializable, FxController, ResultProducer<Company> {

    @FXML
    private Button saveButton;

    @FXML
    private TextField companyNameTextField;

    @FXML
    private TextField ledgerTextField;

    @FXML
    private TextField taxIdTextField;

    private Company company;

    @FXML
    private void clickSaveButton(ActionEvent event) {
        company = getCompany();
        //only get valid object out
        //Soll das so bleiben?
//        if ( company.getViolationMessage() != null ) {
//            Ui.exec(() -> {
//                Ui.build().alert().message("Firma ist inkompatibel: " + company.getViolationMessage()).show(AlertType.ERROR);
//            });
//        }
        Ui.closeWindowOf(taxIdTextField);
    }

    @FXML
    private void clickCancelButton(ActionEvent event) {
        company = null;
        Ui.closeWindowOf(taxIdTextField);
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //enable the save and "saveAndClose" button only on filled TextFields
        saveButton.disableProperty().bind(companyNameTextField.textProperty().isEmpty());

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
    public Company getResult() {
        if ( company == null ) {
            return null;
        }
        return company;
    }

    /**
     * Get the Company back
     */
    private Company getCompany() {
        Company c = new Company();
        c.setName(companyNameTextField.getText());
        c.setTaxId(taxIdTextField.getText());
        c.setLedger(Integer.parseInt(ledgerTextField.getText()));

        return c;
    }

}
