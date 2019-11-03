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
package itest;

import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.dwoss.search.api.SearchRequest;
import eu.ggnet.dwoss.search.api.SearchProvider;
import eu.ggnet.dwoss.search.api.GlobalKey;

import java.util.*;

import javax.ejb.Stateful;

import eu.ggnet.dwoss.search.api.GlobalKey.Component;

import static eu.ggnet.dwoss.search.api.GlobalKey.Component.UNIQUE_UNIT;

/**
 *
 * @author oliver.guenther
 */
@Stateful
public class UniqueUnitSearchProviderStub implements SearchProvider {

    @Override
    public Component getSource() {
        return UNIQUE_UNIT;
    }

    @Override
    public int estimateMaxResults(SearchRequest request) {
        if ( request == null || request.getSearch() == null ) return 0;
        if ( request.getSearch().length() < 2 ) return 0;
        if ( request.getSearch().length() < 6 ) return 10;
        return 0;
    }

    @Override
    public List<ShortSearchResult> search(SearchRequest request, int start, int limit) {
        if ( start > 30 ) return Collections.emptyList();
        if ( request == null || request.getSearch() == null ) return Collections.emptyList();
        if ( request.getSearch().length() < 2 ) return Collections.emptyList();
        if ( request.getSearch().length() < 6 ) {
            List<ShortSearchResult> result = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                result.add(new ShortSearchResult(new GlobalKey(UNIQUE_UNIT, i), "UniqueUnitStub, Search=" + request.getSearch() + ",id=" + i));
            }
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    public String details(GlobalKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
