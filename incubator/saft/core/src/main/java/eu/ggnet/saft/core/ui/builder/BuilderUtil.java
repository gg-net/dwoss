/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.saft.core.ui.builder;

import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.*;

import javax.swing.*;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.Dl;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.*;
import eu.ggnet.saft.core.ui.builder.UiWorkflowBreak.Type;

import static eu.ggnet.saft.core.ui.FxSaft.loadView;

/**
 * Util class for all Builder based work.
 *
 * @author oliver.guenther
 */
public final class BuilderUtil {

    private final static Logger L = LoggerFactory.getLogger(BuilderUtil.class);

    private BuilderUtil() {
        // No instances of util classes.
    }

    /**
     * New jframe based on parameters.
     *
     * @param title
     * @param component
     * @return
     */
    static JFrame newJFrame(String title, JComponent component) {
        JFrame jframe = new JFrame();
        jframe.setTitle(title);
        jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jframe.getContentPane().add(component);
        return jframe;
    }

    /**
     * New JDialog based on parameters.
     *
     * @param swingParent
     * @param title
     * @param component
     * @param modalityType
     * @return
     */
    static JDialog newJDailog(Window swingParent, String title, JComponent component, ModalityType modalityType) {
        JDialog dialog = new JDialog(swingParent);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setModalityType(modalityType);
        // Parse the Title somehow usefull.
        dialog.setTitle(title);
        dialog.getContentPane().add(component);
        return dialog;
    }

    /**
     * Sets default values.
     *
     * @param <T>
     * @param window                the window to modify
     * @param iconReferenzClass     reference class for icons. see SwingSaft.loadIcons.
     * @param relativeLocationAnker anker for relative location placement, propably also the parent.
     * @param storeLocationClass    class inspected if it has the StoreLocation annotation, probally the panel, pane or controller class.
     * @param windowKey             a string representtation for the internal window manager. Something like controller.getClass + optional id.
     * @return the window instance.
     * @throws IOException If icons could not be loaded.
     */
    static <T extends Window> T setWindowProperties(T window, Class<?> iconReferenzClass, Window relativeLocationAnker, Class<?> storeLocationClass, String windowKey) throws IOException { // IO Exeception based on loadIcons
        window.setIconImages(SwingSaft.loadIcons(iconReferenzClass));
        window.pack();
        window.setLocationRelativeTo(relativeLocationAnker);
        if ( isStoreLocation(storeLocationClass) ) Dl.local().lookup(UserPreferences.class).loadLocation(storeLocationClass, window);
        SwingCore.ACTIVE_WINDOWS.put(windowKey, new WeakReference<>(window));
        // Removes on close.
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Clean us up.
                SwingCore.ACTIVE_WINDOWS.remove(windowKey);
                // Store location.
                if ( isStoreLocation(storeLocationClass) ) Dl.local().lookup(UserPreferences.class).storeLocation(storeLocationClass, window);
            }
        });
        return window;
    }

    static void enableCloser(Window window, Object uiElement) {
        if ( uiElement instanceof ClosedListener ) {
            window.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent e) {
                    ((ClosedListener)uiElement).closed();
                }

            });
        }
    }

    static void wait(Window window) throws InterruptedException, IllegalStateException, NullPointerException {
        Objects.requireNonNull(window, "Window is null");
        if ( !window.isVisible() ) {
            L.info("Wait on non visible window called, continue without latch");
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        // Removes on close.
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                latch.countDown();
            }

        });
        latch.await();
    }

    static <V extends Pane> UiParameter producePane(Callable<V> producer, UiParameter parm) {
        try {
            V pane = producer.call();
            return parm.withRootClass(pane.getClass()).withPane(pane);
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }

    static UiParameter breakIfOnceAndActive(UiParameter in) {
        // Look into existing Instances, if in once mode and push up to the front if exist.
        String key = in.toKey();
        if ( in.isOnce() && SwingCore.ACTIVE_WINDOWS.containsKey(key) ) {
            Window window = SwingCore.ACTIVE_WINDOWS.get(key).get();
            if ( window == null || !window.isVisible() ) SwingCore.ACTIVE_WINDOWS.remove(key);
            else {
                if ( window instanceof JFrame ) ((JFrame)window).setExtendedState(JFrame.NORMAL);
                window.toFront();
                throw new UiWorkflowBreak(Type.ONCE);
            }
        }
        return in;
    }

    static UiParameter consumePreResult(UiParameter in) {
        return in.optionalConsumePreResult();
    }

    static UiParameter wrapPane(UiParameter in) {
        return in.withJfxPanel(SwingCore.wrapDirect(in.getPane()));
    }

    static UiParameter constructFxml(UiParameter in) {
        try {
            Class<FxController> controllerClazz = (Class<FxController>)in.getRootClass();  // Cast is a shortcut.
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(loadView(controllerClazz), "No View for " + controllerClazz));
            loader.load();
            Objects.requireNonNull(loader.getController(), "No controller based on " + controllerClazz + ". Controller set in Fxml ?");
            return in.withPane(loader.getRoot()).withController(loader.getController());
        } catch (IOException ex) {
            throw new CompletionException(ex);
        }
    }

    static UiParameter constructSwing(UiParameter in) {
        try {
            Pane pane = in.getPane();
            JComponent component = in.getJfxPanel();
            final Window window = in.isFramed()
                    ? BuilderUtil.newJFrame(in.toTitle(), component)
                    : BuilderUtil.newJDailog(in.getUiParent().getSwingParent(), in.toTitle(), component, in.toSwingModality());
            // Todo: the Icon Referenz Class is not ava
            BuilderUtil.setWindowProperties(window, in.getRefernceClass(), in.getUiParent().getSwingParent(), in.getRefernceClass(), in.toKey());
            in.getClosedListenerImplemetation().ifPresent(ui -> BuilderUtil.enableCloser(window, ui));
            window.setVisible(true);
            return in.withWindow(window);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    static <T> T waitAndProduceResult(UiParameter in) {
        if ( !(in.getType().selectRelevantInstance(in) instanceof ResultProducer) ) {
            throw new IllegalStateException("Calling Produce Result on a none ResultProducer. Try show instead of eval");
        }
        try {
            BuilderUtil.wait(in.getWindow());
        } catch (InterruptedException | IllegalStateException | NullPointerException ex) {
            throw new CompletionException(ex);
        }
        T result = ((ResultProducer<T>)in.getType().selectRelevantInstance(in)).getResult();
        if ( result == null ) throw new UiWorkflowBreak(Type.NULL_RESULT);
        return result;
    }

    private static boolean isStoreLocation(Class<?> key) {
        return (key.getAnnotation(StoreLocation.class) != null);
    }

}
