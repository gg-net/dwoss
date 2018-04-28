package eu.ggnet.dwoss.redtape.itest.eao;

import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

import java.util.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.redtape.ee.emo.AddressEmo;
import eu.ggnet.dwoss.redtape.itest.ArquillianProjectArchive;
import eu.ggnet.dwoss.common.api.values.DocumentType;
import eu.ggnet.dwoss.common.api.values.PaymentMethod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class DossierEaoFindByIdIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void testFindByIds() throws Exception {

        AddressEmo adEmo = new AddressEmo(em);

        List<Long> ids = new ArrayList<>();

        utx.begin();
        em.joinTransaction();

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
        ids.add(dos.getId());

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
        ids.add(dos.getId());

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
        ids.add(dos.getId());
        utx.commit();
        utx.begin();
        em.joinTransaction();

        List<Dossier> dossiers = new DossierEao(em).findByIds(Arrays.asList(ids.get(0), ids.get(1)));
        assertFalse("Dossiers should not be empty", dossiers.isEmpty());
        assertEquals(2, dossiers.size());

        utx.commit();
    }

}
