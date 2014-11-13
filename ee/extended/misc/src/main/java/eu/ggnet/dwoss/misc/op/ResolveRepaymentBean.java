/*
 * Copyright (C) 2014 bastian.venz
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
package eu.ggnet.dwoss.misc.op;

import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;

import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.TradeName;

import static eu.ggnet.dwoss.rules.DocumentType.ANNULATION_INVOICE;
import static eu.ggnet.dwoss.rules.DocumentType.CREDIT_MEMO;

/**
 *
 * @author bastian.venz
 */
@Stateless
public class ResolveRepaymentBean implements ResolveRepayment {

    private static final Date startThisYear;

    private static final Date endhisYear;

    static {
        startThisYear = DateUtils.round(new Date(), Calendar.YEAR);
        Date date = DateUtils.addMilliseconds(startThisYear, -1);
        endhisYear = DateUtils.addYears(date, 1);
    }

    @Inject
    private ReportLineEao reportLineEao;

    @Override
    public List<SimpleReportLine> getRepaymentLines(TradeName contractor) {
        List<ReportLine> findUnreportedUnits = reportLineEao.findUnreportedUnits(contractor, startThisYear, endhisYear);
        return findUnreportedUnits.stream()
                .filter((l) -> {
                    return l.getDocumentType() == ANNULATION_INVOICE || l.getDocumentType() == CREDIT_MEMO;
                }).map((l) -> {
                    return new SimpleReportLine(l.getReportingDate(), l.getRefurbishId(), l.getUniqueUnitId(), l.getContractor(), l.getPartNo(),
                            l.getProductName(), l.getAmount(), l.getPrice(), l.getPurchasePrice(), l.getContractorReferencePrice(), l.getDocumentType(), l.getPositionType());
                }).collect(Collectors.toList());
    }

    @Override
    public void resolveSopo(String sopo) {

    }

}
