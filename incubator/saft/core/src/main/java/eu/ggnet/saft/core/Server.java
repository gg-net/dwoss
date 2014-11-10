package eu.ggnet.saft.core;

import javax.naming.Context;

import org.netbeans.api.annotations.common.NonNull;

/**
 * Service Interface to the Backend implementation.
 */
public interface Server {

    /**
     * Returns a Enterprise Context, optionally creating one if needed.
     * <p/>
     * @return a Enterprise Context, optionally creating one if needed.
     */
    @NonNull
    Context getContext();

    /**
     * Optionally initialise data after server startup in background.
     * e.g. Generate sample data.
     */
    void initialise();

    /**
     * Shutdown the Backend.
     */
    void shutdown();
}
