package eu.ggnet.dwoss.redtape.itest.eao;

import java.util.Date;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.itest.ArquillianProjectArchive;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.redtape.ee.assist.gen.RedTapeDeleteUtils;

import static eu.ggnet.dwoss.redtape.ee.entity.Document.Directive.BALANCE_REPAYMENT;
import static eu.ggnet.dwoss.redtape.ee.entity.Document.Directive.NONE;
import static eu.ggnet.dwoss.core.common.values.DocumentType.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class DocumentEaoIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @After
    public void clearDataBase() throws Exception {
        utx.begin();
        em.joinTransaction();
        RedTapeDeleteUtils.deleteAll(em);
        assertThat(RedTapeDeleteUtils.validateEmpty(em)).isNull();
        utx.commit();
    }

    @Test
    public void testfindOpenByType() throws Exception {
        utx.begin();
        em.joinTransaction();

        makeAnAmountOfDocuments(20, 5, 3, true);
        makeAnAmountOfDocuments(7, 2, 1, true);
        makeAnAmountOfDocuments(11, 3, 2, true);
        makeAnAmountOfDocuments(3, 3, 5, false);
        makeAnAmountOfDocuments(3, 8, 1, false);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        DocumentEao documentEao = new DocumentEao(em);
        List<Document> docs = documentEao.findCloseableCreditMemos();
        assertThat(docs).describedAs("Open CreditMemeos").hasSize(2);
        utx.commit();
    }

    @Test
    public void testfindInvoiceUnpaidAndfindUnBalancedAnulation() throws Exception {
        utx.begin();
        em.joinTransaction();

        Address address = new AddressEmo(em).request("A Test Address");
        Document doc = RedTapeHelper.makeOrderDossier(PaymentMethod.DIRECT_DEBIT, address);
        long customerId = doc.getDossier().getCustomerId();
        RedTapeHelper.addUnitServiceAndComment(doc);
        em.persist(doc);
        doc = RedTapeHelper.transitionTo(doc, INVOICE);
        doc.setActive(true);
        em.persist(doc);
        doc = RedTapeHelper.transitionTo(doc, ANNULATION_INVOICE);
        doc.setActive(true);
        doc.setDirective(BALANCE_REPAYMENT);
        em.persist(doc);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        DocumentEao documentEao = new DocumentEao(em);
        List<Document> docs = documentEao.findInvoiceUnpaid(PaymentMethod.DIRECT_DEBIT);
        assertThat(docs).as("Found InvoiceUnpaid").hasSize(1);

        docs = documentEao.findUnBalancedAnulation(customerId, PaymentMethod.DIRECT_DEBIT);
        assertThat(docs).as("Found UnBalancedAnulation").hasSize(1);
        utx.commit();
    }

    @Test
    public void testFindDocumentsBetweenDates() throws Exception {
        utx.begin();
        em.joinTransaction();

        makeAnAmountOfDocuments(3, 5, 2, true);
        makeAnAmountOfDocuments(3, 5, 2, true);
        makeAnAmountOfDocuments(3, 5, 2, true);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        DocumentEao documentEao = new DocumentEao(em);
        List<Document> docs = documentEao.findDocumentsBetweenDates(new Date(1234567891), new Date(), INVOICE, CREDIT_MEMO, ANNULATION_INVOICE);
        assertThat(docs).describedAs("All Documents in Database").hasSize(6);
        utx.commit();
    }

    private void makeAnAmountOfDocuments(int amountOrder, int amountInvoice, int amountCreditMemo, boolean closed) {
        Dossier dos = new Dossier(PaymentMethod.ADVANCE_PAYMENT, true, 1);
        Document doc = new Document(ORDER, NONE, new DocumentHistory("JUnit", "A History"));
        Address address = new AddressEmo(em).request("A Test Address");
        doc.setInvoiceAddress(address);
        doc.setShippingAddress(address);
        dos.add(doc);

        em.persist(dos);
        dos.setIdentifier("DW" + dos.getId());

        doc = makeMore(doc, amountOrder);
        doc = RedTapeHelper.transitionTo(doc, INVOICE);
        doc = makeMore(doc, amountInvoice);
        doc.setClosed(closed);
        doc = RedTapeHelper.transitionTo(doc, CREDIT_MEMO);
        doc = makeMore(doc, amountCreditMemo);
        doc.setClosed(closed);
        doc.setActive(true);
    }

    @Test
    public void testFindActiveAndOpenByCustomerId() throws Exception {
        int customerId = 5;

        utx.begin();
        em.joinTransaction();

        makeAnAmountOfBlocks(4, customerId);
        makeAnAmountOfBlocks(4, customerId);
        makeAnAmountOfBlocks(4, customerId);
        makeAnAmountOfBlocks(4, customerId);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        DocumentEao eao = new DocumentEao(em);
        Document doc = eao.findActiveAndOpenByCustomerId(BLOCK, customerId);
        List<Document> all = eao.findAll();
        Document last = all.get(all.size() - 1);

        assertThat(doc).describedAs("Document").isNotNull().isEqualTo(last);

        utx.commit();
    }

    private void makeAnAmountOfBlocks(int amount, int customerId) {
        Dossier dos = new Dossier(PaymentMethod.INVOICE, false, customerId);
        Document doc = new Document(BLOCK, NONE, new DocumentHistory("JUnit", "A History"));
        Address address = new AddressEmo(em).request("A Test Address");
        doc.setInvoiceAddress(address);
        doc.setShippingAddress(address);
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
}
