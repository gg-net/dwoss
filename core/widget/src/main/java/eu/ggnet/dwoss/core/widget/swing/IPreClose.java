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
package eu.ggnet.dwoss.core.widget.swing;

import eu.ggnet.dwoss.core.widget.saft.OkCancelWrap;
import eu.ggnet.dwoss.core.widget.saft.VetoableOnOk;

/**
 * Simple Interface to allow sub components to disallow some opperations.
 *
 * @deprecated Use {@link VetoableOnOk} and the {@link OkCancelWrap} in saft.
 */
@Deprecated
public interface IPreClose {

    /**
     * Is called before a closing opperation of type is done
     *
     * @param type the type of the closing operation
     * @return true if the closing opperation may continue as allowed
     */
    boolean pre(CloseType type);

}
