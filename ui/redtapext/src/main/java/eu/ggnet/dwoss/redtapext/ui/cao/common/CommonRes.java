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
package eu.ggnet.dwoss.redtapext.ui.cao.common;

import java.net.URL;

/**
 * Static resources of common.
 *
 * @author oliver.guenther
 */
class CommonRes {

    static URL leftArrow() {
        return PositionListCell.class.getResource("left_arrow.png");
    }

    static URL downArrow() {
        return PositionListCell.class.getResource("down_arrow.png");
    }

}
