package eu.ggnet.dwoss.mandator.api.service;

import java.io.Serializable;

import eu.ggnet.dwoss.rules.SalesChannel;

import lombok.*;

/**
 * Class used as a valueholder for listing actions.
 * <p>
 * @author pascal.perau
 */
@Getter
@ToString
@RequiredArgsConstructor
public class ListingActionConfiguration implements Serializable {

    public enum Type {

        PDF, XLS;

    }

    public enum Location {

        LOCAL, FTP, MAIL;

    }

    private final Type type;

    private final Location location;

    private final SalesChannel channel;

    private final String name;

    /*
     right !!!
     */
}
