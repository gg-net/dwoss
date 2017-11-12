package eu.ggnet.saft.core.fx;

import eu.ggnet.saft.UiUtil;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.Ui;
import eu.ggnet.saft.core.*;
import eu.ggnet.saft.core.all.*;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.core.swing.*;

import java.io.File;
import java.util.concurrent.*;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.*;

import static javafx.stage.Modality.APPLICATION_MODAL;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver.guenther
 * @param <T>
 */
public class FxCreator<T> extends AbstractCreator<T> {

    private final Window parent;

    private final Modality modality;

    private final static Logger L = LoggerFactory.getLogger(FxCreator.class);

    public FxCreator(Callable<T> before, Window parent, Modality modality) {
        super(before);
        this.parent = parent;
        this.modality = modality;
    }

    public FxCreator<?> modality(Modality modality) {
        return new FxCreator<>(before.getCallable(), parent, modality);
    }

    @Override
    public <D> FxCreator<D> call(Callable<D> callable) {
        return new FxCreator<>(() -> {
            if (before.ifPresentIsNull()) return null; // Chainbreaker
            UiCore.backgroundActivityProperty().set(true);
            D r = callable.call();
            UiCore.backgroundActivityProperty().set(false);
            return r;
        }, parent, modality);
    }

    @Override
    public <R extends FxController> UiOk<R> choiceFxml(Class<R> controllerClass) {
        return new FxOk<>(() -> {
            if (before.ifPresentIsNull()) return null; // Chainbreaker
            final T parameter = before.get();
            final FXMLLoader loader = FxSaft.constructFxml(controllerClass, parameter);
            return wrapAndShow(parent, loader.getRoot(), modality, loader.getController());
        }, parent, modality);
    }

    @Override
    public <R extends Pane> UiOk<R> choiceFx(Class<R> paneClazz) {
        return new FxOk<>(() -> {
            if (before.ifPresentIsNull()) return null; // Chainbreaker
            final T parameter = before.get();
            final R pane = FxSaft.construct(paneClazz, parameter);
            return wrapAndShow(parent, pane, modality, pane);
        }, parent, modality);
    }

    @Override
    public <R extends JPanel> FxOk<R> choiceSwing(Class<R> panelClazz) {
        return new FxOk<>(() -> {
            if (before.ifPresentIsNull()) return null; // Chainbreaker
            final T parameter = before.get();
            final R panel = SwingSaft.construct(panelClazz, parameter);
            final SwingNode node = FxCore.wrap(panel);
            return wrapAndShow(parent, node, modality, panel);
        }, parent, modality);
    }

    private static <T, R, P extends Node> OkCancelResult<R> wrapAndShow(Window parent, Node node, Modality modality, R payload) throws InterruptedException, ExecutionException {
        return FxSaft.dispatch(() -> {
            OkCancelStage<SwingNode> s = new OkCancelStage(UiUtil.title(payload.getClass()), node);
            s.initOwner(parent);
            if (modality == null) s.initModality(APPLICATION_MODAL);
            else s.initModality(modality);
            L.warn("setLocationRelativeTo in JavaFx Mode not yet implemented");
            s.showAndWait();
            return new OkCancelResult<>(payload, s.isOk());
        });
    }

    @Override
    public FxOk<File> openFileChooser() {
        return openFileChooser(null);
    }

    @Override
    public FxOk<File> openFileChooser(String title) {
        return new FxOk<>(() -> {
            File file = FxSaft.dispatch(() -> {
                FileChooser fileChooser = new FileChooser();
                if (title == null) fileChooser.setTitle("Open File");
                else fileChooser.setTitle(title);
                return fileChooser.showOpenDialog(FxCore.mainStage());
            });
            return new OkCancelResult<>(file, file != null);
        }, parent, modality);
    }

    @Override
    public <R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> builder) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R extends JPanel> SwingOpenPanel<T, R> openSwing(Class<R> builder, String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R extends Pane> SwingOpenPane<T, R> openFx(Class<R> builder, String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R extends Pane> SwingOpenPane<T, R> openFx(Class<R> panelClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <R extends FxController> SwingOpenFxml<T, R> openFxml(Class<R> controllerClass, String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exec() {
        Ui.exec(this);
    }

}
