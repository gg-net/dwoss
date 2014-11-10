package eu.ggnet.dwoss.redtape.assist;

import java.util.Map;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.sql.DataSource;

import eu.ggnet.dwoss.util.persistence.JpaPersistenceConfiguration;
import eu.ggnet.dwoss.util.persistence.OpenEjbEmbeddedPersistenceConfiguration;

/**
 * Persistence Unit Support Class.
 *
 * @author oliver.guenther
 */
public class RedTapePu {

    public final static String NAME = "redtape-pu";

    public final static String DATASOURCE = "redtapeDataSource";

    public final static String DATASOURCE_UNMANAGED = "redtapeDataSourceUnmanaged";

    public final static OpenEjbEmbeddedPersistenceConfiguration OPENEJB_CONFIG = OpenEjbEmbeddedPersistenceConfiguration.builder()
            .persistenceUnit(NAME).dataSourceManaged(DATASOURCE).dataSourceUnmanaged(DATASOURCE_UNMANAGED).build();

    public static Map<String, String> JPA_IN_MEMORY = JpaPersistenceConfiguration.asHsqldbInMemory(NAME);

    public static Map<String, String> CMP_IN_MEMORY = OPENEJB_CONFIG.asHsqlInMemory();

    @PersistenceUnit(unitName = NAME)
    @Produces
    @RedTapes
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = NAME)
    @Produces
    @RedTapes
    private EntityManager entityManager;

    @Resource(name = DATASOURCE)
    @Produces
    @RedTapes
    private DataSource dataSource;
}
