package eu.ggnet.saft;

import java.awt.Component;

import javafx.scene.Parent;

import eu.ggnet.saft.core.ui.builder.UiAlertBuilder;

import static eu.ggnet.saft.core.ui.builder.UiAlertBuilder.Type.INFO;

/**
 * Entry point for short alerts, like JOptionPane but with saft.
 *
 * @author oliver.guenther
 * @deprecated use Ui.build().alert() instead.
 */
@Deprecated
public class UiAlert {

    /**
     * Creates a fluent alert, adding a title.
     *
     * @param title the title of the alert
     * @return a fluent alert
     */
    public static UiAlertBuilder title(String title) {
        return new UiAlertBuilder().title(title);
    }

    /**
     * Creates a fluent alert, adding a message.
     *
     * @param message the message of the alert.
     * @return a fluent alert.
     */
    public static UiAlertBuilder message(String message) {
        return new UiAlertBuilder().message(message);
    }

    /**
     * Directly shows an alert as info with the supplied message.
     *
     * @param message the message of the alert.
     */
    public static void show(String message) {
        new UiAlertBuilder().message(message).show(INFO);
    }

    /**
     * Directly shows an alert as info based on the parent with the supplied message.
     *
     * @param parent  the (swing) parent
     * @param message the message of the alert
     */
    public static void show(Component parent, String message) {
        new UiAlertBuilder().message(message).parent(parent).show(INFO);
    }

    /**
     * Directly shows an alert as info based on the parent with the supplied message.
     *
     * @param parent  the (javafx) parent
     * @param message the message of the alert
     */
    public static void show(Parent parent, String message) {
        new UiAlertBuilder().message(message).parent(parent).show(INFO);
    }

    /**
     * Directly shows an alert based on the parent with the supplied message,title and type.
     *
     * @param parent  the (swing) parent
     * @param message the message of the alert
     * @param title   the title of the alert
     * @param type    the type of the alert
     */
    public static void show(Component parent, String title, String message, UiAlertBuilder.Type type) {
        new UiAlertBuilder().title(title).message(message).parent(parent).show(type);
    }

    /**
     * Directly shows an alert based on the parent with the supplied message,title and type.
     *
     * @param parent  the (javafx) parent
     * @param message the message of the alert
     * @param title   the title of the alert
     * @param type    the type of the alert
     */
    public static void show(Parent parent, String title, String message, UiAlertBuilder.Type type) {
        new UiAlertBuilder().title(title).message(message).parent(parent).show(type);
    }

    // TODO: Create more shortcuts
}
