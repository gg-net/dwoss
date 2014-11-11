/* 
 * Copyright (C) 2014 pascal.perau
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.util.persistence;

import java.util.HashMap;
import java.util.Map;

import lombok.Value;
import lombok.experimental.Builder;

/**
 * Helper Class to build OpenEjbEmbeddedPersistenceConfiguration.
 * <p>
 * @author oliver.guenther
 */
@Value
public class OpenEjbEmbeddedPersistenceConfiguration {

    private final String persistenceUnit;

    private final String dataSourceManaged;

    private final String dataSourceUnmanaged;

    @Builder
    public OpenEjbEmbeddedPersistenceConfiguration(String persistenceUnit, String dataSourceManaged, String dataSourceUnmanaged) {
        this.persistenceUnit = persistenceUnit;
        this.dataSourceManaged = dataSourceManaged;
        this.dataSourceUnmanaged = dataSourceUnmanaged;
    }

    /**
     * Returns the productive configuration for an openejb embedded server for a mysql Database.
     * <p/>
     * @param host   the host
     * @param schema the schema or database name
     * @param user   the db user
     * @param pass   the db pass
     * @return the productive configuration for an openejb embedded server for a mysql Database.
     */
    public Map<String, String> asMysql(String host, String schema, String user, String pass) {
        Map<String, String> o = new HashMap<>();
        o.put(persistenceUnit + ".hibernate.jdbc.batch_size", "0");
        o.put(persistenceUnit + ".hibernate.hbm2ddl.auto", "validate");
        o.put(persistenceUnit + ".hibernate.dialect", MysqlHibernate3Dialect.class.getName());
        o.put(dataSourceManaged, "new://Resource?type=DataSource");
        o.put(dataSourceManaged + ".UserName", user);
        o.put(dataSourceManaged + ".Password", pass);
        o.put(dataSourceManaged + ".JdbcDriver", "com.mysql.jdbc.Driver");
        o.put(dataSourceManaged + ".JdbcUrl", "jdbc:mysql://" + host + "/" + schema);
        o.put(dataSourceUnmanaged, "new://Resource?type=DataSource");
        o.put(dataSourceUnmanaged + ".UserName", user);
        o.put(dataSourceUnmanaged + ".Password", pass);
        o.put(dataSourceUnmanaged + ".JdbcDriver", "com.mysql.jdbc.Driver");
        o.put(dataSourceUnmanaged + ".JdbcUrl", "jdbc:mysql://" + host + "/" + schema);
        o.put(dataSourceUnmanaged + ".JtaManaged", "false");
        return o;
    }

    /**
     * Returns the productive configuration for an openejb embedded server for a mysql Database with hiberante search provider ram.
     * <p/>
     * @param host   the host
     * @param schema the schema or database name
     * @param user   the db user
     * @param pass   the db pass
     * @return the productive configuration for an openejb embedded server for a mysql Database with hiberante search provider ram.
     */
    public Map<String, String> asMysqlWithSearchRam(String host, String schema, String user, String pass) {
        Map<String, String> o = asMysql(host, schema, user, pass);
        o.put(persistenceUnit + ".hibernate.search.default.directory_provider", "ram");
        o.put(persistenceUnit + "hibernate.search.default.indexmanager", "near-real-time");
        return o;
    }

    /**
     * Returns a configuration for an openejb embedded server which creates a hsqldb file under target/db an a log file.
     * <p/>
     * @return a configuration for an openejb embedded server which creates a hsqldb file under target/db an a log file.
     */
    public Map<String, String> asHsqlFile(String targetFile) {
        Map<String, String> o = new HashMap<>();
        o.put(persistenceUnit + ".hibernate.show_sql", "false");
        o.put(persistenceUnit + ".hibernate.hbm2ddl.auto", "create");
        o.put(persistenceUnit + ".hibernate.jdbc.batch_size", "0");
        o.put(dataSourceManaged, "new://Resource?type=DataSource");
        o.put(dataSourceManaged + ".JdbcDriver", "org.hsqldb.jdbcDriver");
        o.put(dataSourceManaged + ".JdbcUrl", "jdbc:hsqldb:file:" + targetFile + ";hsqldb.sqllog=3");
        o.put(dataSourceUnmanaged, "new://Resource?type=DataSource");
        o.put(dataSourceUnmanaged + "JdbcDriver", "org.hsqldb.jdbcDriver");
        o.put(dataSourceUnmanaged + "JdbcUrl", "jdbc:hsqldb:file:" + targetFile + ";hsqldb.sqllog=3");
        o.put(dataSourceUnmanaged + "JtaManaged", "false");
        return o;
    }

    /**
     * Returns a configuration for an openejb embedded server using a hsqldb in memory.
     * <p/>
     * @return a configuration for an openejb embedded server using a hsqldb in memory.
     */
    public Map<String, String> asHsqlInMemory() {
        Map<String, String> o = new HashMap<>();
        o.put(persistenceUnit + ".hibernate.show_sql", "false");
        o.put(persistenceUnit + ".hibernate.hbm2ddl.auto", "create-drop");
        o.put(persistenceUnit + ".hibernate.jdbc.batch_size", "0");
        o.put(dataSourceManaged, "new://Resource?type=DataSource");
        o.put(dataSourceManaged + ".JdbcDriver", "org.hsqldb.jdbcDriver");
        o.put(dataSourceManaged + ".JdbcUrl", "jdbc:hsqldb:mem:" + dataSourceManaged);
        o.put(dataSourceUnmanaged, "new://Resource?type=DataSource");
        o.put(dataSourceUnmanaged + ".JdbcDriver", "org.hsqldb.jdbcDriver");
        o.put(dataSourceUnmanaged + ".JdbcUrl", "jdbc:hsqldb:mem:" + dataSourceManaged);
        o.put(dataSourceUnmanaged + ".JtaManaged", "false");

        return o;
    }

    /**
     * Returns a hsqldb config with hibernate search provider ram.
     * <p>
     * @return a hsqldb config with hibernate search provider ram.
     */
    public Map<String, String> asHsqlInMemoryWithSearchRam() {
        Map<String, String> o = asHsqlInMemory();
        o.put(persistenceUnit + ".hibernate.search.default.directory_provider", "ram");
        o.put(persistenceUnit + "hibernate.search.default.indexmanager", "near-real-time");
        return o;
    }

}
