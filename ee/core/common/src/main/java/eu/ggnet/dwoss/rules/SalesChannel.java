package eu.ggnet.dwoss.rules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents a sales channel
 */
@Getter
@RequiredArgsConstructor
public enum SalesChannel {

    UNKNOWN("Unbekannt"),
    RETAILER("Händlerkanal"),
    CUSTOMER( "Endkundenkanal");
    
    private final String name;

}
