/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.saft.core.swing;

import java.util.function.Consumer;

import javax.swing.JPanel;

import eu.ggnet.saft.api.ui.ResultProducer;
import eu.ggnet.saft.core.all.VetoableOnOk;

/**
 * Wrapper and Helper for the old Ok/Cancel construct
 *
 * @author oliver.guenther
 */
public class OkCancel {

    /**
     * Wrap method for every solution.
     * The Ok/Cancel construct is an old concept. Better is the javafx Dialog. So this wrapper is only for old DW implementations.
     * If we ever release the API, we might consider to split the different implementations.
     *
     * @param <U>
     * @param <V>
     * @param <T>
     * @param t
     * @return a Panel with Ok Cancel Button.
     */
    public static <U, V, T extends JPanel & VetoableOnOk & ResultProducer<V> & Consumer<U>> OkCancelPanel<V, U, T> wrap(T t) {
        return new OkCancelPanel<>(t);
    }
;

}
