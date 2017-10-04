package eu.ggnet.dwoss.receipt.unit;

import java.util.EnumMap;
import java.util.List;

import javax.persistence.LockModeType;

import org.junit.Test;

import eu.ggnet.dwoss.mandator.MandatorSupporter;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.receipt.product.DesktopBundleView;
import eu.ggnet.dwoss.receipt.stub.ProductProcessorStub;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.spec.SpecAgent;
import eu.ggnet.dwoss.spec.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.entity.DesktopBundle;
import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.util.OkCancelDialog;

/**
 *
 * @author oliver.guenther
 */
public class DesktopBundleViewTryout {

    @Test
    public void tryoutView() throws InterruptedException {
        final ProductSpec spec = new SpecGenerator().makeSpec();

        DesktopBundleView view = new DesktopBundleView(new MandatorSupporter() {
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
        }, new SpecAgent() {
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
        }, new ProductProcessorStub(), TradeName.ACER, TradeName.ACER, ProductGroup.DESKTOP, ProductGroup.MONITOR);
        view.setSpec(new DesktopBundle());

        OkCancelDialog<DesktopBundleView> dialog = new OkCancelDialog<>("BundleView", view);
        dialog.setVisible(true);
        dialog.dispose();
    }
}
