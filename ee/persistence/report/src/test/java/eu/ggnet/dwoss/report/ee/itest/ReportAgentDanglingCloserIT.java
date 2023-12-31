package eu.ggnet.dwoss.report.ee.itest;

import java.text.ParseException;
import java.util.*;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.report.ee.ReportAgent;
import eu.ggnet.dwoss.report.ee.ReportParameter;
import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.dwoss.report.ee.assist.gen.ReportLineGenerator;
import eu.ggnet.dwoss.report.ee.entity.Report;
import eu.ggnet.dwoss.report.ee.entity.ReportLine;
import eu.ggnet.dwoss.report.ee.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.core.system.util.Utils;

import static eu.ggnet.dwoss.core.common.values.DocumentType.*;
import static eu.ggnet.dwoss.core.common.values.PositionType.UNIT;
import static eu.ggnet.dwoss.core.common.values.tradename.TradeName.LENOVO;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class ReportAgentDanglingCloserIT extends ArquillianProjectArchive {

    private final static Date D0 = parse("2012-01-01");

    private final static Date D1 = parse("2013-01-01");

    private final static Date D2 = parse("2013-01-15");

    private final ReportLineGenerator generator = new ReportLineGenerator();

    @EJB
    private ReportAgent agent;

    @Inject
    @Reports
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testDanglingCloser() throws Exception {
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
        em.persist(invoice1);
        em.persist(invoice2);
        em.persist(annulationInvoiceFor2);
        em.persist(invoice3);
        em.persist(invoice4);
        Report r = new Report("Lenovo", LENOVO, D1, D2);
        r.add(invoice1);
        r.add(invoice2);
        r.add(annulationInvoiceFor2);
        em.persist(r);

        ReportLine danglingComplaintFor2 = generator.makeReportLine(asList(LENOVO), D1, 5, asList(UNIT), asList(COMPLAINT));
        danglingComplaintFor2.setRefurbishId(invoice2.getRefurbishId());
        danglingComplaintFor2.setUniqueUnitId(invoice2.getUniqueUnitId());
        invoice2.add(danglingComplaintFor2);
        annulationInvoiceFor2.add(danglingComplaintFor2);
        em.persist(danglingComplaintFor2);

        long idDangling = danglingComplaintFor2.getId();

        utx.commit();

        List<ReportLine> lines = agent.findAllEager(ReportLine.class);
        assertEquals("Size of prepared elements has changed, verifiy Test expectations", 6, lines.size());

        List<Report> reports = agent.findAll(Report.class);
        assertEquals("Should only have one report", 1, reports.size());

        Set<ReportLine> attached = agent.attachDanglingComplaints(LENOVO, D2);
        assertEquals("Unexpected ammount of dangling complaints", 1, attached.size());
        assertEquals("Unexpected ReportLine.id of dangling complaint", idDangling, attached.iterator().next().getId());

        Set<ReportLine> unreported2 = agent.prepareReport(
                new ReportParameter.Builder().contractor(LENOVO).start(D0).end(D2).reportName("A Report").build(),
                true).getAllLines();
        assertNull("The unreported lines does still contain the dangling complaint", filter(unreported2, idDangling));

        NavigableSet<ReportLine> reportlines = agent.findReportResult(reports.get(0).getId()).getAllLines();
        assertNotNull("The original report does not contain the dangling complaint", filter(reportlines, idDangling));

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
