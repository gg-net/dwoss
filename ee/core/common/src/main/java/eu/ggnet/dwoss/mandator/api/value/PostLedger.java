package eu.ggnet.dwoss.mandator.api.value;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.rules.PositionType;

import lombok.Value;

/**
 *
 * @author Bastian Venz <bastian.venz at gg-net.de>
 */
@Value
public class PostLedger implements Serializable {

    @Value
    public static class LedgerValue implements Serializable {

        public LedgerValue(Integer primaryCustomerId) {
            this.primaryLedgerId = primaryCustomerId;
            this.possibleLedgersIds = null;
        }

        public LedgerValue(Integer primaryCustomerId, List<Integer> possiblePostLedgersIds) {
            this.primaryLedgerId = primaryCustomerId;
            this.possibleLedgersIds = possiblePostLedgersIds;
        }

        private Integer primaryLedgerId;

        private List<Integer> possibleLedgersIds;
    }

    private final Map<PositionType, LedgerValue> ledgerCustomers;

    public Optional<Integer> get(PositionType type) {
        return Optional.ofNullable(ledgerCustomers.get(type)).map(lv -> lv.getPrimaryLedgerId());
    }

    public Optional<List<Integer>> getPossible(PositionType type) {
        return Optional.ofNullable(ledgerCustomers.get(type)).map(lv -> lv.getPossibleLedgersIds());
    }

}
