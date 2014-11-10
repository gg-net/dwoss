package eu.ggnet.dwoss.redtape.entity;

import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Reminder;

import java.util.Arrays;
import java.util.Objects;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class DossierTest {

    @Test
    public void testGetActive() {
        Dossier dos = new Dossier();

        Document doc1 = new Document();
        doc1.setType(DocumentType.ORDER);
        doc1.setActive(true);
        dos.add(doc1);

        Document doc2 = new Document();
        doc2.setType(DocumentType.ORDER);
        doc2.setActive(false);
        dos.add(doc2);

        Document doc3 = new Document();
        doc3.setType(DocumentType.INVOICE);
        doc3.setActive(true);
        dos.add(doc3);

        Document doc4 = new Document();
        doc4.setType(DocumentType.CREDIT_MEMO);
        doc4.setActive(true);
        dos.add(doc4);

        assertEquals("ActiveDocument(Type=Order)", doc1, dos.getActiveDocuments(DocumentType.ORDER).get(0));
        assertEquals("ActiveDocument(Type=Invoice)", doc3, dos.getActiveDocuments(DocumentType.INVOICE).get(0));
        assertEquals("ActiveDocuments", 3, dos.getActiveDocuments().size());
    }

    @Test
    public void testChangesAllowedOpenDossier() {
        Dossier dos1 = sampleOpenDossier();
        Dossier dos2 = sampleOpenDossier();
        assertTrue(dos1.changesAllowed(dos2));

        dos2.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        dos2.setDispatch(!dos2.isDispatch());
        assertTrue(dos1.changesAllowed(dos2));
    }

    @Test
    public void testChangesAllowedClosedDossier() {
        Dossier dos1 = sampleClosedDossier();
        Dossier dos2 = sampleClosedDossier();
        assertTrue(dos1.changesAllowed(dos2));
        dos2.setReminder(new Reminder());
        dos2.setComment("Ein Kommentar");
        assertTrue(dos1.changesAllowed(dos2));
    }

    @Test
    public void testChangesAllowedClosedDossierPaymentMethod() {
        Dossier dos1 = sampleClosedDossier();
        Dossier dos2 = sampleClosedDossier();
        dos2.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        assertFalse(dos1.getPaymentMethod() == dos2.getPaymentMethod());
        assertFalse("Changes should not be allowed", dos1.changesAllowed(dos2));
    }

    @Test
    public void testChangesAllowedClosedDossierCustomerId() {
        Dossier dos1 = sampleClosedDossier();
        Dossier dos2 = sampleClosedDossier();
        dos2.setCustomerId(999);
        assertFalse(dos1.getCustomerId() == dos2.getCustomerId());
        assertFalse("Changes should not be allowed", dos1.changesAllowed(dos2));
    }

    @Test
    public void testChangesAllowedClosedDossierDispatch() {
        Dossier dos1 = sampleClosedDossier();
        Dossier dos2 = sampleClosedDossier();
        dos2.setDispatch(!dos1.isDispatch());
        assertFalse("Changes should not be allowed", dos1.changesAllowed(dos2));
    }

    @Test
    public void testChangesAllowedClosedDossierIdentifier() {
        Dossier dos1 = sampleClosedDossier();
        Dossier dos2 = sampleClosedDossier();
        dos2.setIdentifier("Blubbla");
        assertFalse(Objects.equals(dos1.getIdentifier(), dos2.getIdentifier()));
        assertFalse("Changes should not be allowed", dos1.changesAllowed(dos2));
    }

    private Dossier sampleOpenDossier() {
        Dossier dos = new Dossier(PaymentMethod.ADVANCE_PAYMENT, true, 1);
        Document d1 = new Document(DocumentType.ORDER, Document.Directive.NONE, null);
        dos.add(d1);
        return dos;
    }

    private Dossier sampleClosedDossier() {
        Dossier dos = new Dossier(PaymentMethod.ADVANCE_PAYMENT, true, 1);
        Document d1 = new Document(DocumentType.ORDER, Document.Directive.NONE, null);
        d1.setClosed(true);
        dos.add(d1);
        dos.setClosed(true);
        return dos;
    }

    @Test
    public void testChangesAllowedOpenDossierWithClosedInvoice() {
        Dossier dos1 = sampleOpenDossierWithClosedInvoice();
        Dossier dos2 = sampleOpenDossierWithClosedInvoice();
        dos2.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        assertFalse(dos1.getPaymentMethod() == dos2.getPaymentMethod());
        assertFalse(dos1.isClosed());
        assertFalse(dos2.isClosed());
        assertFalse("Changes should not be allowed", dos1.changesAllowed(dos2));
    }

    @Test
    public void testGetRelevantPositionsCapitalAsset() {
        final int UNIQUE_UNIT_ID = 1;
        Dossier dos1 = new Dossier();
        Document dos1CapitalAsset = new Document(DocumentType.CAPITAL_ASSET, Document.Directive.HAND_OVER_GOODS, null);
        dos1CapitalAsset.setActive(true);
        Position p1 = new Position();
        p1.setUniqueUnitId(UNIQUE_UNIT_ID);
        p1.setType(PositionType.UNIT);
        Position p2 = new Position();
        p2.setType(PositionType.COMMENT);
        dos1CapitalAsset.appendAll(p1, p2);
        dos1.add(dos1CapitalAsset);
        assertEquals(1, dos1.getRelevantUniqueUnitIds().size());
        assertEquals(UNIQUE_UNIT_ID, (int)dos1.getRelevantUniqueUnitIds().iterator().next());
        dos1CapitalAsset.add(Document.Condition.CANCELED);
        assertTrue("Should have no relevant UniqueUnit ids, but there are:  " + dos1.getRelevantUniqueUnitIds(), dos1.getRelevantUniqueUnitIds().isEmpty());
    }

    @Test
    public void testGetRelevantPositionsCapitalReturns() {
        final int UNIQUE_UNIT_ID = 1;
        Dossier dos1 = new Dossier();
        Document dos1CapitalAsset = new Document(DocumentType.RETURNS, Document.Directive.HAND_OVER_GOODS, null);
        dos1CapitalAsset.setActive(true);
        Position p1 = new Position();
        p1.setUniqueUnitId(UNIQUE_UNIT_ID);
        p1.setType(PositionType.UNIT);
        Position p2 = new Position();
        p2.setType(PositionType.COMMENT);
        dos1CapitalAsset.appendAll(p1, p2);
        dos1.add(dos1CapitalAsset);
        assertEquals(1, dos1.getRelevantUniqueUnitIds().size());
        assertEquals(UNIQUE_UNIT_ID, (int)dos1.getRelevantUniqueUnitIds().iterator().next());
        dos1CapitalAsset.add(Document.Condition.CANCELED);
        assertTrue("Should have no relevant UniqueUnit ids, but there are:  " + dos1.getRelevantUniqueUnitIds(), dos1.getRelevantUniqueUnitIds().isEmpty());
    }

    @Test
    public void testGetRelevantPositions() {
        Dossier dos1 = new Dossier();
        Document dos1Order = new Document(DocumentType.ORDER, Document.Directive.CREATE_INVOICE, null);
        dos1Order.setActive(true);
        Position p1 = new Position();
        p1.setUniqueUnitId(1);
        p1.setType(PositionType.UNIT);
        Position p2 = new Position();
        p2.setType(PositionType.COMMENT);
        dos1Order.appendAll(p1, p2);
        dos1.add(dos1Order);
        assertEquals(1, dos1.getRelevantUniqueUnitIds().size());

        Position p3 = new Position();
        p3.setUniqueUnitId(2);
        p3.setType(PositionType.UNIT);
        dos1Order.append(p3);
        Document dos1Invoice = dos1Order.partialClone();
        dos1Invoice.setActive(true);
        dos1Invoice.setType(DocumentType.INVOICE);
        dos1.add(dos1Invoice);
        assertTrue(dos1.getRelevantUniqueUnitIds().containsAll(Arrays.asList(new Integer[]{1, 2})));

        Document dos1CreditMemo = new Document(DocumentType.CREDIT_MEMO, Document.Directive.BALANCE_REPAYMENT, null);
        dos1CreditMemo.setActive(true);
        dos1CreditMemo.append(p3.partialClone());
        dos1.add(dos1CreditMemo);
        assertEquals("Size should be 1", 1, dos1.getRelevantUniqueUnitIds().size());
        assertTrue("UniqueUnitId 1 should be in the list", dos1.getRelevantUniqueUnitIds().contains(1));

        Dossier dos2 = new Dossier();
        Document blocker = new Document(DocumentType.BLOCK, Document.Directive.NONE, null);
        blocker.setActive(true);
        Position pb1 = new Position();
        pb1.setUniqueUnitId(1);
        pb1.setType(PositionType.UNIT);
        Position pb2 = new Position();
        pb2.setUniqueUnitId(2);
        pb2.setType(PositionType.UNIT);
        Position pb3 = new Position();
        pb3.setUniqueUnitId(3);
        pb3.setType(PositionType.UNIT);
        Position pb4 = new Position();
        pb4.setUniqueUnitId(4);
        pb4.setType(PositionType.UNIT);
        Position pb5 = new Position();
        pb5.setUniqueUnitId(5);
        pb5.setType(PositionType.UNIT);
        blocker.appendAll(pb1, pb2, pb3, pb4, pb5);
        dos2.add(blocker);
        assertTrue("UnitId 1,2,3,4,5 should be in the list", dos2.getRelevantUniqueUnitIds().containsAll(Arrays.asList(new Integer[]{1, 2, 3, 4, 5})));
        pb5.setType(PositionType.SERVICE);
        assertTrue("UnitId 1,2,3,4 should be in the list", dos2.getRelevantUniqueUnitIds().containsAll(Arrays.asList(new Integer[]{1, 2, 3, 4})));
    }

    private Dossier sampleOpenDossierWithClosedInvoice() {
        Dossier dos = new Dossier(PaymentMethod.ADVANCE_PAYMENT, true, 1);
        Document d1 = new Document(DocumentType.INVOICE, Document.Directive.NONE, null);
        d1.setActive(true);
        d1.setClosed(true);
        dos.add(d1);
        return dos;
    }
}
