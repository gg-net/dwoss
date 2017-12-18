package eu.ggnet.dwoss.receipt.unit;

import java.util.EnumMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.LockModeType;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.junit.Test;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.receipt.ProductProcessor;
import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.stub.ProductProcessorStub;
import eu.ggnet.dwoss.rules.DocumentType;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.uniqueunit.UniqueUnitAgent;
import eu.ggnet.dwoss.uniqueunit.assist.CategoryProductDto;
import eu.ggnet.dwoss.uniqueunit.entity.*;
import eu.ggnet.saft.api.Reply;
import eu.ggnet.saft.Client;

/**
 * Tryout Test for Unit View.
 * <p/>
 * @author oliver.guenther
 */
public class UnitViewTryout {

    @Test
    public void tryOut() throws Exception {
        final ProductSpec spec = new SpecGenerator().makeSpec();
        final Product product = new Product(spec.getModel().getFamily().getSeries().getGroup(),
                spec.getModel().getFamily().getSeries().getBrand(), spec.getPartNo(), spec.getModel().getName());
        JOptionPane.showMessageDialog(null, "Generated Product is : " + product.getTradeName().getName() + " - " + product.getGroup().getName());

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        Client.addSampleStub(MandatorSupporter.class, new MandatorSupporter() {
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
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public PostLedger loadPostLedger() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ShippingTerms loadShippingTerms() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String loadMandatorAsHtml() {
                return loadMandator().toHtml();
            }

        });

        Client.addSampleStub(SpecAgent.class, new SpecAgent() {
            @Override
            public ProductSpec findProductSpecByPartNoEager(String partNo) {
                if ( partNo == null ) return null;
                if ( partNo.startsWith("X") ) return null;
                spec.setPartNo(partNo);
                return spec;
            }
            // <editor-fold defaultstate="collapsed" desc="Unneeded Methods">

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
            // </editor-fold>
        });

        Client.addSampleStub(UnitSupporter.class, new UnitSupporter() {
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

        Client.addSampleStub(UniqueUnitAgent.class, new UniqueUnitAgent() {
            @Override
            public Product findProductByPartNo(String partNo) {
                if ( partNo == null ) return null;
                if ( partNo.startsWith("X") ) return null;
                product.setPartNo(partNo);
                return product;
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

        });

        Client.addSampleStub(ProductProcessor.class, new ProductProcessorStub());

        UnitController controller = new UnitController();

        UnitModel model = new UnitModel();
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
