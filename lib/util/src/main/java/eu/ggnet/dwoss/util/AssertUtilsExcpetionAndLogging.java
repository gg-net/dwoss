package eu.ggnet.dwoss.util;

/**
 * Contains Util methods, which on error result in an exception and an slf4j log entry.
 *
 * @author oliver.guenther
 * @deprecated use Objects.notNull() See http://overload.ahrensburg.gg-net.de/jira/browse/DW-1152
 */
@Deprecated
public class AssertUtilsExcpetionAndLogging {

    @Deprecated
    public static void notNull(Object o) throws NullPointerException {
        notNull(o, null);
    }

    @Deprecated
    public static void notNull(Object o, String name) throws NullPointerException {
        if ( o == null ) {
            String msg = "Name=" + name;
            throw new NullPointerException(msg);
        }
    }
}
