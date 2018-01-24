package eu.ggnet.dwoss.redtape.op.itest;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.*;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.customer.assist.gen.CustomerGeneratorOperation;
import eu.ggnet.dwoss.redtapext.ee.DocumentSupporter;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.RedTapeWorker.Addresses;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.op.itest.support.*;
import eu.ggnet.dwoss.rules.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the correct changes of Flags in a Document
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class DocumentSupporterFlagIT extends ArquillianProjectArchive {

    @EJB
    private RedTapeWorker redTapeWorker;

    @EJB
    private DocumentSupporter documentSupporter;

    @Inject
    private CustomerGeneratorOperation cgo;

    private long customerId;

    private final String arranger = "JUnit";

    @Inject
    private DatabaseCleaner cleaner;

    @Before
    public void makeOneCustomer() {
        customerId = cgo.makeCustomer();
    }

    @After
    public void clearDatabase() throws Exception {
        cleaner.clear();
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
