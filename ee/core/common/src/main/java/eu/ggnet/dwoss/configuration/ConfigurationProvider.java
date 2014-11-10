package eu.ggnet.dwoss.configuration;

import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * Implement and supply the implementation in the final assembly to configure the sample, local or remote installation.
 * <p>
 * @author oliver.guenther
 */
public interface ConfigurationProvider {

    /**
     * Implementation may supply some more configuration to an embedded openejb server, but must not.
     * A sample configuration should always be only in memory, and generate data on startup via initializeSample.
     * If not, implementation must return a empty map.
     * <p>
     * @return an optionall configuration that will be added to a sample installation.
     */
    @NotNull
    Map<String, String> openejbAddToEmbeddedSampleConfiguration();

    /**
     * Implementation must supply a full openejb configuration for all persistent sources.
     * <p>
     * @return a full configuration for an embedded openejb server.
     */
    @NotNull
    Map<String, String> openejbEmbeddedLocalConfiguration();

    /**
     * This method is called after the server is up and running in the sample mode.
     * Here optional generation of sample data can be supplied.
     */
    void initializeSample();

}
