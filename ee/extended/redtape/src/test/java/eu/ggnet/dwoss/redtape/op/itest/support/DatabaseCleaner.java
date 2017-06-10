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
package eu.ggnet.dwoss.redtape.op.itest.support;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.customer.assist.Customers;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.report.assist.Reports;
import eu.ggnet.dwoss.rights.assist.Rights;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.util.Utils;

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
        Utils.clearH2Db(redTapeEm, rightsEm, reportEm, customerEm, specEm, stockEm, uniqueunitEm);
    }

}
