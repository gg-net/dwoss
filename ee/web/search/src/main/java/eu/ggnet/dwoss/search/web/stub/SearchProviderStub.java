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
import javax.inject.Singleton;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.search.api.GlobalKey.Component;
import eu.ggnet.dwoss.search.api.*;

import static eu.ggnet.dwoss.search.api.GlobalKey.Component.CUSTOMER;

/**
 * Stub of the SearchProvider
 *
 * @author oliver.guenther
 */
@ManagedBean
@Singleton
public class SearchProviderStub implements SearchProvider {

    private static final Logger L = LoggerFactory.getLogger(SearchProviderStub.class);

    private final Map<SearchRequest, List<ShortSearchResult>> searches = new HashMap<>();

    @Override
    public Component getSource() {
        return CUSTOMER;
    }

    @Override
    public int estimateMaxResults(SearchRequest request) {
        int size = genAndGet(request).size();
        L.info("estimateMaxResults(searchRequest={}) = {}", request, size);
        return size;
    }

    @Override
    public List<ShortSearchResult> search(SearchRequest request, int start, int limit) {
        List<ShortSearchResult> data = genAndGet(request);
        if ( data.size() > start ) {
            List<ShortSearchResult> result = data.subList(start, data.size() - start < limit ? start + (data.size() - start) : start + limit);
            L.info("search(request={}, start={}, limit={}) = (max={}), {} ", request, start, limit, data.size(), result);
            return result;
        }
        L.info("search(request={}, start={}, limit={}) = (max={}),[] ", request, start, limit, data.size());
        return Collections.emptyList();
    }

    @Override
    public String details(GlobalKey key) {
        // JW: copied an example here as I can't use Customer.class at this layer
        return "<table width=\"100%\"><tr><td colspan=2 ><b>Kid: 1604&nbsp;-&nbsp;Leon Breuer</b></td></tr><tr><td valign=top><b>Bevorzugte Rechnungs- und Lieferadresse</b><br />Leon Breuer<br />Gartenstraße 2<br />32427 Köln</td><td valign=top rowspan=4 ><b>Kontakt(e)</b>:<ul><li>Leon&nbsp;Breuer&nbsp;(m)&nbsp;<b>&oplus;</b><br />Adresse(n):<ul><li><i>Bevorzugte: Rechnungsadresse<br />Gartenstraße 2<br />DE&nbsp;32427&nbsp;Köln</i></li></ul>Kommunikationsinformationen:<ul><li>PHONE:&nbsp;+49 99 123456789</li><li>MOBILE:&nbsp;+49 555 12344321</li><li>EMAIL:&nbsp;breuer@example.com</li></ul></li></ul></td></tr><tr><td valign=top><b>Kundeneigenschaften</b>:<ul><li>Nachnahme bestätigt</li><li>Systemhauskunde</li></ul></td></tr><tr><td valign=top><b>Sonstiges</b>:<br />Weitere Kundennummer(n):<ul><li>SAGE: 76686238</li></ul></td></tr><tr><td valign=top><b>Mandantenspezifische Informationen</b>:<ul><li>Mandant: SAMPLE<ul><li>Versandkonditionen:DEALER_ONE</li><li>Zahlungskonditionen:Händler 3% Rabatt</li><li>Zahlungsmodalität:Nachnahme</li><li>Erlaubte Verkaufskanäle:[Händlerkanal, Endkundenkanal, Unbekannt]</li></ul></li></ul></tr></td></table>";

    }

    private List<ShortSearchResult> genAndGet(SearchRequest request) {
        if ( searches.containsKey(request) ) return searches.get(request);
        List<ShortSearchResult> gen = new ArrayList<>();
        int max = (int)(Math.random() * 500) + 5;
        for (int i = 0; i < max; i++) {
            gen.add(new ShortSearchResult(new GlobalKey(CUSTOMER, i), "Search of " + request.getSearch() + " and random " + RandomStringUtils.randomAlphabetic(20)));
        }
        searches.put(request, gen);
        return gen;
    }

}
