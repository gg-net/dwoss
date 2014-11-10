package eu.ggnet.dwoss.stock.assist;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author oliver.guenther
 */
@Deprecated
public class StockSupport {

    @Inject
    @Stocks
    private EntityManager entityManager;

    @Deprecated
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
