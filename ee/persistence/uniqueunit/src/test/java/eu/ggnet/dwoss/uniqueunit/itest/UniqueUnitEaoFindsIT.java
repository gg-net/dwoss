package eu.ggnet.dwoss.uniqueunit.itest;

import java.util.Date;
import java.util.Iterator;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.time.DateUtils;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.search.api.*;
import eu.ggnet.dwoss.uniqueunit.ee.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Condition;
import eu.ggnet.dwoss.uniqueunit.itest.support.ArquillianProjectArchive;

import static eu.ggnet.dwoss.search.api.GlobalKey.Component.UNIQUE_UNIT;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.REFURBISHED_ID;
import static eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier.SERIAL;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class UniqueUnitEaoFindsIT extends ArquillianProjectArchive {

    private final static String PARTNO_1 = "AA.BBBBB.CCC";

    @Inject
    private UserTransaction utx;

    @Inject
    @UniqueUnits
    private EntityManager em;

    @Inject
    private Instance<SearchProvider> searchProviders;

    /**
     * Multiple tests on find.
     *
     * @throws Exception
     */
    @Test
    public void testFinds() throws Exception {
        Product p = new Product(ProductGroup.MONITOR, TradeName.FUJITSU, PARTNO_1, "The Notebook");

        UniqueUnitEao eao = new UniqueUnitEao(em);

        UniqueUnit unit1 = new UniqueUnit();
        unit1.setMfgDate(DateUtils.addDays(new Date(), -5));
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
        unit3.setIdentifier(SERIAL, "FHASKHFUAERHFAGTQ5");
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

        utx.begin();
        em.joinTransaction();
        em.persist(p);
        em.persist(unit1);
        em.persist(unit2);
        em.persist(unit3);
        em.persist(unit4);
        em.persist(unit5);
        utx.commit();

        utx.begin();
        em.joinTransaction();

        assertThat(eao.findByIdentifier(REFURBISHED_ID, "22223").getId()).as("Id of Unit1 via findByIdentifier 22223").isEqualTo(unit1.getId());
        assertThat(eao.findByIdentifier(REFURBISHED_ID, "54422").getId()).as("Id of Unit2 via findByIdentifier 54422").isEqualTo(unit2.getId());
        assertThat(eao.findByPartialIdentifier(REFURBISHED_ID, "54*")).as("Result of 54* should contation unit2 and unit4").contains(unit2, unit4);
        assertThat(eao.findByPartialIdentifier(REFURBISHED_ID, "*22*")).as("Result of *22* should contation unit1 and unit2").contains(unit1, unit2);

        assertThat(eao.countFind("*5*")).as("Counting *5*").isEqualTo(3);
        assertThat(eao.find("*5*")).as("Find of *5*").contains(unit2, unit3, unit4);
        utx.commit();

        SearchProvider search = null;
        for (Iterator<SearchProvider> iterator = searchProviders.iterator(); iterator.hasNext();) {
            SearchProvider next = iterator.next();
            if ( next.getSource() == UNIQUE_UNIT ) search = next;
        }

        assertThat(search.estimateMaxResults(new SearchRequest("*5*"))).as("Counting via Searchprovider *5*").isEqualTo(3);
        assertThat(search.search(new SearchRequest("*5*"), 0, 1000)).as("Find of *5*").extracting(ssr -> ssr.key).
                contains(
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, unit2.getId()),
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, unit3.getId()),
                        new GlobalKey(GlobalKey.Component.UNIQUE_UNIT, unit4.getId())
                );

    }
}
