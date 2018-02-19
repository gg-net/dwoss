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
package eu.ggnet.dwoss.report.ee.assist.gen;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;
import eu.ggnet.dwoss.report.ee.assist.Reports;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

@Stateless
@TransactionAttribute(REQUIRES_NEW)
public class ReportLineGeneratorOperation {

    @Inject
    @Reports
    private EntityManager reportEm;

    @Inject
    private MonitorFactory monitorFactory;

    private final ReportLineGenerator generator = new ReportLineGenerator();

    public void makeReportLines(int amount) {
        SubMonitor m = monitorFactory.newSubMonitor("Erzeuge " + amount + " ReportLines", amount);
        m.start();
        for (int i = 0; i < amount; i++) {
            reportEm.persist(generator.makeReportLine());
            m.worked(1);
        }

    }

}
