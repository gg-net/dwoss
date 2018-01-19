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
package eu.ggnet.dwoss.stock.ee;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.stock.api.PicoStockUnit;
import eu.ggnet.dwoss.stock.api.StockApi;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;

/**
 * Entwurf.
 *
 * @author oliver.guenther
 */
@Remote(StockApi.class)
@Stateless
public class StockApiBean implements StockApi {

    @Inject
    private StockUnitEao eao;

    @Override
    public List<PicoStockUnit> findAll() {
        return eao.findAll().stream().map(StockUnit::toPico).collect(Collectors.toList());
    }

    @Override
    public List<PicoUnit> filterAvailable(List<PicoUnit> units) {
        Set<Integer> avialableUuIds = eao.findByUniqueUnitIds(units.stream().map(PicoUnit::getUniqueUnitId).collect(Collectors.toList()))
                .stream().map(StockUnit::getUniqueUnitId).collect(Collectors.toSet());
        return units.stream().filter(p -> avialableUuIds.contains(p.getUniqueUnitId())).collect(Collectors.toList());
    }

}
