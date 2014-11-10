package eu.ggnet.dwoss.redtape.assist;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author oliver.guenther
 */
@Deprecated
public class RedTapeSupport {

    @Inject
    @RedTapes
    private EntityManager entityManager;

    @Deprecated
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
