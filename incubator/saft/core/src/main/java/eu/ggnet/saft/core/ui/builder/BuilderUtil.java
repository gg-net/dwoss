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
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import javax.swing.*;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.*;
import eu.ggnet.saft.api.ui.*;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.ui.UserPreferences;
import eu.ggnet.saft.core.ui.builder.UiWorkflowBreak.Type;

import static eu.ggnet.saft.core.ui.FxSaft.loadView;

/**
 * Util class for all Builder based work.
 *
 * @author oliver.guenther
 */
public final class BuilderUtil {

    /**
     * A simple wrapper for the name generation to discover icons.
     * The name is build like this:
     * <ol>
     * <li>If the referencing class ends with one of {@link IconConfig#VIEW_SUFFIXES} remove that part</li>
     * <li>Generate name by permuting "Rest of referencing class
     * name"{@link IconConfig#ICON_SUFFIXES}{@link IconConfig#SIZE_SUFFIXES}{@link IconConfig#FILES}</li>
     * <li></li>
     * <li></li>
     * </ol>
     */
    private final static class IconConfig {

        private final static java.util.List<String> VIEW_SUFFIXES = Arrays.asList("Controller", "View", "ViewCask", "Presenter");

        private final static java.util.List<String> ICON_SUFFIXES = Arrays.asList("Icon");

        private final static java.util.List<String> SIZE_SUFFIXES = Arrays.asList("", "_016", "_024", "_032", "_048", "_064", "_128", "_256", "_512");

        private final static java.util.List<String> FILES = Arrays.asList(".png", ".jpg", ".gif");

        private static Set<String> possibleIcons(Class<?> clazz) {
            String head = VIEW_SUFFIXES
                    .stream()
                    .filter(s -> clazz.getSimpleName().endsWith(s))
                    .map(s -> clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - s.length()))
                    .findFirst()
                    .orElse(clazz.getSimpleName());

