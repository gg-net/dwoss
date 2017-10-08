package eu.ggnet.saft.core;

import java.awt.Component;

import javafx.scene.Parent;

import static eu.ggnet.saft.core.UiAlert.Type.INFO;

/**
 * Entry point for short alerts, like JOptionPane but with saft.
 *
 * @author oliver.guenther
 */
public class Alert {

    /**
     * Creates a fluent alert, adding a title.
     *
     * @param title the title of the alert
     * @return a fluent alert
     */
    public static UiAlert title(String title) {
        return new UiAlert().title(title);
    }

    /**
     * Creates a fluent alert, adding a message.
     *
     * @param message the message of the alert.
     * @return a fluent alert.
     */
    public static UiAlert message(String message) {
        return new UiAlert().message(message);
    }

    /**
     * Directly shows an alert as info with the supplied message.
     *
     * @param message the message of the alert.
     */
    public static void show(String message) {
        new UiAlert().message(message).show(INFO);
    }

    /**
     * Directly shows an alert as info based on the parent with the supplied message.
     *
     * @param parent  the (swing) parent
     * @param message the message of the alert
     */
    public static void show(Component parent, String message) {
        new UiAlert().message(message).parent(parent).show(INFO);
    }

    /**
     * Directly shows an alert as info based on the parent with the supplied message.
     *
     * @param parent  the (javafx) parent
     * @param message the message of the alert
     */
    public static void show(Parent parent, String message) {
        new UiAlert().message(message).parent(parent).show(INFO);
    }

    /**
     * Directly shows an alert based on the parent with the supplied message,title and type.
     *
     * @param parent  the (swing) parent
     * @param message the message of the alert
     * @param title   the title of the alert
     * @param type    the type of the alert
     */
    public static void show(Component parent, String title, String message, UiAlert.Type type) {
        new UiAlert().title(title).message(message).parent(parent).show(type);
    }

    /**
     * Directly shows an alert based on the parent with the supplied message,title and type.
     *
     * @param parent  the (javafx) parent
     * @param message the message of the alert
     * @param title   the title of the alert
     * @param type    the type of the alert
     */
    public static void show(Parent parent, String title, String message, UiAlert.Type type) {
        new UiAlert().title(title).message(message).parent(parent).show(type);
    }

    // TODO: Create more shortcuts
}
