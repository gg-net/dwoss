package eu.ggnet.dwoss.customer.priv;

import javax.ejb.*;

/**
 *
 * @author bastian.venz
 */
@Remote
@Local
public interface SearchSingleton {

    /**
     * Recreate the hibernate search index of Customer.
     */
    void reindexSearch();

}
