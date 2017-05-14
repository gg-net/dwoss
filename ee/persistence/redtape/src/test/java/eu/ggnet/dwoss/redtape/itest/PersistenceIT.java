package eu.ggnet.dwoss.redtape.itest;

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

import eu.ggnet.dwoss.redtape.assist.RedTapes;
import eu.ggnet.dwoss.redtape.eao.PositionEao;
import eu.ggnet.dwoss.redtape.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.entity.*;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.PositionType;

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

        Position p = new PositionBuilder().setType(PositionType.COMMENT).setDescription("Comment").setName("Comment").createPosition();
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

        Position p1 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("Kommentare über Kommentare").setName("Kommentar").createPosition();
        Position p2 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("Noch mehr Kommentare").setName("Kommentar").createPosition();
        Position p3 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("So das war aber der Letzte").setName("Kommentar").createPosition();
        document.append(p1);
        document.append(p2);
        document.append(p3);

        Document document1 = new Document();
        document1.setActive(false);
        document1.setInvoiceAddress(addressTwo);
        document1.setShippingAddress(addressOne);
        document1.setHistory(new DocumentHistory("Nutzer", "Bemerkung"));

        Position p4 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("Ein zweites Doc mit Kommentaren").setName("Kommentar").createPosition();
        Position p5 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("Was sind das für Kommentare").setName("Kommentar").createPosition();
        Position p6 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("comment,comment.comment").setName("Kommentar").createPosition();
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
