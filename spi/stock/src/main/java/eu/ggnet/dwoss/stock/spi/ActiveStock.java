/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.spi;

import eu.ggnet.dwoss.stock.api.PicoStock;

/**
 * Stock Ui Api.
 *
 * @author oliver.guenther
 */
public interface ActiveStock {

    // Todo: It would be much more apropriated, than there exists a method to open a new selection ui. Or I could push it to core.
    void setActiveStock(PicoStock stock);

    PicoStock getActiveStock();

}
