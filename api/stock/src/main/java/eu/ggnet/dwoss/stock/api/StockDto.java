/*
 * Copyright (C) 2021 GG-Net GmbH
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
package eu.ggnet.dwoss.stock.api;

import java.util.List;

import eu.ggnet.dwoss.stock.api.PicoStock;

/**
 *
 * @author mirko.schulze
 */
public class StockDto {

    private List<PicoStock> stocks;

    private PicoStock selectedStock;

    public StockDto(List<PicoStock> stocks, PicoStock selectedStock) {
        this.stocks = stocks;
        this.selectedStock = selectedStock;
    }

    public List<PicoStock> getStocks() {
        return stocks;
    }

    public PicoStock getSelectedStock() {
        return selectedStock;
    }

}
