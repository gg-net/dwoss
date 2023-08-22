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
package eu.ggnet.dwoss.redtapext.ee.reporting;

import javax.ejb.Remote;

import eu.ggnet.dwoss.core.common.FileJacket;
import eu.ggnet.dwoss.report.api.StockCount;

/**
 *
 * @author oliver.guenther
 */
@Remote
public interface RedTapeCloserManual {

    void executeManual(String arranger);

    /**
     * Returns a stock count of now.
     *
     * @return a stock count of now.
     */
    StockCount countStock();

    /**
     * Returns a stockCount as Xls Report.
     *
     * @return a stockCount as Xls Report.
     */
    FileJacket countStockAsXls();

        /**
     * Returns all stored stockCounts as Xls Report.
     *
     * @return all stored stockCounts as Xls Report.
     */
    FileJacket countStockHistoryAsXls();

    
}
