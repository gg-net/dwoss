package eu.ggnet.saft.core;

import javafx.scene.Parent;

import java.awt.Component;

import static eu.ggnet.saft.core.UiAlert.Type.INFO;

/**
 * Entry point for short alerts.
 *
 * @author oliver.guenther
 */
public class Alert {

    public static UiAlert title(String title) {
        return new UiAlert().title(title);
    }

    public static UiAlert message(String message) {
        return new UiAlert().message(message);
    }

    public static void show(String message) {
        new UiAlert().message(message).show(INFO);
    }

    public static void show(Component parent, String message) {
        new UiAlert().message(message).parent(parent).show(INFO);
    }

    public static void show(Parent parent, String message) {
        new UiAlert().message(message).parent(parent).show(INFO);
    }

    public static void show(Component parent, String title, String message, UiAlert.Type type) {
        new UiAlert().title(title).message(message).parent(parent).show(type);
    }

    public static void show(Parent parent, String title, String message, UiAlert.Type type) {
        new UiAlert().title(title).message(message).parent(parent).show(type);
    }

    // TODO: Create more shortcuts
}
