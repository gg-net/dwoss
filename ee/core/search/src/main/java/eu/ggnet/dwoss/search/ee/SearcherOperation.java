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
package eu.ggnet.dwoss.search.ee;

import java.util.*;

import javax.ejb.LocalBean;
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
@LocalBean
public class SearcherOperation implements Searcher {

    private final Logger LOG = LoggerFactory.getLogger(SearcherOperation.class);

    private final int LIMIT = 5;

    @Inject
    private Instance<SearchProvider> providers;

    private final List<SearchProvider> activeProviders = new ArrayList<>();

    private SearchRequest request = null;

    private int start = 0;

    private boolean lastResultNotEmpty = false;

    private int estimatedMax = 0;

    @Override
    public void initSearch(SearchRequest request) {
        LOG.info("search inited with {}", request);
        this.request = request;
        this.start = 0;
        this.estimatedMax = 0;
        activeProviders.clear();
        if ( isInvalidate() ) {
            this.lastResultNotEmpty = false;
        } else {
            this.lastResultNotEmpty = true;
            for (SearchProvider provider : providers) {
                // TODO: If we add the component to the filterrequest, remove them here.
                activeProviders.add(provider);
            }
        }
    }

    @Override
    public List<ShortSearchResult> next() {
        if ( isInvalidate() || activeProviders.isEmpty() ) return Collections.EMPTY_LIST;
        LOG.debug("called next() activeProvider(component={}).search(request={}, start={}, LIMIT={})",
                activeProviders.get(0).getSource(), request, start, LIMIT);
        List<ShortSearchResult> result = activeProviders.get(0).search(request, start, LIMIT);
        start += LIMIT;
        while (result.isEmpty() && !activeProviders.isEmpty()) { // change providers till one returns a result ore no providers are there.
            activeProviders.remove(0);
            // The If is needed  if remove has tacken the last one. Wihle will terminate after that.
            if ( !activeProviders.isEmpty() ) result = activeProviders.get(0).search(request, 0, LIMIT);
            start = LIMIT;
        }
        lastResultNotEmpty = (!result.isEmpty());
        return result;
    }

    @Override
    public boolean hasNext() {
        return lastResultNotEmpty;
    }

    @Override
    public int estimateMaxResults() {
        // TODO: For now, we only call estimatedMax once. If the implementations ever get undeterministic, we have to optimize that.
        if ( isInvalidate() ) return 0;
        if ( estimatedMax > 0 ) return estimatedMax;
        for (SearchProvider provider : activeProviders) {
            estimatedMax += provider.estimateMaxResults(request);
        }
        return estimatedMax;
    }

    private boolean isInvalidate() {
        if ( request == null ) return true;
        if ( StringUtils.isBlank(request.getSearch()) ) return true;
        if ( providers.isUnsatisfied() ) {
            // TODO: Verifiy, that isUnsatisfied doesen't take too long.
            LOG.warn("No SearchProviders are found, but SearchOperation.next was called");
            return true;
        }
        return false;
    }

    @Override
    public String details(GlobalKey key) {
        if ( key == null ) return "";
        if ( isInvalidate() ) return "";
        for (SearchProvider provider : providers) {
            if ( provider.getSource() == key.getComponent() ) return provider.details(key);
        }
        return "";
    }

}
