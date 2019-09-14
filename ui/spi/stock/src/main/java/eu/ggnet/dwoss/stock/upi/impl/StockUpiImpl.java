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
package eu.ggnet.dwoss.stock.upi.impl;

import org.openide.util.lookup.ServiceProvider;

import eu.ggnet.dwoss.stock.api.PicoStock;
import eu.ggnet.dwoss.stock.upi.StockUpi;

/**
 *
 * @author oliver.guenther
 */
@ServiceProvider(service = StockUpi.class)
public class StockUpiImpl implements StockUpi {

    private PicoStock activeStock;

    @Override
    public PicoStock getActiveStock() {
        return activeStock;
    }

    @Override
    public void setActiveStock(PicoStock activeStock) {
        this.activeStock = activeStock;
    }

}
