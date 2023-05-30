/*
 * Copyright (C) 2023 GG-Net GmbH
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
package eu.ggnet.dwoss.report.ee.test;


import org.junit.Test;

import eu.ggnet.dwoss.report.api.StockCount;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
public class StockCountApiTest {
    
    @Test
    public void test() {
        StockCount sc = new StockCount.Builder().created(now()).build();
        eu.ggnet.dwoss.report.ee.entity.StockCount sce = eu.ggnet.dwoss.report.ee.entity.StockCount.fromApi(sc);
        StockCount sc2 = sce.toApi();
        assertThat(sc).isEqualTo(sc2);        
    }
}
