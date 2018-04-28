package eu.ggnet.dwoss.redtape.test.util;

import java.text.*;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.ee.entity.util.DocumentEquals;
import eu.ggnet.dwoss.common.api.values.DocumentType;
import eu.ggnet.dwoss.common.api.values.PositionType;

import static eu.ggnet.dwoss.redtape.ee.entity.util.DocumentEquals.Property.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author pascal.perau
 */
public class DocumentEqualsTest {

    private final static DocumentEquals EQ = new DocumentEquals()
                .ignore(ID, ACTIVE, HISTORY, PREDECESSOR, DIRECTIVE, FLAGS)
                .ignoreAddresses()
                .igonrePositionOrder()
                .ignorePositions(PositionType.COMMENT);


    @Test
    public void testActualWithDifferentTime() throws ParseException {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy - HH");
        Date d1 = df.parse("01.01.2011 - 01");
        Date d2 = df.parse("01.01.2011 - 02");
        assertFalse(d1.equals(d2));
        assertTrue(DateUtils.isSameDay(d1, d2));

        Document doc1 = sampleClosedDocument();
        Document doc2 = doc1.partialClone();
        doc2.setDossier(doc1.getDossier());
        assertTrue(EQ.equals(doc1, doc2));
        // Setting a day equals Date.
        doc1.setActual(d1);
        doc2.setActual(d2);
        assertTrue(EQ.equals(doc1, doc2));
    }

    @Test
    public void testChangesAllowedOk() {
        Document doc1 = sampleClosedDocument();
        Document doc2 = doc1.partialClone();
        doc2.setDossier(doc1.getDossier());

        assertTrue("The following Documents should allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));
        doc2.setInvoiceAddress(new Address("MUHHHHHH"));
        assertTrue("The following Documents should allow changes:\n" + doc1 + "\n" + doc2,EQ.equals(doc1,doc2));

        doc2.add(Document.Flag.CUSTOMER_BRIEFED);
        assertTrue("The following Documents should allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));

        doc2.append(Position.builder().type(PositionType.COMMENT).build());
        assertTrue("The following Documents should allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));
    }

    @Test
    public void testChangesAllowedPosition() {
        Document doc1 = sampleClosedDocument();
        Document doc2 = doc1.partialClone();
        doc2.setDossier(doc1.getDossier());

        doc2.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        assertFalse("The following Documents should not allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));
    }

    @Test
    public void testChangesAllowedConditions() {
        Document doc1 = sampleClosedDocument();
        Document doc2 = doc1.partialClone();
        doc2.setDossier(doc1.getDossier());

        doc2.add(Document.Condition.PAID);
        assertFalse("The following Documents should not allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));
    }

    @Test
    public void testChangesAllowedDossier() {
        Document doc1 = sampleClosedDocument();
        Document doc2 = doc1.partialClone();

        assertFalse("The following Documents should not allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));
    }

    @Test
    public void testChangesAllowedType() {
        Document doc1 = sampleClosedDocument();
        Document doc2 = doc1.partialClone();
        doc2.setDossier(doc1.getDossier());

        doc2.setType(DocumentType.INVOICE);
        assertFalse("The following Documents should not allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));
    }

    @Test
    public void testChangesAllowedIdentifier() {
        Document doc1 = sampleClosedDocument();
        Document doc2 = doc1.partialClone();
        doc2.setDossier(doc1.getDossier());

        doc2.setIdentifier("MUHAAAA");
        assertFalse("The following Documents should not allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));
    }

    @Test
    public void testChangesAllowedClosed() {
        Document doc1 = sampleClosedDocument();
        Document doc2 = doc1.partialClone();
        doc2.setDossier(doc1.getDossier());

        doc2.setClosed(false);
        assertFalse("The following Documents should not allow changes:\n" + doc1 + "\n" + doc2, EQ.equals(doc1,doc2));
    }

    private Document sampleClosedDocument() {
        Dossier dos = new Dossier();

        Address a1 = new Address("ShippingAddress");
        Address a2 = new Address("InvoiceAddress");

        Document doc1 = new Document();
        doc1.setShippingAddress(a1);
        doc1.setInvoiceAddress(a2);
        doc1.setType(DocumentType.ORDER);
        doc1.add(Document.Flag.CUSTOMER_BRIEFED);
        doc1.setClosed(true);
        dos.add(doc1);
        doc1.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        doc1.append(Position.builder().amount(1).type(PositionType.PRODUCT_BATCH).build());
        doc1.append(Position.builder().amount(1).type(PositionType.COMMENT).build());
        return doc1;
    }

}
