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

import java.util.List;

import eu.ggnet.dwoss.search.api.SearchResult;

/**
 * A statfull searcher for all connected modules.
 *
 *
 *
 * @author oliver.guenther
 */
public interface Searcher {

    /**
     * Set a new search string, resetting every thing that happend before.
     *
     * @param search the search string.
     */
    void initSearch(String search);

    /**
     * Returns a list of the next search results. If the list is empty, no more values can be found.
     *
     * @return a list of the next search results, never null.
     */
    List<SearchResult> next();

    /**
     * Returns true as long as there might be a next result.
     *
     * @return true as long as there might be a next result.
     */
    boolean hasNext();

    /**
     * Estimate the max result count.
     * This might help for some progress display. As of the nondeterministic search process, this method is explicitly designed to return some useful
     * estimation,
     * but always return very quickly. Multiple calls may return different results, especially on long running searches over multiple sources. An implementation
     * will make sure, that a call to this method always returns a value higher than the actual count.
     *
     * @return
     */
    int estimateMaxResults();

}
