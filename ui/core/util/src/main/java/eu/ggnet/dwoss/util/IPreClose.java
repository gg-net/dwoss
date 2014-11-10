package eu.ggnet.dwoss.util;

/**
 * Simple Interface to allow sub components to disallow some opperations.
 */
public interface IPreClose {

    /**
     * Is called before a closing opperation of type is done
     *
     * @param type the type of the closing operation
     * @return true if the closing opperation may continue as allowed
     */
    boolean pre(CloseType type);

}
