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
package tryout;

import java.util.Arrays;
import java.util.Collections;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.client.DwOssMain;
import eu.ggnet.dwoss.assembly.client.support.login.LoggedInTimeoutManager;
import eu.ggnet.dwoss.core.system.autolog.LoggerProducer;
import eu.ggnet.dwoss.core.widget.AbstractGuardian;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.dwoss.rights.api.Operator;
import eu.ggnet.dwoss.core.widget.Dl;
import eu.ggnet.dwoss.core.widget.auth.AuthenticationException;
import eu.ggnet.dwoss.core.widget.auth.Guardian;

import static javafx.event.EventType.ROOT;

/**
 *
 * @author oliver.guenther
 */
public class LoggedInTimeoutTryout {

    public static class LoggedInTimeoutApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            Dl.local().add(Guardian.class, new AbstractGuardian() {
                @Override
                public void login(String user, char[] pass) throws AuthenticationException {
                    if ( "test".equalsIgnoreCase(user) && "test".equals(String.valueOf(pass)) ) {
                        setRights(new Operator(user, 123, Collections.emptyList()));
                        return;
                    } // success
                    if ( "admin".equalsIgnoreCase(user) && "admin".equals(String.valueOf(pass)) ) {
                        setRights(new Operator(user, 666, Arrays.asList(AtomicRight.values())));
                        return;
                    } // success
                    throw new AuthenticationException("User or Pass wrong");
                }
            });

            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            initializer.disableDiscovery();
            initializer.addPackages(true, DwOssMain.class);
            initializer.addPackages(LoggerProducer.class); // core.system. autolog
            SeContainer container = initializer.initialize();

            LoggedInTimeoutManager loggedInTimeout = container.getBeanManager().createInstance().select(LoggedInTimeoutManager.class).get();

            Pane pane = loggedInTimeout.createToolbarElementOnce();
            Scene s = new Scene(pane);

            KeyCombination keysCtrlShiftL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
            stage.setScene(s);
            s.addEventHandler(ROOT, (e) -> {
                loggedInTimeout.resetTime();
            });

            // Global Eventlistener for activity tracking
            s.addEventFilter(KeyEvent.KEY_RELEASED, k -> {
                if ( keysCtrlShiftL.match(k) ) {
                    loggedInTimeout.manualTimeout();
                }
            });

            stage.show();
            loggedInTimeout.startTime();

        }

    }

    public static void main(String[] args) {
        Application.launch(LoggedInTimeoutApplication.class);
    }
}
