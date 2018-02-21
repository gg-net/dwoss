/*
 * Copyright (C) 2018 GG-Net GmbH
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

import javax.swing.JLabel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import eu.ggnet.dwoss.util.HtmlPane;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;

/**
 *
 * @author oliver.guenther
 */
public class HtmlPaneTryout extends Application {

    public static void main(String[] args) {
//        launch(args);
        Ui.exec(() -> {
            UiCore.startSwing(() -> new JLabel("Main Applikation"));
            Ui.build().fx().show(() -> "<h1>Heading</h1><p>Hallo Welt</p>", () -> new HtmlPane());
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        HtmlPane p = new HtmlPane();
        p.accept("<h1>Heading</h1><p>Hallo Welt</p>");
        primaryStage.setScene(new Scene(p));
        primaryStage.show();
    }
}
