package eu.ggnet.dwoss.redtape.eao;

import eu.ggnet.dwoss.redtape.eao.DocumentEao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.entity.Address;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.entity.Dossier;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;

import static eu.ggnet.dwoss.redtape.entity.Document.Directive.*;
import static eu.ggnet.dwoss.rules.DocumentType.*;
import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class DocumentEaoIT {

    private EntityManager em;

    private EntityManagerFactory emf;

    private Address add;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(RedTapePu.NAME, RedTapePu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();
        add = new AddressEmo(em).request("A Test Address");
        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        add = null;
        if ( em != null && em.isOpen() ) em.close();
        if ( emf != null && emf.isOpen() ) emf.close();
    }

    @Test
    public void testfindOpenByType() throws InterruptedException {
        em.getTransaction().begin();

        makeAnAmountOfDocuments(20, 5, 3, true);
        makeAnAmountOfDocuments(7, 2, 1, true);
        makeAnAmountOfDocuments(11, 3, 2, true);
        makeAnAmountOfDocuments(3, 3, 5, false);
        makeAnAmountOfDocuments(3, 8, 1, false);

        em.getTransaction().commit();

        em.getTransaction().begin();

        DocumentEao documentEao = new DocumentEao(em);
        List<Document> docs = documentEao.findCloseableCreditMemos();
        assertEquals("There should be open CreditMemos", 2, docs.size());
        em.getTransaction().commit();
    }

    @Test
    public void testFindDocumentsBetweenDates() {
        em.getTransaction().begin();
        makeAnAmountOfDocuments(3, 5, 2, true);
        makeAnAmountOfDocuments(3, 5, 2, true);
        makeAnAmountOfDocuments(3, 5, 2, true);
        em.getTransaction().commit();

        em.getTransaction().begin();
        DocumentEao documentEao = new DocumentEao(em);
        List<Document> docs = documentEao.findDocumentsBetweenDates(new Date(1234567891), new Date(), INVOICE, CREDIT_MEMO, ANNULATION_INVOICE);
        assertTrue("Number of Documents should be 6", docs.size() == 6);
        em.getTransaction().commit();

    }

    @Test
    public void testFindActiveAndOpenByCustomerId() {
        int customerId = 5;
        em.getTransaction().begin();
        makeAnAmountOfBlocks(4, customerId);
        makeAnAmountOfBlocks(4, customerId);
        makeAnAmountOfBlocks(4, customerId);
        makeAnAmountOfBlocks(4, customerId);
        em.getTransaction().commit();

        em.getTransaction().begin();
        DocumentEao eao = new DocumentEao(em);
        Document doc = eao.findActiveAndOpenByCustomerId(BLOCK, customerId);
        assertNotNull(doc);
        assertEquals(4,doc.getDossier().getId());
        assertEquals(20,doc.getId());
        em.getTransaction().commit();
    }

    private void makeAnAmountOfDocuments(int amountOrder, int amountInvoice, int amountCreditMemo, boolean closed) {
        Dossier dos = new Dossier(PaymentMethod.ADVANCE_PAYMENT, true, 1);
        Document doc = new Document(ORDER, NONE, new DocumentHistory("JUnit", "A History"));
        doc.setInvoiceAddress(add);
        doc.setShippingAddress(add);
        dos.add(doc);

        em.persist(dos);
        dos.setIdentifier("DW" + dos.getId());

        doc = makeMore(doc, amountOrder);
        doc = lastActiveNewChangeType(doc, INVOICE);
        doc = makeMore(doc, amountInvoice);
        doc.setClosed(closed);
        doc = lastActiveNewChangeType(doc, CREDIT_MEMO);
        doc = makeMore(doc, amountCreditMemo);
        doc.setClosed(closed);
        doc.setActive(true);
    }

    private void makeAnAmountOfBlocks(int amount, int customerId) {
        Dossier dos = new Dossier(PaymentMethod.INVOICE, false, customerId);
        Document doc = new Document(BLOCK, NONE, new DocumentHistory("JUnit", "A History"));
        doc.setInvoiceAddress(add);
        doc.setShippingAddress(add);
        dos.add(doc);
        em.persist(dos);
        doc = makeMore(doc, amount);
        doc.setActive(true);
    }

    private Document makeMore(Document init, int amount) {
        Document last = init;
        for (int i = 0; i < amount; i++) {
            Document doc = last.partialClone();
            doc.setPredecessor(last);
            doc.setDossier(last.getDossier());
            doc.setHistory(new DocumentHistory("Junit", "History"));
            em.persist(doc);
            last = doc;
        }
        return last;
    }

    private Document lastActiveNewChangeType(Document last, DocumentType type) {
        last.setActive(true);
        Document result = last.partialClone();
        result.setPredecessor(last);
        result.setDossier(last.getDossier());
        result.setType(type);
        result.setHistory(new DocumentHistory("Junit", "History"));
        em.persist(result);
        return result;
    }
}
