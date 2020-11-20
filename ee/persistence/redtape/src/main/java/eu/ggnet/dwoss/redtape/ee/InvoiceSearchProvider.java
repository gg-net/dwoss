/*
 * Copyright (C) 2018 GG-Net GmbH
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
package eu.ggnet.dwoss.redtape.ee;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.core.common.values.DocumentType;
import eu.ggnet.dwoss.redtape.ee.eao.DocumentEao;
import eu.ggnet.dwoss.redtape.ee.format.DocumentFormater;
import eu.ggnet.dwoss.search.api.GlobalKey.Component;
import eu.ggnet.dwoss.search.api.*;

/**
 * Searchprovider for the Document Entity.
 *
 * @author oliver.guenther
 */
@Stateless
public class InvoiceSearchProvider implements SearchProvider {

    @Inject
    private DocumentEao documentEao;

    @Override
    public Component getSource() {
        return GlobalKey.Component.REDTAPE_DOCUMENT_INVOICE;
    }

    @Override
    public int estimateMaxResults(SearchRequest request) {
        return (int)documentEao.countFindByIdentifierAndType(request.getSearch(), DocumentType.INVOICE);
    }

    @Override
    public List<ShortSearchResult> search(SearchRequest request, int start, int limit) {
        return documentEao.findByIdentifierAndType(request.getSearch(), DocumentType.INVOICE, start, limit)
                .stream()
                .map(d -> new ShortSearchResult(new GlobalKey(Component.REDTAPE_DOCUMENT_INVOICE, d.getId()), d.getIdentifier() + " - Kid: " + d.getDossier().getCustomerId()))
                .collect(Collectors.toList());
    }

    @Override
    public String details(GlobalKey key) {
        return DocumentFormater.toHtmlDetailedWithPositions(documentEao.findById(key.id));
    }

}
