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

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import eu.ggnet.dwoss.assembly.client.Main;
import eu.ggnet.dwoss.assembly.client.support.login.LoggedInTimeout;
import eu.ggnet.dwoss.core.system.autolog.LoggerProducer;
import eu.ggnet.dwoss.core.widget.event.UserChange;
import eu.ggnet.dwoss.rights.api.AtomicRight;
import eu.ggnet.saft.api.Authorisation;

import static javafx.event.EventType.ROOT;

/**
 *
 * @author oliver.guenther
 */
public class LoggedInTimeoutTryout {

    public static class LoggedInTimeoutApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            SeContainerInitializer initializer = SeContainerInitializer.newInstance();
            initializer.disableDiscovery();
            initializer.addPackages(true, Main.class);
            initializer.addPackages(LoggerProducer.class); // core.system. autolog
            SeContainer container = initializer.initialize();

            LoggedInTimeout loggedInTimeout = container.getBeanManager().createInstance().select(LoggedInTimeout.class).get();

            Pane pane = loggedInTimeout.createPane();
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

            loggedInTimeout.setTimeoutAction(() -> {
                Alert a = new Alert(AlertType.INFORMATION);
                a.setContentText("Timeout Happend, updateing rights");
                a.showAndWait();
                loggedInTimeout.startTime();
                Set<Authorisation> auth = new HashSet<>();
                auth.add(AtomicRight.MODIFY_LOGGED_IN_TIMEOUT);
                loggedInTimeout.changeUser(new UserChange("Demo", auth));
            });
            stage.show();
            loggedInTimeout.startTime();
            loggedInTimeout.setTimeoutAndStartTime(LocalTime.of(0, 1, 30));
            loggedInTimeout.setTimeoutStore(e -> System.out.println("Storing: " + e));
        }

    }

    public static void main(String[] args) {
        Application.launch(LoggedInTimeoutApplication.class);
    }
}
