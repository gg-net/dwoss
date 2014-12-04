package eu.ggnet.saft.core.swing;

import javafx.stage.Modality;
import javax.swing.*;

import java.awt.Window;
import java.util.concurrent.Callable;

/**
 *
 * @author oliver.guenther
 * @param <T>
 * @param <R>
 */
public class SwingOpenPanel<T, R extends JPanel> extends AbstractSwingOpen<T, R> {

    public SwingOpenPanel(Callable<T> before, Window parent, Modality modality, String id, Class<R> panelClass) {
        super(before, parent, modality, id, panelClass);
    }

    @Override
    protected T2<R> build(T parameter, Class<R> panelClass) throws Exception {
        R panel = SwingSaft.construct(panelClass, parameter);
        return new T2(panel, panel);
    }

}
