package eu.ggnet.dwoss.redtape.itest;

import eu.ggnet.dwoss.redtape.ee.entity.Address;
import eu.ggnet.dwoss.redtape.ee.entity.Reminder;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.entity.PositionBuilder;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;
import eu.ggnet.dwoss.redtape.ee.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.ee.entity.Document;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;
import eu.ggnet.dwoss.redtape.ee.eao.PositionEao;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.core.common.values.PositionType;

/**
 *
 * @author oliver.guenther
 */
@RunWith(Arquillian.class)
public class PersistenceIT extends ArquillianProjectArchive {

    @Inject
    @RedTapes
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    public void example() throws Exception {
        utx.begin();
        em.joinTransaction();

        Address addressOne = new Address("Test Straße 10, 123456 Testing");
        em.persist(addressOne);

        Dossier dos = new Dossier();
        dos.setComment("Ein Kommentar");
        dos.setCustomerId(10);
        dos.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);

        Document doc = new Document();
        doc.add(Condition.PAID);
        doc.setActive(true);
        doc.setInvoiceAddress(addressOne);
        doc.setShippingAddress(addressOne);
        doc.setHistory(new DocumentHistory("Nutzer", "Bemerkung"));

        Position p = new PositionBuilder().type(PositionType.COMMENT).description("Comment").name("Comment").build();
        doc.append(p);
        doc.setDirective(Document.Directive.PREPARE_SHIPPING);
        dos.add(doc);

        em.persist(dos);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        Address addressTwo = new Address("Persistencstraße 42, 1337 PersistencTest");
        em.persist(addressTwo);

        addressOne = em.merge(addressOne);

        Dossier dossier = new Dossier();
        dossier.setComment("Das ist nun ein Weitere Kommentar");
        dossier.setCustomerId(12);
        dossier.setReminder(new Reminder(new Date(), new Date(), "Junit"));

        Document document = new Document();
        document.setActive(true);
        document.add(Condition.PAID);
        document.add(Condition.CONFIRMED);
        document.setInvoiceAddress(addressTwo);
        document.setShippingAddress(addressOne);
        document.setHistory(new DocumentHistory("Nutzer", "Bemerkung"));

        Position p1 = new PositionBuilder().type(PositionType.COMMENT).description("Kommentare über Kommentare").name("Kommentar").build();
        Position p2 = new PositionBuilder().type(PositionType.COMMENT).description("Noch mehr Kommentare").name("Kommentar").build();
        Position p3 = new PositionBuilder().type(PositionType.COMMENT).description("So das war aber der Letzte").name("Kommentar").build();
        document.append(p1);
        document.append(p2);
        document.append(p3);

        Document document1 = new Document();
        document1.setActive(false);
        document1.setInvoiceAddress(addressTwo);
        document1.setShippingAddress(addressOne);
        document1.setHistory(new DocumentHistory("Nutzer", "Bemerkung"));

        Position p4 = new PositionBuilder().type(PositionType.COMMENT).description("Ein zweites Doc mit Kommentaren").name("Kommentar").build();
        Position p5 = new PositionBuilder().type(PositionType.COMMENT).description("Was sind das für Kommentare").name("Kommentar").build();
        Position p6 = new PositionBuilder().type(PositionType.COMMENT).description("comment,comment.comment").name("Kommentar").build();
        document1.append(p4);
        document1.append(p5);
        document1.append(p6);
        document1.setDirective(Document.Directive.NONE);

        dossier.add(document1);

        em.persist(dossier);

        utx.commit();
        utx.begin();
        em.joinTransaction();

        CriteriaQuery<Dossier> q = em.getCriteriaBuilder().createQuery(Dossier.class);

        List<Dossier> dossiers = em.createQuery(q.select(q.from(Dossier.class))).getResultList();

        PositionEao positionEao = new PositionEao(em);
        Logger L = LoggerFactory.getLogger(PersistenceIT.class);

        for (Dossier ds : dossiers) {
            L.info(ds.toMultiLine());
        }
        utx.commit();
    }

}
