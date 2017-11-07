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

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;

import javax.swing.*;

import javafx.stage.Modality;

import eu.ggnet.saft.api.ui.Frame;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.SwingCore;
import eu.ggnet.saft.core.UserPreferences;
import eu.ggnet.saft.core.all.UiUtil;
import eu.ggnet.saft.core.swing.SwingSaft;

import lombok.Setter;
import lombok.experimental.Accessors;

import static eu.ggnet.saft.core.Client.lookup;


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
 * Handles Swing elements on Saft.
 * This class has no impact how the emelemts are wrapped, only that the elements are based on Swing.
 *
 * @author oliver.guenther
 */
@Accessors(fluent = true)
public class Swing {

    // maybe a panel could also happen
    /**
     * Represents the parent of the ui element, optional.
     */
    private Window swingParent = null;

    /**
     * Sets the once mode.
     * If set to true, an once mode is enable. This ensures that one one window of the same type is created and show.
     * If minimised it becomes reopend, if in the back it becomes moved to the front.
     */
    @Setter
    private boolean once = false;

    /**
     * An optional id. Replaces the id part in a title like: this is a title of {id}
     */
    @Setter
    private String id = null;

    /**
     * An optional title. If no title is given, the classname is used.
     */
    @Setter
    private String title = null;

    /**
     * Enables the Frame mode, makeing the created window a first class element.
     */
    @Setter
    private boolean frame = false;

    /**
     * Optional value for the modality.
     */
    @Setter
    private Modality modality = null;

    /**
     * Represents the parent of the ui element, optional.
     *
     * @param swingParent the parent
     * @return this as fluent usage
     */
    public Swing parent(Window swingParent) {
        this.swingParent = swingParent;
        return this;
    }

