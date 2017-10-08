package tryout.stub;

import java.util.*;

import eu.ggnet.dwoss.customer.api.*;
import eu.ggnet.dwoss.customer.assist.gen.CustomerGenerator;
import eu.ggnet.dwoss.customer.priv.ConverterUtil;
import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.rules.*;

/**
 *
 * @author pascal.perau
 */
public class CustomerServiceStub implements CustomerService {

    private eu.ggnet.dwoss.customer.entity.Customer c;

    public CustomerServiceStub() {
        CustomerGenerator gen = new CustomerGenerator();
        c = gen.makeOldCustomer();
    }

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
        return ConverterUtil.convert(c, "BLA", new DefaultCustomerSalesdata(ShippingCondition.DEALER_ONE, PaymentCondition.CUSTOMER, PaymentMethod.DIRECT_DEBIT, Arrays.asList(SalesChannel.CUSTOMER), Arrays.asList(0L))).toHtmlHighDetailed();
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
        return c.toHtml();
    }

}
