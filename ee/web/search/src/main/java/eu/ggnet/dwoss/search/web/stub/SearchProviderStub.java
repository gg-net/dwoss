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
package eu.ggnet.dwoss.search.web.stub;

import java.util.*;

import javax.annotation.ManagedBean;

import org.apache.commons.lang3.RandomStringUtils;

import eu.ggnet.dwoss.search.api.GlobalKey.Component;
import eu.ggnet.dwoss.search.api.*;

import static eu.ggnet.dwoss.search.api.GlobalKey.Component.CUSTOMER;

/**
 * Stub of the SearchProvider
 *
 * @author oliver.guenther
 */
@ManagedBean
public class SearchProviderStub implements SearchProvider {

    private final Map<SearchRequest, List<ShortSearchResult>> searches = new HashMap<>();

    @Override
    public Component getSource() {
        return CUSTOMER;
    }

    @Override
    public int estimateMaxResults(SearchRequest request) {
        return genAndGet(request).size();
    }

    @Override
    public List<ShortSearchResult> search(SearchRequest request, int start, int limit) {
        List<ShortSearchResult> data = genAndGet(request);
        if ( data.size() > start ) {
            return data.subList(start, data.size() - start < limit ? data.size() - start : limit);
        }
        return Collections.emptyList();

    }

    @Override
    public String details(GlobalKey key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private List<ShortSearchResult> genAndGet(SearchRequest request) {
        if ( searches.containsKey(request) ) return searches.get(request);
        List<ShortSearchResult> gen = new ArrayList<>();
        int max = (int)(Math.random() * 500);
        for (int i = 0; i < max; i++) {
            gen.add(new ShortSearchResult(new GlobalKey(CUSTOMER, i), "Search of " + request.getSearch() + " and random " + RandomStringUtils.randomAlphabetic(20)));
        }
        searches.put(request, gen);
        return gen;
    }

}
