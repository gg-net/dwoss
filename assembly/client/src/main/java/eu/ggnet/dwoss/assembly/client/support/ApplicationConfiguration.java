/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.assembly.client.support;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This is the one time we can use a classic singleton.
 * We need this for the future switching to fx in the main ui and later in jpro.
 * In both cases, we cannot inject values into the Application instance directly, as cdi is not yet started.
 * On the other hand, we will never need different values per client session (in jpro) so a classic singleton is safe.
 *
 * @author oliver.guenther
 */
public class ApplicationConfiguration {

    private static ApplicationConfiguration instance;

    private final ConnectionParameter connectionParameter;

    /**
     * One time init and instance creator
     *
     * @param connectionParameter the conectionParameter
     * @return the inited ApplicationConfiguration.
     */
    public static ApplicationConfiguration initInstance(ConnectionParameter connectionParameter) {
        if ( instance != null ) throw new RuntimeException("Singleton instance allready initialized with " + instance);
        instance = new ApplicationConfiguration(connectionParameter);
        return instance;
    }

    public static ApplicationConfiguration instance() {
        return Objects.requireNonNull(instance, "Singleton not yet initialized, call initInstance() once first");
    }

    private ApplicationConfiguration(ConnectionParameter connectionParameter) {
        this.connectionParameter = Objects.requireNonNull(connectionParameter, "connectionParameter must not be null");
    }

    /**
     * Returns the connection parameters set on startup.
     *
     * @return the connection parameters
     */
    public ConnectionParameter connectionParameter() {
        return connectionParameter;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
