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
package eu.ggnet.dwoss.common;

import java.awt.Window;

import eu.ggnet.saft.core.*;

/**
 *
 * @author oliver.guenther
 */
// TODO: In the new UI Framework we need to add the Guardion through a listener to the UiCore or SaftCore
public class DwOssCore {

    /**
     * Inspects the tree of Causes, if it contaions a LayerEightException.
     * If LayerEightExceptin is found, only the message is shown.
     * Else the hole stacktree.
     *
     * @param parent
     * @param e      the ChildExcpetion
     * @deprecated use {@link UiCore#handle(java.lang.Throwable) } instead.
     */
    @Deprecated
    public static void show(Window parent, Exception e) {
        UiCore.handle(e);
    }

}
