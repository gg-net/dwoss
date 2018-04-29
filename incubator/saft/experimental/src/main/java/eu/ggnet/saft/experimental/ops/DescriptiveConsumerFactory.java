/*
 * Copyright (C) 2014 GG-Net GmbH
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
package eu.ggnet.saft.experimental.ops;

import java.util.List;

/**
 * A way to create dependent actions on demand.
 * <p>
 * @author oliver.guenther
 * @param <T>
 */
public interface DescriptiveConsumerFactory<T> {

    /**
     * Returns a collection with dependent actions, may be empty but never null.
     * <p>
     * @param t the instance for which this is dependent.
     * @return a collection with dependent actions, may be empty but never null.
     */
    List<DescriptiveConsumer<T>> of(T t);
}
