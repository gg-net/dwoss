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
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.*;

import javax.swing.*;

import javafx.stage.Modality;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiUtil;
import eu.ggnet.saft.api.ui.Frame;
import eu.ggnet.saft.api.ui.IdSupplier;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.swing.SwingSaft;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * Contains shared elements of all Ui builders.
 *
 * @author oliver.guenther
 */
@Getter
@ToString
public abstract class AbstractBuilder {

    /**
     * Calls the callable in the same thread, while sending progress information into the ui.
     *
     * @param <A>
     * @param callable
     * @return
     */
    protected static <A> A callWithProgress(Callable<A> callable) {
        return Ui.progress().call(callable);
    }

    // Internal Parameter class
    @Builder
    @Accessors(fluent = true)
    protected static class Params {

        protected final Class<?> panelClazz;

        @Getter
        @Setter
        protected String id = null;

        protected final String titleTemplate;

        protected final boolean framed;

        protected final Dialog.ModalityType modalityType;

        protected void optionalSupplyId(Object possibleIdSupplier) {
            if ( id == null && possibleIdSupplier instanceof IdSupplier ) id = ((IdSupplier)possibleIdSupplier).id();
        }

        protected String title() {
            return titleTemplate == null ? UiUtil.title(panelClazz, id) : titleTemplate;
        }

        protected String key() {
            return panelClazz.getName() + (id == null ? "" : ":" + id);
        }
    ;

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
     * Default = true.
     */
    protected boolean once = true;

    /**
     * An optional id. Replaces the id part in a title like: this is a title of {id}
     * Default = null.
     */
    protected String id = null;

    /**
     * An optional title. If no title is given, the classname is used.
     * Default = null
     */
    protected String title = null;

    /**
     * Enables the Frame mode, makeing the created window a first class element.
     * Default = false
     */
    protected boolean frame = false;

    /**
     * Optional value for the modality.
     * Default = null
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

    protected Window constructAndShow(JComponent component, Params params) throws ExecutionException, InterruptedException, InvocationTargetException {
        Window window = SwingSaft.dispatch(() -> {
            Window w = null;
            if ( params.framed ) {
                // TODO: Reuse Parent and Modality ?
                JFrame jframe = new JFrame();
                jframe.setTitle(params.title());
                jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                jframe.getContentPane().add(component);
                w = jframe;
            } else {
                JDialog dialog = new JDialog(swingParent);
                dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                dialog.setModalityType(params.modalityType);
                // Parse the Title somehow usefull.
                dialog.setTitle(params.title());
                dialog.getContentPane().add(component);
                w = dialog;
            }
            w.setIconImages(SwingSaft.loadIcons(params.panelClazz));
            w.pack();
            w.setLocationRelativeTo(swingParent);
            Client.lookup(UserPreferences.class).loadLocation(params.panelClazz, params.id, w);
            w.setVisible(true);
            return w;
        });
        SwingCore.ACTIVE_WINDOWS.put(params.key(), new WeakReference<>(window));
        // Removes on close.
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // Clean us up.
                SwingCore.ACTIVE_WINDOWS.remove(params.key());
                // Store location.
                Client.lookup(UserPreferences.class).storeLocation(params.panelClazz, params.id, window);
            }
        });
        return window;
    }

    protected Params buildParameterBackedUpByDefaults(Class<?> panelClazz) {
        return Params.builder()
                .panelClazz(panelClazz)
                .id(AbstractBuilder.this.id)
                .titleTemplate(AbstractBuilder.this.title)
                .framed(!frame ? panelClazz.getAnnotation(Frame.class) != null : frame)
                .modalityType(UiUtil.toSwing(modality).orElse(Dialog.ModalityType.MODELESS)).build();
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