package tryout.stub;

import eu.ggnet.dwoss.common.api.values.PaymentMethod;
import eu.ggnet.dwoss.common.api.values.ShippingCondition;
import eu.ggnet.dwoss.common.api.values.PaymentCondition;
import eu.ggnet.dwoss.common.api.values.CustomerFlag;
import eu.ggnet.dwoss.common.api.values.SalesChannel;
import eu.ggnet.dwoss.customer.api.CustomerMetaData;
import eu.ggnet.dwoss.customer.api.CustomerService;
import eu.ggnet.dwoss.customer.api.UiCustomer;

import java.util.*;

import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;

/**
 *
 * @author pascal.perau
 */
public class CustomerServiceStub implements CustomerService {

    private eu.ggnet.dwoss.customer.ee.entity.Customer customer;

    public CustomerServiceStub() {
        CustomerGenerator gen = new CustomerGenerator();
        customer = gen.makeCustomer();
    }

    @Override
    public CustomerMetaData asCustomerMetaData(long customerId) {
        return new CustomerMetaData(customerId, "testmail@schnurz.de", PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT, ShippingCondition.DEFAULT, EnumSet.noneOf(CustomerFlag.class), EnumSet.of(SalesChannel.CUSTOMER), null);
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
        //TODO Olli fragen ob okay
//return ConverterUtil.convert(customer, "BLA", new DefaultCustomerSalesdata(ShippingCondition.DEALER_ONE, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT, Arrays.asList(SalesChannel.CUSTOMER), Arrays.asList(0L))).toHtmlHighDetailed();
        return customer.toHtml();
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
