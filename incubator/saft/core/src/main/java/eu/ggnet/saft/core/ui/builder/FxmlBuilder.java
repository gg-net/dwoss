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

import java.awt.EventQueue;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javafx.application.Platform;

import eu.ggnet.saft.Ui;
import eu.ggnet.saft.UiCore;
import eu.ggnet.saft.api.ui.FxController;
import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.ui.builder.UiParameter.Type;

import lombok.experimental.Accessors;


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
 * Handles Swing elements on Saft.
 * This class has no impact how the emelemts are wrapped, only that the elements are based on Swing.
 *
 * @author oliver.guenther
 */
@Accessors(fluent = true)
public class FxmlBuilder extends AbstractBuilder {

    private final PreBuilder preBuilder;

    public FxmlBuilder(PreBuilder pre) {
        super(pre);
        this.preBuilder = pre;
        SwingCore.ensurePlatformIsRunning();
    }

    /**
     * Creates a fxml view/controller via the controllerClass and shows it on the correct thread.
     * <p>
     * Case: Ia.
     *
     * @param <V>                 the type
     * @param fxmlControllerClass the swingPanelProducer of the JPanel, must not be null and must not return null.
     */
    public <V extends FxController> void show(Class<V> fxmlControllerClass) {
        internalShow(null, fxmlControllerClass);
    }

    /**
     * Creates a fxml view/controller via the controllerClass, supplies the consumer part with the result of the preProducer and shows it.
     * <p>
     * Case: Ib
     *
     * @param <P>                 result type of the preProducer
     * @param <V>
     * @param fxmlControllerClass the swingPanelProducer of the JPanel, must not be null and must not return null.
     * @param preProducer         the preproducer, must not be null
     *                            javafxPaneProducer swingPanelProducer the swingPanelProducer, must not be null and must not return null.
     */
    public <P, V extends FxController & Consumer<P>> void show(Callable<P> preProducer, Class<V> fxmlControllerClass) {
        internalShow(preProducer, fxmlControllerClass);
    }

    /**
     * Creates a fxml view/controller via the controllerClass, shows it and returns the evaluated result as Optional.
     * <p>
     * Case: Ic
     *
     * @param <T>                 type of the result
     * @param <V>
     * @param fxmlControllerClass the swingPanelProducer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, V extends FxController & ResultProducer<T>> Result<T> eval(Class<V> fxmlControllerClass) {
        return new Result<>(internalShow(null, fxmlControllerClass)
                .thenApplyAsync(BuilderUtil::waitAndProduceResult, UiCore.getExecutor()));
    }

    /**
     * Creates a fxml view/controller via the controllerClass, supplies the consumer part with the result of the preProducer, shows it and returns the evaluated
     * result as
     * Optional.
     *
     * @param <T>                 type of the result
     * @param <P>                 result type of the preProducer
     * @param <V>
     * @param preProducer         the preproducer, must not be null
     * @param fxmlControllerClass the swingPanelProducer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, P, V extends FxController & Consumer<P> & ResultProducer<T>> Result<T> eval(Callable<P> preProducer, Class<V> fxmlControllerClass) {
        return new Result<>(internalShow(preProducer, fxmlControllerClass)
                .thenApplyAsync(BuilderUtil::waitAndProduceResult, UiCore.getExecutor()));
    }

    /**
     * Internal implementation, breaks the compile safty of the public methodes.
     * For now we have two normal execptions. The UiWorkflowBreak (allready open) and the NoSuchElementException (no result)
     *
     * @param <P>
     * @param <V>
     * @param preProducer
     * @param javafxPaneProducer
     * @return
     */
    private <T, P, V extends FxController> CompletableFuture<UiParameter> internalShow(Callable<P> preProducer, Class<V> fxmlControllerClass) {
        Objects.requireNonNull(fxmlControllerClass, "The fxmlControllerClass is null, not allowed");
        // TODO: the parent handling must be optimized. And the javaFx
        UiParameter parm = UiParameter.builder().type(Type.FXML).id(preBuilder.id).title(preBuilder.title).frame(preBuilder.frame)
                .once(preBuilder.once).modality(preBuilder.modality).uiParent(preBuilder.uiParent).build();

        // Produce the ui instance
        return CompletableFuture
                .runAsync(() -> L.debug("Starting new Ui Element creation"), UiCore.getExecutor()) // Make sure we are not switching from Swing to JavaFx directly, which fails.
                .thenApplyAsync((v) -> parm.withRootClass(fxmlControllerClass).withPreResult(Optional.ofNullable(preProducer).map(pp -> Ui.progress().call(pp)).orElse(null)), UiCore.getExecutor())
                .thenApply(BuilderUtil::breakIfOnceAndActive)
                .thenApplyAsync(BuilderUtil::constructFxml, Platform::runLater)
                .thenApplyAsync(BuilderUtil::consumePreResult, UiCore.getExecutor())
                .thenApplyAsync(BuilderUtil::wrapPane, Platform::runLater)
                .thenApplyAsync(BuilderUtil::constructSwing, EventQueue::invokeLater);
    }

}
