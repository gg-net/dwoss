package eu.ggnet.dwoss.misc.ee;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import eu.ggnet.dwoss.customer.ee.entity.Customer;

/**
 *
 * @author pascal.perau
 */
@Remote
public interface CustomerAdressLabelMerge {

    Map<String, List<Customer>> mergeCustomerAfterAddressLabel();
}
