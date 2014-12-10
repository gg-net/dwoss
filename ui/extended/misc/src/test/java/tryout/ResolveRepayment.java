/*
 * Copyright (C) 2014 bastian.venz
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

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.junit.Test;

import eu.ggnet.dwoss.misc.repayment.ResolveRepaymentController;

/**
 *
 * @author bastian.venz
 */
public class ResolveRepayment {

    private boolean complete = false;

    @Test
    public void runTryout() throws InterruptedException {
        new JFXPanel();    // To start the platform

        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Stage stage = new Stage();
                    stage.setTitle("Resolve Repayment");
                    GridPane page = (GridPane)FXMLLoader.load(ResolveRepaymentController.loadFxml());
                    Scene scene = new Scene(page, Color.ALICEBLUE);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                complete = true;
            }
        });

        while (!complete) {
            Thread.sleep(500);
        }

    }
}
