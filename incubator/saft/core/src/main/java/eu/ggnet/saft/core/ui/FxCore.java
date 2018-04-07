package eu.ggnet.saft.core.ui;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JPanel;

import javafx.embed.swing.SwingNode;
import javafx.stage.Stage;

import eu.ggnet.saft.UiCore;

/**
 *
 * @author oliver.guenther
 */
public class FxCore {

    public final static Map<String, WeakReference<Stage>> ACTIVE_STAGES = new ConcurrentHashMap<>();

    public static Stage mainStage() {
        return UiCore.getMainStage();
    }

    public static SwingNode wrap(final JPanel p) throws ExecutionException, InterruptedException, InvocationTargetException {
        return SwingSaft.dispatch(() -> {
            SwingNode swingNode = new SwingNode();
            swingNode.setContent(p);
            return swingNode;
        });
    }

}
