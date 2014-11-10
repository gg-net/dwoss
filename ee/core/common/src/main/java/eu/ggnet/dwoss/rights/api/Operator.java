package eu.ggnet.dwoss.rights.api;

import java.io.Serializable;
import java.util.*;

import lombok.Value;

/**
 * This is a Data Transfer Object for {@link Operator}.
 * <p>
 * @author Bastian Venz
 * <p>
 */
@Value
public class Operator implements Serializable {

    private final String username;

    private final int quickLoginKey;

    private final List<AtomicRight> rights;

}
