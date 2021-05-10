package eu.ggnet.dwoss.receipt.itest;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.spec.ee.assist.Specs;
import eu.ggnet.dwoss.spec.ee.entity.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Arquillian.class)
public class ReceiptProductLogicPossibleDeadlockIT extends ArquillianProjectArchive {

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testDeadlockProductSpec() throws Exception {
        //Test was runnig into a Deadlock if no Product Model is setted!
        //It was not Displaying an Error but we hoped for an Exception!
        //To Recreated the Deadlock comment the Line Between the "Comment This" Comments

        assertThatThrownBy(() -> {
            utx.begin();
            em.joinTransaction();
            ProductSeries productSeries = new ProductSeries(TradeName.ACER, ProductGroup.MISC, "TestSeries");
            ProductFamily productFamily = new ProductFamily("TestFamily", productSeries);
            ProductModel productModel = new ProductModel("TestModel");

            //Comment This
            //productModel.setFamily(productFamily);
            //Comment This
            em.persist(productSeries);
            em.persist(productFamily);
            em.persist(productModel);
            utx.commit();
        }).hasRootCauseInstanceOf(javax.validation.ConstraintViolationException.class);
    }
}
