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
package eu.ggnet.dwoss.stock.ee;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.stock.api.*;
import eu.ggnet.dwoss.stock.ee.eao.StockEao;
import eu.ggnet.dwoss.stock.ee.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.ee.entity.Stock;
import eu.ggnet.dwoss.stock.ee.entity.StockUnit;

import static eu.ggnet.dwoss.stock.ee.StockApiLocalBean.toSimple;

/*
 * Implementation of the remote stock api.
 *
 * @author oliver.guenther
 */
@Stateless
public class StockApiBean implements StockApi {

    @Inject
    private StockTransactionProcessorOperation stp;

    @Inject
    private StockEao stockEao;

    @Inject
    private StockUnitEao stockUnitEao;

    @Override
    public List<PicoStock> findAllStocks() {
        return stockEao.findAll().stream().map(Stock::toPicoStock).collect(Collectors.toList());
    }

    @Override
    public void perpareTransferByUniqueUnitIds(List<Long> uniqueUnitIds, int destinationStockId, String arranger, String comment) throws UserInfoException {
        List<StockUnit> stockUnits = stockUnitEao.findByUniqueUnitIds(uniqueUnitIds.stream().map(Long::intValue).collect(Collectors.toList()));
        stp.perpareTransfer(stockUnits, destinationStockId, arranger, comment);
    }

    @Override
    public SimpleStockUnit findByUniqueUnitId(long uniqueUnitId) {
        return toSimple(stockUnitEao.findByUniqueUnitId((int)uniqueUnitId), stockEao.findAll());
    }

}
