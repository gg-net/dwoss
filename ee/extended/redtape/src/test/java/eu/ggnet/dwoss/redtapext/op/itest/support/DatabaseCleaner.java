/*
 * Copyright (C) 2017 GG-Net GmbH
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
package eu.ggnet.dwoss.redtapext.op.itest.support;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.dwoss.rights.ee.assist.Rights;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.stock.ee.assist.Stocks;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerDeleteUtils;
import eu.ggnet.dwoss.redtape.ee.assist.gen.RedTapeDeleteUtils;
import eu.ggnet.dwoss.report.ee.assist.gen.ReportDeleteUtils;
import eu.ggnet.dwoss.rights.ee.assist.gen.RightsDeleteUtils;
import eu.ggnet.dwoss.spec.ee.assist.gen.SpecDeleteUtils;
import eu.ggnet.dwoss.stock.ee.assist.gen.StockDeleteUtils;
import eu.ggnet.dwoss.uniqueunit.ee.assist.gen.UniqueUnitsDeleteUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author olive
 */
@Stateless
public class DatabaseCleaner {

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @Inject
    @Rights
    private EntityManager rightsEm;

    @Inject
    @Reports
    private EntityManager reportEm;

    @Inject
    @Customers
    private EntityManager customerEm;

    @Inject
    @Specs
    private EntityManager specEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    @UniqueUnits
    private EntityManager uniqueunitEm;

    public void clear() throws Exception {
        RedTapeDeleteUtils.deleteAll(redTapeEm);
        assertThat(RedTapeDeleteUtils.validateEmpty(redTapeEm)).isNull();
        RightsDeleteUtils.deleteAll(rightsEm);
        assertThat(RightsDeleteUtils.validateEmpty(rightsEm)).isNull();
        ReportDeleteUtils.deleteAll(reportEm);
        assertThat(ReportDeleteUtils.validateEmpty(reportEm)).isNull();
        CustomerDeleteUtils.deleteAll(customerEm);
        assertThat(CustomerDeleteUtils.validateEmpty(customerEm)).isNull();
        SpecDeleteUtils.deleteAll(specEm);
        assertThat(SpecDeleteUtils.validateEmpty(specEm)).isNull();
        StockDeleteUtils.deleteAll(stockEm);
        assertThat(StockDeleteUtils.validateEmpty(stockEm)).isNull();
        UniqueUnitsDeleteUtils.deleteAll(uniqueunitEm);
        assertThat(UniqueUnitsDeleteUtils.validateEmpty(uniqueunitEm)).isNull();
    }

}
