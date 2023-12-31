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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.enterprise.inject.se.SeContainerInitializer;

/**
 * Helper class to create a configuriation for a cdi SeContainer.
 * This class is a classic singleton. This is needed and ok cause of:
 * <ul>
 * <li>The configuration is used globally and will be used globally in jpro.one</li>
 * <li>The configuration is needed to create the container, not in the container</li>
 * <li>The only way to get parameters to the javafx application directly is via String parameters, which is improbable for this case</li>
 * <li>There needs to be a way to supply container configuration information for dw pro.</li>
 * <li>Auto discovery is disabled in the contationer, as we pull in classes from the server, which should not be injectable in the client</li>
 * </ul>
 *
 * @author oliver.guenther
 */
public class ContainerConfiguration {

    private static ContainerConfiguration instance;

    private final List<Class<?>> fullPackages = new ArrayList<>();

    private final List<Class<?>> packages = new ArrayList<>();

    public static ContainerConfiguration instance() {
        if ( instance == null ) instance = new ContainerConfiguration();
        return instance;
    }

    /**
     * Returns a unmodifiable collection of all full packages.
     *
     * @return full packages
     */
    public Class<?>[] fullPackages() {
        return fullPackages.toArray(new Class<?>[]{});
    }

    /**
     * Returns a unmodifiable collection of all packages.
     *
     * @return full packages
     */
    public Class<?>[] packages() {
        return packages.toArray(new Class<?>[]{});
    }

    /**
     * See {@link SeContainerInitializer#addPackages(java.lang.Class...) }
     *
     * @param clazz the class pointing to the package.
     */
    public void addPackages(Class<?>... clazz) {
        addPackages(false, clazz);
    }

    /**
     * See {@link SeContainerInitializer#addPackages(boolean, java.lang.Class...)
     *
     * @param withSubPackages scan recursivly
     * @param clazz           the class pointing to the package.
     */
    public void addPackages(boolean withSubPackages, Class<?>... clazz) {
        if ( clazz == null ) return;
        Stream.of(clazz).forEach(c -> {
            if ( withSubPackages ) fullPackages.add(c);
            else packages.add(c);
        });
    }

}
