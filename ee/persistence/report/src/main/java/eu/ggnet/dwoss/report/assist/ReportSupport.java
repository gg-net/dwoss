package eu.ggnet.dwoss.report.assist;

import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 *
 * @author oliver.guenther
 */
@Deprecated
public class ReportSupport {

    @Inject
    @Reports
    private EntityManager entityManager;

    @Deprecated
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
