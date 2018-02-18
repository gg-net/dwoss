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

import java.util.*;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.api.GlobalKey.Component;
import eu.ggnet.dwoss.search.api.*;
import eu.ggnet.dwoss.uniqueunit.ee.eao.ProductEao;
import eu.ggnet.dwoss.uniqueunit.ee.entity.Product;
import eu.ggnet.dwoss.uniqueunit.ee.format.ProductFormater;

import static eu.ggnet.dwoss.search.api.GlobalKey.Component.UNIQUE_PRODUCT;

/**
 * Providers Search for Unique Product.
 *
 * @author oliver.guenther
 */
@Stateless
public class ProductSearchProvider implements SearchProvider {

    private final static Logger L = LoggerFactory.getLogger(ProductSearchProvider.class);

    @Inject
    private ProductEao eao;

    @Override
    public Component getSource() {
        return GlobalKey.Component.UNIQUE_PRODUCT;
    }

    @Override
    public int estimateMaxResults(SearchRequest request) {
        return eao.findByPartNo(request.getSearch()) == null ? 0 : 1;
    }

    @Override
    public List<ShortSearchResult> search(SearchRequest request, int start, int limit) {
        if ( start >= 1 ) return Collections.emptyList();
        Product p = eao.findByPartNo(request.getSearch());
        if ( p == null ) return Collections.emptyList();
        return Arrays.asList(new ShortSearchResult(new GlobalKey(UNIQUE_PRODUCT, p.getId()), ProductFormater.toNameWithPartNo(p)));
    }

    @Override
    public String details(GlobalKey key) {

        return ProductFormater.toHtml(eao.findById(key.getId()));
    }

}
