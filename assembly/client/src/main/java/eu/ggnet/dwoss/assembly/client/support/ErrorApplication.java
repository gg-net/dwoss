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

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Used to display a default Error message on Startup.
 *
 * @author oliver.guenther
 */
public class ErrorApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Fehler beim Start");
        a.setHeaderText("Fehler bei Start.");
        a.setContentText("Es ist ein Fehler beim Start aufgetreten. Bitte Console und/oder Logs prüfen.");
        a.show();
    }

}
