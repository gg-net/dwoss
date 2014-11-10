package eu.ggnet.dwoss.redtape;

import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Position;
import eu.ggnet.dwoss.redtape.entity.PositionBuilder;
import eu.ggnet.dwoss.redtape.entity.DocumentHistory;
import eu.ggnet.dwoss.redtape.entity.Address;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.SalesProduct;
import eu.ggnet.dwoss.redtape.entity.Reminder;

import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.*;

import eu.ggnet.dwoss.redtape.assist.RedTapePu;
import eu.ggnet.dwoss.redtape.eao.SalesProductEao;
import eu.ggnet.dwoss.redtape.entity.Document.Condition;

import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.rules.PositionType;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class PersistenceIT {

    @Test
    public void example() throws InterruptedException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(RedTapePu.NAME, RedTapePu.JPA_IN_MEMORY);
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        Address adresses = new Address("Test Straße 10, 123456 Testing");
        em.persist(adresses);

        Dossier dos = new Dossier();
        dos.setComment("Ein Kommentar");
        dos.setCustomerId(10);
        dos.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);

        Document doc = new Document();
        doc.add(Condition.PAID);
        doc.setActive(true);
        doc.setInvoiceAddress(adresses);
        doc.setShippingAddress(adresses);
        doc.setHistory(new DocumentHistory("Nutzer", "Bemerkung"));

        Position p = new PositionBuilder().setType(PositionType.COMMENT).setDescription("Comment").setName("Comment").createPosition();
        doc.append(p);
        doc.setDirective(Document.Directive.PREPARE_SHIPPING);
        dos.add(doc);

        em.persist(dos);
        em.getTransaction().commit();

        em.getTransaction().begin();

        Address address = new Address("Persistencstraße 42, 1337 PersistencTest");
        em.persist(address);

        Dossier dossier = new Dossier();
        dossier.setComment("Das ist nun ein Weitere Kommentar");
        dossier.setCustomerId(12);
        dossier.setReminder(new Reminder(new Date(), new Date(), "Junit"));

        Document document = new Document();
        document.setActive(true);
        document.add(Condition.PAID);
        document.add(Condition.CONFIRMED);
        document.setInvoiceAddress(address);
        document.setShippingAddress(adresses);
        document.setHistory(new DocumentHistory("Nutzer", "Bemerkung"));

        Position p1 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("Kommentare über Kommentare").setName("Kommentar").createPosition();
        Position p2 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("Noch mehr Kommentare").setName("Kommentar").createPosition();
        Position p3 = new PositionBuilder().setType(PositionType.COMMENT).setDescription("So das war aber der Letzte").setName("Kommentar").createPosition();
        document.append(p1);
        document.append(p2);
        document.append(p3);

        Document document1 = new Document();
        document1.setActive(false);
        document1.setInvoiceAddress(address);
        document1.setShippingAddress(adresses);
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
        em.getTransaction().commit();

        em.getTransaction().begin();
        CriteriaQuery<Dossier> q = em.getCriteriaBuilder().createQuery(Dossier.class);

        List<Dossier> dossiers = em.createQuery(q.select(q.from(Dossier.class))).getResultList();

//        PositionEao positionEao = new PositionEao(em);
//        for (Dossier ds : dossiers) {
//            System.out.println(ds);
//            for (Document dc : ds.getDocuments()) {
//                System.out.println("  " + dc);
//                for (Position ps : dc.getPositions().values()) {
//                    System.out.println("    " + ps);
//                }
//                System.out.println("   - count=" + positionEao.countByDocumentId(dc.getId()));
//                System.out.println("   - countNative=" + positionEao.countNativByDocumentId(dc.getId()));
//            }
//        }
        em.getTransaction().commit();
    }

    @Test
    public void testSalesProduct() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(RedTapePu.NAME, RedTapePu.JPA_IN_MEMORY);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        SalesProduct product1 = new SalesProduct("This.IsPart.One", "Part One", 12d, 1, "This is a description");
        SalesProduct product2 = new SalesProduct("This.IsPart.Two", "Part Two", 12d, 2, "This is a descriptionThis is a description");
        SalesProduct product3 = new SalesProduct("This.IsPart.Three", "Part Three", 12d, 3, "This is a descriptionThis is a descriptionThis is a description");
        SalesProduct product4 = new SalesProduct("This.IsPart.Four", "Part Four", 12d, 4, "This is a descriptionThis is a descriptionThis is a descriptionThis is a descriptionThis is a description");
        em.persist(product1);
        em.persist(product2);
        em.persist(product3);
        em.persist(product4);
        em.getTransaction().commit();

        em.getTransaction().begin();

        SalesProductEao eao = new SalesProductEao(emf.createEntityManager());

        SalesProduct findByPartNo = eao.findById("This.IsPart.One");
        assertEquals("FindbyPartNo returns a other Object as expected!", findByPartNo, product1);
        SalesProduct findByUniqueUnitProductId = eao.findByUniqueUnitProductId(3);
        assertEquals("Die Objekte der Methode findByUniqueUnitProductId waren nicht gleich", product3, findByUniqueUnitProductId);
        em.getTransaction().commit();
    }
}
