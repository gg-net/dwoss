/*
 * Copyright (C) 2020 GG-Net GmbH
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
package eu.ggnet.dwoss.redtape.itest.stub;

import java.util.List;

import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApiLocal;

/**
 *
 * @author oliver.guenther
 */
public class UniqueUnitApiLocalStub implements UniqueUnitApiLocal {

    @Override
    public SimpleUniqueUnit findByRefurbishedId(String refurbishId) {
        return null;
    }

    @Override
    public SimpleUniqueUnit findById(long uniqueUnitId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SimpleUniqueUnit> findByIds(List<Long> uniqueUnitIds) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
