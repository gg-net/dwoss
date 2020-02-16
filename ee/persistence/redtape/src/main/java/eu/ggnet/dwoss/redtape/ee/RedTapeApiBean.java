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

import javax.ejb.Stateless;
import javax.inject.Inject;

import eu.ggnet.dwoss.redtape.api.RedTapeApi;
import eu.ggnet.dwoss.redtape.api.SanityResult;
import eu.ggnet.dwoss.redtape.ee.eao.DossierEao;

/**
 *
 * @author oliver.guenther
 */
@Stateless
public class RedTapeApiBean implements RedTapeApi {

    @Inject
    private DossierEao eao;

    @Override
    public SanityResult sanityCheck(long uniqueUnitId) {
        if ( eao.isUnitBlocked((int)uniqueUnitId) ) return new SanityResult.Builder().blocked(true).details("RedTape hat ein offenes Dokument").build();
        return new SanityResult.Builder().blocked(false).details("").build();
    }
}
