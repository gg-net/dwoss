package tryout.stub;

import eu.ggnet.dwoss.rules.PaymentCondition;
import eu.ggnet.dwoss.customer.api.UiCustomer;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.rules.PaymentMethod;
import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.rules.SalesChannel;
import eu.ggnet.dwoss.rules.CustomerFlag;
import eu.ggnet.dwoss.rules.ShippingCondition;

import java.util.*;


/**
 *
 * @author pascal.perau
 */
public class CustomerServiceStub implements CustomerService {

    @Override
    public CustomerMetaData asCustomerMetaData(long customerId) {
        return new CustomerMetaData(customerId, "testmail@schnurz.de", PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT, ShippingCondition.DEFAULT, EnumSet.noneOf(CustomerFlag.class), EnumSet.of(SalesChannel.CUSTOMER));
    }

    @Override
    public UiCustomer asUiCustomer(long customerId) {
        return new UiCustomer(customerId, "Mr.", "Blubba", "Blubbamann", "dawoot?!", "Woot da heck?!", "hans@example.com", 0);
    }

    @Override
    public List<UiCustomer> asUiCustomers(String search) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<UiCustomer> asUiCustomers(String company, String firstName, String lastName, String email, boolean appendWildcard) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String asHtmlHighDetailed(long id) {
        return "Super, Duper, High Detailed Customer Of Doom For Id" + id;
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

}
