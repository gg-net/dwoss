package eu.ggnet.dwoss.redtape.itest.eao;

import eu.ggnet.dwoss.redtape.ee.entity.Address;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

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
import eu.ggnet.dwoss.redtape.itest.ArquillianProjectArchive;
import eu.ggnet.dwoss.rules.PaymentMethod;

import static eu.ggnet.dwoss.redtape.ee.entity.Document.Directive.NONE;
import static eu.ggnet.dwoss.rules.DocumentType.BLOCK;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class DocumentEaoTwoIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

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
