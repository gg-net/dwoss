package tryout.stub;

import eu.ggnet.dwoss.core.common.values.PaymentCondition;
import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.core.common.values.ShippingCondition;

import java.util.*;
import java.util.stream.Collectors;

import eu.ggnet.dwoss.customer.api.*;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.Customer;

/**
 *
 * @author pascal.perau
 */
public class CustomerServiceStub implements CustomerService {

    private final List<Customer> customers;

    public CustomerServiceStub() {
        CustomerGenerator gen = new CustomerGenerator();
        customers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            customers.add(gen.makeCustomer());
        }
    }

    private long normalize(long id) {
        if ( id >= customers.size() ) return (long)(customers.size() / 2);
        return id;
    }

    @Override
    public CustomerMetaData asCustomerMetaData(long customerId) {
        return new CustomerMetaData(customerId, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT, ShippingCondition.SIX_MIN_TEN, EnumSet.noneOf(CustomerFlag.class), EnumSet.of(SalesChannel.CUSTOMER), null);
    }

    @Override
    public UiCustomer asUiCustomer(long customerId) {
        return new UiCustomer(customerId, "Mr.", "Blubba", "Blubbamann", "dawoot?!", "Woot da heck?!", "hans@example.com", 0);
    }

    @Override
    public List<UiCustomer> asUiCustomers(String search) {
        // TODO: Returns random, make it work some day.
        return customers.stream().map(Customer::toUiCustomer).collect(Collectors.toList());
    }

    @Override
    public List<UiCustomer> asUiCustomers(String company, String firstName, String lastName, String email, boolean appendWildcard) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String asHtmlHighDetailed(long id) {
        return customers.get((int)normalize(id)).toHtml();
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
    public String defaultEmailCommunication(long customerId) {
        return "testmail@schnurz.de";
    }

}
