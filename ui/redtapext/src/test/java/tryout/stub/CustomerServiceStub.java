package tryout.stub;

import eu.ggnet.dwoss.core.common.values.PaymentCondition;
import eu.ggnet.dwoss.core.common.values.SalesChannel;
import eu.ggnet.dwoss.core.common.values.CustomerFlag;
import eu.ggnet.dwoss.core.common.values.PaymentMethod;
import eu.ggnet.dwoss.core.common.values.ShippingCondition;

import java.util.*;

import eu.ggnet.dwoss.customer.api.*;
import eu.ggnet.dwoss.customer.ee.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.ee.entity.*;

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
        // Injecting a virtual id.
        List<UiCustomer> result = new ArrayList<>();
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            result.add(new UiCustomer(
                    i,
                    c.preferedContact().map(Contact::getTitle).orElse(""),
                    c.preferedContact().map(Contact::getFirstName).orElse(""),
                    c.preferedContact().map(Contact::getLastName).orElse(""),
                    c.preferedCompany().map(Company::getName).orElse(null),
                    c.toName(),
                    c.getDefaultEmailCommunication().map(Communication::getIdentifier).orElse(null),
                    0));

        }
        return result;
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
