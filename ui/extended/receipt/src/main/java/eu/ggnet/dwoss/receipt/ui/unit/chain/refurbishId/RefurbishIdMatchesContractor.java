/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.receipt.ui.unit.chain.refurbishId;

import java.util.Objects;

import eu.ggnet.dwoss.mandator.api.service.MandatorService;
import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;
import eu.ggnet.dwoss.common.api.values.TradeName;
import eu.ggnet.saft.Dl;

/**
 * Tries to lookup the refurbishId in the Database, continues if it doesn't exist.
 * <p/>
 * @author oliver.guenther
 */
public class RefurbishIdMatchesContractor implements ChainLink<String> {

    private final TradeName contractor;

    public RefurbishIdMatchesContractor(TradeName contractor) {
        this.contractor = Objects.requireNonNull(contractor, "Contractor must not be null");
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        if ( !Dl.remote().contains(MandatorService.class) )
            return new ChainLink.Result<>(value, ValidationStatus.WARNING, "Kein MandatorService");
        if ( Dl.remote().lookup(MandatorService.class).isAllowedRefurbishId(contractor, value) ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.ERROR, "SopoNr ist nicht für Lieferant " + contractor.getName() + " zulässig");
    }
}
