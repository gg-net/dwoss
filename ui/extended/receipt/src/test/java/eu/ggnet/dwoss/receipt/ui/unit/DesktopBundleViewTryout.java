package eu.ggnet.dwoss.receipt.ui.unit;

import java.util.EnumSet;
import java.util.List;

import javax.persistence.LockModeType;

import org.junit.Test;

import eu.ggnet.dwoss.common.api.values.ProductGroup;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.dwoss.common.ui.OkCancelDialog;
import eu.ggnet.dwoss.mandator.api.value.*;
import eu.ggnet.dwoss.mandator.ee.Mandators;
import eu.ggnet.dwoss.receipt.ui.product.DesktopBundleView;
import eu.ggnet.dwoss.receipt.ui.tryout.stub.ProductProcessorStub;
import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.assist.gen.SpecGenerator;
import eu.ggnet.dwoss.spec.ee.entity.DesktopBundle;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;

/**
 *
 * @author oliver.guenther
 */
public class DesktopBundleViewTryout {

    @Test
    public void tryoutView() throws InterruptedException {
        final ProductSpec spec = new SpecGenerator().makeSpec();

        DesktopBundleView view = new DesktopBundleView(new Mandators() {
            @Override
            public Mandator loadMandator() {
                return new Mandator.Builder()
                        .company(CompanyGen.makeCompany())
                        .dossierPrefix("DW")
                        .buildPartial();
            }

            @Override
            public DefaultCustomerSalesdata loadSalesdata() {
                throw new UnsupportedOperationException("loadSalesdata - Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ReceiptCustomers loadReceiptCustomers() {
                return ReceiptCustomers.builder().build();
            }

            @Override
            public SpecialSystemCustomers loadSystemCustomers() {
                throw new UnsupportedOperationException("loadSystemCustomers - Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public Contractors loadContractors() {
                return new Contractors(EnumSet.of(TradeName.ALSO, TradeName.ACER), EnumSet.of(TradeName.ACER));
            }

            @Override
            public PostLedger loadPostLedger() {
                throw new UnsupportedOperationException("loadPostLedger - Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
