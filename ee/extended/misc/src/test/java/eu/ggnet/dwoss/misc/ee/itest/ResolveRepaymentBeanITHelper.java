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
package eu.ggnet.dwoss.misc.ee.itest;

import java.util.Arrays;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.report.ee.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.ee.eao.ReportLineEao;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;

/**
 *
 * @author olive
 */
@Stateless
public class ResolveRepaymentBeanITHelper {

    @Inject
    private ReportLineGenerator generator;

    @Inject
    private ReportLineEao reportLineEao;

    @Inject
    private UniqueUnitEao uniqueUniteao;

    public void generateLines(int amount) {
        for (int i = 0; i < amount;
                i++) {
            ReportLine makeReportLine = generator.makeReportLine(Arrays.asList(TradeName.AMAZON), DateUtils.addDays(new Date(), -30), 25);
            makeReportLine.setPositionType(PositionType.UNIT);
            makeReportLine.setDocumentType(DocumentType.ANNULATION_INVOICE);
            reportLineEao.getEntityManager().persist(makeReportLine);
        }
    }

    public UniqueUnit changeContractors(int uniqueUnitID, TradeName name) {
        UniqueUnit uu = uniqueUniteao.findById(uniqueUnitID);
        uu.fetchEager();
        uu.setContractor(name);
        return uu;
    }

}
