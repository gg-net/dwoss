package eu.ggnet.dwoss.report.assist;

import java.util.Map;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.persistence.*;
import javax.sql.DataSource;

import eu.ggnet.dwoss.util.persistence.JpaPersistenceConfiguration;
import eu.ggnet.dwoss.util.persistence.OpenEjbEmbeddedPersistenceConfiguration;

public final class ReportPu {

    public final static String NAME = "report-pu";

    public final static String DATASOURCE = "reportDataSource";

    public final static String DATASOURCE_UNMANAGED = "reportDataSourceUnmanaged";

    public final static OpenEjbEmbeddedPersistenceConfiguration OPENEJB_CONFIG = OpenEjbEmbeddedPersistenceConfiguration.builder()
            .persistenceUnit(NAME).dataSourceManaged(DATASOURCE).dataSourceUnmanaged(DATASOURCE_UNMANAGED).build();

    public static Map<String, String> JPA_IN_MEMORY = JpaPersistenceConfiguration.asHsqldbInMemoryWithSearchRam(NAME);

    public static Map<String, String> CMP_IN_MEMORY = OPENEJB_CONFIG.asHsqlInMemoryWithSearchRam();

    @PersistenceUnit(unitName = NAME)
    @Produces
    @Reports
    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = NAME)
    @Produces
    @Reports
    private EntityManager entityManager;

    @Resource(name = DATASOURCE)
    @Produces
    @Reports
    private DataSource dataSource;
}
