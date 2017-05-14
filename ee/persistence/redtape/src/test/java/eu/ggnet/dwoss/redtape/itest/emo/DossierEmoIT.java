package eu.ggnet.dwoss.redtape.itest.emo;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.DossierEao;
import eu.ggnet.dwoss.redtape.emo.DossierEmo;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.redtape.itest.ArquillianProjectArchive;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PositionType;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
@RunWith(Arquillian.class)
public class DossierEmoIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testRequest() throws Exception {
        DossierEmo dossierEmo = new DossierEmo(em);
        utx.begin();
        em.joinTransaction();
        Document document = dossierEmo.requestActiveDocumentBlock(1, "Addresse", "Comment", "Test");
        assertNotNull(document);
        Dossier dossier = document.getDossier();
        assertTrue(dossier.getId() > 0);
        assertNotNull(dossier.getActiveDocuments());
        assertFalse(dossier.getActiveDocuments().isEmpty());
        utx.commit();
        utx.begin();
        em.joinTransaction();
        Document document2 = dossierEmo.requestActiveDocumentBlock(1, "Addresse", "Comment", "Test");
        assertNotNull(document2);
        Dossier dossier2 = document2.getDossier();
        assertEquals(dossier.getId(), dossier2.getId());
        utx.commit();
    }

    @Test
    public void testRemoveHistory() throws Exception {
        DossierEmo dossierEmo = new DossierEmo(em);
        utx.begin();
        em.joinTransaction();
        Document last = dossierEmo.requestActiveDocumentBlock(2, "Addresse Zwei", "Comment", "Test");
        last.setActive(false);
        Dossier dossier = last.getDossier();
        for (int i = 0; i < 20; i++) {
            Document d = new Document(DocumentType.BLOCK, Document.Directive.NONE, new DocumentHistory("JUnit", "JUnit"));
            d.append(new PositionBuilder().setType(PositionType.COMMENT).setName("JUnit").setDescription("JUnit").createPosition());
            d.setActive(false);
            d.setInvoiceAddress(last.getInvoiceAddress());
            d.setShippingAddress(last.getShippingAddress());
            d.setPredecessor(last);
            dossier.add(d);
            em.persist(d);
            last = d;
        }
        last.setActive(true);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        dossier = new DossierEao(em).findById(dossier.getId());
        assertTrue(dossier.getDocuments().size() > 10);
        dossierEmo.removeHistoryFromBlock(dossier.getId());

        utx.commit();
        utx.begin();
        em.joinTransaction();

        dossier = new DossierEao(em).findById(dossier.getId());
        assertEquals(1, dossier.getDocuments().size());
        utx.commit();
    }
}
