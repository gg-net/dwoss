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

import eu.ggnet.saft.Ui;

/**
 * Result handler for ui activity, implements optional and completableFuture.
 *
 * @author oliver.guenther
 */
public class Result<T> {

    private final static Logger L = LoggerFactory.getLogger(Result.class);

    private Optional<T> opt = null;

    private CompletableFuture<T> cf = null;

    // TODO: Change the result and all producing classes to us the CompletableFuture for a full async callback implementaion.
    Result(Optional<T> opt) {
        this.opt = opt;
    }

    public Result(CompletableFuture<T> cf) {
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
        if ( opt != null ) return opt;
        try {
            return Optional.of(cf.get());
        } catch (InterruptedException ex) {
            Ui.handle(ex);
        } catch (ExecutionException ex) {
            if ( ex.getCause() instanceof OnceException ) {
                L.info("OnceException in opt, returning empty");
                return Optional.empty();
            }
            Ui.handle(ex);
        } catch (NoSuchElementException ex) {
            L.info("NoSuchElement in opt, returning empty");
            return Optional.empty();
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
        if ( cf != null ) return cf;
        return CompletableFuture.supplyAsync(() -> opt.get()); // Old style
    }

}
