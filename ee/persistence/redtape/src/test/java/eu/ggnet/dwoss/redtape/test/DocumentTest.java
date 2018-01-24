package eu.ggnet.dwoss.redtape.test;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.rules.*;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class DocumentTest {

    @Test
    public void testAppend() {
        Document doc = new Document();

        assertEquals("Document.posistion.size", 0, doc.getPositions().size());

        Position p1 = doc.append(Position.builder().amount(1).type(PositionType.UNIT).build());

        assertEquals("Document.posistion.size", 1, doc.getPositions().size());
        assertEquals(p1, doc.getPosition(p1.getId()));

        doc.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        doc.append(Position.builder().amount(1).type(PositionType.UNIT).build());

        assertEquals("Document.posistion.size", 3, doc.getPositions().size());
        assertEquals("Postions order", Arrays.asList(1, 2, 3), new ArrayList<>(doc.getPositions().keySet()));
    }

    @Test
    public void testRemove() {
        Document doc = new Document();
        Position p1 = doc.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        assertEquals("Document.posistion.size", 1, doc.getPositions().size());

        doc.remove(p1);
        assertEquals("Document.posistion.size", 0, doc.getPositions().size());

        p1 = doc.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        doc.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        Position p3 = doc.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        doc.append(Position.builder().amount(1).type(PositionType.UNIT).build());

        assertEquals("Document.posistion.size", 4, doc.getPositions().size());
        assertEquals("Postions order", Arrays.asList(1, 2, 3, 4), new ArrayList<>(doc.getPositions().keySet()));

        doc.remove(p1);
        assertEquals("Document.posistion.size", 3, doc.getPositions().size());
        assertEquals("Postions order", Arrays.asList(1, 2, 3), new ArrayList<>(doc.getPositions().keySet()));
        assertEquals("Position p3 should have id 2", 2, p3.getId());
    }

    @Test
    public void testPartialCloneAndEqualsContent() {

        Dossier dos = new Dossier();

        Address a1 = new Address("ShippingAddress");
        Address a2 = new Address("InvoiceAddress");
        Address a3 = new Address("Another ShippingAddress");
        Address a4 = new Address("Another InvoiceAddress");

        Document doc1 = new Document();
        doc1.setShippingAddress(a1);
        doc1.setInvoiceAddress(a2);
        doc1.setType(DocumentType.ORDER);
        doc1.add(Document.Flag.CUSTOMER_BRIEFED);
        dos.add(doc1);
        doc1.append(Position.builder().amount(1).type(PositionType.UNIT).build());

        //copy and test equality
        Document doc2 = doc1.partialClone();
        doc2.setDossier(dos);

        assertTrue("The following Documents are not equal:\n" + doc1 + "\n" + doc2, doc1.equalsContent(doc2));

        //add and remove positions with equality test
        Position p1 = doc2.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        Position p2 = doc2.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        assertFalse("Should not be equals, but is.\n- " + doc1 + "\n- " + doc2,doc1.equalsContent(doc2));

        doc2.remove(p1);
        doc2.remove(p2);
        assertTrue(doc1.equalsContent(doc2));

        //add and remove flags with equality test
        doc2.setClosed(true);
        assertFalse(doc1.equalsContent(doc2));
        doc2.setClosed(false);
        assertTrue(doc1.equalsContent(doc2));

        //change Document type
        doc2.setType(DocumentType.INVOICE);
        assertFalse(doc1.equalsContent(doc2));
        doc2.setType(DocumentType.ORDER);
        assertTrue(doc1.equalsContent(doc2));

        //change Tax Type
        doc2.setTaxType(TaxType.UNTAXED);
        assertFalse(doc1.equalsContent(doc2));
        doc2.setTaxType(TaxType.GENERAL_SALES_TAX_DE_SINCE_2007);
        assertTrue(doc1.equalsContent(doc2));

        //change addresses
        doc2.setShippingAddress(a3);
        doc2.setInvoiceAddress(a4);
        assertFalse(doc2.equalsContent(doc1));
        doc2.setShippingAddress(a1);
        doc2.setInvoiceAddress(a2);
        assertTrue(doc2.equalsContent(doc1));
    }

    @Test
    public void testGetPositionsByType() {
        Document doc1 = new Document();
        doc1.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        doc1.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        doc1.append(Position.builder().amount(1).type(PositionType.UNIT).build());
        doc1.append(Position.builder().amount(1).type(PositionType.COMMENT).build());
        doc1.append(Position.builder().amount(1).type(PositionType.COMMENT).build());
        doc1.append(Position.builder().amount(1).type(PositionType.SHIPPING_COST).build());

        assertEquals("Service Positions", 0, doc1.getPositions(PositionType.SERVICE).size());
        assertEquals("Service Positions", 1, doc1.getPositions(PositionType.SHIPPING_COST).size());
        assertEquals("Service Positions", 2, doc1.getPositions(PositionType.COMMENT).size());
        assertEquals("Service Positions", 3, doc1.getPositions(PositionType.UNIT).size());
    }

}
