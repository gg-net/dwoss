/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.assembly.remote;

import java.io.IOException;
import java.net.URL;

import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;

/**
 *
 * @author oliver.guenther
 */
public class DwPreloader extends Preloader {

    private Stage stage;

    private BorderPane p;

    Scene createSplashScreen() {
        Image splash;
        try {
            splash = new Image(loadSplash().openStream());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
       
        p = new BorderPane();
        p.setCenter(new ImageView(splash));
        Scene s = new Scene(p, splash.getWidth(), splash.getHeight(), Color.TRANSPARENT);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), p);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        return s;
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification scn) {
        if ( scn.getType() == StateChangeNotification.Type.BEFORE_START ) {
            FadeTransition ft = new FadeTransition(Duration.millis(1000), p);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(t -> stage.hide());
            ft.play();
        }
    }

    static URL loadSplash() {
        return ClassLoader.getSystemClassLoader().getResource("splash.png");
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(createSplashScreen());
        stage.show();
    }
}
