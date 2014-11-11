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
