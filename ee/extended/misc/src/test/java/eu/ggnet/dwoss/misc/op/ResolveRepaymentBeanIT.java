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

import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.apache.commons.lang.time.DateUtils;
import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.report.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.eao.ReportLineEao;
import eu.ggnet.dwoss.report.entity.ReportLine;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.rules.PositionType;

import static eu.ggnet.dwoss.rules.DocumentType.ANNULATION_INVOICE;
import static eu.ggnet.dwoss.rules.TradeName.AMAZON;

import static org.fest.assertions.api.Assertions.*;

public class ResolveRepaymentBeanIT {

    private EJBContainer container;

    @Inject
    private ResolveRepayment bean;

    @Inject
    private ResolveRepaymentBeanITHelper helper;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(ReportPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testGetRepaymentLines() {
        int amount = 50;
        helper.generateLines(amount);

        List<SimpleReportLine> repaymentLines = bean.getRepaymentLines(AMAZON);
        assertThat(repaymentLines).isNotEmpty().hasSize(amount);
    }

    @Stateless
    public static class ResolveRepaymentBeanITHelper {

        @Inject
        private ReportLineGenerator generator;

        @Inject
        ReportLineEao eao;

        public void generateLines(int amount) {
            for (int i = 0; i < amount; i++) {
                ReportLine makeReportLine = generator.makeReportLine(Arrays.asList(AMAZON), DateUtils.addDays(new Date(), 10), 25);
                makeReportLine.setPositionType(PositionType.UNIT);
                makeReportLine.setDocumentType(ANNULATION_INVOICE);
                eao.getEntityManager().persist(makeReportLine);
            }

        }
    }
}
