package eu.ggnet.dwoss.spec;

import javax.ejb.Local;
import javax.ejb.Remote;

import eu.ggnet.dwoss.spec.entity.ProductSpec;
import eu.ggnet.dwoss.util.persistence.RemoteAgent;

/**
 * The SpecAgent.
 * <p>
 * @author oliver.guenther
 */
@Remote
@Local
public interface SpecAgent extends RemoteAgent {

    /**
     * Finds a ProductSpec by part no and fetches the object tree eager.
     * <p>
     * @param partNo the part no to search for
     * @return a ProductSpec or null if non found.
     */
    ProductSpec findProductSpecByPartNoEager(String partNo);
}
