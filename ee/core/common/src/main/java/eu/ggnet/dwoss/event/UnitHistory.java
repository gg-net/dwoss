package eu.ggnet.dwoss.event;

import java.io.Serializable;

import lombok.Value;

/**
 * History Event designated for a UniqueUnit.
 *
 * @author oliver.guenther
 */
@Value
public class UnitHistory implements Serializable {

    private final int uniqueUnitId;

    private final String comment;

    private final String arranger;

}
