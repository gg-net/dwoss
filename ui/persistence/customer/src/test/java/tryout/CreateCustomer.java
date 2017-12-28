package tryout;

import java.util.*;

import org.junit.Test;

import eu.ggnet.dwoss.common.AbstractGuardian;
import eu.ggnet.dwoss.customer.CustomerCreateController;
import eu.ggnet.dwoss.customer.CustomerCreateView;
import eu.ggnet.dwoss.customer.api.*;
import eu.ggnet.dwoss.customer.priv.OldCustomer;
import eu.ggnet.dwoss.customer.priv.OldCustomerAgent;
import eu.ggnet.dwoss.rules.*;
import eu.ggnet.dwoss.util.OkCancelDialog;
import eu.ggnet.saft.core.auth.AuthenticationException;
import eu.ggnet.saft.Client;
import eu.ggnet.saft.core.auth.Guardian;

/**
 *
 * @author oliver.guenther
 */
public class CreateCustomer {

    @Test
    public void tryout() {
        Client.addSampleStub(Guardian.class, new AbstractGuardian() {
            @Override
            public void login(String user, char[] pass) throws AuthenticationException {
            }
        });

        Client.addSampleStub(OldCustomerAgent.class, new OldCustomerAgent() {

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

        Client.addSampleStub(CustomerService.class, new CustomerService() {

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
            public List<UiCustomer> asUiCustomers(String company, String firstName, String lastName, String email, boolean appendWildcard) {
                System.out.println("got called");
                return new ArrayList<>();
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
        });

        CustomerCreateView view = new CustomerCreateView();
        CustomerCreateController controller = new CustomerCreateController();
        controller.setView(view);
        view.setController(controller);

        OkCancelDialog<CustomerCreateView> dialog = new OkCancelDialog<>("Kundenmanagement", view);
        dialog.setVisible(true);
        System.out.println(view.getCustomer());
    }

}
