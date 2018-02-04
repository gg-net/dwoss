package eu.ggnet.saft.core.ui.builder;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import javafx.scene.Parent;
import javafx.scene.control.Alert;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.core.ui.*;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import static eu.ggnet.saft.core.ui.AlertType.INFO;

/**
 * Fluent Alert Dialog , replacement for JOptionPane.
 * See {@link Alert} as starting point.
 *
 * @author oliver.guenther
 */
@ToString
@EqualsAndHashCode
public class AlertBuilder {

    private final static Logger L = LoggerFactory.getLogger(AlertBuilder.class);

    /**
     * The title.
     */
    private String title = "Information";

    /**
     * The body or message.
     */
    private String message;

    /**
     * A optional parent of Swing.
     */
    private Component swingParent = null;

    /**
     * A optional parent of JavaFx.
     */
    private Parent javafxParent = null;

    public AlertBuilder(PreBuilder pre) {
        swingParent = pre.swingParent;
        if ( !StringUtils.isBlank(pre.title) ) title = pre.title;
    }

    public AlertBuilder() {
    }

    /**
     * Set the title of the alert.
     *
     * @param title the title
     * @return the alert for fluent usage.
     */
    public AlertBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Set the message of the alert.
     *
     * @param message the message
     * @return the alert for fluent usage.
     */
    public AlertBuilder message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Appends to the message prepended by a new line.
     *
     * @param message the message to append
     * @return the alert for fluent usage.
     */
    public AlertBuilder nl(String message) {
        this.message += "\n" + message;
        return this;
    }

    /**
     * Appends a new line to the message.
     *
     * @return the alert for fluent usage.
     */
    public AlertBuilder nl() {
        this.message += "\n";
        return this;
    }

    /**
     * Sets an optional (Swing) parent to alert.
     *
     * @param swingParent the swing parent
     * @return the alert for fluent usage.
     */
    public AlertBuilder parent(Component swingParent) {
        this.swingParent = swingParent;
        return this;
    }

    /**
     * Sets an optional (JavaFx) parent to alert.
     *
     * @param javafxParent the javafx parent
     * @return the alert for fluent usage.
     */
    public AlertBuilder parent(Parent javafxParent) {
        this.javafxParent = javafxParent;
        return this;
    }

    /**
     * Shows the final alert of type info.
     */
    public void show() {
        show(INFO);
    }

    /**
     * Shows the final alert.
     * For now the implementation is done with a JOptionPane.
     *
     * @param type the type of the alert.
     */
    public void show(AlertType type) {
        try {
            // TODO: At the moment, I only have a Swing implementation.
            SwingSaft.dispatch(() -> {
                JOptionPane.showMessageDialog(discoverRoot(), message, title, type.getOptionPaneType());
                return null;
            });
        } catch (ExecutionException | InterruptedException | InvocationTargetException ex) {
            Ui.handle(ex);
        }
    }

    private Component discoverRoot() {
        if ( !UiCore.isRunning() ) L.warn("UiCore not running, Alert still usable, but not great");
        if ( UiCore.isFx() ) {
            L.warn("Root discovery in FxMode not yet implemented");
            return null;
        }
        // In Swing mode.
        if ( swingParent != null ) return SwingCore.windowAncestor(swingParent).orElse(SwingCore.mainFrame());
        if ( javafxParent != null ) return SwingCore.windowAncestor(javafxParent).orElse(SwingCore.mainFrame());
        return SwingCore.mainFrame();
    }
}
