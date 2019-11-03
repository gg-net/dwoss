package eu.ggnet.dwoss.report.ee.itest;

import eu.ggnet.dwoss.report.ee.itest.support.ArquillianProjectArchive;

import java.text.ParseException;
import java.util.*;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.ReportAgent.SearchParameter;
import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.dwoss.report.ee.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.entity.partial.SimpleReportLine;
import eu.ggnet.dwoss.core.system.Utils;

import static eu.ggnet.dwoss.common.api.values.DocumentType.*;
import static eu.ggnet.dwoss.common.api.values.PositionType.UNIT;
import static eu.ggnet.dwoss.common.api.values.TradeName.LENOVO;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ReportAgentSearchIT extends ArquillianProjectArchive {

    private final static Date D0 = parse("2012-01-01");

    private final static Date D1 = parse("2013-01-01");

    private final static Date D2 = parse("2013-01-15");

    @EJB
    private ReportAgent agent;

    @Inject
    @Reports
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private final ReportLineGenerator generator = new ReportLineGenerator();

    @Test
    public void testSearch() throws Exception {
        utx.begin();
        em.joinTransaction();

        ReportLine invoice1 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
        ReportLine invoice2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
        ReportLine annulationInvoiceFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(ANNULATION_INVOICE));
        annulationInvoiceFor2.setRefurbishId(invoice2.getRefurbishId());
        annulationInvoiceFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
        invoice2.add(annulationInvoiceFor2);

        ReportLine invoice3 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
        ReportLine invoice4 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
        ReportLine complaintFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(COMPLAINT));
        complaintFor2.setRefurbishId(invoice2.getRefurbishId());
        complaintFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
        invoice2.add(complaintFor2);
        annulationInvoiceFor2.add(complaintFor2);

        ReportLine differentFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
        differentFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
        invoice2.add(differentFor2);
        ReportLine different2For2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(INVOICE));
        different2For2.setUniqueUnitId(invoice2.getUniqueUnitId());
        invoice2.add(different2For2);

        em.persist(invoice1);
        em.persist(invoice2);
        em.persist(annulationInvoiceFor2);
        em.persist(invoice3);
        em.persist(invoice4);
        em.persist(differentFor2);
        em.persist(different2For2);

        String refurbishId = invoice2.getRefurbishId();

        utx.commit();

        long count = agent.count(new SearchParameter(refurbishId));
        assertTrue(count > 1);

        List<SimpleReportLine> find = agent.findSimple(new SearchParameter(refurbishId), 0, 100);
        assertEquals(count, find.size());
    }

    private ReportLine filter(Collection<ReportLine> lines, long id) {
        for (ReportLine line : lines) {
            if ( line.getId() == id ) return line;
        }
        return null;
    }

    private static Date parse(String dateString) {
        try {
            return Utils.ISO_DATE.parse(dateString);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
