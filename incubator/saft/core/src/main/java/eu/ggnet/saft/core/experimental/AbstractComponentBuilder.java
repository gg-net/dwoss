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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import javafx.stage.Modality;

import eu.ggnet.saft.api.ui.Frame;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.all.UiUtil;
import eu.ggnet.saft.core.swing.SwingSaft;

import lombok.experimental.Accessors;

/**
 * Contains shared elements of all Ui builders.
 *
 * @author oliver.guenther
 */
@Accessors(fluent = true)
public abstract class AbstractComponentBuilder {

    // Internal Parameter class
    protected class Params {

        protected final Class<?> panelClazz;

        protected final String id;

        protected final String title;

        protected final boolean framed;

        protected final Dialog.ModalityType modalityType;

        protected final String key;

        protected Params(Class<?> panelClazz, String id, String title, boolean framed, ModalityType modalityType, String key) {
            this.panelClazz = panelClazz;
            this.id = id;
            this.title = title;
            this.framed = framed;
            this.modalityType = modalityType;
            this.key = key;
        }

    }

    // maybe a panel could also happen
    /**
     * Represents the parent of the ui element, optional.
     */
    protected Window swingParent = null;

    /**
     * Sets the once mode.
     * If set to true, an once mode is enable. This ensures that one one window of the same type is created and show.
     * If minimised it becomes reopend, if in the back it becomes moved to the front.
     */
    protected boolean once = false;

    /**
     * An optional id. Replaces the id part in a title like: this is a title of {id}
     */
    protected String id = null;

    /**
     * An optional title. If no title is given, the classname is used.
     */
    protected String title = null;

    /**
     * Enables the Frame mode, makeing the created window a first class element.
     */
    protected boolean frame = false;

    /**
     * Optional value for the modality.
     */
    protected Modality modality = null;

    /**
     * If we are in once mode, an active window with the supplied key is brought to the front;
     *
     * @param key the key of the window in the internal registry.
     * @return true, if an active window was found.
     */
    protected boolean isOnceModeAndActiveWithSideeffect(String key) {
        // Look into existing Instances, if in once mode and push up to the front if exist.
        if ( once && SwingCore.ACTIVE_WINDOWS.containsKey(key) ) {
            Window window = SwingCore.ACTIVE_WINDOWS.get(key).get();
            if ( window == null || !window.isVisible() ) SwingCore.ACTIVE_WINDOWS.remove(key);
            else {
                if ( window instanceof JFrame ) ((JFrame)window).setExtendedState(JFrame.NORMAL);
                window.toFront();
                return true;
            }
        }
        return false;
    }

    protected Window constructAndShow(JComponent panel, Params p) throws ExecutionException, InterruptedException, InvocationTargetException {
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
            Client.lookup(UserPreferences.class).loadLocation(p.panelClazz, p.id, w);
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
                Client.lookup(UserPreferences.class).storeLocation(p.panelClazz, p.id, window);
            }
        });
        return window;
    }

    protected Params buildParameterBackedUpByDefaults(Class<?> panelClazz) {
        return new Params(
                panelClazz,
                AbstractComponentBuilder.this.id,
                (AbstractComponentBuilder.this.title == null ? UiUtil.title(panelClazz, id) : AbstractComponentBuilder.this.title),
                (frame ? panelClazz.getAnnotation(Frame.class) != null : frame),
                UiUtil.toSwing(modality).orElse(Dialog.ModalityType.MODELESS),
                panelClazz.getName() + (id == null ? "" : ":" + id)
        );

    }

    protected static void wait(Window window) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);

        // Removes on close.
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                latch.countDown();
            }

        });
        latch.await(); //TODO: What happens if we were called on the EventQueue ???
    }

}
