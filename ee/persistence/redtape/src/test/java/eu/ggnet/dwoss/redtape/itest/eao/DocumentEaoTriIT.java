package eu.ggnet.dwoss.redtape.itest.eao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.itest.ArquillianProjectArchive;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;

import static eu.ggnet.dwoss.redtape.entity.Document.Directive.NONE;
import static eu.ggnet.dwoss.rules.DocumentType.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class DocumentEaoTriIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

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
        doc = lastActiveNewChangeType(doc, INVOICE);
        doc = makeMore(doc, amountInvoice);
        doc.setClosed(closed);
        doc = lastActiveNewChangeType(doc, CREDIT_MEMO);
        doc = makeMore(doc, amountCreditMemo);
        doc.setClosed(closed);
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
