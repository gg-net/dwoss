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
package eu.ggnet.dwoss.misc.ui.saleschannel;

import java.util.List;

import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.model.SalesChannelLine;

/**
 *
 * @author oliver.guenther
 */
public class SalesChannelManagerData {

    public final List<SalesChannelLine> lines;

    public final List<Stock> stocks;

    public SalesChannelManagerData(List<SalesChannelLine> lines, List<Stock> stocks) {
        this.lines = lines;
        this.stocks = stocks;
    }

}
