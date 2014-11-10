package eu.ggnet.dwoss.configuration;

import eu.ggnet.dwoss.util.TempUtil;

/**
 * Constants.
 * <p>
 * @author oliver.guenther
 */
public final class GlobalConfig {

    /**
     * A central place there the client can put exports.
     */
    public static final String APPLICATION_PATH_OUTPUT = TempUtil.getDirectory("output") + "/";

    /**
     * The actual tax.
     * Not really good here, but acceptable for now.
     */
    public static final double TAX = 0.19;

}
