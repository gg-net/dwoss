package eu.ggnet.dwoss.mandator.api.value;

import java.util.HashMap;
import java.util.Map;

import eu.ggnet.dwoss.rules.TradeName;

/**
 *
 * @author oliver.guenther
 */
public class CustomersBuilder {

    private final Map<TradeName, Long> contractorCustomers = new HashMap<>();

    public CustomersBuilder put(TradeName contractor, long id) {
        contractorCustomers.put(contractor, id);
        return this;
    }

    public DeleteCustomers toDelete() {
        return new DeleteCustomers(contractorCustomers);
    }

    public RepaymentCustomers toRepayment() {
        return new RepaymentCustomers(contractorCustomers);
    }

    public ScrapCustomers toScrap() {
        return new ScrapCustomers(contractorCustomers);
    }

}
