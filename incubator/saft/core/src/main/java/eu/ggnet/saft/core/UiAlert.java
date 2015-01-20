package eu.ggnet.saft.core;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import javafx.scene.Parent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.swing.SwingSaft;

import lombok.*;

/**
 * Replacement for JOptionPane.
 *
 * @author oliver.guenther
 */
@ToString
@EqualsAndHashCode
public class UiAlert {

    private final static Logger L = LoggerFactory.getLogger(UiAlert.class);

    @AllArgsConstructor
    public enum Type {

        INFO(JOptionPane.INFORMATION_MESSAGE),
        WARNING(JOptionPane.WARNING_MESSAGE),
        ERROR(JOptionPane.ERROR_MESSAGE);

        @Getter
        private final int optionPaneType;

    }

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

    public UiAlert title(String title) {
        this.title = title;
        return this;
    }

    public UiAlert message(String message) {
        this.message = message;
        return this;
    }

    /**
     * Appends to the message prepended by a new line.
     *
     * @param message the message to append
     * @return the UiAlert.
     */
    public UiAlert nl(String message) {
        this.message += "\n" + message;
        return this;
    }

    public UiAlert nl() {
        this.message += "\n";
        return this;
    }

    public UiAlert parent(Component swingParent) {
        this.swingParent = swingParent;
        return this;
    }

    public UiAlert parent(Parent javafxParent) {
        this.javafxParent = javafxParent;
        return this;
    }

    public void show(Type type) {
        try {
            // TODO: At the moment, I only have a Swing implementation.
            SwingSaft.dispatch(() -> {
                JOptionPane.showMessageDialog(discoverRoot(), message, title, type.getOptionPaneType());
                return null;
            });
        } catch (ExecutionException | InterruptedException | InvocationTargetException ex) {
            UiCore.handle(ex);
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
