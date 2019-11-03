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
package eu.ggnet.dwoss.receipt.ui.unit.chain;

import java.util.*;

import eu.ggnet.dwoss.receipt.ee.UnitSupporter;
import eu.ggnet.dwoss.receipt.ui.unit.chain.date.DateNotNull;
import eu.ggnet.dwoss.receipt.ui.unit.chain.date.InPast;
import eu.ggnet.dwoss.receipt.ui.unit.chain.partno.MandatorAllowedPartNo;
import eu.ggnet.dwoss.receipt.ui.unit.chain.partno.ProductSpecExists;
import eu.ggnet.dwoss.receipt.ui.unit.chain.refurbishId.RefurbishIdMatchesContractor;
import eu.ggnet.dwoss.receipt.ui.unit.chain.refurbishId.RefurbishIdNotExist;
import eu.ggnet.dwoss.receipt.ui.unit.chain.serial.SerialAvailable;
import eu.ggnet.dwoss.receipt.ui.unit.chain.string.NotEmpty;

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;

import eu.ggnet.dwoss.spec.ee.SpecAgent;

/**
 * Minimal Chains for Auto selection mode.
 * <p/>
 * @author oliver.guenther
 */
public class AutoChains extends Chains {

    AutoChains() {
    }

    @Override
    public List<ChainLink<String>> newRefubishIdChain(TradeName contractor, UnitSupporter unitSupporter, boolean isEdit) {
        if ( isEdit ) {
            List<ChainLink<String>> result = new ArrayList<>();
            result.add(new NotEmpty());
            return result;
        }
        return Arrays.asList(
                new NotEmpty(),
                new RefurbishIdMatchesContractor(contractor), new RefurbishIdNotExist(unitSupporter));
    }

    @Override
    public List<ChainLink<String>> newSerialChain(UnitSupporter unitSupporter, String editRefurbhisId) {
        return Arrays.asList(new NotEmpty(), new SerialAvailable(unitSupporter, editRefurbhisId));
    }

    @Override
    public List<ChainLink<String>> newPartNoChain(SpecAgent specAgent, Set<TradeName> allowedBrands) {
        return Arrays.asList(new NotEmpty(), new ProductSpecExists(specAgent), new MandatorAllowedPartNo(specAgent, allowedBrands));
    }

    @Override
    public List<ChainLink<Date>> newMfgDateChain() {
        return Arrays.asList(new DateNotNull(), new InPast());
    }
}
