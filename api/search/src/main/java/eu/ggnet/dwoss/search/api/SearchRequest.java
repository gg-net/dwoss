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

import java.io.Serializable;

/**
 * Wrapper for a request.
 * This class will be enhanced in the future.
 *
 * @author oliver.guenther
 */
public class SearchRequest implements Serializable {

    /**
     * May be null.
     */
    public final String search;

    public SearchRequest(String search) {
        this.search = search;
    }

    /**
     * 
     * @return
     * @deprecated use public field.
     */
    @Deprecated
    public String getSearch() {
        return search;
    }
    
    @Override
    public String toString() {
        return "SearchRequest{" + "search=" + search + '}';
    }

}
