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
package eu.ggnet.dwoss.core.widget.ops;

import java.util.function.Consumer;


/**
 * A Wrapper for a consumer, which also holds a title.
 * <p>
 * @author oliver.guenther
 * @param <T> the type
 */
public class DescriptiveConsumer<T> {

    private final String title;

    private final Consumer<T> consumer;

    public DescriptiveConsumer(String title, Consumer<T> consumer) {
        this.title = title;
        this.consumer = consumer;
    }

    public Consumer<T> consumer() {
        return consumer;
    }

    public String title() {
        return title;
    }
}
