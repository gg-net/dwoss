package eu.ggnet.dwoss.stock.emo;

import java.util.*;

import javax.validation.constraints.*;

import lombok.Value;
import lombok.experimental.Builder;

/**
 * Parameter Class for {@link StockTransactionEmo#transfer}.
 * <p>
 * @author oliver.guenther
 */
@Value
@Builder
public class Transfer {

    /**
     * The stockIds to transfer.
     */
    @NotNull
    @Size(min = 1)
    private final List<Integer> stockUnitIds;

    /**
     * The id of the destination stock.
     */
    private final int destinationStockId;

    /**
     * A optional arranger.
     */
    private final String arranger;

    /**
     * A optional comment.
     */
    private final String comment;

    /**
     * The maximum amount of units a trasaction should hold.
     */
    @Min(1)
    @Max(9999)
    private final int maxTransactionSize;

}
