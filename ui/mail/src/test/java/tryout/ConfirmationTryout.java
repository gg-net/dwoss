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

import java.util.concurrent.CancellationException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.stage.Stage;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;

import static javafx.scene.control.ButtonType.OK;

/**
 *
 * @author oliver.guenther
 */
public class ConfirmationTryout {

    public static class Tryout extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            UiCore.startJavaFx(primaryStage, () -> {
                Button b = new Button("Click Me");
                b.setOnAction((ActionEvent event) -> {
                    Ui.build().dialog().eval(() -> {
                        Alert alert = new Alert(AlertType.CONFIRMATION);
                        alert.setTitle("Händlerliste versenden");
                        alert.setHeaderText("Möchten Sie die Händlerliste jetzt versenden");
                        return alert;
                    }).cf().thenAccept((ButtonType t) -> {if( t != OK) throw new CancellationException("Result is null");})
                           .thenRun(() -> System.out.println("JO")).handle(Ui.handler());
                });
                return b;
            });
            primaryStage.show();
        }

        public static void main(String[] args) {
            launch(args);
        }

    }

    public static void main(String[] args) {
        Tryout.main(args);
    }

}
