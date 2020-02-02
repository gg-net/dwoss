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
package eu.ggnet.dwoss.assembly.remote.cdi;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author oliver.guenther
 */
public class FxmlLoaderProducer {

    private final Logger L = LoggerFactory.getLogger(FxmlLoaderProducer.class);

    @Inject
    private Instance<Object> instance;

    @Produces
    public FxmlLoaderInitializer createLoader() {
        return new FxmlLoaderInitializer((Class<?> param) -> {
            L.debug("call(): creating Loader for {}", param);
            return instance.select(param).get();
        });
    }
}
