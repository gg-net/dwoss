package eu.ggnet.dwoss.redtape.itest.eao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.ee.entity.*;
import eu.ggnet.dwoss.redtape.itest.ArquillianProjectArchive;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;

import static eu.ggnet.dwoss.redtape.ee.entity.Document.Directive.NONE;
import static eu.ggnet.dwoss.rules.DocumentType.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class DocumentEaoOneIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

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
