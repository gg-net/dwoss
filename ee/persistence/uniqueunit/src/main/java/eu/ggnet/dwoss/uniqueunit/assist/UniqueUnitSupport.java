package eu.ggnet.dwoss.uniqueunit.assist;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author oliver.guenther
 */
@Deprecated
public class UniqueUnitSupport {

    @Inject
    @UniqueUnits
    private EntityManager entityManager;

    @Deprecated
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
