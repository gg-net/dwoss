package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;

import lombok.Value;

/**
 * Parameters for export to finalcial accounting Systems.
 * For now this is only used in the GSOffice Export.
 * <p>
 * @author oliver.guenther
 */
@Value
public class FinancialAccounting  implements Serializable{

    /**
     * Default Ledger of customers, if no extra ledger is set.
     */
    private final int defaultLedger;

    /**
     * If set, the individual customer ledgers are ignored.
     */
    private final boolean disableCustomerLedgers;

}
