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
package eu.ggnet.dwoss.core.widget.saft;

/**
 * Implementing classes can veto a close on Ok, for example on a validation error.
 *
 * @author oliver.guenther
 */
public interface VetoableOnOk {

    /**
     * Must return true if the wrapped window may be closed, otherwise false.
     *
     * @return true, if closing is ok.
     */
    boolean mayClose();

}
