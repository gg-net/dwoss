/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support;

import java.util.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

import eu.ggnet.saft.experimental.auth.AuthenticationException;
import eu.ggnet.saft.experimental.auth.Guardian;

/**
 *
 * @author oliver.guenther
 */
public class FirstLoginController {

    private static class AuthenticationData {

        private final String userName;

        private final char[] passWord;

        public AuthenticationData(String userName, char[] passWord) {
            this.userName = Objects.requireNonNull(userName, "username must not be null");
            this.passWord = Objects.requireNonNull(passWord, "password must not be null");
        }

        @Override
        public String toString() {
            return "AuthenticationData{" + "userName=" + userName + ", passWord=" + Arrays.toString(passWord) + '}';
        }

    }

    @FXML
    private GridPane root;

    @FXML
    private Circle connectionCircle;

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passField;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label statusLabel;

    @FXML
    private Button shutdownButton;

    @FXML
    private Button loginButton;

    private Optional<Guardian> futureGuardian = Optional.empty();

    private Optional<AuthenticationData> authenticationData = Optional.empty();

    private FirstLoginListener loginListener;

    @FXML
    void initialize() {
        // Enable Login only if, userfield and passfields have content
        loginButton.disableProperty().bind(userField.textProperty().isEmpty().or(passField.textProperty().isEmpty()));
        loginButton.setDefaultButton(true);

        loginButton.setOnAction((e) -> {
            authenticationData = Optional.of(new AuthenticationData(userField.getText(), passField.getText().toCharArray()));
            disableInputAndSimulateProgress();
            lazyAuthenticate();
        });

        shutdownButton.setOnAction(e -> loginListener().shutdown());
    }

    public void setLoginListener(FirstLoginListener loginListener) {
        this.loginListener = Objects.requireNonNull(loginListener, "loginListener must not be null");
    }

    /**
     * Setting the Guardian and if authetication data is available, starts the authentication.
     *
     * @param guardian the guardian
     */
    public void setAndActivateGuardian(Guardian guardian) {
        futureGuardian = Optional.ofNullable(guardian);
        if ( futureGuardian.isPresent() ) {
            Platform.runLater(() -> {
                statusLabel.setText("Serververbindung hergestellt");
                connectionCircle.setFill(javafx.scene.paint.Color.GREEN);
            });
            lazyAuthenticate();
        }
    }

    /**
     * starts authentication only if guardian and authenticationdata is set, otherwise does nothing.
     */
    private synchronized void lazyAuthenticate() {
        if ( futureGuardian.isEmpty() ) return;
        if ( authenticationData.isEmpty() ) return;
        try {
            futureGuardian.get().login(authenticationData.get().userName, authenticationData.get().passWord);
            loginListener().loginSuccessful();
        } catch (AuthenticationException ex) {
            Platform.runLater(() -> {
                statusLabel.setText(ex.getMessage());
                enableInput();
            });
        }
    }

    private void disableInputAndSimulateProgress() {
        progressBar.progressProperty().set(-1);
        statusLabel.setText("Versuche Anmeldung");
        passField.setText(""); // removes password and disables login
        userField.disableProperty().set(true);
        passField.disableProperty().set(true);
    }

    private void enableInput() {
        userField.disableProperty().set(false);
        passField.disableProperty().set(false);
        progressBar.progressProperty().set(0);
    }

    private FirstLoginListener loginListener() {
        // Safty bridge. As we can not be sure, the listener is set, but must be.
        return Objects.requireNonNull(loginListener, "FirstLoginListener is null, but must be set. Verify, that setFirstLoginListener is called before");
    }
}
