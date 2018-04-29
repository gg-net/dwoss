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
import javafx.scene.control.Dialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.Ui;
import eu.ggnet.saft.core.UiCore;
import eu.ggnet.saft.core.ui.SwingCore;
import eu.ggnet.saft.core.ui.builder.UiParameter.Type;

/**
 *
 * @author oliver.guenther
 */
public class DialogBuilder {

    private static final Logger L = LoggerFactory.getLogger(DialogBuilder.class);

    private final PreBuilder preBuilder;

    public DialogBuilder(PreBuilder pre) {
        this.preBuilder = pre;
        SwingCore.ensurePlatformIsRunning();
    }

    /**
     * Creates the javafx Dialog via the producer, shows it and returns the evaluated result as Optional.
     *
     * @param <T>            type of the result
     * @param <V>
     * @param dialogProducer the javafx Dialog producer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, V extends Dialog<T>> Result<T> eval(Callable<V> dialogProducer) {
        return new Result<>(internalShow(null, dialogProducer)
                .thenApplyAsync(BuilderUtil::waitAndProduceResult, UiCore.getExecutor()));
    }

    /**
     * Creates the javafx Dialog via the producer, supplies the consumer part with the result of the preProducer, shows it and returns the evaluated result as
     * Optional.
     *
     * @param <T>            type of the result
     * @param <P>            result type of the preProducer
     * @param <V>
     * @param preProducer    the preproducer, must not be null
     * @param dialogProducer the javafx Dialog producer, must not be null and must not return null.
     * @return the result of the evaluation, never null.
     */
    public <T, P, V extends Dialog<T> & Consumer<P>> Result<T> eval(Callable<P> preProducer, Callable<V> dialogProducer) {
        return new Result<>(internalShow(preProducer, dialogProducer)
                .thenApplyAsync(BuilderUtil::waitAndProduceResult, UiCore.getExecutor()));
    }

    private <T, P, V extends Dialog<T>> CompletableFuture<UiParameter> internalShow(Callable<P> preProducer, Callable<V> dialogProducer) {
        Objects.requireNonNull(dialogProducer, "The javafxPaneProducer is null, not allowed");
        // TODO: the parent handling must be optimized. And the javaFx
        UiParameter parm = UiParameter.builder().type(Type.DIALOG).id(preBuilder.id).title(preBuilder.title).frame(preBuilder.frame)
                .once(preBuilder.once).modality(preBuilder.modality).uiParent(preBuilder.uiParent).build();

        // Produce the ui instance
        CompletableFuture<UiParameter> uniChain = CompletableFuture
                .runAsync(() -> L.debug("Starting new Ui Element creation"), UiCore.getExecutor()) // Make sure we are not switching from Swing to JavaFx directly, which fails.
                .thenApplyAsync(v -> BuilderUtil.produceDialog(dialogProducer, parm), Platform::runLater)
                .thenApplyAsync((UiParameter p) -> p.withPreResult(Optional.ofNullable(preProducer).map(pp -> Ui.progress().call(pp)).orElse(null)), UiCore.getExecutor())
                .thenApply(BuilderUtil::breakIfOnceAndActive)
                .thenApply(BuilderUtil::consumePreResult);

        if ( UiCore.isSwing() ) {
            return uniChain
                    .thenApply(BuilderUtil::modifyDialog)
                    .thenApplyAsync(BuilderUtil::createJFXPanel, EventQueue::invokeLater)
                    .thenApplyAsync(BuilderUtil::wrapPane, Platform::runLater) // Swing Specific
                    .thenApplyAsync(BuilderUtil::constructSwing, EventQueue::invokeLater); // Swing Specific
        } else if ( UiCore.isFx() ) {
            return uniChain
                    .thenApplyAsync(BuilderUtil::constructDialog, Platform::runLater);
        } else {
            throw new IllegalStateException("UiCore is neither Fx nor Swing");
        }
    }

}
