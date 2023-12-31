package eu.ggnet.dwoss.redtapext.op.itest;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;

import net.sf.jasperreports.engine.JasperPrint;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.mandator.api.DocumentViewType;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.dwoss.redtapext.op.itest.support.*;

import static org.junit.Assert.assertNotNull;

/**
 *
 * @author bastian.venz
 */
@RunWith(Arquillian.class)
public class DocumentSupporterIT extends ArquillianProjectArchive {

    @EJB
    private DocumentSupporter documentSupporter;

    @Inject
    private DatabaseCleaner cleaner;

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
    }

    /**
     * Test of create method, of class RedTapeOperation.
     */
    @Test
    public void testCreate() throws UserInfoException {
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
