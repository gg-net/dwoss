package eu.ggnet.dwoss.util.persistence;

import java.sql.Types;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * This is a Workaround for https://hibernate.onjira.com/browse/HHH-6935 .
 *
 * @author oliver.guenther
 */
public class MysqlHibernate3Dialect extends MySQL5InnoDBDialect {

    public MysqlHibernate3Dialect() {
        super();
        registerColumnType(Types.BOOLEAN, "bit(1)");
    }
}
