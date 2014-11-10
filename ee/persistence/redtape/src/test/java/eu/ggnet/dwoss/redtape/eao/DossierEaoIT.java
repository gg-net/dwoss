package eu.ggnet.dwoss.redtape.eao;

import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.eao.DossierEao;
import eu.ggnet.dwoss.redtape.entity.Document;

import java.util.Arrays;
import java.util.List;

import javax.persistence.*;

import org.junit.Test;

import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.emo.AddressEmo;

import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.PaymentMethod;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class DossierEaoIT {

    @Test
    public void testFindByIds() throws InterruptedException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(RedTapePu.NAME, RedTapePu.JPA_IN_MEMORY);
        EntityManager em = emf.createEntityManager();

        AddressEmo adEmo = new AddressEmo(em);

        em.getTransaction().begin();

        Dossier dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        dos.setCustomerId(1);
        Document doc = new Document();
        doc.setType(DocumentType.ORDER);
        doc.setActive(true);
        doc.setDirective(Document.Directive.NONE);
        doc.setHistory(new DocumentHistory("JUnit", "Automatische Erstellung eines leeren Dokuments"));

        doc.setInvoiceAddress(adEmo.request("Bla Bla"));
        doc.setShippingAddress(adEmo.request("Auch"));
        dos.add(doc);

        em.persist(dos);

        dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        dos.setCustomerId(1);
        doc = new Document();
        doc.setType(DocumentType.ORDER);
        doc.setActive(true);
        doc.setDirective(Document.Directive.NONE);
        doc.setHistory(new DocumentHistory("JUnit", "Automatische Erstellung eines leeren Dokuments"));

        doc.setInvoiceAddress(adEmo.request("Bla Bla"));
        doc.setShippingAddress(adEmo.request("Auch"));
        dos.add(doc);

        em.persist(dos);

        dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        dos.setCustomerId(1);
        doc = new Document();
        doc.setType(DocumentType.ORDER);
        doc.setActive(true);
        doc.setDirective(Document.Directive.NONE);
        doc.setHistory(new DocumentHistory("JUnit", "Automatische Erstellung eines leeren Dokuments"));

        doc.setInvoiceAddress(adEmo.request("Bla Bla"));
        doc.setShippingAddress(adEmo.request("Auch"));
        dos.add(doc);

        em.persist(dos);
        em.getTransaction().commit();

        em.getTransaction().begin();

        List<Dossier> dossiers = new DossierEao(em).findByIds(Arrays.asList(1l, 3l));
        assertFalse("Dossiers should not be empty", dossiers.isEmpty());
        assertEquals(2, dossiers.size());
    }

    @Test
    public void testFindByClosed() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(RedTapePu.NAME, RedTapePu.JPA_IN_MEMORY);
        EntityManager em = emf.createEntityManager();

        AddressEmo adEmo = new AddressEmo(em);

        em.getTransaction().begin();

        Dossier dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        dos.setCustomerId(1);
        Document doc = new Document();
        doc.setType(DocumentType.ORDER);
        doc.setActive(true);
        doc.setDirective(Document.Directive.NONE);
        doc.setHistory(new DocumentHistory("JUnit", "Automatische Erstellung eines leeren Dokuments"));

        doc.setInvoiceAddress(adEmo.request("Bla Bla"));
        doc.setShippingAddress(adEmo.request("Auch"));
        dos.add(doc);

        em.persist(dos);

        dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        dos.setCustomerId(1);
        doc = new Document();
        doc.setType(DocumentType.ORDER);
        doc.setActive(true);
        doc.setDirective(Document.Directive.NONE);
        doc.setHistory(new DocumentHistory("JUnit", "Automatische Erstellung eines leeren Dokuments"));

        doc.setInvoiceAddress(adEmo.request("Bla Bla"));
        doc.setShippingAddress(adEmo.request("Auch"));
        dos.add(doc);

        em.persist(dos);

        dos = new Dossier();
        dos.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        dos.setCustomerId(1);
        dos.setClosed(true);
        doc = new Document();
        doc.setType(DocumentType.ORDER);
        doc.setActive(true);
        doc.setDirective(Document.Directive.NONE);
        doc.setHistory(new DocumentHistory("JUnit", "Automatische Erstellung eines leeren Dokuments"));

        doc.setInvoiceAddress(adEmo.request("Bla Bla"));
        doc.setShippingAddress(adEmo.request("Auch"));
        doc.setClosed(true);
        dos.add(doc);

        em.persist(dos);
        em.getTransaction().commit();

        em.getTransaction().begin();

        List<Dossier> dossiers = new DossierEao(em).findByClosed(false);
        assertFalse("Dossiers should not be empty", dossiers.isEmpty());
        assertEquals(2, dossiers.size());
    }

}
