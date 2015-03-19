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

import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import eu.ggnet.saft.api.ui.FxController;

/**
 *
 * @author oliver.guenther
 */
public class MobileMainApp extends Application {

    // First testing round, manual set ip
    private final static String URL = "http://192.168.1.148:4204/dwoss-ee-extended-redtape-1.0-SNAPSHOT/unitOverseer/unit";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = constructFxml(UnitAvailabilityController.class, null);
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

    public static <R extends FxController> URL loadView(Class<R> controllerClazz) {
        if ( !controllerClazz.getSimpleName().endsWith("Controller") )
            throw new IllegalArgumentException(controllerClazz + " does not end with Controller");
        String head = controllerClazz.getSimpleName().substring(0, controllerClazz.getSimpleName().length() - "Controller".length());
        return controllerClazz.getResource(head + "View.fxml");
    }

    public static <T, R extends FxController> FXMLLoader constructFxml(Class<R> controllerClazz, T parameter) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(loadView(controllerClazz), "No View for " + controllerClazz));
        loader.load();
        R controller = (R)Objects.requireNonNull(loader.getController(), "No controller based on " + controllerClazz + ". Controller set in Fxml ?");
        if ( parameter != null && controller instanceof Consumer ) {
            try {
                ((Consumer<T>)controller).accept(parameter);
            } catch (ClassCastException e) {
//                LoggerFactory.getLogger(FxSaft.class).warn(controller.getClass() + " implements Consumer, but not of type " + parameter.getClass());
            }
        }
        return loader;
    }
}
