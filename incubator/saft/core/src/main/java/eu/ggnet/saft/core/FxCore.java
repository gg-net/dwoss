package eu.ggnet.saft.core;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import javax.swing.JPanel;

import javafx.embed.swing.SwingNode;
import javafx.stage.Stage;

import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.core.swing.SwingSaft;

/**
 *
 * @author oliver.guenther
 */
public class FxCore {

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
