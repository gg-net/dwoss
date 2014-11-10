package eu.ggnet.dwoss.rules;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.*;

/**
 * These are possible special operations that either show a status, like saleable, or have in common, that a unit is block some there for some internal/external
 * process.
 *
 * @author oliver.guenther
 */
@RequiredArgsConstructor
@Getter
public enum ReceiptOperation implements Serializable {

    /**
     * The internal rework process.
     */
    INTERNAL_REWORK("Interne Nacharbeiten", true),
    /**
     * The internal process for missing parts.
     */
    MISSING_PARTS("Fehlende Teile", true),
    /**
     * The external process for a repair of the manufacturer.
     */
    REPAIR("Reparatur", true),
    /**
     * The sale able process, meaning the unit is available and not blocked any there.
     */
    // HINT: OG doesn't like a process indicator which is handled different, but accepts it for now.
    SALEABLE("Verkaufsfähig", false),
    /**
     * The in sale process, meaning the unit is reserved or sold.
     */
    // HINT: OG doesn't like a process indicator which is handled different, but accepts it for now.
    IN_SALE("Im Verkauf oder Reported (akiver Kundenauftrag)", false);

    private final String note;

    private final boolean backedByCustomer;

    public static List<ReceiptOperation> valuesBackedByCustomer() {
        return Arrays.asList(values()).stream().filter(ReceiptOperation::isBackedByCustomer).collect(Collectors.toList());
    }

}
