package eu.ggnet.dwoss.uniqueunit.eao;

import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;

import java.util.*;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

import org.junit.Test;

import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;
import eu.ggnet.dwoss.uniqueunit.entity.Product;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Condition;

import eu.ggnet.dwoss.util.validation.ConstraintViolationFormater;

import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit.Identifier.SERIAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UniqueUnitEao2IT {

    private final static String PARTNO_1 = "AA.BBBBB.CCC";

    @Test
    public void testFindByIdentifier() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(UniqueUnitPu.NAME, UniqueUnitPu.JPA_IN_MEMORY);
        EntityManager em = emf.createEntityManager();
        Product p = new Product(ProductGroup.MONITOR, TradeName.FUJITSU, PARTNO_1, "The Notebook");

        UniqueUnitEao uus = new UniqueUnitEao(em);

        UniqueUnit unit1 = new UniqueUnit();
        unit1.setMfgDate(new Date());
        unit1.setIdentifier(SERIAL, "ROFFFLAASSS");
        unit1.setIdentifier(REFURBISHED_ID, "22223");
        unit1.setContractor(TradeName.ONESELF);
        unit1.setCondition(Condition.AS_NEW);
        unit1.setProduct(p);

        UniqueUnit unit2 = new UniqueUnit();
        unit2.setIdentifier(SERIAL, "FASREAGFSGSWE");
        unit2.setIdentifier(REFURBISHED_ID, "54422");
        unit2.setContractor(TradeName.ONESELF);
        unit2.setCondition(Condition.AS_NEW);
        unit2.setProduct(p);

        UniqueUnit unit3 = new UniqueUnit();
        unit3.setIdentifier(SERIAL, "FHASKHFUAERHFAGTQ");
        unit3.setIdentifier(REFURBISHED_ID, "33323");
        unit3.setContractor(TradeName.ONESELF);
        unit3.setCondition(Condition.AS_NEW);
        unit3.setProduct(p);

        UniqueUnit unit4 = new UniqueUnit();
        unit4.setIdentifier(SERIAL, "GFSKLFDIACVHDAIERQ");
        unit4.setIdentifier(REFURBISHED_ID, "54142");
        unit4.setContractor(TradeName.ONESELF);
        unit4.setCondition(Condition.AS_NEW);
        unit4.setProduct(p);

        UniqueUnit unit5 = new UniqueUnit();
        unit5.setIdentifier(SERIAL, "FDALOIFUOWE");
        unit5.setIdentifier(REFURBISHED_ID, "29471");
        unit5.setContractor(TradeName.ONESELF);
        unit5.setCondition(Condition.AS_NEW);
        unit5.setProduct(p);
        Set<ConstraintViolation<UniqueUnit>> validate = Validation.buildDefaultValidatorFactory().getValidator().validate(unit1);
        assertTrue(ConstraintViolationFormater.toMultiLine(new HashSet<>(validate), true), validate.isEmpty());

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(p);
        em.persist(unit1);
        em.persist(unit2);
        em.persist(unit3);
        em.persist(unit4);
        em.persist(unit5);
        tx.commit();

        UniqueUnit runit1 = uus.findByIdentifier(REFURBISHED_ID, "22223");

        assertEquals(unit1.getId(), runit1.getId());

        UniqueUnit runit2 = uus.findByIdentifier(REFURBISHED_ID, "54422");

        assertEquals(unit2.getId(), runit2.getId());

        List<UniqueUnit> runits = uus.findByPartialIdentifier(REFURBISHED_ID, "54*");

        for (UniqueUnit runit : runits) {
            assertTrue(runit.equals(unit2) || runit.equals(unit4));
        }

        runits = uus.findByPartialIdentifier(REFURBISHED_ID, "*22*");

        for (UniqueUnit runit : runits) {
            assertTrue(runit.equals(unit1) || runit.equals(unit2));
        }

    }
}
