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
package eu.ggnet.dwoss.uniqueunit.ee;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApiLocal;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;

/**
 * Local Api implementation.
 *
 * @author oliver.guenther
 */
@Stateless
public class UniqueUnitApiLocalBean implements UniqueUnitApiLocal {

    @Inject
    private UniqueUnitEao eao;

    @Override
    public SimpleUniqueUnit findByRefurbishedId(String refurbishId) {
        UniqueUnit uu = eao.findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);
        if ( uu != null ) return uu.toSimple();
        uu = eao.findByRefurbishedIdInHistory(refurbishId);
        if ( uu != null ) return new SimpleUniqueUnit.Builder().mergeFrom(uu.toSimple()).lastRefurbishId(refurbishId).build();
        return null;
    }

    @Override
    public SimpleUniqueUnit findById(long uniqueUnitId) {
        UniqueUnit uu = eao.findById((int)uniqueUnitId);
        if ( uu != null ) return uu.toSimple();
        return null;
    }

    @Override
    public List<SimpleUniqueUnit> findByIds(List<Long> uniqueUnitIds) {
        if ( uniqueUnitIds == null || uniqueUnitIds.isEmpty() ) return Collections.emptyList();
        return eao.findByIds(uniqueUnitIds.stream().mapToInt(l -> l.intValue()).boxed().collect(Collectors.toList()))
                .stream().map(UniqueUnit::toSimple).collect(Collectors.toList());
    }

}
