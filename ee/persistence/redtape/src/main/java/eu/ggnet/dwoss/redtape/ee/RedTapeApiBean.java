/*
 * Copyright (C) 2020 GG-Net GmbH
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

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.redtape.api.RedTapeApi;
import eu.ggnet.dwoss.redtape.api.UnitAvailability;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;
import eu.ggnet.dwoss.stock.api.SimpleStockUnit;
import eu.ggnet.dwoss.stock.api.StockApiLocal;
import eu.ggnet.dwoss.uniqueunit.api.SimpleUniqueUnit;
import eu.ggnet.dwoss.uniqueunit.api.UniqueUnitApiLocal;

/**
 * RedTape API implementation.
 * Requires unique unit api and stock api, cause only that a global unit status can be resolved.
 *
 * @author oliver.guenther
 */
@Stateless
@LocalBean
public class RedTapeApiBean implements RedTapeApi {

    @Inject
    private DossierEao eao;

    @Inject
    private UniqueUnitApiLocal uniqueUnitApi;

    @Inject
    private StockApiLocal stockApi;

    @Override
    public UnitAvailability findUnitByRefurbishIdAndVerifyAviability(String refurbishId) {
        SimpleUniqueUnit suu = uniqueUnitApi.findByRefurbishedId(refurbishId);
        UnitAvailability.Builder builder = new UnitAvailability.Builder();
        // If no unique unit exits, not existend.
        if ( suu == null ) return builder.refurbishId(refurbishId).available(false).exists(false).build();

        builder.exists(true).refurbishId(suu.refurbishedId()).lastRefurbishId(suu.lastRefurbishId()).uniqueUnitId(suu.id());
        SimpleStockUnit ssu = stockApi.findByUniqueUnitId(suu.id());
        // If no stock unit exists, not avialable
        if ( ssu == null ) return builder.available(false).build();

        builder.stockInformation(ssu.stockTransaction().map(t -> "\nAuf " + t.shortDescription())
                .orElseGet(() -> ssu.stock().map(s -> "\nAuf " + s.shortDescription).orElse(null)));
        builder.stockId(ssu.stock().map(s -> s.id));
        // Blocked by logic transaction
        if ( ssu.onLogicTransaction() ) return builder.available(false).build();

        // Sanity check
        if ( eao.isUnitBlocked((int)suu.id()) ) return builder.available(false).conflictDescription("RedTape hat ein offenes Dokument !").build();

        return builder.available(true).build();
    }
}
