package eu.ggnet.dwoss.util.dialog;

import java.awt.Component;

import javax.swing.JOptionPane;

import lombok.experimental.Builder;

/**
 * Alert Dialog (like {@link JOptionPane#showMessageDialog(java.awt.Component, java.lang.Object, java.lang.String, int) ).
 * <p>
 * @author oliver.guenther
 */
@Builder
public class Alert {

    /**
     * The title, show in the decorations of the Window.
     */
    private String title = "";

    /**
     * The body or message.
     */
    private final String body;

    /**
     * A optional parent of Swing.
     */
    private Component parent = null;

    /**
     * Shows the Dialog as Information.
     */
    public void show() {
        JOptionPane.showMessageDialog(parent, body, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows the Dialog as Error.
     */
    public void showAsError() {
        JOptionPane.showMessageDialog(parent, body, title, JOptionPane.ERROR_MESSAGE);
    }

}
