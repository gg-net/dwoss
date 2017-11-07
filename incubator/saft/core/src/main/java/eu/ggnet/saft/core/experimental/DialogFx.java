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
package eu.ggnet.saft.core.experimental;

import java.util.Optional;
import java.util.concurrent.Callable;

import javafx.scene.control.Dialog;

import static java.util.Optional.empty;

/**
 *
 * @author oliver.guenther
 */
public class DialogFx {

    public <T, V extends Dialog<T>> Optional<T> eval(Callable<V> swingPanelProducer) {
        return empty();
    }

}