    /**
     * Creates the JPanel via the producer and shows it on the correct thread.
     * <p>
     * Case: Ia.
     *
     * @param <V>                the type
     * @param swingPanelProducer the swingPanelProducer of the JPanel, must not be null and must not return null.
     */
    public <V extends JPanel> void show(Callable<V> swingPanelProducer) {
        try {
            Objects.requireNonNull(swingPanelProducer, "The swingPanelProducer is null, not allowed");
            V panel = SwingSaft.dispatch(swingPanelProducer);
            Params p = buildParameterBackedUpByDefaults(panel.getClass());
            if ( isOnceModeAndActiveWithSideeffect(p.key) ) return;
            constructAndShow(panel, p); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
        } catch (ExecutionException | InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the JPanel via the producer, supplies the consumer part with the result of the preProducer and shows it.
     * <p>
     * Case: Ib
     *
     * @param <P>                result type of the preProducer
     * @param <V>
     * @param preProducer        the preproducer, must not be null
     * @param swingPanelProducer the swingPanelProducer, must not be null and must not return null.
     */
    public <P, V extends JPanel & Consumer<P>> void show(Callable<P> preProducer, Callable<V> swingPanelProducer) {
        try {
            Objects.requireNonNull(preProducer, "The pre producer is null, not allowed");
            Objects.requireNonNull(swingPanelProducer, "The swingPanelProducer is null, not allowed");
            V panel = SwingSaft.dispatch(swingPanelProducer);
            Params p = buildParameterBackedUpByDefaults(panel.getClass());
            if ( isOnceModeAndActiveWithSideeffect(p.key) ) return;
            panel.accept(preProducer.call()); // Calling the preproducer and setting the result in the panel
            constructAndShow(panel, p); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the JPanel via the producer, shows it and returns the evaluated result as Optional.
     * <p>
     * Case: Ic
     *
     * @param <T>                type of the result
     * @param <V>
     * @param swingPanelProducer the swingPanelProducer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, V extends JPanel & ResultProducer<T>> Optional<T> eval(Callable<V> swingPanelProducer) {
        try {
            Objects.requireNonNull(swingPanelProducer, "The swingPanelProducer is null, not allowed");
            V panel = SwingSaft.dispatch(swingPanelProducer);  // Creating the panel on the right thread
            Params p = buildParameterBackedUpByDefaults(panel.getClass());
            if ( isOnceModeAndActiveWithSideeffect(p.key) ) return Optional.empty();
            Window window = constructAndShow(panel, p); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            return waitAndGetResult(window, panel);
        } catch (InterruptedException | InvocationTargetException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates the JPanel via the producer, supplies the consumer part with the result of the preProducer, shows it and returns the evaluated result as
     * Optional.
     *
     * @param <T>                type of the result
     * @param <P>                result type of the preProducer
     * @param <V>
     * @param preProducer        the preproducer, must not be null
     * @param swingPanelProducer the swingPanelProducer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, P, V extends JPanel & Consumer<P> & ResultProducer<T>> Optional<T> eval(Callable<P> preProducer, Callable<V> swingPanelProducer) {
        try {
            Objects.requireNonNull(preProducer, "The pre producer is null, not allowed");
            Objects.requireNonNull(swingPanelProducer, "The swingPanelProducer is null, not allowed");
            V panel = SwingSaft.dispatch(swingPanelProducer); // Creating the panel on the right thread
            Params p = buildParameterBackedUpByDefaults(panel.getClass());
            if ( isOnceModeAndActiveWithSideeffect(p.key) ) return Optional.empty();
            panel.accept(preProducer.call()); // Calling the preproducer and setting the result in the panel
            Window window = constructAndShow(panel, p); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            return waitAndGetResult(window, panel);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <T> Optional<T> waitAndGetResult(Window window, ResultProducer<T> panel) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        // Removes on close.
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                latch.countDown();
            }

        });

        latch.await(); //TODO: What happens if we were called on the EventQueue ???
        return Optional.ofNullable(panel.getResult());
    }

    /**
     * If we are in once mode, an active window with the supplied key is brought to the front;
     *
     * @param key the key of the window in the internal registry.
     * @return true, if an active window was found.
     */
    private boolean isOnceModeAndActiveWithSideeffect(String key) {
        // Look into existing Instances, if in once mode and push up to the front if exist.
        if ( once && SwingCore.ACTIVE_WINDOWS.containsKey(key) ) {
            Window window = SwingCore.ACTIVE_WINDOWS.get(key).get();
            if ( window == null || !window.isVisible() ) /* cleanup saftynet */ SwingCore.ACTIVE_WINDOWS.remove(key);
            else {
                if ( window instanceof JFrame ) ((JFrame)window).setExtendedState(JFrame.NORMAL);
                window.toFront();
                return true;
            }
        }
        return false;
    }

    private Window constructAndShow(JPanel panel, Params p) throws ExecutionException, InterruptedException, InvocationTargetException {
        Window window = SwingSaft.dispatch(() -> {
            Window w = null;
            if ( p.framed ) {
                // TODO: Reuse Parent and Modality ?
                JFrame jframe = new JFrame();
                jframe.setTitle(p.title);
                jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                jframe.getContentPane().add(panel);
                w = jframe;
            } else {
                JDialog dialog = new JDialog(swingParent);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setModalityType(p.modalityType);
                // Parse the Title somehow usefull.
                dialog.setTitle(p.title);
                dialog.getContentPane().add(panel);
                w = dialog;
            }
            w.setIconImages(SwingSaft.loadIcons(p.panelClazz));
            w.pack();
            w.setLocationRelativeTo(swingParent);
            lookup(UserPreferences.class).loadLocation(p.panelClazz, p.id, w);
            w.setVisible(true);
            return w;
        });
        SwingSaft.enableCloser(window, panel);
        SwingCore.ACTIVE_WINDOWS.put(p.key, new WeakReference<>(window));

        // Removes on close.
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                // Clean us up.
                SwingCore.ACTIVE_WINDOWS.remove(p.key);
                // Store location.
                lookup(UserPreferences.class).storeLocation(p.panelClazz, p.id, window);

            }

        });
        return window;
    }

    private Params buildParameterBackedUpByDefaults(Class<?> panelClazz) {
        return new Params(
                panelClazz,
                Swing.this.id,
                (Swing.this.title == null ? UiUtil.title(panelClazz, id) : Swing.this.title),
                (frame ? panelClazz.getAnnotation(Frame.class) != null : frame),
                UiUtil.toSwing(modality).orElse(Dialog.ModalityType.MODELESS),
                panelClazz.getName() + (id == null ? "" : ":" + id)
        );

    }

    // Internal Parameter class
    private class Params {

        private final Class<?> panelClazz;

        private final String id;

        private final String title;

        private final boolean framed;

        private final Dialog.ModalityType modalityType;

        private final String key;

        public Params(Class<?> panelClazz, String id, String title, boolean framed, ModalityType modalityType, String key) {
            this.panelClazz = panelClazz;
            this.id = id;
            this.title = title;
            this.framed = framed;
            this.modalityType = modalityType;
            this.key = key;
        }

    }

}
