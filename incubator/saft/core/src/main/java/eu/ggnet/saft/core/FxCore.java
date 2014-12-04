package eu.ggnet.saft.core;

import eu.ggnet.saft.core.swing.SwingSaft;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import javafx.embed.swing.SwingNode;
import javafx.stage.Stage;
import javax.swing.JPanel;

/**
 *
 * @author oliver.guenther
 */
public class FxCore {

    static Stage mainStage = null;

    public static Stage mainStage() {
        return mainStage;
    }

    public static SwingNode wrap(final JPanel p) throws ExecutionException, InterruptedException, InvocationTargetException {
        return SwingSaft.dispatch(() -> {
            SwingNode swingNode = new SwingNode();
            swingNode.setContent(p);
            return swingNode;
        });
    }

}
