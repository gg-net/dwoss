package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.AddressType;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Document;

import java.util.*;

import javax.ejb.*;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.customer.assist.CustomerPu;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;

import eu.ggnet.dwoss.redtape.RedTapeWorker.Addresses;


import eu.ggnet.dwoss.stock.assist.StockPu;

import static org.junit.Assert.*;

/**
 * Test the correct changes of Flags in a Document
 *
 * @author oliver.guenther
 */
public class DocumentSupporterFlagIT {

    private EJBContainer container;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private DocumentSupporter documentSupporter;

    @Inject
    private CustomerGeneratorOperation cgo;

    private long customerId;

    private final String arranger = "JUnit";

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(CustomerPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(RedTapePu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
        customerId = cgo.makeCustomer();
    }

    @After
    public void tearDown() {
        container.close();
    }

    @Test
    public void testDocumentFlags() {
        Document doc = testBriefed();
        doc = testOrder(doc);
        doc = testInvoice(doc);
        testCreditMemo(doc);
        testClosed();

    }

    private void testClosed() {
        Dossier dos = redTapeWorker.create(customerId, true, "JUnit");
        Document doc = dos.getActiveDocuments().get(0);
        addRandomPositions(doc);
        doc = redTapeWorker.update(doc, null, arranger);
        assertNotBriefed(doc);

        dos = documentSupporter.briefed(doc, arranger);
        assertExactlyBriefed(dos.getActiveDocuments().get(0));
        doc = dos.getActiveDocuments().get(0);

        doc.setClosed(true);
        doc = redTapeWorker.update(doc, null, arranger);
        assertExactlyBriefed(doc);

    }

    private Document testBriefed() {
        Dossier dos = redTapeWorker.create(customerId, true, "JUnit");
        Document doc = dos.getActiveDocuments().get(0);
        addRandomPositions(doc);
        doc = redTapeWorker.update(doc, null, arranger);
        assertNotBriefed(doc);

        dos = documentSupporter.briefed(doc, arranger);
        assertExactlyBriefed(dos.getActiveDocuments().get(0));
        return dos.getActiveDocuments().get(0);
    }

    private Document testOrder(Document doc) {

        // Changes that impact the Exactly Briefed Flag, but not the Briefed Flag
        modify2ndPosition(doc);
        doc = redTapeWorker.update(doc, null, arranger);
        assertOnlyBriefed(doc);

        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments().get(0);

        changeInvoiceAddress(doc);
        doc = redTapeWorker.update(doc, null, arranger);
        assertOnlyBriefed(doc);

        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments().get(0);
        assertFalse(PaymentMethod.CASH_ON_DELIVERY == doc.getDossier().getPaymentMethod());
        doc.getDossier().setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        doc = redTapeWorker.update(doc, null, arranger);
        assertOnlyBriefed(doc);

        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments().get(0);
        doc.getDossier().setDispatch(!doc.getDossier().isDispatch());
        doc = redTapeWorker.update(doc, null, arranger);
        assertOnlyBriefed(doc);

        // Changes that have no impact to Exactly Briefed and Briefed
        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments().get(0);
        doc.add(Document.Condition.PAID);
        doc = redTapeWorker.update(doc, null, arranger);
        assertExactlyBriefed(doc);

        doc.setDirective(Document.Directive.PREPARE_SHIPPING);
        doc = redTapeWorker.update(doc, null, arranger);
        assertExactlyBriefed(doc);
        return doc;
    }

    private Document testInvoice(Document doc) {

        doc.setType(DocumentType.INVOICE);
        doc = redTapeWorker.update(doc, null, arranger);
        assertNotBriefed(doc);

        // Changes that impact the Exactly Briefed Flag, but not the Briefed Flag
        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments(DocumentType.INVOICE).get(0);
        modify2ndPosition(doc);
        doc = redTapeWorker.update(doc, null, arranger);
        assertOnlyBriefed(doc);

        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments(DocumentType.INVOICE).get(0);
        changeInvoiceAddress(doc);
        doc = redTapeWorker.update(doc, null, arranger);
        assertOnlyBriefed(doc);

        // Changes that have no impact to Exactly Briefed and Briefed
        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments(DocumentType.INVOICE).get(0);
        doc.add(Document.Condition.PICKED_UP);
        doc = redTapeWorker.update(doc, null, arranger);
        assertExactlyBriefed(doc);

        doc.setDirective(Document.Directive.WAIT_FOR_MONEY);
        doc = redTapeWorker.update(doc, null, arranger);
        assertExactlyBriefed(doc);
        return doc;
    }

    private Document testCreditMemo(Document doc) {

        doc.setType(DocumentType.CREDIT_MEMO);
        doc = redTapeWorker.update(doc, null, arranger);
        assertNotBriefed(doc);

        // Changes that impact the Exactly Briefed Flag, but not the Briefed Flag
        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments(DocumentType.CREDIT_MEMO).get(0);
        modify2ndPosition(doc);
        doc = redTapeWorker.update(doc, null, arranger);
        assertOnlyBriefed(doc);

        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments(DocumentType.CREDIT_MEMO).get(0);
        changeInvoiceAddress(doc);
        doc = redTapeWorker.update(doc, null, arranger);
        assertOnlyBriefed(doc);

        // Changes that have no impact to Exactly Briefed and Briefed
        doc = documentSupporter.briefed(doc, arranger).getActiveDocuments(DocumentType.CREDIT_MEMO).get(0);
        doc.add(Document.Condition.REPAYMENT_BALANCED);
        doc = redTapeWorker.update(doc, null, arranger);
        assertExactlyBriefed(doc);

        doc.setDirective(Document.Directive.SEND_CASH_ON_DELIVERY_CONTRACT);
        doc = redTapeWorker.update(doc, null, arranger);
        assertExactlyBriefed(doc);
        return doc;
    }

    private void addRandomPositions(Document doc) {
        doc.append(NaivBuilderUtil.comment());
        doc.append(NaivBuilderUtil.service());
        doc.append(NaivBuilderUtil.shippingcost());
    }

    private void modify2ndPosition(Document doc) {
        Position p2 = doc.getPosition(2);
        p2.setPrice(p2.getPrice() + 50);
        p2.setAfterTaxPrice(p2.getPrice() + 50);
    }

    private void changeInvoiceAddress(Document doc) {
        cgo.scrambleAddress(customerId, AddressType.INVOICE);
        Addresses addresses = redTapeWorker.requestAdressesByCustomer(customerId);
        doc.setInvoiceAddress(addresses.getInvoice());
    }

    private void assertOnlyBriefed(Document doc) {
        assertTrue("Document must contain " + Document.Flag.CUSTOMER_BRIEFED + " but is " + doc, doc.getFlags().contains(Document.Flag.CUSTOMER_BRIEFED));
        assertFalse("Document must not contain " + Document.Flag.CUSTOMER_EXACTLY_BRIEFED + " but is " + doc, doc.getFlags().contains(Document.Flag.CUSTOMER_EXACTLY_BRIEFED));
    }

    private void assertExactlyBriefed(Document doc) {
        assertTrue("Document must contain " + Document.Flag.CUSTOMER_BRIEFED + " but is " + doc, doc.getFlags().contains(Document.Flag.CUSTOMER_BRIEFED));
        assertTrue("Document must contain " + Document.Flag.CUSTOMER_EXACTLY_BRIEFED + " but is " + doc, doc.getFlags().contains(Document.Flag.CUSTOMER_EXACTLY_BRIEFED));
    }

    private void assertNotBriefed(Document doc) {
        assertFalse("Document must not contain " + Document.Flag.CUSTOMER_BRIEFED + " but is " + doc, doc.getFlags().contains(Document.Flag.CUSTOMER_BRIEFED));
        assertFalse("Document must not contain " + Document.Flag.CUSTOMER_EXACTLY_BRIEFED + " but is " + doc, doc.getFlags().contains(Document.Flag.CUSTOMER_EXACTLY_BRIEFED));
    }

//    @Stateless
//    public static class DocumentSupportFlagCustomerHelper {
//
//        @Inject
//        private CustomerGeneratorOperation cgo;
//
//        private final Random R = new Random();
//
//        public void changeInvoiceAddress(long customerId) {
//            cgo.scrambleAddress(customerId, AddressType.INVOICE);
//        }
//
//    }
}