            return ICON_SUFFIXES.stream()
                    .map(e -> head + e)
                    .flatMap(h -> SIZE_SUFFIXES.stream().map(e -> h + e))
                    .flatMap(h -> FILES.stream().map(e -> h + e))
                    .collect(Collectors.toCollection(() -> new TreeSet<String>()));
        }

    }

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
        window.setIconImages(loadAwtImages(iconReferenzClass));
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

    static void wait(Window window) throws InterruptedException, IllegalStateException, NullPointerException {
        Objects.requireNonNull(window, "Window is null");
        if ( !window.isVisible() ) {
            L.debug("Wait on non visible window called, continue without latch");
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

    static <V extends JPanel> UiParameter produceJPanel(Callable<V> producer, UiParameter parm) {
        try {
            V panel = producer.call();
            L.debug("produceJPanel: {}", panel);
            return parm.withRootClass(panel.getClass()).withJPanel(panel);
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }

    static <V extends Pane> UiParameter producePane(Callable<V> producer, UiParameter parm) {
        try {
            V pane = producer.call();
            L.debug("producePane: {}", pane);
            return parm.withRootClass(pane.getClass()).withPane(pane);
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }

    static <T, V extends Dialog<T>> UiParameter produceDialog(Callable<V> producer, UiParameter parm) {
        try {
            V dialog = producer.call();
            L.debug("produceDialog: {}", dialog);
            return parm.withRootClass(dialog.getClass()).withDialog(dialog).withPane(dialog.getDialogPane());
        } catch (Exception ex) {
            throw new CompletionException(ex);
        }
    }

    /**
     * Modifies the Dialog to be used in a swingOrMain environment.
     *
     * @param in
     * @return
     */
    static UiParameter modifyDialog(UiParameter in) {
        Dialog dialog = in.getDialog();
        dialog.getDialogPane().getScene().setRoot(new BorderPane()); // Remove the DialogPane form the Scene, otherwise an Exception is thrown
        dialog.getDialogPane().getButtonTypes().stream().map(t -> dialog.getDialogPane().lookupButton(t)).forEach(b -> { // Add Closing behavior on all buttons.
            ((Button)b).setOnAction(e -> {
                L.debug("Close on Dialog called");
                Ui.closeWindowOf(dialog.getDialogPane());
            });
        });
        return in;
    }

    static UiParameter breakIfOnceAndActive(UiParameter in) {
        // TODO: Implement the JavaFx way.
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

    // Call only from Swing EventQueue
    static UiParameter createJFXPanel(UiParameter in) {
        return in.withJPanel(new JFXPanel());
    }

    /**
     * Plafrom.runlater() : Wraps a pane into a jfxpanel, which must have been set on the in.getPanel.
     *
     * @param in
     * @return modified in.
     */
    static UiParameter wrapPane(UiParameter in) {
        if ( !(in.getJPanel() instanceof JFXPanel) ) throw new IllegalArgumentException("JPanel not instance of JFXPanel : " + in);
        JFXPanel fxp = (JFXPanel)in.getJPanel();
        fxp.setScene(new Scene(in.getPane(), Color.TRANSPARENT));
        SwingCore.mapParent(fxp);
        return in;
    }

    static UiParameter produceFxml(UiParameter in) {
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

    static UiParameter constructJavaFx(UiParameter in) {
        L.warn("constructJavaFx is not yet complete, but should start to work");

        Pane pane = in.getPane();

        Stage stage = new Stage();
        if ( !in.isFramed() ) stage.initOwner(in.getUiParent().fxOrMain());
        if ( in.getModality() != null ) stage.initModality(in.getModality());
        stage.setTitle(in.toTitle());
        stage.getIcons().addAll(loadJavaFxImages(in.getRefernceClass()));

//            BuilderUtil.setWindowProperties(window, in.getRefernceClass(), in.getUiParent().swingOrMain(), in.getRefernceClass(), in.toKey());
// Das fehlt noch
        in.getClosedListenerImplemetation().ifPresent(elem -> stage.setOnCloseRequest(e -> elem.closed()));
        stage.setScene(new Scene(pane));
        stage.showAndWait();
        return in;
    }

    static UiParameter constructDialog(UiParameter in) {
        L.warn("constructJavaFx is not yet complete, but should start to work");

        Dialog<?> dialog = in.getDialog();

        if ( !in.isFramed() ) dialog.initOwner(in.getUiParent().fxOrMain());
        if ( in.getModality() != null ) dialog.initModality(in.getModality());
        dialog.setTitle(in.toTitle());
//        stage.getIcons().addAll(loadJavaFxImages(in.getRefernceClass())); Not in dialog avialable.

//            BuilderUtil.setWindowProperties(window, in.getRefernceClass(), in.getUiParent().swingOrMain(), in.getRefernceClass(), in.toKey());
// Das fehlt noch
        in.getClosedListenerImplemetation().ifPresent(elem -> dialog.setOnCloseRequest(e -> elem.closed()));
        dialog.showAndWait();
        return in;
    }

    static UiParameter constructSwing(UiParameter in) {
        try {
            L.debug("constructSwing");
            JComponent component = in.getJPanel(); // Must be set at this point.
            final Window window = in.isFramed()
                    ? BuilderUtil.newJFrame(in.toTitle(), component)
                    : BuilderUtil.newJDailog(in.getUiParent().swingOrMain(), in.toTitle(), component, in.toSwingModality());
            BuilderUtil.setWindowProperties(window, in.getRefernceClass(), in.getUiParent().swingOrMain(), in.getRefernceClass(), in.toKey());
            in.getClosedListenerImplemetation().ifPresent(elem -> window.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent e) {
                    elem.closed();
                }

            }));
            window.setVisible(true);
            L.debug("constructSwing.setVisible(true)");
            return in.withWindow(window);
        } catch (IOException e) {
            throw new CompletionException(e);
        }
    }

    static <T> T waitAndProduceResult(UiParameter in) {
        if ( !(in.getType().selectRelevantInstance(in) instanceof ResultProducer || in.getType().selectRelevantInstance(in) instanceof Dialog) ) {
            throw new IllegalStateException("Calling Produce Result on a none ResultProducer. Try show instead of eval");
        }
        try {
            if ( UiCore.isSwing() ) BuilderUtil.wait(in.getWindow()); // Only needed in Swing mode. In JavaFx the showAndWait() is allways used.
        } catch (InterruptedException | IllegalStateException | NullPointerException ex) {
            throw new CompletionException(ex);
        }
        if ( in.getType().selectRelevantInstance(in) instanceof ResultProducer ) {
            T result = ((ResultProducer<T>)in.getType().selectRelevantInstance(in)).getResult();
            if ( result == null ) throw new UiWorkflowBreak(Type.NULL_RESULT);
            return result;
        } else {
            T result = ((Dialog<T>)in.getType().selectRelevantInstance(in)).getResult();
            if ( result == null ) throw new UiWorkflowBreak(Type.NULL_RESULT);
            return result;
        }

    }

    private static boolean isStoreLocation(Class<?> key) {
        return (key.getAnnotation(StoreLocation.class) != null);
    }

    private static java.util.List<java.awt.Image> loadAwtImages(Class<?> reference) throws IOException {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return IconConfig.possibleIcons(reference).stream()
                .map(n -> reference.getResource(n))
                .filter(u -> u != null)
                .map(t -> toolkit.getImage(t))
                .collect(Collectors.toList());
    }

    private static java.util.List<Image> loadJavaFxImages(Class<?> reference) {
        return IconConfig.possibleIcons(reference).stream()
                .map(n -> reference.getResourceAsStream(n))
                .filter(u -> u != null)
                .map(r -> new Image(r))
                .collect(Collectors.toList());
    }
}
