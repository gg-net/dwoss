/*
 * Copyright (C) 2024 GG-Net GmbH
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

import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.rights.api.User;
import eu.ggnet.saft.core.ui.*;

import jakarta.enterprise.context.Dependent;

import static eu.ggnet.saft.core.ui.Bind.Type.SHOWING;

/**
 *
 * @author oliver.guenther
 */
@Title("Password ändern")
@Dependent
public class ChangePasswordPane extends GridPane implements Consumer<User>, ResultProducer<ChangePasswordPane.PasswordChange> {
    
    public static class PasswordChange {
        
        private final long id;
        
        private final char[] password;

        public PasswordChange(long id, char[] password) {
            this.id = id;
            this.password = password;
        }

        public long id() {
            return id;
        }

        public char[] password() {
            return password;
        }

        @Override
        public String toString() {
            return "PasswordChange{" + "id=" + id + ", password=" + password + '}';
        }
        
    }

    @Bind(SHOWING)
    private BooleanProperty showing = new SimpleBooleanProperty();
    
    private final static Logger L = LoggerFactory.getLogger(ChangePasswordPane.class);

    private boolean succsess = false;

    private final PasswordField passwordField = new PasswordField();

    private final Label userName = new Label();

    private User user;

    public ChangePasswordPane() {

        add(new Label("Benutzer:"), 0, 0);
        add(userName, 1, 0);

        add(new Label("Password:"), 0, 1);

        add(passwordField, 1, 1);
        add(new Label("Wiederholen"), 0, 2);
        PasswordField pf1 = new PasswordField();
        add(pf1, 1, 2);
        Button ok = new Button("Ok");
        ok.setOnAction(e -> {
            succsess = true;
            showing.set(false);
        });
        Button cancel = new Button("Abbrechen");
        cancel.setOnAction(e -> showing.set(false));

        ok.disableProperty().bind(passwordField.textProperty().isEmpty().or(passwordField.textProperty().isNotEqualTo(pf1.textProperty())));

        FlowPane buttons = new FlowPane(ok, cancel);
        add(buttons, 0, 3, 2, 1);

    }

    @Override
    public void accept(User user) {
        this.user = user;
        userName.setText(user.getUsername());
    }

    @Override
    public ChangePasswordPane.PasswordChange getResult() {
        if ( !succsess ) return null;
        if ( user == null) {
            L.warn("User is null, was never set");
            return null;
        }
        if (user.getId().isEmpty() || user.getId().get() == 0) {
            L.warn("User.id is unset or 0, persistence not possible");
            return null;
        }
        if (passwordField.getText().isBlank()) {
            L.warn("Password is blank");
            return null;            
        }
        return new PasswordChange(user.getId().get(), passwordField.getText().toCharArray());
    }

}
