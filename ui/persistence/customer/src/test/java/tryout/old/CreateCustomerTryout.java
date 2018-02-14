package tryout.old;

import eu.ggnet.dwoss.customer.opi.CustomerMetaData;
import eu.ggnet.dwoss.customer.opi.UiCustomer;
import eu.ggnet.dwoss.customer.opi.CustomerService;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.common.AbstractGuardian;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomer;
import eu.ggnet.dwoss.customer.ee.priv.OldCustomerAgent;
import eu.ggnet.dwoss.customer.ui.old.CustomerCreateWithSearchController;
import eu.ggnet.dwoss.customer.ui.old.CustomerCreateWithSearchView;
import eu.ggnet.dwoss.mandator.Mandators;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.saft.Dl;
import eu.ggnet.saft.core.auth.AuthenticationException;
import eu.ggnet.saft.core.auth.Guardian;

import static org.mockito.Mockito.mock;

/**
 *
 * @author oliver.guenther
 */
public class CreateCustomerTryout {

    public final static Logger L = LoggerFactory.getLogger(CreateCustomerTryout.class);

    public static void main(String[] args) {

        Dl.remote().add(Mandators.class, mock(Mandators.class));

        Dl.local().add(Guardian.class, new AbstractGuardian() {
            {
                setAllUsersnames(Arrays.asList("hans", "claus", "peter"));
            }

            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
            }
        });

        Dl.remote().add(OldCustomerAgent.class, new OldCustomerAgent() {

            @Override
            public OldCustomer findById(long id) {
                OldCustomer customer = new OldCustomer("Die Firma", "Herr", "Max", "Mustermann", "Keine Bemerkungen die ganz lang sein muss um zu testen ob die tabelle richtig funzt, was erst bei richtig richtig langem text auffällt und olli sicher nervt xD",
                        "Helle Strasse 22", "12345", "Musterhausen", "LIStraße", "LIPLZ", "LIOrt");
                customer.setKundenID(1);
                customer.setTelefonnummer("0190 666666");
                customer.setHandynummer("0123456789");
                customer.setPaymentMethod(PaymentMethod.ADVANCE_PAYMENT);
                customer.setPaymentCondition(PaymentCondition.DEALER);
                customer.setShippingCondition(ShippingCondition.DEALER_ONE);
                customer.setAllowedSalesChannels(EnumSet.of(SalesChannel.CUSTOMER, SalesChannel.RETAILER));
                customer.addFlag(CustomerFlag.CONFIRMS_DOSSIER);
                customer.setHaendler(true);
                return customer;
            }

            @Override
            public OldCustomer store(OldCustomer old) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        Dl.remote().add(CustomerService.class, new CustomerService() {

            @Override
            public List<UiCustomer> asUiCustomers(String company, String firstName, String lastName, String email, boolean appendWildcard) {
                L.info("call of asUiCustomers(company={},firstName={},lastName={},email={},wildCard={}", company, firstName, lastName, email, appendWildcard);
                return new ArrayList<>();
            }

            //<editor-fold defaultstate="collapsed" desc="Unused Methods">
            @Override
            public CustomerMetaData asCustomerMetaData(long customerId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public UiCustomer asUiCustomer(long customerId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public List<UiCustomer> asUiCustomers(String search) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String asHtmlHighDetailed(long id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String findComment(long id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void updateCustomerFlags(long customerId, Set<CustomerFlag> flags) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public List<CustomerMetaData> allAsCustomerMetaData() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public List<Long> allSystemCustomerIds() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public String asNewHtmlHighDetailed(long id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            //</editor-fold>
        });

        CustomerCreateWithSearchView view = new CustomerCreateWithSearchView();
        CustomerCreateWithSearchController controller = new CustomerCreateWithSearchController();
        controller.setView(view);
        view.setController(controller);

        OkCancelDialog<CustomerCreateWithSearchView> dialog = new OkCancelDialog<>("Kundenmanagement", view);
        dialog.setVisible(true);
        System.out.println(view.getCustomer());
        System.exit(0);
    }

}
