package eu.ggnet.dwoss.report.ee.itest;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.common.api.values.DocumentType;
import eu.ggnet.dwoss.common.api.values.PositionType;
import eu.ggnet.dwoss.report.ee.assist.Reports;
import eu.ggnet.dwoss.report.ee.entity.*;
import eu.ggnet.dwoss.report.ee.itest.support.ArquillianProjectArchive;

import static eu.ggnet.dwoss.common.api.values.TradeName.ONESELF;

@RunWith(Arquillian.class)
public class PersistenceIT extends ArquillianProjectArchive {

    @Inject
    @Reports
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testPersistence() throws Exception {
        ReportLine line1 = ReportLineBuilder.create("PersName1", "This is a TestDescription1", 137, "DW0037", 3, "RE0008", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");

        ReportLine line2 = ReportLineBuilder.create("PersName2", "This is a TestDescription2", 1337, "DW0013", 3, "RE001", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");

        ReportLine line3 = ReportLineBuilder.create("PersName3", "This is a TestDescription3", 13, "DW1337", 3, "RE0003", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");

        Report report = new Report("TestReport", ONESELF,
                new Date(Calendar.getInstance().getTimeInMillis() - 100000), new Date());

        utx.begin();
        em.joinTransaction();
        em.persist(line1);
        em.persist(line2);
        em.persist(line3);

        report.add(line1);
        report.add(line2);
        report.add(line3);
        em.persist(report);

        utx.commit();
    }
}
