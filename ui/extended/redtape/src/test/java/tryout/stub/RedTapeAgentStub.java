package tryout.stub;

import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.redtape.ee.entity.Document;
import eu.ggnet.dwoss.redtape.ee.entity.SalesProduct;
import eu.ggnet.dwoss.redtape.ee.entity.Position;
import eu.ggnet.dwoss.redtape.ee.entity.Dossier;

import java.util.*;

import javax.persistence.LockModeType;

import eu.ggnet.dwoss.configuration.GlobalConfig;
import eu.ggnet.dwoss.redtape.ee.RedTapeAgent;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Condition;
import eu.ggnet.dwoss.redtape.ee.entity.Document.Directive;
import eu.ggnet.dwoss.rules.*;

import static eu.ggnet.dwoss.rules.PositionType.PRODUCT_BATCH;
import static eu.ggnet.dwoss.rules.PositionType.SERVICE;
import static eu.ggnet.dwoss.rules.TaxType.REVERSE_CHARGE;

/**
 *
 * @author pascal.perau
 */
public class RedTapeAgentStub implements RedTapeAgent {

    private List<Dossier> dossiers = new ArrayList<>();

    private double tax = (1 + GlobalConfig.DEFAULT_TAX.getTax());

    {
        // Dossier 1
        Dossier dos1 = new Dossier();
        dos1.setComment("dos1");
        dos1.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);

        Document doc1 = new Document();
        doc1.setActive(true);
        doc1.setType(DocumentType.ORDER);
        doc1.add(Condition.CONFIRMED);
        doc1.setDirective(Directive.NONE);

        Position p1 = new Position();
        Position p2 = new Position();
        Position p3 = new Position();
        Position p4 = new Position();
        Position p5 = new Position();
        Position p6 = new Position();
        Position p7 = new Position();
        Position p8 = new Position();

        p1.setName("A Unit Position, SopoNr:123456 Noch Text");
        p2.setName("Item of Position 2");
        p3.setName("Item of Position 3");
        p4.setName("Unit of Position 4");
        p5.setName("Comment of Position 5");
        p6.setName("Item of Position 6");
        p7.setName("Comment of Position 7");
        p8.setName("Transportation Cost of Position 8");

        p1.setType(PositionType.UNIT);
        p2.setType(PositionType.SERVICE);
        p3.setType(PositionType.SERVICE);
        p4.setType(PositionType.UNIT);
        p5.setType(PositionType.COMMENT);
        p6.setType(PositionType.SERVICE);
        p7.setType(PositionType.COMMENT);
        p8.setType(PositionType.SHIPPING_COST);

        p1.setDescription("Am I loud and clear or am i breaking up "
                + "am i still your charme or am i just bad luck are we getting closer or are we just getting more lost "
                + "I' ll show you mine if you show me yours first let's compare scars I' ll tell you whose is worse "
                + "let's unwrite these pages and replace them with our own words we live on front porches and swing life away "
                + "we get by just fine here on minimum wages if love is a labour I'll slave till the end "
                + "i won't cross these streets until you hold my hand");

        p5.setDescription("Am I loud and clear or am i breaking up "
                + "am i still your charme or am i just bad luck are we getting closer or are we just getting more lost "
                + "I' ll show you mine if you show me yours first let's compare scars I' ll tell you whose is worse "
                + "let's unwrite these pages and replace them with our own words we live on front porches and swing life away "
                + "we get by just fine here on minimum wages if love is a labour I'll slave till the end "
                + "i won't cross these streets until you hold my hand");

        p7.setDescription("Nicht ganz so lange songtextbeschreibung wie bei position nr 5");

        p1.setPrice(10);
        p2.setPrice(20);
        p3.setPrice(30);
        p4.setPrice(40);
        p6.setPrice(60);
        p8.setPrice(80);

        doc1.appendAll(p1, p2, p3, p4, p5, p6, p7, p8);

        dos1.add(doc1);

        // Dossier 2
        Dossier dos2 = new Dossier();
        dos2.setDispatch(true);
        dos2.setComment("dos2");
        dos2.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
        Document doc2 = new Document();
        doc2.setType(DocumentType.ORDER);
        doc2.setTaxType(REVERSE_CHARGE);
        doc2.add(Condition.CONFIRMED);
        dos2.add(doc2);
        doc2.append(Position.builder().type(PRODUCT_BATCH).amount(1000).price(221.45).tax(REVERSE_CHARGE.getTax()).name("Futurama eyePhone 3")
                .description("The eyePhone, a fictional augmented reality-enbaled smart glasses product depicted in the episode "
                        + "\"Attack of the Killer App\" of the American animated sitcom Futurama.").build());
        doc2.setDirective(Directive.NONE);
        doc2.setActive(true);

        // Dossier 3
        Dossier dos3 = new Dossier();
        dos3.setComment("dos3");
        dos3.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);

        Document doc3 = new Document();
        doc3.setDirective(Directive.NONE);
        doc3.setType(DocumentType.INVOICE);
        doc3.add(Condition.CONFIRMED);
        doc3.add(Condition.PAID);
        doc3.setIdentifier("RE_die erste");
        doc3.setActive(true);
        doc3.append(Position.builder().type(SERVICE).name("Position").description("A Position").build());

        dos3.add(doc3);

        // Dossier 4
        Dossier dos4 = new Dossier();
        dos4.setComment("dos4");
        dos4.setPaymentMethod(PaymentMethod.INVOICE);

        Document doc4 = new Document();
        doc4.setDirective(Directive.NONE);

        doc4.setType(DocumentType.INVOICE);
        doc4.add(Condition.CONFIRMED);
        doc4.add(Condition.PICKED_UP);
        doc4.setActive(true);

        doc4.setIdentifier("RE_die zweite");
        doc4.append(Position.builder().type(SERVICE).name("Position").description("A Position").build());

        dos4.add(doc4);

        dossiers.add(dos1);
        dossiers.add(dos2);
        dossiers.add(dos3);
        dossiers.add(dos4);

    }

    @Override
    public void remove(SalesProduct salesProduct) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SalesProduct merge(SalesProduct salesProduct) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Dossier> findDossiersOpenByCustomerIdEager(long customerId) {
        return dossiers;
    }

    @Override
    public List<Dossier> findDossiersClosedByCustomerIdEager(long customerId, int start, int amount) {
        return new ArrayList<>();
    }

    @Override
    public List<Dossier> findAllEagerDescending(int start, int end) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> long count(Class<T> entityClass) {
        if ( entityClass.equals(Dossier.class) ) return dossiers.size();
        return 0;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        if ( entityClass.equals(Dossier.class) ) return (List<T>)dossiers;
        return Collections.EMPTY_LIST;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
        if ( entityClass.equals(Dossier.class) ) return (List<T>)dossiers;
        return Collections.EMPTY_LIST;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass) {
        if ( entityClass.equals(Dossier.class) ) return (List<T>)dossiers;
        return Collections.EMPTY_LIST;
    }

    @Override
    public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
        if ( entityClass.equals(Dossier.class) ) return (List<T>)dossiers;
        return Collections.EMPTY_LIST;
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id) {
        System.out.println("found or havent found dossier by id");
        return null;
    }

    @Override
    public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
        System.out.println("found or havent found dossier by id");
        return null;
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id) {
        System.out.println("found or havent found dossier by id");
        return null;
    }

    @Override
    public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
        System.out.println("found or havent found dossier by id");
        return null;
    }

    @Override
    public SalesProduct persist(SalesProduct salesProduct) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
