package eu.ggnet.dwoss.redtape.emo;

import eu.ggnet.dwoss.redtape.emo.DossierEmo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.tools.Diagnostic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.eao.DossierEao;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;

import eu.ggnet.dwoss.rules.PositionType;
import eu.ggnet.dwoss.rules.DocumentType;

import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
@Category(Diagnostic.class)
public class DossierEmoIT {

    private EntityManager em;

    private EntityManagerFactory emf;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(RedTapePu.NAME, RedTapePu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testRequest() {
        DossierEmo dossierEmo = new DossierEmo(em);
        em.getTransaction().begin();
        Document document = dossierEmo.requestActiveDocumentBlock(1, "Addresse", "Comment", "Test");
        assertNotNull(document);
        Dossier dossier = document.getDossier();
        assertTrue(dossier.getId() > 0);
        assertNotNull(dossier.getActiveDocuments());
        assertFalse(dossier.getActiveDocuments().isEmpty());
        em.getTransaction().commit();

        em.getTransaction().begin();
        Document document2 = dossierEmo.requestActiveDocumentBlock(1, "Addresse", "Comment", "Test");
        assertNotNull(document2);
        Dossier dossier2 = document2.getDossier();
        assertEquals(dossier.getId(),dossier2.getId());
        em.getTransaction().commit();
    }

    @Test
    public void testRemoveHistory() {
        DossierEmo dossierEmo = new DossierEmo(em);
        em.getTransaction().begin();
        Document last = dossierEmo.requestActiveDocumentBlock(1, "Addresse", "Comment", "Test");
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
        em.getTransaction().commit();

        em.getTransaction().begin();
        dossier = new DossierEao(em).findById(dossier.getId());
        assertTrue(dossier.getDocuments().size() > 10);
        dossierEmo.removeHistoryFromBlock(dossier.getId());
        em.getTransaction().commit();

        em.close();
        em = emf.createEntityManager();

        em.getTransaction().begin();
        dossier = new DossierEao(em).findById(dossier.getId());
        assertEquals(1,dossier.getDocuments().size());
        em.getTransaction().commit();
    }
}
