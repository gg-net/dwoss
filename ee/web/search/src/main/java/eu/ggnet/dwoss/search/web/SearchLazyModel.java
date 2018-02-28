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
package eu.ggnet.dwoss.search.web;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.api.SearchRequest;
import eu.ggnet.dwoss.search.api.ShortSearchResult;
import eu.ggnet.dwoss.search.ee.SearcherOperation;

import lombok.Getter;

/**
 *
 * @author oliver.guenther
 */
public class SearchLazyModel extends LazyDataModel<ShortSearchResult> {

    private static final Logger L = LoggerFactory.getLogger(SearchLazyModel.class);

    private final Map<String, ShortSearchResult> viewModel = new HashMap<>();

    private final Map<ShortSearchResult, String> viewKeyModel = new HashMap<>();

    private final List<ShortSearchResult> alreadyLoaded = new ArrayList<>();

    @Getter
    private String search;

    public void setSearch(String search) {
        if ( Objects.equals(this.search, search) ) return;
        alreadyLoaded.clear();
        this.search = search;
        searcher.initSearch(new SearchRequest(search));
    }

    public SearcherOperation getSearcher() {
        return searcher;
    }

    public void setSearcher(SearcherOperation searcher) {
        this.searcher = searcher;
    }

    private SearcherOperation searcher;

    public SearchLazyModel(SearcherOperation searcher) {
        this.searcher = searcher;
    }

    @Override
    public ShortSearchResult getRowData(String key) {
        ShortSearchResult searchResult = viewModel.get(key);
        L.info("getRowData(key={}) returns searchResult={}", key, searchResult);
        return searchResult;
    }

    @Override
    public Object getRowKey(ShortSearchResult searchResult) {
        String key = viewKeyModel.get(searchResult);
        L.info("getRowKey(searchResult={}) returns key={}", searchResult, key);
        return key;
    }

    @Override
    public int getRowCount() {
        return searcher.estimateMaxResults();
    }

    @Override
    public List<ShortSearchResult> load(int start, int limit, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        L.info("load(first={},pageSize={})", start, limit);
        if ( StringUtils.isBlank(search) ) return Collections.emptyList();

        while (searcher.hasNext() && alreadyLoaded.size() < start + limit) {
            alreadyLoaded.addAll(searcher.next());
        }

        List<ShortSearchResult> pagedResult = new ArrayList<>();

        if ( alreadyLoaded.size() > start ) {
            pagedResult = alreadyLoaded.subList(start, alreadyLoaded.size() - start < limit ? start + (alreadyLoaded.size() - start) : start + limit);
        }

        viewKeyModel.clear();
        viewModel.clear();
        for (int i = 0; i < pagedResult.size(); i++) {
            viewKeyModel.put(pagedResult.get(i), Integer.toString(i));
            viewModel.put(Integer.toString(i), pagedResult.get(i));
        }

        return pagedResult;
    }
}
