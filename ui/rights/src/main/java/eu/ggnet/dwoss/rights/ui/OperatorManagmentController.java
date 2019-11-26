/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.rights.ui;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import eu.ggnet.dwoss.core.system.util.Utils;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.saft.core.ui.ResultProducer;

/**
 *
 * @author Bastian Venz
 */
public class OperatorManagmentController implements Initializable, Consumer<UiOperator>, ResultProducer<UiOperator>, FxController {

    /**
     * This Class converts {@link Integer},{@link Long},{@link Double} and {@link Float} to String and back.
     */
    class NumberStringConverter extends StringConverter<Number> {

        private final Class claaz;

        public NumberStringConverter(Class claaz) {
            this.claaz = claaz;
        }

        @Override
        public String toString(Number t) {
            return t.toString();
        }

        @Override
        public Number fromString(String string) {
            if ( claaz == Integer.class ) {
                return Integer.parseInt(string);
            } else if ( claaz == Long.class ) {
                return Long.parseLong(string);
            } else if ( claaz == Double.class ) {
                return Double.parseDouble(string);
            } else if ( claaz == Float.class ) {
                return Float.parseFloat(string);
            }
            return 0;

        }

    }

    @FXML
    private Label userIdLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField quickloginField;

    @FXML
    private Label passwordLabel;

    @FXML
    private TextField newPasswordField;

    @FXML
    private TextField saltField;

    @FXML
    private ListView<AtomicRight> rightsList;

    @FXML
    private ListView<UiPersona> personasList;

    private UiOperator uiOperator;

    private boolean ok = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rightsList.setCellFactory(new RightsListCell.Factory());
        personasList.setCellFactory(new PersonaListCell.Factory());

    }

    @Override
    public void accept(UiOperator old) {
        System.out.println("accept called");
        setOperator(old);
    }

    public void setOperator(UiOperator op) {
        this.uiOperator = (op != null) ? op : new UiOperator();
        System.out.println("Im Set" + uiOperator);
        userIdLabel.setText("" + uiOperator.idProperty().get());
        Bindings.bindBidirectional(quickloginField.textProperty(), uiOperator.quickLoginKeyProperty(), new NumberStringConverter(Integer.class));
        Bindings.bindBidirectional(usernameField.textProperty(), uiOperator.usernameProperty());
        Bindings.bindBidirectional(passwordLabel.textProperty(), uiOperator.passwordProperty());
        Bindings.bindBidirectional(saltField.textProperty(), uiOperator.saltProperty());
        Bindings.bindBidirectional(rightsList.itemsProperty(), uiOperator.rightsProperty());
    }

    @Override
    public UiOperator getResult() {
        if ( ok ) return uiOperator;
        return null;
    }

    @FXML
    public void onConfirm() {
        ok = true;
        Ui.closeWindowOf(userIdLabel);
    }

    @FXML
    public void onCancel() {
        Ui.closeWindowOf(userIdLabel);
    }

    @FXML
    public void onSetPassword() {
        if ( Utils.isBlank(newPasswordField.getText()) || Utils.isBlank(saltField.getText()) ) return; // Bad solution, cause there is no feedback.
        byte[] hashPassword = hashPassword(newPasswordField.getText(), saltField.getText().getBytes());
        uiOperator.passwordProperty().set(new String(hashPassword));
        uiOperator.saltProperty().set(saltField.getText());
    }

    private static byte[] hashPassword(String password, byte[] salt) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes("UTF-8"));
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString().getBytes("UTF-8");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            throw new RuntimeException();
        }
    }

    public static URL loadFxml() {
        return OperatorManagmentController.class.getResource("OperatorManagmentView.fxml");
    }
}
