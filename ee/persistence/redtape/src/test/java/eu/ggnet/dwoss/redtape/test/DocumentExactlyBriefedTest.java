package eu.ggnet.dwoss.redtape.test;

import eu.ggnet.dwoss.redtape.ee.entity.Address;
import eu.ggnet.dwoss.redtape.ee.entity.PositionBuilder;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

import org.junit.*;

import eu.ggnet.dwoss.rules.*;

import static eu.ggnet.dwoss.rules.TaxType.UNTAXED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pascal.perau
 */
public class DocumentExactlyBriefedTest {

    private Document doc1;

    private Document doc2;

    @Before
    public void setUp() {
        Address a1 = new Address("ShippingAddress");
        Address a2 = new Address("InvoiceAddress");

        doc1 = new Document();
        doc1.setShippingAddress(a1);
        doc1.setInvoiceAddress(a2);
        doc1.setType(DocumentType.ORDER);
        doc1.add(Document.Flag.CUSTOMER_BRIEFED);
        doc1.setDossier(new Dossier());
        doc1.append(new PositionBuilder().type(PositionType.UNIT).build());
        doc2 = doc1.partialClone();
        doc2.setDossier(new Dossier());

    }

    @After
    public void tearDown() {
        doc1 = null;
        doc2 = null;
    }

    @Test
    public void testPositionChange() {
        assertTrue("Documents difference must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));

        doc2.append(new PositionBuilder().type(PositionType.UNIT).build());
        doc2.append(new PositionBuilder().type(PositionType.UNIT).build());
        assertFalse("Position change must invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
    }

    @Test
    public void testDispatchChange() {
        assertTrue("Documents difference must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
        doc1.getDossier().setDispatch(!doc1.getDossier().isDispatch());
        assertFalse("Dispatch change must invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
    }

    @Test
    public void testPaymentMethodChange() {
        assertTrue("Documents difference must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
        assertFalse(doc2.getDossier().getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY);
        doc1.getDossier().setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);
        assertFalse("PaymentMethod change must invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
    }

    @Test
    public void testTypeChange() {
        assertTrue("Documents difference must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
        assertFalse(doc2.getType() == DocumentType.INVOICE);
        doc1.setType(DocumentType.INVOICE);
        assertFalse("Type change must invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
    }

    @Test
    public void testTaxTypeChange() {
        assertTrue("Documents difference must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
        assertFalse(doc2.getTaxType() == UNTAXED);
        doc1.setTaxType(UNTAXED);
        assertFalse("TaxType change must invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
    }

    @Test
    public void testInvoiceAddressChange() {
        assertTrue("Documents difference must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
        doc1.setInvoiceAddress(new Address("MuhBlub"));
        assertFalse("Invoice Address change must invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
    }

    @Test
    public void testShippingAddressChange() {
        assertTrue("Documents difference must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
        doc1.setShippingAddress(new Address("MuhBlub"));
        assertFalse("Shipping Address change must invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
    }

    @Test
    public void testChangesWithNoImpact() {
        assertTrue("Documents difference must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));

        doc1.setActive(!doc2.isActive());
        assertTrue("Active change must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));

        doc1.setHistory(new DocumentHistory("Junit", "Test"));
        assertTrue("History change must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));

        doc1.setPredecessor(doc2);
        assertTrue("Predecessor change must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));

        doc1.setClosed(true);
        assertFalse("Documents must have different Flags", doc1.isClosed() == doc2.isClosed());
        assertTrue("Flag change must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));

        doc1.add(Document.Condition.CONFIRMED);
        assertFalse("Documents must have different Conditions", doc1.getConditions().equals(doc2.getConditions()));
        assertTrue("Condition change must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));

        doc1.setDirective(Document.Directive.PREPARE_SHIPPING);
        assertFalse("Documents must have different Directives", doc1.getDirective().equals(doc2.getDirective()));
        assertTrue("Directive change must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));

        doc1.setIdentifier("Ein Identifier");
        assertTrue("Identifier change must not invalidate exactly briefed:\n" + doc1 + "\n" + doc2, doc1.isStillExactlyBriefed(doc2));
    }
}
