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
package eu.ggnet.saft.core.experimental;

import java.awt.Component;
import java.awt.Window;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.SwingCore;
import eu.ggnet.saft.core.fx.FxSaft;

/**
 *
 * @author oliver.guenther
 */
public class DialogBuilder extends AbstractBuilder {

    public DialogBuilder() {
        SwingCore.ensurePlatformIsRunning();
    }

    /**
     * Sets the once mode.
     * If set to true, an once mode is enable. This ensures that one one window of the same type is created and show.
     * If minimised it becomes reopend, if in the back it becomes moved to the front.
     *
     * @param once the once mode
     * @return this as fluent usage
     */
    public DialogBuilder once(boolean once) {
        super.once = once;
        return this;
    }

    /**
     * An optional id. Replaces the id part in a title like: this is a title of {id}
     *
     * @param id the optional id.
     * @return this as fluent usage
     */
    public DialogBuilder id(String id) {
        super.id = id;
        return this;
    }

    /**
     * An optional title. If no title is given, the classname is used.
     *
     * @param title the title;
     * @return this as fluent usage
     */
    public DialogBuilder title(String title) {
        super.title = title;
        return this;
    }

    /**
     * Enables the Frame mode, makeing the created window a first class element.
     *
     * @param frame if true frame is assumed.
     * @return this as fluent usage
     */
    public DialogBuilder frame(boolean frame) {
        super.frame = frame;
        return this;
    }

    /**
     * Optional value for the modality.
     *
     * @param modality the modality to use
     * @return this as fluent usage
     */
    public DialogBuilder modality(Modality modality) {
        super.modality = modality;
        return this;
    }

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param swingParent the parent
     * @return this as fluent usage
     */
    public DialogBuilder parent(Component swingParent) {
        super.swingParent = SwingCore.windowAncestor(swingParent).orElse(SwingCore.mainFrame());
        return this;
    }

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param javaFxParent the parent
     * @return this as fluent usage
     */
    public DialogBuilder parent(Parent javaFxParent) {
        super.swingParent = SwingCore.windowAncestor(javaFxParent).orElse(SwingCore.mainFrame());
        return this;
    }

    /**
     * Creates the javafx Dialog via the producer, shows it and returns the evaluated result as Optional.
     *
     * @param <T>            type of the result
     * @param <P>            result type of the preProducer
     * @param <V>
     * @param dialogProducer the javafx Dialog producer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, V extends Dialog<T>> Optional<T> eval(Callable<V> dialogProducer) {
        try {
            Objects.requireNonNull(dialogProducer, "The dialogProducer is null, not allowed");

            V dialog = FxSaft.dispatch(dialogProducer);
            Params p = buildParameterBackedUpByDefaults(dialog.getClass());
            if ( isOnceModeAndActiveWithSideeffect(p.key()) ) return Optional.empty();
            dialog.getDialogPane().getScene().setRoot(new BorderPane()); // Remove the DialogPane form the Scene, otherwise an Exception is thrown
            Window window = constructAndShow(SwingCore.wrap(dialog.getDialogPane()), p, Dialog.class); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            dialog.getDialogPane().getButtonTypes().stream().map(t -> dialog.getDialogPane().lookupButton(t)).forEach(b -> { // Add Closing behavior on all buttons.
                ((Button)b).setOnAction(e -> {
                    L.debug("Close on Dialog called");
                    Ui.closeWindowOf(window);
                });
            });
            wait(window);
            return Optional.ofNullable(dialog.getResult());

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates the javafx Dialog via the producer, supplies the consumer part with the result of the preProducer, shows it and returns the evaluated result as
     * Optional.
     *
     * @param <T>            type of the result
     * @param <P>            result type of the preProducer
     * @param <V>
     * @param preProducer    the preproducer, must not be null
     * @param dialogProducer the javafx Dialog producer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, P, V extends Dialog<T> & Consumer<P>> Optional<T> eval(Callable<P> preProducer, Callable<V> dialogProducer) {
        try {
            Objects.requireNonNull(dialogProducer, "The dialogProducer is null, not allowed");

            V dialog = FxSaft.dispatch(dialogProducer);
            Params p = buildParameterBackedUpByDefaults(dialog.getClass());
            P preResult = callWithProgress(preProducer);
            p.optionalSupplyId(preResult);
            if ( isOnceModeAndActiveWithSideeffect(p.key()) ) return Optional.empty();
            dialog.accept(preResult); // Calling the preproducer and setting the result in the panel
            dialog.getDialogPane().getScene().setRoot(new BorderPane()); // Remove the DialogPane form the Scene, otherwise an Exception is thrown
            Window window = constructAndShow(SwingCore.wrap(dialog.getDialogPane()), p, Dialog.class); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            dialog.getDialogPane().getButtonTypes().stream().map(t -> dialog.getDialogPane().lookupButton(t)).forEach(b -> { // Add Closing behavior on all buttons.
                ((Button)b).setOnAction(e -> {
                    L.debug("Close on Dialog called");
                    Ui.closeWindowOf(window);
                });
            });
            wait(window);
            return Optional.ofNullable(dialog.getResult());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
