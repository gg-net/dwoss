package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.entity.Address;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;

import org.junit.*;

import net.sf.jasperreports.engine.JasperPrint;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;


import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import static org.junit.Assert.*;

/**
 *
 * @author bastian.venz
 */
public class DocumentSupporterIT {

    public DocumentSupporterIT() {
    }
    //<editor-fold defaultstate="collapsed" desc=" SetUp ">

    private EJBContainer container;

    @EJB
    private DocumentSupporter documentSupporter;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }

    /**
     * Test of create method, of class RedTapeOperation.
     */
    @Test
    public void testCreate() {
        Dossier dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        dos.setDispatch(true);
        dos.setCustomerId(1);

        Document doc = new Document();
        doc.setType(DocumentType.ORDER);
        doc.setActive(true);
        doc.setDirective(Document.Directive.WAIT_FOR_MONEY);
        doc.setHistory(new DocumentHistory("JUnit", "Automatische Erstellung eines leeren Dokuments"));

        Address a = new Address("Herr Muh\nMuhstrasse 7\n12345 Muhstadt");
        doc.setInvoiceAddress(a);
        doc.setShippingAddress(a);
        dos.add(doc);

        doc.append(NaivBuilderUtil.comment());
        doc.append(NaivBuilderUtil.service());
        doc.append(NaivBuilderUtil.shippingcost());

        JasperPrint print = documentSupporter.render(doc, DocumentViewType.DEFAULT);
        assertNotNull("A JasperPrint should not be null", print);
    }
}
