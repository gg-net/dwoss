package eu.ggnet.dwoss.receipt.unit;

import java.util.*;
import java.util.regex.Pattern;

import javax.persistence.LockModeType;
import javax.swing.UIManager;

import eu.ggnet.dwoss.mandator.Mandators;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.receipt.ProductProcessor;
import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.stub.ProductProcessorStub;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.rules.TradeName;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.entity.*;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.api.PicoUnit;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.dwoss.uniqueunit.entity.dto.CategoryProductDto;
import eu.ggnet.dwoss.uniqueunit.entity.dto.UnitCollectionDto;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.api.Reply;

import static eu.ggnet.dwoss.rules.TradeName.ACER;
import static eu.ggnet.dwoss.rules.TradeName.ONESELF;

/**
 * Tryout Test for Unit View.
 * <
 * import static eu.ggnet.dwoss.rules.TradeName.ONESELF;
 * p/>
 *
 * @author oliver.guenther
 */
public class UnitViewTryout {

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        final SpecGenerator GEN = new SpecGenerator();

        final List<ProductSpec> productSpecs = new ArrayList<>();

        final Map<String, ProductModel> productModels = new HashMap<>();

        final Map<String, ProductSeries> productSeries = new HashMap<>();

        final Map<String, ProductFamily> productFamily = new HashMap<>();

        final List<Product> products = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            ProductSpec spec = GEN.makeSpec();
            productSpecs.add(spec);
            products.add(new Product(spec.getModel().getFamily().getSeries().getGroup(),
                    spec.getModel().getFamily().getSeries().getBrand(), spec.getPartNo(), spec.getModel().getName()));
            productModels.putIfAbsent(spec.getModel().getName(), spec.getModel());
            productFamily.putIfAbsent(spec.getModel().getFamily().getName(), spec.getModel().getFamily());
            productSeries.putIfAbsent(spec.getModel().getFamily().getSeries().getName(), spec.getModel().getFamily().getSeries());

        }

//        final Product product = new Product(spec.getModel().getFamily().getSeries().getGroup(),
//                spec.getModel().getFamily().getSeries().getBrand(), spec.getPartNo(), spec.getModel().getName());
        Dl.remote().add(Mandators.class, new Mandators() {
            @Override
            public Mandator loadMandator() {
                return Mandator.builder()
                        .defaultMailSignature(null)
                        .smtpConfiguration(null)
                        .mailTemplateLocation(null)
                        .company(CompanyGen.makeCompany())
                        .dossierPrefix("DW")
                        .documentIntermix(null)
                        .documentIdentifierGeneratorConfigurations(new EnumMap<>(DocumentType.class))
                        .build();
            }

            @Override
            public DefaultCustomerSalesdata loadSalesdata() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ReceiptCustomers loadReceiptCustomers() {
                return ReceiptCustomers.builder().build();
            }

            @Override
            public SpecialSystemCustomers loadSystemCustomers() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Contractors loadContractors() {
                return new Contractors(EnumSet.allOf(TradeName.class), TradeName.getManufacturers());
            }

            @Override
            public PostLedger loadPostLedger() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ShippingTerms loadShippingTerms() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });

        Dl.remote().add(SpecAgent.class, new SpecAgent() {
            @Override
            public ProductSpec findProductSpecByPartNoEager(String partNo) {
                return productSpecs.stream().filter(p -> Objects.equals(partNo, p.getPartNo())).findFirst().orElse(null);
            }
            // <editor-fold defaultstate="collapsed" desc="Unneeded Methods">

            @Override
            public <T> long count(Class<T> entityClass) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAll(Class<T> entityClass) {
                if ( entityClass.equals(ProductSpec.class) ) return (List<T>)productSpecs;
                else if ( entityClass.equals(ProductSeries.class) ) return (List<T>)new ArrayList<>(productSeries.values());
                else if ( entityClass.equals(ProductFamily.class) ) return (List<T>)new ArrayList<>(productFamily.values());
                else if ( entityClass.equals(ProductModel.class) ) return (List<T>)new ArrayList<>(productModels.values());
                return Collections.emptyList();
            }

            @Override
            public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAllEager(Class<T> entityClass) {
                return findAll(entityClass);
            }

            @Override
            public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
                return findAll(entityClass, start, amount);
            }

            @Override
            public <T> T findById(Class<T> entityClass, Object id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findByIdEager(Class<T> entityClass, Object id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            // </editor-fold>
        });

        Dl.remote().add(UnitSupporter.class, new UnitSupporter() {
            @Override
            public boolean isRefurbishIdAvailable(String refurbishId) {
                return Pattern.matches("([2-8][0-9]{4}|A1[0-9]{3})", refurbishId);
            }

            @Override
            public boolean isSerialAvailable(String serial) {
                if ( serial == null ) return true;
                return !serial.startsWith("AAAA");
            }

            @Override
            public String findRefurbishIdBySerial(String serial) {
                if ( serial == null ) return null;
                if ( serial.startsWith("B") ) return "12345";
                return null;
            }
        });

        Dl.remote().add(UniqueUnitAgent.class, new UniqueUnitAgent() {
            @Override
            public Product findProductByPartNo(String partNo) {
                return products.stream().filter(p -> Objects.equals(partNo, p.getPartNo())).findFirst().orElse(null);

            }
            // <editor-fold defaultstate="collapsed" desc="Unneeded Methods">

            @Override
            public UniqueUnit findUnitByIdentifierEager(UniqueUnit.Identifier type, String identifier) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> long count(Class<T> entityClass) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAll(Class<T> entityClass) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAll(Class<T> entityClass, int start, int amount) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAllEager(Class<T> entityClass) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> List<T> findAllEager(Class<T> entityClass, int start, int amount) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findById(Class<T> entityClass, Object id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findById(Class<T> entityClass, Object id, LockModeType lockModeType) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findByIdEager(Class<T> entityClass, Object id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public <T> T findByIdEager(Class<T> entityClass, Object id, LockModeType lockModeType) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Product findProductByPartNoEager(String partNo) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public CategoryProduct createOrUpdate(CategoryProductDto dto, String username) throws NullPointerException {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            // </editor-fold>

            @Override
            public Reply<Void> deleteCategoryProduct(long id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Reply<Void> addToUnitCollection(PicoUnit unit, long unitCollectionId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Reply<Void> unsetUnitCollection(PicoUnit unit) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Reply<UnitCollection> createOnProduct(long productId, UnitCollectionDto dto, String username) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Reply<UnitCollection> update(UnitCollectionDto dto, String username) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Reply<Void> delete(UnitCollection dto) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });

        Dl.remote().add(ProductProcessor.class, new ProductProcessorStub());

        UnitController controller = new UnitController();

        UnitModel model = new UnitModel();
        model.setContractor(ONESELF);
        model.setMode(ACER);

        controller.setModel(model);

        final UnitView view = new UnitView(null);
        view.setModel(model);

        controller.setView(view);
        view.setController(controller);
        controller.init();
        // To Model

        view.setVisible(true);
        System.out.println("View canceled ? " + view.isCancel());
        System.out.println(view.getUnit());
        System.out.println(model.getProduct());
        System.out.println(model.getOperation());
        System.out.println(model.getOperationComment());
    }
}
