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
package eu.ggnet.dwoss.uniqueunit.ee.op;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.uniqueunit.api.SimpleUnit;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUnit.Builder;
import eu.ggnet.dwoss.uniqueunit.api.UnitFinder;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit;
import eu.ggnet.dwoss.uniqueunit.ee.entity.UniqueUnit.Identifier;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class UnitFinderOperation implements UnitFinder {

    @Inject
    private UniqueUnitEao eao;
    
    @Override
    public SimpleUnit findBy(String refurbishId) {
        Builder suBuilder = new SimpleUnit.Builder();
        UniqueUnit uu = eao.findByIdentifier(Identifier.REFURBISHED_ID, refurbishId);
        if (uu == null) {
            uu = eao.findByRefurbishedIdInHistory(refurbishId);
            suBuilder.lastRefurbishId(refurbishId);
        }
        if (uu == null) return null;
        return suBuilder
                .id(uu.getId())
                .refurbishedId(uu.getRefurbishId())
                .shortDescription(UniqueUnitFormater.toPositionName(uu))
                .build();        
    }
    
}
