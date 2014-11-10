package eu.ggnet.dwoss.uniqueunit.eao;

import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;

import java.text.*;
import java.util.*;

import javax.persistence.*;

import org.junit.*;

import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static org.junit.Assert.*;

/**
 *
 * @author pascal.perau
 */
public class UniqueUnitEaoIT {

    private EntityManagerFactory emf;

    private EntityManager em;

    private UniqueUnit unit_62325;

    private Product product;

    private UniqueUnitEao unitEao;

    private final static String REFURBISHID_62325 = "62325";

    private final static Date _2012_12_01_;

    private final static Date _2012_12_10_;

    private final static Date _2012_12_20_;

    private final static String PARTNO_1 = "AA.BBBBB.CCC";

    private final static String PARTNO_2 = "AA.BBBBB.DDD";

    private final static String PARTNO_3 = "AA.BBBBB.EEE";

    static {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            _2012_12_01_ = df.parse("2012-12-01");
            _2012_12_10_ = df.parse("2012-12-10");
            _2012_12_20_ = df.parse("2012-12-20");
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory(UniqueUnitPu.NAME, UniqueUnitPu.JPA_IN_MEMORY);
        em = emf.createEntityManager();
        em.getTransaction().begin();

        product = new Product(ProductGroup.MONITOR, TradeName.ACER, PARTNO_1, "The Notebook");
        em.persist(product);

        Product p2 = new Product(ProductGroup.MONITOR, TradeName.ACER, PARTNO_2, "The Notebook");
        em.persist(p2);
        Product p3 = new Product(ProductGroup.MONITOR, TradeName.ACER, PARTNO_3, "The Notebook");
        em.persist(p3);

        unit_62325 = new UniqueUnit();
        unit_62325.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, REFURBISHID_62325);
        unit_62325.setContractor(TradeName.AMAZON);
        unit_62325.setCondition(UniqueUnit.Condition.ALMOST_NEW);
        unit_62325.setProduct(product);
        unit_62325.setInputDate(_2012_12_01_);
        em.persist(unit_62325);

        UniqueUnit u2 = new UniqueUnit();
        u2.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "99999");
        u2.setContractor(TradeName.ACER);
        u2.setCondition(UniqueUnit.Condition.ALMOST_NEW);
        u2.setInputDate(_2012_12_10_);
        u2.setProduct(product);
        em.persist(u2);

        UniqueUnit u3 = new UniqueUnit();
        u3.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "99998");
        u3.setContractor(TradeName.ACER);
        u3.setCondition(UniqueUnit.Condition.AS_NEW);
        u3.setProduct(p2);
        u3.setInputDate(_2012_12_10_);
        em.persist(u3);

        UniqueUnit u4 = new UniqueUnit();
        u4.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "99997");
        u4.setContractor(TradeName.ACER);
        u4.setCondition(UniqueUnit.Condition.AS_NEW);
        u4.setProduct(p3);
        u4.setInputDate(_2012_12_10_);
        em.persist(u4);

        UniqueUnit u5 = new UniqueUnit();
        u5.setIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "99996");
        u5.setContractor(TradeName.ACER);
        u5.setCondition(UniqueUnit.Condition.AS_NEW);
        u5.setProduct(p3);
        u5.setInputDate(_2012_12_20_);
        em.persist(u5);

        em.getTransaction().commit();
    }

    @After
    public void tearDown() {
        em.close();
        emf.close();
    }

    @Test
    public void testFindByProductPartNosInputDate() {
        em.getTransaction().begin();
        unitEao = new UniqueUnitEao(em);
        List<UniqueUnit> uus = unitEao.findByProductPartNosInputDate(Arrays.asList(PARTNO_1, PARTNO_2), _2012_12_01_, _2012_12_10_);
        assertFalse(uus.isEmpty());
        assertEquals(3, uus.size());
        em.getTransaction().commit();
    }

    @Test
    public void testFindByProductPartNo() {
        em.getTransaction().begin();
        unitEao = new UniqueUnitEao(em);
        List<UniqueUnit> uus = unitEao.findByProductPartNo(PARTNO_1);
        assertFalse(uus.isEmpty());
        assertEquals(2, uus.size());
        em.getTransaction().commit();
    }

    @Test
    public void testFindIdentifier() {
        em.getTransaction().begin();
        unitEao = new UniqueUnitEao(em);
        assertNotNull(unitEao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, REFURBISHID_62325));
        assertNull(unitEao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "65475"));
        assertEquals(unit_62325, unitEao.findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, REFURBISHID_62325));
        em.getTransaction().commit();
    }

    @Test
    public void testFindPartIdentifier() {
        em.getTransaction().begin();
        unitEao = new UniqueUnitEao(em);
        assertTrue(unitEao.findByPartialIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "632*").isEmpty());
        assertTrue(unitEao.findByPartialIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, "623*").contains(unit_62325));
        em.getTransaction().commit();
    }

    @Test
    public void testFindIdentifiers() {
        em.getTransaction().begin();
        unitEao = new UniqueUnitEao(em);
        assertEquals(2, unitEao.findByIdentifiers(REFURBISHED_ID, new TreeSet<>(Arrays.asList("99999", "99998"))).size());
        assertTrue(unitEao.findByIdentifiers(REFURBISHED_ID, Arrays.asList(REFURBISHID_62325)).contains(unit_62325));
        em.getTransaction().commit();
    }

}
