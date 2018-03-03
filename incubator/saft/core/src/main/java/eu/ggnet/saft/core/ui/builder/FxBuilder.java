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
package eu.ggnet.saft.core.ui.builder;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

import javax.swing.JComponent;
import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.Pane;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.api.ui.Once;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.ui.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import static eu.ggnet.saft.core.ui.builder.AbstractBuilder.callWithProgress;

/*
    I - 4 Fälle:
    a. nur zeigen. Ui consumiert nix und prodziert kein result
    b. consumer ui of type v
    c. result producer of type r
    d. conumer and result producer of type v,r

    II - 3. Uis
    a. Swing JPanel
    b. JavaFx Pane
    c. JavaFxml + Controller Class


    Examples:
    Ui.build().fx().parrent().id("blaa").eval(fdsafdsafddsa);

    Ui.build().swing().show(()->Demo());

 */
/**
 * Handles Fx elements on Saft.
 * This class has no impact how the emelemts are wrapped, only that the elements are based on Swing.
 *
 * @author oliver.guenther
 */
@Accessors(fluent = true)
public class FxBuilder extends AbstractBuilder {

    public FxBuilder() {
        super();
        SwingCore.ensurePlatformIsRunning();
    }

    public FxBuilder(PreBuilder pre) {
        super(pre);
        SwingCore.ensurePlatformIsRunning();
    }

    /**
     * Creates the javafx Pane via the producer and shows it on the correct thread.
     * <p>
     * Case: Ia.
     *
     * @param <V>                the type
     * @param javafxPaneProducer the producer of the JPanel, must not be null and must not return null.
     */
    public <V extends Pane> void show(Callable<V> javafxPaneProducer) {
        intEval(null, javafxPaneProducer);
    }

    /**
     * Creates the javafx Pane via the producer, supplies the consumer part with the result of the preProducer and shows it.
     * <p>
     * Case: Ib
     *
     * @param <P>                result type of the preProducer
     * @param <V>                javafx Pane and Consumer type
     * @param preProducer        the preProducer, must not be null
     * @param javafxPaneProducer the producer of the JPanel, must not be null and must not return null.
     */
    public <P, V extends Pane & Consumer<P>> void show(Callable<P> preProducer, Callable<V> javafxPaneProducer) {
        intEval(preProducer, javafxPaneProducer);
    }

