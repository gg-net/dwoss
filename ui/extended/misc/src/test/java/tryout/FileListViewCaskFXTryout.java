/*
 * Copyright (C) 2017 GG-Net GmbH
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
import javafx.scene.Scene;
import javafx.stage.Stage;
import eu.ggnet.dwoss.misc.files.*;

/**
 *
 * @author jens.papenhagen
 */
public class FileListViewCaskFXTryout {

    private static boolean complete = false;

    
    public static void main(String[] args) throws InterruptedException {
        JFXPanel jfxPanel = new JFXPanel(); // To start the platform

        Platform.runLater(() -> {
            Stage stage = new Stage();
            Scene scene = new Scene(new FileListViewCaskFX());

            stage.setScene(scene);
            stage.setTitle("FileListViewCaskFX");
            stage.showAndWait();

            complete = true;
        });

        while (!complete) {
            Thread.sleep(500);
        }
    }
}
