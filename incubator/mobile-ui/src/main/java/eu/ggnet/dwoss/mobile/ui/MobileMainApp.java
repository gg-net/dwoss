/*
 * Copyright (C) 2015 GG-Net GmbH
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
package eu.ggnet.dwoss.mobile.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import eu.ggnet.saft.core.fx.FxSaft;

/**
 *
 * @author oliver.guenther
 */
public class MobileMainApp extends Application {

    private final static String URL = "http://localhost:4204/dwoss-ee-extended-redtape-1.0-SNAPSHOT/unitOverseer/unit";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = FxSaft.constructFxml(UnitAvailabilityController.class, null);
        UnitAvailabilityController controller = loader.getController();
        controller.url = URL;
        Pane p = loader.getRoot();

        primaryStage.setTitle("Client!");
        primaryStage.setScene(new Scene(p));
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
