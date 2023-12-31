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
package eu.ggnet.dwoss.search.api;

import java.util.List;

import jakarta.ejb.Local;

/**
 * Provides an implementation to supply search results, multiple implementations are allowed.
 *
 * @author oliver.guenther
 */
@Local
public interface SearchProvider {

    /**
     * Must return the component identifying the source, never null.
     *
     * @return the componente
     */
    GlobalKey.Component getSource();

    /**
     * Returns an estimate result count based on the request.
     * May change over time.
     *
     * @param request the request.
     * @return an estimate result count based on the request.
     */
    int estimateMaxResults(SearchRequest request);

    /**
     * Returns a list of results for the search request, never null.
     *
     * @param request the request
     * @param start   the start
     * @param limit   the limit
     * @return the result.
     */
    List<ShortSearchResult> search(SearchRequest request, int start, int limit);

    /**
     * Return details based on the short result.
     *
     * @param key the global key as identifier for the details.
     * @return a string, hopefully html with details.
     */
    String details(GlobalKey key);

}
