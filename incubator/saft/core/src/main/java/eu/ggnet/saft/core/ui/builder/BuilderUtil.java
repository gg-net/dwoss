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
import java.util.concurrent.CountDownLatch;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.Dl;
import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.api.ui.StoreLocation;
import eu.ggnet.saft.core.ui.*;

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


    private static boolean isStoreLocation(Class<?> key) {
        return (key.getAnnotation(StoreLocation.class) != null);
    }

}
