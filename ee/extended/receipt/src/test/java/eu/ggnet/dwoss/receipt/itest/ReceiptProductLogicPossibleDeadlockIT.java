package eu.ggnet.dwoss.receipt.itest;

import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;
import eu.ggnet.dwoss.spec.ee.entity.ProductModel;
import eu.ggnet.dwoss.spec.ee.entity.ProductSeries;
import eu.ggnet.dwoss.spec.ee.entity.ProductFamily;
import eu.ggnet.dwoss.spec.ee.entity.Desktop;
import eu.ggnet.dwoss.spec.ee.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.ee.entity.piece.Display;
import eu.ggnet.dwoss.spec.ee.entity.Notebook;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.ggnet.dwoss.receipt.ee.ProductProcessor;
import eu.ggnet.dwoss.receipt.itest.support.ArquillianProjectArchive;
import eu.ggnet.dwoss.core.common.values.ProductGroup;
import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.spec.ee.assist.Specs;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ReceiptProductLogicPossibleDeadlockIT extends ArquillianProjectArchive {

    @EJB
    private ProductProcessor productLogic;

    @Inject
    @Specs
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    @Test
    @Ignore
    public void testDeadlockProductSpec() throws Exception {
        //
        //
        //Test will run into a Deadlock if no Product Modell is setted!
        //It will Display no Error but we hope for an Exception!
        //To Recreated the Deadlock comment the Line Between the "Comment This" Comments
        //
        //
        //Possible Reason that it will here appear a deadlock ist a bug in EJB that by a Validator Exception not return a exception
        //but hang there. Maybe fixed in next version.
        //TODO when used new Version of EJB then testet deadlock again!
        //

        Display display = new Display(Display.Size._10_1, Display.Resolution.VGA, Display.Type.MATT, Display.Ration.FOUR_TO_THREE);

        ProductModel productModel = new ProductModel("TestModel");
        productModel.setFamily(new ProductFamily("TestFamily"));
        //Create a CPU and GPU and persist it.
        utx.begin();
        em.joinTransaction();
        Cpu cpu = new Cpu(Cpu.Series.CORE, "TestCPU", Cpu.Type.MOBILE, 2.0, 5);
        Gpu gpu = new Gpu(Gpu.Type.MOBILE, Gpu.Series.GEFORCE_100, "TestGPU");
        ProductSeries productSeries = new ProductSeries(TradeName.ONESELF, ProductGroup.MISC, "TestSeries");
        ProductFamily productFamily = new ProductFamily("TestFamily", productSeries);

        //Comment This
        //productModel.setFamily(productFamily);
        //Comment This
        em.persist(cpu);
        em.persist(gpu);
        em.persist(productSeries);
        em.persist(productFamily);
        em.persist(productModel);
        utx.commit();

        Notebook notebook = new Notebook(display, Desktop.Os.LINUX,
                cpu, null,
                gpu, null, 2048, null);
        notebook.add(Desktop.Hdd.SSD_0016);
        notebook.add(Desktop.Hdd.ROTATING_2000);
        notebook.add(Desktop.Odd.DVD_ROM);
        notebook.setExtras(ProductSpec.Extra.E_SATA, ProductSpec.Extra.HIGHT_CHANGEABLE);

        notebook.setPartNo("LX.ASDFG.GHJ");
        notebook.setModel(productModel);

        ProductSpec testSpec = productLogic.create(notebook, productModel, 0);

        assertThat(testSpec).isNotNull();
    }
}
