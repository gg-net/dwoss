/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
