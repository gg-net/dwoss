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
package eu.ggnet.dwoss.uniqueunit.itest.support;

import jakarta.ejb.Stateless;

import eu.ggnet.dwoss.stock.api.SimpleStockUnit;
import eu.ggnet.dwoss.stock.api.StockApiLocal;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class StockApiLocalStub implements StockApiLocal {

    @Override
    public String findByUniqueUnitIdAsHtml(long uniqueUnitId) {
        return "StockUnitAsHtml by UniqueUnit.id=" + uniqueUnitId;
    }

    @Override
    public SimpleStockUnit findByUniqueUnitId(long uniqueUnitId) {
        return null;
    }

}
