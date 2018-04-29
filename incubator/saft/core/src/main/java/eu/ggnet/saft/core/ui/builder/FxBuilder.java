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
import javafx.scene.layout.Pane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.ResultProducer;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.ui.builder.UiParameter.Type;


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
public class FxBuilder {

    private static final Logger L = LoggerFactory.getLogger(FxBuilder.class);

    private final PreBuilder preBuilder;

    public FxBuilder(PreBuilder preBuilder) {
        if ( UiCore.isSwing() ) SwingCore.ensurePlatformIsRunning();
        this.preBuilder = preBuilder;
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
        internalShow(null, javafxPaneProducer).handle(Ui.handler());
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
        internalShow(preProducer, javafxPaneProducer).handle(Ui.handler());
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
        return new Result<>(internalShow(null, javafxPaneProducer)
                .thenApplyAsync(BuilderUtil::waitAndProduceResult, UiCore.getExecutor()));
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
        return new Result<>(internalShow(preProducer, javafxPaneProducer)
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
    private <T, P, V extends Pane> CompletableFuture<UiParameter> internalShow(Callable<P> preProducer, Callable<V> javafxPaneProducer) {
        Objects.requireNonNull(javafxPaneProducer, "The javafxPaneProducer is null, not allowed");
        // TODO: the parent handling must be optimized. And the javaFx
        UiParameter parm = UiParameter.builder().type(Type.FX).id(preBuilder.id).title(preBuilder.title).frame(preBuilder.frame)
                .once(preBuilder.once).modality(preBuilder.modality).uiParent(preBuilder.uiParent).build();

        // Produce the ui instance
        CompletableFuture<UiParameter> uniChain = CompletableFuture
                .runAsync(() -> L.debug("Starting new Ui Element creation"), UiCore.getExecutor()) // Make sure we are not switching from Swing to JavaFx directly, which fails.
                .thenApplyAsync(v -> BuilderUtil.producePane(javafxPaneProducer, parm), Platform::runLater)
                .thenApplyAsync((UiParameter p) -> p.withPreResult(Optional.ofNullable(preProducer).map(pp -> Ui.progress().call(pp)).orElse(null)), UiCore.getExecutor())
                .thenApply(BuilderUtil::breakIfOnceAndActive) // Siwng specific
                .thenApply(BuilderUtil::consumePreResult);

        if ( UiCore.isSwing() ) {
            return uniChain
                    .thenApplyAsync(BuilderUtil::createJFXPanel, EventQueue::invokeLater)
                    .thenApplyAsync(BuilderUtil::wrapPane, Platform::runLater) // Swing Specific
                    .thenApplyAsync(BuilderUtil::constructSwing, EventQueue::invokeLater); // Swing Specific
        } else if ( UiCore.isFx() ) {
            return uniChain
                    .thenApplyAsync(BuilderUtil::constructJavaFx, Platform::runLater);
        } else {
            throw new IllegalStateException("UiCore is neither Fx nor Swing");
        }
    }

}
