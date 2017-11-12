package eu.ggnet.saft.core.swing;

import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.all.*;
import eu.ggnet.saft.core.fx.FxSaft;

import java.awt.Window;
import java.io.File;
import java.util.concurrent.*;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.*;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver.guenther
 * @param <T>
 */
public class SwingCreator<T> extends AbstractCreator<T> {

    private final Logger L = LoggerFactory.getLogger(SwingCreator.class);

    private final Window parent;

    private final Modality modality;

    public SwingCreator(Callable<T> before, Window parent, Modality modality) {
        super(before);
        this.parent = parent;
        this.modality = modality;
    }

    public SwingCreator<?> modality(Modality modality) {
        return new SwingCreator<>(before.getCallable(), parent, modality);
    }

    @Override
    public <D> SwingCreator<D> call(Callable<D> callable) {
        return new SwingCreator<>(() -> {
            if (before.ifPresentIsNull()) return null; // Chainbreaker
            UiCore.backgroundActivityProperty().set(true);
            D r = callable.call();
            UiCore.backgroundActivityProperty().set(false);
            return r;
        }, parent, modality);
    }

    @Override
    public <R extends Pane> SwingOk<R> choiceFx(Class<R> paneClazz) {
        return new SwingOk<>(() -> {
            if (before.ifPresentIsNull()) return null; // Chainbreaker
            final T parameter = before.get();
            SwingCore.ensurePlatformIsRunning();
            final R pane = FxSaft.construct(paneClazz, parameter);
            JFXPanel p = SwingCore.wrap(pane);
            return SwingSaft.wrapInChoiceAndShow(parent, p, modality, pane);
        }, parent, modality);
    }

    @Override
    public <R extends FxController> UiOk<R> choiceFxml(Class<R> controllerClass) {
        return new SwingOk<>(() -> {
            if (before.ifPresentIsNull()) return null; // Chainbreaker
            final T parameter = before.get();
            SwingCore.ensurePlatformIsRunning();
            final FXMLLoader loader = FxSaft.constructFxml(controllerClass, parameter);
            JFXPanel p = SwingCore.wrap(loader.getRoot());
            return SwingSaft.wrapInChoiceAndShow(parent, p, modality, loader.getController());
        }, parent, modality);
    }

    @Override
    public <R extends JPanel> SwingOk<R> choiceSwing(Class<R> panelClazz) {
        return new SwingOk<>(() -> {
            if (before.ifPresentIsNull()) return null; // Chainbreaker
            final T parameter = before.get(); // Call outside all ui threads assumed
            final R panel = SwingSaft.construct(panelClazz, parameter);
            return SwingSaft.wrapInChoiceAndShow(parent, panel, modality, panel);
        }, parent, modality);
    }

    @Override
    public <R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> panelClass) {
        return openSwing(panelClass, null);
    }

    @Override
    public <R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> panelClass, String id) {
        return new SwingOpenPanel<>(before.getCallable(), parent, modality, id, panelClass);
    }

    @Override
    public <R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass, String id) {
        return new SwingOpenPane<>(before.getCallable(), parent, modality, id, panelClass);
    }

    @Override
    public <R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass) {
        return openFx(panelClass, null);
    }

    @Override
    public <R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass) {
        return openFxml(controllerClass, null);
    }

    @Override
    public <R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass, String id) {
        return new SwingOpenFxml<>(before.getCallable(), parent, modality, id, controllerClass);
    }

    @Override
    public SwingOk<File> openFileChooser() {
        return openFileChooser(null);
    }

    @Override
    public SwingOk<File> openFileChooser(String title) {
        return new SwingOk<>(() -> {
            SwingCore.ensurePlatformIsRunning();
            File file = FxSaft.dispatch(() -> {
                FileChooser fileChooser = new FileChooser();
                if (title == null) fileChooser.setTitle("Open File");
                else fileChooser.setTitle(title);
                return fileChooser.showOpenDialog(new Stage());
            });
            return new OkCancelResult<>(file, file != null);
        }, parent, modality);
    }

    @Override
    public void exec() {
        Ui.exec(this);
    }

}
