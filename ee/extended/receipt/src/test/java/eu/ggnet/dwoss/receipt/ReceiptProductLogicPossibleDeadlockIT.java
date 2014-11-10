package eu.ggnet.dwoss.receipt;

import eu.ggnet.dwoss.receipt.ProductProcessor;
import eu.ggnet.dwoss.spec.entity.piece.Gpu;
import eu.ggnet.dwoss.spec.entity.Desktop;
import eu.ggnet.dwoss.spec.entity.Notebook;
import eu.ggnet.dwoss.spec.entity.ProductModel;
import eu.ggnet.dwoss.spec.entity.piece.Cpu;
import eu.ggnet.dwoss.spec.entity.piece.Display;
import eu.ggnet.dwoss.spec.entity.ProductSeries;
import eu.ggnet.dwoss.spec.entity.ProductFamily;
import eu.ggnet.dwoss.spec.entity.ProductSpec;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.*;

import eu.ggnet.dwoss.configuration.SystemConfig;
import eu.ggnet.dwoss.rules.ProductGroup;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.assist.SpecPu;
import eu.ggnet.dwoss.spec.assist.Specs;
import eu.ggnet.dwoss.stock.assist.StockPu;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnitPu;

import static org.junit.Assert.assertTrue;

public class ReceiptProductLogicPossibleDeadlockIT {

    //<editor-fold defaultstate="collapsed" desc=" SetUp ">
    private EJBContainer container;

    @EJB
    private ProductProcessor productLogic;

    @Inject
    @Specs
    private EntityManagerFactory emf;

    @Before
    public void setUp() throws NamingException {
        Map<String, Object> c = new HashMap<>();
        c.putAll(SpecPu.CMP_IN_MEMORY);
        c.putAll(UniqueUnitPu.CMP_IN_MEMORY);
        c.putAll(StockPu.CMP_IN_MEMORY);
        c.putAll(SystemConfig.OPENEJB_EJB_XML_DISCOVER);
        c.putAll(SystemConfig.OPENEJB_LOG_WARN);
        container = EJBContainer.createEJBContainer(c);
        container.getContext().bind("inject", this);
    }

    @After
    public void tearDown() {
        container.close();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc=" DeadLock Testings ">

    @Test
    @Ignore
    public void testDeadlockProductSpec() {
        //
        //
        //Test will run into a Deadlock if no Product Modell is setted!
        //It will Display no Error but we hope for a Exception!
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
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
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
        em.getTransaction().commit();

        Notebook notebook = new Notebook(display, Desktop.Os.LINUX,
                cpu, null,
                gpu, null, 2048, null);
        notebook.add(Desktop.Hdd.SSD_0016);
        notebook.add(Desktop.Hdd.ROTATING_2000);
        notebook.add(Desktop.Odd.DVD_ROM);
        notebook.setExtras(ProductSpec.Extra.E_SATA, ProductSpec.Extra.HIGHT_CHANGEABLE);

        notebook.setPartNo("LX.ASDFG.GHJ");
        notebook.setModel(productModel);

        ProductSpec testSpec = productLogic.create(notebook, productModel);

        assertTrue(true);

    }
    //</editor-fold>
}
