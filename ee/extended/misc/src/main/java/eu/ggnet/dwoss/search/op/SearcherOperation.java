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
package eu.ggnet.dwoss.search.op;

import java.util.Collections;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.api.*;

/**
 * Implementation of the Search
 *
 * @author oliver.guenther
 */
@Stateful
public class SearcherOperation implements Searcher {

    private final Logger LOG = LoggerFactory.getLogger(SearcherOperation.class);

    private final int LIMIT = 5;

    @Inject
    private Instance<SearchProvider> providers;

    private SearchProvider activeProvider;

    private SearchRequest request = null;

    private int start = 0;

    private boolean lastResultNotEmpty = false;

    @Override
    public void initSearch(SearchRequest request) {
        LOG.info("search inited with {}", request);
        this.request = request;
        this.start = 0;
        if ( isInvalidate() ) {
            this.lastResultNotEmpty = false;
        } else {
            this.lastResultNotEmpty = true;
        }
    }

    @Override
    public List<ShortSearchResult> next() {
        if ( isInvalidate() ) return Collections.EMPTY_LIST;
        // Todo: For now I only support one provider.
        LOG.debug("called next() activeProvider.search(request={}, start={}, LIMIT={})", request, start, LIMIT);
        List<ShortSearchResult> result = activeProvider.search(request, start, LIMIT);
        start += LIMIT;
        lastResultNotEmpty = (!result.isEmpty());
        return result;
    }

    @Override
    public boolean hasNext() {
        return lastResultNotEmpty;
    }

    @Override
    public int estimateMaxResults() {
        if ( isInvalidate() ) return 0;
        return activeProvider.estimateMaxResults(request);
    }

    private boolean isInvalidate() {
        init();
        if ( activeProvider == null ) return true;
        if ( request == null ) return true;
        if ( StringUtils.isBlank(request.getSearch()) ) return true;
        return false;
    }

    private void init() {
        if ( activeProvider != null ) return; // TODO: For now I only use the first one.
        if ( providers.isUnsatisfied() ) {
            LOG.warn("No SearchProviders are found, but SearchOperation.next was called");
        } else {
            activeProvider = providers.iterator().next(); // TODO: For now I only use the first one.
        }
    }

}
