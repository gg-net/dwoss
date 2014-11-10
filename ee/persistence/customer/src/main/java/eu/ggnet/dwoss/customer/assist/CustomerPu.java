package eu.ggnet.dwoss.customer.assist;

import java.util.Map;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.persistence.*;
import javax.sql.DataSource;

import eu.ggnet.dwoss.util.persistence.JpaPersistenceConfiguration;
import eu.ggnet.dwoss.util.persistence.OpenEjbEmbeddedPersistenceConfiguration;

/**
 * Persistence Unit for Customers in DW.
 * <p>
 * @author pascal.perau
 */
public class CustomerPu {

    public final static String NAME = "customer-pu";

    public final static String DATASOURCE = "customerDataSource";

    public final static String DATASOURCE_UNMANAGED = "customerDataSourceUnmanaged";

    public final static OpenEjbEmbeddedPersistenceConfiguration OPENEJB_CONFIG = OpenEjbEmbeddedPersistenceConfiguration.builder()
            .persistenceUnit(NAME).dataSourceManaged(DATASOURCE).dataSourceUnmanaged(DATASOURCE_UNMANAGED).build();

    public static Map<String, String> JPA_IN_MEMORY = JpaPersistenceConfiguration.asHsqldbInMemoryWithSearchRam(NAME);

    public static Map<String, String> CMP_IN_MEMORY = OPENEJB_CONFIG.asHsqlInMemoryWithSearchRam();

    @PersistenceContext(unitName = NAME)
    @Produces
    @Customers
    private EntityManager entityManager;

    @Resource(name = NAME)
    @Produces
    @Customers
    private DataSource dataSource;

}
