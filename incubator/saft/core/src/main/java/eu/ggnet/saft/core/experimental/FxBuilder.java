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
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;

import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.SwingCore;
import eu.ggnet.saft.core.swing.SwingSaft;

import lombok.experimental.Accessors;

/*
    I - 4 Fälle:
    a. nur zeigen. Ui consumiert nix und prodziert kein result
    b. consumer ui of type v
    c. result producer of type r
    d. conumer and result producer of type v,r

    II - 3. Uis
    a. Swing JPanel
    b. JavaFx Pane
    c. JavaFxml + Controller Class


    Examples:
    Ui.fx().parrent().id("blaa").eval(fdsafdsafddsa);

    Ui.swing().show(()->Demo());

 */
/**
 * Handles Fx elements on Saft.
 * This class has no impact how the emelemts are wrapped, only that the elements are based on Swing.
 *
 * @author oliver.guenther
 */
@Accessors(fluent = true)
public class FxBuilder extends AbstractBuilder {

    public FxBuilder() {
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
    public FxBuilder once(boolean once) {
        super.once = once;
        return this;
    }

    /**
     * An optional id. Replaces the id part in a title like: this is a title of {id}
     *
     * @param id the optional id.
     * @return this as fluent usage
     */
    public FxBuilder id(String id) {
        super.id = id;
        return this;
    }

    /**
     * An optional title. If no title is given, the classname is used.
     *
     * @param title the title;
     * @return this as fluent usage
     */
    public FxBuilder title(String title) {
        super.title = title;
        return this;
    }

    /**
     * Enables the Frame mode, makeing the created window a first class element.
     *
     * @param frame if true frame is assumed.
     * @return this as fluent usage
     */
    public FxBuilder frame(boolean frame) {
        super.frame = frame;
        return this;
    }

    /**
     * Optional value for the modality.
     *
     * @param modality the modality to use
     * @return this as fluent usage
     */
    public FxBuilder modality(Modality modality) {
        super.modality = modality;
        return this;
    }

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param swingParent the parent
     * @return this as fluent usage
     */
    public FxBuilder parent(Component swingParent) {
        super.swingParent = SwingCore.windowAncestor(swingParent).orElse(SwingCore.mainFrame());
        return this;
    }

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param javaFxParent the parent
     * @return this as fluent usage
     */
    public FxBuilder parent(Parent javaFxParent) {
        super.swingParent = SwingCore.windowAncestor(javaFxParent).orElse(SwingCore.mainFrame());
        return this;
    }

    /**
     * Creates the javafx Pane via the producer and shows it on the correct thread.
     * <p>
     * Case: Ia.
     *
     * @param <V>                the type
     * @param javafxPaneProducer the producer of the JPanel, must not be null and must not return null.
     */
    public <V extends Pane> void show(Callable<V> javafxPaneProducer) {
        try {
            Objects.requireNonNull(javafxPaneProducer, "The javafxPaneProducer is null, not allowed");
            V pane = javafxPaneProducer.call();
            Params p = buildParameterBackedUpByDefaults(pane.getClass());
            if ( isOnceModeAndActiveWithSideeffect(p.key()) ) return;
            Window window = constructAndShow(SwingCore.wrap(pane), p, pane.getClass()); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            SwingSaft.enableCloser(window, pane);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the javafx Pane via the producer, supplies the consumer part with the result of the preProducer and shows it.
     * <p>
     * Case: Ib
     *
     * @param <P>                result type of the preProducer
     * @param <V>                javafx Pane and Consumer type
     * @param preProducer        the preProducer, must not be null
     * @param javafxPaneProducer the producer of the JPanel, must not be null and must not return null.
     */
    public <P, V extends Pane & Consumer<P>> void show(Callable<P> preProducer, Callable<V> javafxPaneProducer) {
        try {
            Objects.requireNonNull(preProducer, "The pre producer is null, not allowed");
            Objects.requireNonNull(javafxPaneProducer, "The javafxPaneProducer is null, not allowed");
            V pane = SwingSaft.dispatch(javafxPaneProducer);
            Params p = buildParameterBackedUpByDefaults(pane.getClass());
            P preResult = callWithProgress(preProducer);
            p.optionalSupplyId(preResult);
            if ( isOnceModeAndActiveWithSideeffect(p.key()) ) return;
            pane.accept(preResult); // Calling the preproducer and setting the result in the panel
            Window window = constructAndShow(SwingCore.wrap(pane), p, pane.getClass()); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            SwingSaft.enableCloser(window, pane);
        } catch (InterruptedException | InvocationTargetException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the javafx Pane via the producer, shows it and returns the evaluated result as Optional.
     * <p>
     * Case: Ic
     *
     * @param <T>                type of the result
     * @param <V>
     * @param javafxPaneProducer the producer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, V extends Pane & ResultProducer<T>> Optional<T> eval(Callable<V> javafxPaneProducer) {
        try {
            Objects.requireNonNull(javafxPaneProducer, "The javafxPaneProducer is null, not allowed");
            V pane = SwingSaft.dispatch(javafxPaneProducer);  // Creating the panel on the right thread
            Params p = buildParameterBackedUpByDefaults(pane.getClass());
            if ( isOnceModeAndActiveWithSideeffect(p.key()) ) return Optional.empty();
            Window window = constructAndShow(SwingCore.wrap(pane), p, pane.getClass()); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            SwingSaft.enableCloser(window, pane);
            wait(window);
            return Optional.ofNullable(pane.getResult());
        } catch (InterruptedException | InvocationTargetException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates the javafx Pane via the producer, supplies the consumer part with the result of the preProducer, shows it and returns the evaluated result as
     * Optional.
     *
     * @param <T>                type of the result
     * @param <P>                result type of the preProducer
     * @param <V>
     * @param preProducer        the preproducer, must not be null
     * @param javafxPaneProducer the producer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, P, V extends Pane & Consumer<P> & ResultProducer<T>> Optional<T> eval(Callable<P> preProducer, Callable<V> javafxPaneProducer) {
        try {
            Objects.requireNonNull(preProducer, "The pre producer is null, not allowed");
            Objects.requireNonNull(javafxPaneProducer, "The javafxPaneProducer is null, not allowed");
            V pane = SwingSaft.dispatch(javafxPaneProducer); // Creating the panel on the right thread
            Params p = buildParameterBackedUpByDefaults(pane.getClass());
            P preResult = callWithProgress(preProducer);
            p.optionalSupplyId(preResult);
            if ( isOnceModeAndActiveWithSideeffect(p.key()) ) return Optional.empty();
            pane.accept(preResult); // Calling the preproducer and setting the result in the panel
            Window window = constructAndShow(SwingCore.wrap(pane), p, pane.getClass()); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            SwingSaft.enableCloser(window, pane);
            wait(window);
            return Optional.ofNullable(pane.getResult());
        } catch (InterruptedException | InvocationTargetException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

}
