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
 * Helper to bind Instance with action.
 * <p>
 * @author oliver.guenther
 * @param <T> the type
 */
public class DescriptiveConsumerRunner<T> implements Runnable {

    private final DescriptiveConsumer<T> descriptiveConsumer;

    private final T intance;

    public DescriptiveConsumerRunner(DescriptiveConsumer<T> descriptiveConsumer, T intance) {
        this.descriptiveConsumer = descriptiveConsumer;
        this.intance = intance;
    }

    @Override
    public void run() {
        descriptiveConsumer.consumer().accept(intance);
    }

    public String title() {
        return descriptiveConsumer.title();
    }

    public Consumer<T> consumer() {
        return descriptiveConsumer.consumer();
    }

    @Override
    public String toString() {
        return "DescriptiveConsumerRunner{" + "descriptiveConsumer=" + descriptiveConsumer + ", intance=" + intance + '}';
    }

}
