package eu.ggnet.dwoss.report;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.report.entity.Report;
import eu.ggnet.dwoss.report.entity.ReportLine;

import java.util.*;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.report.assist.ReportPu;

import static eu.ggnet.dwoss.rules.TradeName.*;

public class PersistenceIT {

    private EntityManagerFactory emf;

    private EntityManager em;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(ReportPu.NAME, ReportPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void after() {
        if ( em != null && em.isOpen() ) em.close();
        if ( emf != null && emf.isOpen() ) emf.close();
    }

    @Test
    public void testPersistence() {
        ReportLine line1 = new ReportLine("PersName1", "This is a TestDescription1", 137, "DW0037", 3, "RE0008", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");

        ReportLine line2 = new ReportLine("PersName2", "This is a TestDescription2", 1337, "DW0013", 3, "RE001", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");

        ReportLine line3 = new ReportLine("PersName3", "This is a TestDescription3", 13, "DW1337", 3, "RE0003", PositionType.UNIT,
                DocumentType.INVOICE, 2, 1, 0.19, 100, 119, 37, "This is the Invoice Address", "123", 2, "SERIALNUMBER", new Date(), 3, "PArtNo", "test@gg-net.de");

        Report report = new Report("TestReport", ONESELF,
                new Date(Calendar.getInstance().getTimeInMillis() - 100000), new Date());

        em.getTransaction().begin();
        em.persist(line1);
        em.persist(line2);
        em.persist(line3);
        em.getTransaction().commit();

        report.add(line1);
        report.add(line2);
        report.add(line3);
        em.getTransaction().begin();
        em.persist(report);
        em.getTransaction().commit();

    }
}
