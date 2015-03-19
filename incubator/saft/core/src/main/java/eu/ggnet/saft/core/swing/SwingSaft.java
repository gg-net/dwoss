package eu.ggnet.saft.core.swing;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.*;

import javafx.stage.Modality;

import org.slf4j.LoggerFactory;

import eu.ggnet.saft.api.ui.ClosedListener;
import eu.ggnet.saft.api.ui.Initialiser;
import eu.ggnet.saft.core.all.OkCancelResult;
import eu.ggnet.saft.core.all.UiUtil;

/**
 *
 * @author oliver.guenther
 */
public class SwingSaft {

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

        private final static java.util.List<String> VIEW_SUFFIXES = Arrays.asList("Controller", "View", "ViewCask");

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

    public static <T, R extends JPanel> R construct(Class<R> panelClazz, T parameter) throws Exception {
        return dispatch(() -> {
            R panel = panelClazz.getConstructor().newInstance();
            if ( panel instanceof Initialiser ) {
                ((Initialiser)panel).initialise();
            }
            if ( parameter != null && panel instanceof Consumer ) {
                try {
                    ((Consumer<T>)panel).accept(parameter);
                } catch (ClassCastException e) {
                    LoggerFactory.getLogger(SwingSaft.class).warn(panel.getClass() + " implements Consumer, but not of type " + parameter.getClass());
                }
            }
            return panel;
        });
    }

    public static <T, R, P extends JComponent> OkCancelResult<R> wrapInChoiceAndShow(Window parent, P panel, Modality modality, R payload) throws ExecutionException, InterruptedException, InvocationTargetException {
        return dispatch(() -> {
            OkCancelDialog<P> dialog = new OkCancelDialog<>(parent, panel);
            dialog.setTitle(UiUtil.title(payload.getClass()));
            dialog.setModalityType(UiUtil.toSwing(modality).orElse(ModalityType.APPLICATION_MODAL));
            dialog.pack();
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
            return new OkCancelResult<>(payload, dialog.isOk());
        });
    }

    public static <T> T dispatch(Callable<T> callable) throws ExecutionException, InterruptedException, InvocationTargetException {
        FutureTask<T> task = new FutureTask(callable);
        if ( EventQueue.isDispatchThread() ) task.run();
        else EventQueue.invokeAndWait(task);
        return task.get();
    }

    public static void execute(Runnable runnable) {
        if ( EventQueue.isDispatchThread() ) runnable.run();
        else try {
            EventQueue.invokeAndWait(runnable);
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException(ex.getClass().getSimpleName() + " in execute:" + ex.getLocalizedMessage(), ex);
        }
    }

    public static void enableCloser(Window window, Object uiElement) {
        if ( uiElement instanceof ClosedListener ) {
            window.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosed(WindowEvent e) {
                    ((ClosedListener)uiElement).closed();
                }

            });
        }
    }

    public static java.util.List<Image> loadIcons(Class<?> reference) throws IOException {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return IconConfig.possibleIcons(reference).stream()
                .map(n -> reference.getResource(n))
                .filter(u -> u != null)
                .map(t -> toolkit.getImage(t))
                .collect(Collectors.toList());
    }

}
