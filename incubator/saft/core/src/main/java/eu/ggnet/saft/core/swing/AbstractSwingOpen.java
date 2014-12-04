package eu.ggnet.saft.core.swing;

import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.all.OnceCaller;
import eu.ggnet.saft.core.all.UiUtil;
import eu.ggnet.saft.api.ui.Frame;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

import javafx.stage.Modality;

import javax.swing.*;

public abstract class AbstractSwingOpen<T, R> implements Callable<Window> {

    protected static class T2<R> {

        private final JComponent panel;

        private final R source;

        public T2(JComponent panel, R source) {
            this.panel = panel;
            this.source = source;
        }

    }

    private final OnceCaller<T> before;
    // Never Null, because in the fallback case
    private final Window parent;

    private final Modality modality;

    private final String id;

    private final Class<R> creatorClass;

    public AbstractSwingOpen(Callable<T> before, Window parent, Modality modality, String id, Class<R> creatorClass) {
        this.before = new OnceCaller<>(before);
        this.parent = parent;
        this.modality = modality;
        this.id = id;
        this.creatorClass = creatorClass;
    }

    @Override
    public Window call() throws Exception {
        String key = creatorClass.getName() + (id == null ? "" : ":" + id);
        // Look into existing Instances and push up to the front if exist.
        if (SwingCore.ACTIVE_WINDOWS.containsKey(key)) {
            Window window = SwingCore.ACTIVE_WINDOWS.get(key).get();
            if (window == null || !window.isVisible()) /* cleanup saftynet */ SwingCore.ACTIVE_WINDOWS.remove(key);
            else {
                if (window instanceof JFrame) ((JFrame) window).setExtendedState(JFrame.NORMAL);
                window.toFront();
                return window;
            }
        }

        // Here it's clear, that our instance does not exist, so we create one.
        if (before.ifPresentIsNull()) return null; // Chainbreaker
        final T parameter = before.get(); // Call outside all ui threads assumed. Parameter null dosn't mean chainbreaker.
        T2<R> t2 = build(parameter, creatorClass);

        Window window = SwingSaft.dispatch(() -> {
            Window w = null;
            if (creatorClass.getAnnotation(Frame.class) != null) {
                // TODO: Reuse Parent and Modality ?
                JFrame frame = new JFrame();
                frame.setTitle(UiUtil.title(creatorClass, id));
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.getContentPane().add(t2.panel);
                w = frame;
            } else {
                JDialog dialog = new JDialog(parent);
                dialog.setModalityType(UiUtil.toSwing(modality).orElse(Dialog.ModalityType.MODELESS));  // This is an "application", default no modaltiy at all
                // Parse the Title somehow usefull.
                dialog.setTitle(UiUtil.title(creatorClass, id));
                dialog.getContentPane().add(t2.panel);
                w = dialog;
            }
            w.setIconImages(SwingSaft.loadIcons(creatorClass));
            w.pack();
            w.setLocationRelativeTo(parent);
            w.setVisible(true);
            return w;
        });
        SwingSaft.enableCloser(window, t2.source);
        SwingCore.ACTIVE_WINDOWS.put(key, new WeakReference<>(window));

        // Removes on close.
        window.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                // Clean us up.
                SwingCore.ACTIVE_WINDOWS.remove(key);
            }

        });

        return window;
    }

    protected abstract T2<R> build(T parameter, Class<R> clazz) throws Exception;

    public void exec() {
        Ui.exec(this);
    }

}
