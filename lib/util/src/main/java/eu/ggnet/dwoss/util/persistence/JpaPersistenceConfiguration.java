package eu.ggnet.dwoss.util.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author oliver.guenther
 */
public class JpaPersistenceConfiguration {

    /**
     * Returns a plain jpa configuration using a hsqldb in memory.
     * <p/>
     * @param db
     * @return a plain jpa configuration using a hsqldb in memory.
     */
    public static Map<String, String> asHsqldbInMemory(String db) {
        Map<String, String> o = new HashMap<>();
        o.put("hibernate.show_sql", "false");
        o.put("hibernate.hbm2ddl.auto", "create-drop");
        o.put("hibernate.jdbc.batch_size", "0");
        o.put("javax.persistence.jtaDataSource", "");
        o.put("javax.persistence.nonJtaDataSource", "");
        o.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
        o.put("javax.persistence.jdbc.user", "sa");
        o.put("javax.persistence.jdbc.password", "");
        o.put("javax.persistence.jdbc.url", "jdbc:hsqldb:mem:" + db);
        return o;
    }

    public static Map<String, String> asHsqldbInMemoryWithSearchRam(String db) {
        Map<String, String> o = asHsqldbInMemory(db);
        o.put("hibernate.search.default.directory_provider", "ram");
        o.put("hibernate.search.default.indexmanager", "near-real-time");
        return o;
    }

}
