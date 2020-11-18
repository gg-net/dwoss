/*
 * Copyright (C) 2017 GG-Net GmbH
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

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.api.GlobalKey.Component;
import eu.ggnet.dwoss.search.api.*;
import eu.ggnet.dwoss.uniqueunit.ee.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.ee.format.UniqueUnitFormater;

import static eu.ggnet.dwoss.search.api.GlobalKey.Component.UNIQUE_UNIT;

/**
 * Providers Search for Unique Unit
 *
 * @author oliver.guenther
 */
@Stateless
public class UniqueUnitSearchProvider implements SearchProvider {

    private final static Logger L = LoggerFactory.getLogger(UniqueUnitSearchProvider.class);

    @Inject
    private UniqueUnitEao eao;

    @Override
    public Component getSource() {
        return GlobalKey.Component.UNIQUE_UNIT;
    }

    @Override
    public int estimateMaxResults(SearchRequest request) {
        return eao.countFind(request.getSearch());
    }

    @Override
    public List<ShortSearchResult> search(SearchRequest request, int start, int limit) {
        return eao.find(request.getSearch(), start, limit).stream()
                .map((u) -> new ShortSearchResult(new GlobalKey(UNIQUE_UNIT, u.getId()), UniqueUnitFormater.toPositionName(u))).collect(Collectors.toList());

    }

    @Override
    public String details(GlobalKey key) {
        return UniqueUnitFormater.toHtmlDetailed(eao.findById((int)key.id)); // Downcast to int needed, otherwise the galaxy explodes.
    }

}
