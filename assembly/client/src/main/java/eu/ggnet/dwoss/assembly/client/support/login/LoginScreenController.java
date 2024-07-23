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
package eu.ggnet.dwoss.assembly.client.support.login;

import java.util.*;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.core.system.GlobalConfig;
import eu.ggnet.dwoss.core.system.version.Version;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.saft.core.ui.ClosedListener;
import eu.ggnet.saft.core.ui.FxController;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;
import eu.ggnet.dwoss.core.widget.dl.RemoteDl;
import eu.ggnet.dwoss.core.widget.dl.RemoteLookup;

import jakarta.enterprise.context.Dependent;

/**
 * A loginscreen for user/pass authentication, quicklogin authentication and lazy setting of the authentication system.
 * This loginscreen displays two fields: user and pass. The focus is set on the user field.
 * <p>
 * The quicklogin feature is used by pressing "+" followed by 3 numbers.
 * The three characters will not be displayed and consumed directly.
 * A failed quicklogin will not be visible.
 * </p>
 * <p>
 * The lazy setting of the authentication system is done via {@link #setAndActivateGuardianAndRemote(eu.ggnet.saft.experimental.auth.Guardian) }.
 * The first authentication is happening, if user and pass is supplied and the guardian is set. Both things can happen in paralell.
 * </p>
 * <p>
 * The success and cancel operation are consumed, as this class is also used with saft.
 * </p>
 * <p>
 * <i>This class cannot use CDI, as it is used at least once before the CDI container startup</i>
 * </p>
 *
 * @author oliver.guenther
 */
@Dependent
public class LoginScreenController implements ClosedListener, FxController, Consumer<LoginScreenConfiguration> {

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

    private Logger log = LoggerFactory.getLogger(LoginScreenController.class);

    private boolean authenticationSuccessful = false;

    private Optional<Guardian> guardian = Optional.empty();

    private Optional<AuthenticationData> authenticationData = Optional.empty();

    private Optional<RemoteDl> remote = Optional.empty();

    private Consumer<Pane> onSuccess;

    private Runnable onCancel;

    private boolean quickLogin = false;

    private boolean apiValid = false;

    private String quickLoginValue = "";

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

        // wenn + eingegeben wird dann sammle die nächsten 3 werte ein
        userField.textProperty().addListener((ob, o, n) -> {
            if ( quickLogin == true ) {
                quickLoginValue += n;
                userField.setText("");
                if ( quickLoginValue.length() == 3 ) {

                    try {
                        int id = Integer.parseInt(quickLoginValue);
                        // TODO: Wenn CDI geht, das noch mal überdenken, der LoginScreenController ist der einziger ohne CDI.
                        if ( Dl.local().lookup(Guardian.class) == null ) {
                            log.warn("Guardian == null, no Quicklogin available (yet)");
                        } else if ( Dl.local().lookup(Guardian.class).quickAuthenticate(id) ) {
                            log.debug("Quicklogin successful");
                            authenticationSuccessful = true;
                            onSuccess.accept(root);
                        } else {
                            log.debug("Quicklogin failed, key does not match");
                        }
                    } catch (NumberFormatException e) {
                        log.debug("Quicklogin failed, not a number");
                    } finally {
                        // Allways deactivate Quicklogin.
                        quickLoginValue = "";
                        quickLogin = false;
                    }
                }
            } else if ( "+".equals(n) ) {
                quickLogin = true;
                userField.setText("");
            }
        });

        shutdownButton.setOnAction(e -> onCancel.run());
    }

    /**
     * Setting the Guardian and if authetication data is available, starts the authentication.
     *
     * @param guardian the guardian
     * @param remote   the remote lookup
     */
    public void setAndActivateGuardianAndRemote(Guardian guardian, RemoteDl remote) {
        this.guardian = Optional.ofNullable(guardian);
        this.remote = Optional.ofNullable(remote);
        if ( this.guardian.isPresent() && this.remote.isPresent() ) {
            if ( GlobalConfig.API_VERSION != remote.lookup(Version.class).api() ) {
                Platform.runLater(() -> {
                    statusLabel.setText("ApiVersion stimmt nicht überein. Server:" + remote.lookup(Version.class).api() + ", Client:" + GlobalConfig.API_VERSION);
                    connectionCircle.setFill(javafx.scene.paint.Color.MEDIUMVIOLETRED);
                });                
            } else {
                apiValid = true;
                Platform.runLater(() -> {
                    statusLabel.setText("Serververbindung hergestellt");
                    connectionCircle.setFill(javafx.scene.paint.Color.GREEN);
                });
                lazyAuthenticate();
            }
        }
    }

    /**
     * starts authentication only if guardian and authenticationdata is set, otherwise does nothing.
     */
    private synchronized void lazyAuthenticate() {
        if ( !guardian.isPresent() ) return;
        if ( !authenticationData.isPresent() ) return;
        if ( !apiValid ) return;
        try {
            guardian.get().login(authenticationData.get().userName, authenticationData.get().passWord);
            authenticationSuccessful = true;
            onSuccess.accept(root);
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

    /**
     * On close (via ui x) runs the cancel operation.
     */
    @Override
    public void closed() {
        if ( authenticationSuccessful ) return;
        onCancel.run();
    }

    /**
     * Consumes the screen configuration for success, cancel and optional a guardian.
     *
     * @param c the configuration.
     */
    @Override
    public void accept(LoginScreenConfiguration c) {
        onSuccess = c.onSuccess();
        onCancel = c.onCancel();
        setAndActivateGuardianAndRemote(c.guardian().orElse(null), c.remote().orElse(null));
    }

}
