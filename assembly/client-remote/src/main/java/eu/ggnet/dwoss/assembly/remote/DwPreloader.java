package eu.ggnet.dwoss.assembly.remote;

import java.io.IOException;
import java.net.URL;

import javafx.animation.FadeTransition;
import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
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
        ImageView iv = new ImageView(splash);
        p.setCenter(iv);
        Scene s = new Scene(p, splash.getWidth(), splash.getHeight());
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
