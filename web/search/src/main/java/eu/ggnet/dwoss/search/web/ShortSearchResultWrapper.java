/*
 * Copyright (C) 2021 GG-Net GmbH
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

import eu.ggnet.dwoss.search.api.ShortSearchResult;

/**
 * Wrapper for JSF.
 *
 * @author oliver.guenther
 */
public class ShortSearchResultWrapper {

    private final ShortSearchResult shortSearchResult;

    public ShortSearchResultWrapper(ShortSearchResult shortSearchResult) {
        this.shortSearchResult = shortSearchResult;
    }

    public long getKey() {
        return shortSearchResult.key.id;
    }

    public String getComponent() {
        return shortSearchResult.key.component.name();
    }

    public String getShortDescription() {
        return shortSearchResult.shortDescription;
    }

    @Override
    public String toString() {
        return "ShortSearchResultWrapper{" + shortSearchResult + '}';
    }

}
