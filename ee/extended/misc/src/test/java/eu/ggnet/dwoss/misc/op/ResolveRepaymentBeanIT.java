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

import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.report.assist.ReportPu;
import eu.ggnet.dwoss.report.entity.partial.SimpleReportLine;

/**
 *
 * @author bastian.venz
 */
import static eu.ggnet.dwoss.rules.TradeName.ACER;

public class ResolveRepaymentBeanIT {

    private EJBContainer container;

    @Inject
    ResolveRepayment bean;

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
        List<SimpleReportLine> repaymentLines = bean.getRepaymentLines(ACER);
        System.out.println(repaymentLines);
    }
}
