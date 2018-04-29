/*
 * Copyright (C) 2018 GG-Net GmbH
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

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.saft.core.Ui;

import lombok.NonNull;

/**
 * Result handler for ui activity, implements optional and completableFuture.
 *
 * @author oliver.guenther
 */
public class Result<T> {

    private final static Logger L = LoggerFactory.getLogger(Result.class);

    private CompletableFuture<T> cf = null;

    public Result(@NonNull CompletableFuture<T> cf) {
        this.cf = cf;
    }

    /**
     * Returns the result as optional, waiting for the completion of all possible async activity.
     * This method is blocking until a result is available or an exception happens. Make sure to
     * put this on a non ui thread. e.g. Ui.exec(() -> ...opt()...);
     * Optional.
     *
     * @return the result as optional, waiting for the completion of all possible async activity.
     */
    public Optional<T> opt() {
        try {
            return Optional.of(cf.get());
        } catch (InterruptedException ex) {
            Ui.handle(ex);
        } catch (ExecutionException ex) {
            if ( ex.getCause() instanceof UiWorkflowBreak ) {
                L.debug(ex.getCause() + ", retruning empty");
                return Optional.empty();
            }
            Ui.handle(ex);
        }
        L.error("Impposible End, returning empty");
        return Optional.empty();
    }

    /**
     * Returns a CompletableFuture for further async usage.
     * The actual implementation contains either the value of the previous procession or throws a {@link NoSuchElementException} if empty.
     *
     * @return a CompletableFuture
     */
    public CompletableFuture<T> cf() {
        return cf;
    }

}
