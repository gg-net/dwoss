package eu.ggnet.dwoss.event;

import eu.ggnet.dwoss.rules.SalesChannel;

import lombok.Value;

/**
 * Informs about a SalesChannelChange
 * <p>
 * @author oliver.guenther
 */
@Value
public class SalesChannelChange {

    private final int uniqueUnitId;

    private final SalesChannel newChannel;

}
