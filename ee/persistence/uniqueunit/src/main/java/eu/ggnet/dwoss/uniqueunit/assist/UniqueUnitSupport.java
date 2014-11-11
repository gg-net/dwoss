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