    /**
     * Creates the javafx Pane via the producer, shows it and returns the evaluated result as Optional.
     * <p>
     * Case: Ic
     *
     * @param <T>                type of the result
     * @param <V>
     * @param javafxPaneProducer the producer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, V extends Pane & ResultProducer<T>> Result<T> eval(Callable<V> javafxPaneProducer) {
        return new Result<>(intEval(null, javafxPaneProducer));
    }

    /**
     * Creates the javafx Pane via the producer, supplies the consumer part with the result of the preProducer, shows it and returns the evaluated result as
     * Optional.
     *
     * @param <T>                type of the result
     * @param <P>                result type of the preProducer
     * @param <V>
     * @param preProducer        the preproducer, must not be null
     * @param javafxPaneProducer the producer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, P, V extends Pane & Consumer<P> & ResultProducer<T>> Result<T> eval(Callable<P> preProducer, Callable<V> javafxPaneProducer) {
        return new Result<>(intEval(preProducer, javafxPaneProducer));
    }

    /**
     * Internal implementation, breaks the compile safty of the public methodes.
     *
     * @param <P>
     * @param <V>
     * @param preProducer
     * @param javafxPaneProducer
     * @return
     */
    private <T, P, V extends Pane> Optional<T> intEval2(Callable<P> preProducer, Callable<V> javafxPaneProducer) {
        try {
            Objects.requireNonNull(javafxPaneProducer, "The javafxPaneProducer is null, not allowed");
            // Phase I
            V pane = FxSaft.dispatch(javafxPaneProducer); // Creating the panel on the right thread
            Params p = buildParameterBackedUpByDefaults(pane.getClass());
            P preResult = null;
            if ( preProducer != null ) {
                preResult = callWithProgress(preProducer);
                p.optionalSupplyId(preResult);
            }
            if ( isOnceModeAndActiveWithSideeffect(p.key()) ) return Optional.empty();
            if ( preProducer != null && pane instanceof Consumer ) {
                ((Consumer)pane).accept(preResult); // Calling the preproducer and setting the result in the panel
            }
            Window window = constructAndShow(SwingCore.wrap(pane), p, pane.getClass()); // Constructing the JFrame/JDialog, setting the parameters and makeing it visible
            SwingSaft.enableCloser(window, pane);
            wait(window);
            if ( pane instanceof ResultProducer ) return Optional.ofNullable((T)((ResultProducer)pane).getResult());
            return null; // using the return value without a ResultProducer must fail.
        } catch (InterruptedException | InvocationTargetException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Internal implementation, breaks the compile safty of the public methodes.
     * For now we have two normal execptions. The OnceException (allready open) and the NoSuchElementException (no result)
     *
     * @param <P>
     * @param <V>
     * @param preProducer
     * @param javafxPaneProducer
     * @return
     */
    private <T, P, V extends Pane> CompletableFuture<T> intEval(Callable<P> preProducer, Callable<V> javafxPaneProducer) {
        Objects.requireNonNull(javafxPaneProducer, "The javafxPaneProducer is null, not allowed");
        PreBuilder preBuilder = new PreBuilder().id(id).title(title).frame(frame).modality(modality).parent(swingParent); // Refactor later

        // Produce the ui instance
        CompletableFuture<V> paneBuildFuture = CompletableFuture
                .runAsync(() -> L.info("Starting Saft Callback on UiCore.executor"), UiCore.getExecutor()) // Make sure we are not switching from Swing to JavaFx directly, which fails.
                .supplyAsync(() -> {
                    try {
                        return javafxPaneProducer.call();
                    } catch (Exception ex) {
                        throw new CompletionException(ex);
                    }
                }, Platform::runLater);

        // Construct default parameters.
        CompletableFuture<PaneAndParms<P>> paneAndParmsFuture
                = paneBuildFuture.thenApplyAsync((V pane) -> new PaneAndParms(pane, buildParameterBackedUpByDefaults2(pane.getClass(), preBuilder), preBuilder.swingParent), UiCore.getExecutor());

        if ( preProducer != null ) { // Optimise. Only if the preproducer is of type IdSupplier, it needs to be called before the once verifycation.
            paneAndParmsFuture = paneAndParmsFuture.thenApply(t -> {
                t.preResult = Ui.progress().call(preProducer);
                t.params.optionalSupplyId(t.preResult);
                return t; // Better, make a new t
            });
        }

        paneAndParmsFuture = paneAndParmsFuture.thenApply((PaneAndParms<P> t) -> {
            if ( isOnceModeAndActiveWithSideeffect(once, t.params.key()) ) throw new OnceException();
            return t;
        });

        if ( preProducer != null ) {
            paneAndParmsFuture = paneAndParmsFuture.thenApply((PaneAndParms<P> t) -> {
                ((Consumer)t.pane).accept(t.preResult);
                return t;
            });
        }

        paneAndParmsFuture = paneAndParmsFuture.thenApplyAsync((PaneAndParms<P> pp) -> {
            pp.jfxPanel = SwingCore.wrapDirect(pp.pane);
            return pp;
        }, Platform::runLater);


        CompletableFuture<PaneAndWindow> paneAndWindowFuture = paneAndParmsFuture.thenApplyAsync((PaneAndParms<P> pp) -> {
            try {
                JComponent component = pp.jfxPanel;
                final Window window = pp.params.framed
                        ? BuilderUtil.newJFrame(pp.params.title(), component)
                        : BuilderUtil.newJDailog(swingParent, pp.params.title(), component, pp.params.modalityType);
                // Todo: the Icon Referenz Class is not ava
                BuilderUtil.setWindowProperties(window, pp.pane.getClass(), swingParent, pp.pane.getClass(), pp.params.key());
                BuilderUtil.enableCloser(window, pp.pane);
                window.setVisible(true);
                return new PaneAndWindow(pp.pane, window);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, EventQueue::invokeLater);

        CompletableFuture resultFuture = paneAndWindowFuture.thenApplyAsync((PaneAndWindow pw) -> {
            if ( pw.pane instanceof ResultProducer ) {         // Hint: in case of no resultproduce, the wait is also not needed
                try {
                    BuilderUtil.wait(pw.window);
                } catch (InterruptedException | IllegalStateException | NullPointerException ex) {
                    throw new CompletionException(ex);
                }
                Object result = ((ResultProducer)pw.pane).getResult();
                if ( result == null ) throw new NoSuchElementException(); // Window was "canceld"
                return result;
            }
            return null;
        }, UiCore.getExecutor());

        return resultFuture;
    }

    protected Params buildParameterBackedUpByDefaults2(Class<?> rootClass, PreBuilder b) {
        Once onceAnnotation = rootClass.getAnnotation(Once.class);
        if ( onceAnnotation != null ) once = onceAnnotation.value();
        return Params.builder()
                .rootClazz(rootClass)
                .id(b.id)
                .titleTemplate(b.title)
                .framed(!b.frame ? rootClass.getAnnotation(eu.ggnet.saft.api.ui.Frame.class) != null : b.frame)
                .modalityType(toSwing(b.modality).orElse(Dialog.ModalityType.MODELESS)).build();
    }

    // For now I use this as an internal result class. Better splitt (one without result) and merge with params.
    private static class PaneAndParms<Z> {

        private Pane pane;

        private Params params;

        private Z preResult;

        private Window swingParent;

        private JFXPanel jfxPanel;

        public PaneAndParms(Pane pane, Params params, Window swingParent) {
            this.pane = pane;
            this.params = params;
            this.swingParent = swingParent;
        }

    }

    @AllArgsConstructor
    @Getter
    private static class PaneAndWindow {

        private Pane pane;

        private Window window;

    }

    protected boolean isOnceModeAndActiveWithSideeffect(boolean once, String key) {
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

}
