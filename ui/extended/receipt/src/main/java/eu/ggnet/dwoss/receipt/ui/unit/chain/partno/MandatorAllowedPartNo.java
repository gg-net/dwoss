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
package eu.ggnet.dwoss.receipt.ui.unit.chain.partno;

import java.util.Objects;
import java.util.Set;

import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.ui.unit.chain.ChainLink;
import eu.ggnet.dwoss.common.api.values.TradeName;

import eu.ggnet.dwoss.spec.ee.SpecAgent;
import eu.ggnet.dwoss.spec.ee.entity.ProductSpec;

/**
 * Validates the PartNo is of a Brand that the Mandator may sale.
 * <p/>
 * @author oliver.guenther
 */
public class MandatorAllowedPartNo implements ChainLink<String> {

    private final SpecAgent specAgent;

    private final Set<TradeName> allowedBrands;

    public MandatorAllowedPartNo(SpecAgent specAgent, Set<TradeName> allowedBrands) {
        this.specAgent = Objects.requireNonNull(specAgent, "SpecAgent must not be null");
        this.allowedBrands = Objects.requireNonNull(allowedBrands, "Mandator must not be null");
    }

    @Override
    public ChainLink.Result<String> execute(String value) {
        ProductSpec spec = specAgent.findProductSpecByPartNoEager(value);
        if ( spec == null || allowedBrands.contains(spec.getModel().getFamily().getSeries().getBrand()) ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.ERROR, "Mandant darf keine Geräte der Marke " + spec.getModel().getFamily().getSeries().getBrand().getName() + " verkaufen");
    }
}
