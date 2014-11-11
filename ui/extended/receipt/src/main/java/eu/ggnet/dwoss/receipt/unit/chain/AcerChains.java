/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.receipt.unit.chain;

import java.util.*;

import eu.ggnet.dwoss.receipt.UnitSupporter;
import eu.ggnet.dwoss.receipt.unit.chain.date.DateNotNull;
import eu.ggnet.dwoss.receipt.unit.chain.date.InPast;
import eu.ggnet.dwoss.receipt.unit.chain.partno.*;
import eu.ggnet.dwoss.receipt.unit.chain.refurbishId.RefurbishIdMatchesContractor;
import eu.ggnet.dwoss.receipt.unit.chain.refurbishId.RefurbishIdNotExist;
import eu.ggnet.dwoss.receipt.unit.chain.serial.*;
import eu.ggnet.dwoss.receipt.unit.chain.string.*;

import eu.ggnet.dwoss.rules.TradeName;

import eu.ggnet.dwoss.spec.SpecAgent;

/**
 * The Chains for Acer.
 * <p/>
 * @author oliver.guenther
 */
public class AcerChains extends Chains {

    AcerChains() {
    }

    @Override
    public List<ChainLink<String>> newRefubishIdChain(TradeName contractor, UnitSupporter unitSupporter, boolean isEdit) {
        if ( isEdit ) {
            List<ChainLink<String>> result = new ArrayList<>();
            result.add(new NotEmpty());
            return result;
        }
        return Arrays.asList(
                new NotEmpty(), new Trim(), new ToUpperCase(),
                new RefurbishIdMatchesContractor(contractor), new RefurbishIdNotExist(unitSupporter));
    }

    @Override
    public List<ChainLink<String>> newSerialChain(UnitSupporter unitSupporter, String editRefurbhishId) {
        return Arrays.asList(new NotEmpty(), new Trim(), new ToUpperCase(),
                new ValidAcerSerial(), new SerialAvailable(unitSupporter, editRefurbhishId), new AcerSerialToPartNoAndMfgDate(),
                new SerialWasOnceInStock(unitSupporter, editRefurbhishId), new ValidAcerSerialNice());
    }

    @Override
    public List<ChainLink<String>> newPartNoChain(SpecAgent specAgent, Set<TradeName> allowedBrands) {
        return Arrays.asList(new NotEmpty(), new Trim(), new ToUpperCase(), new ValidAcerPartNo(), new ProductSpecExists(specAgent),
                new MandatorAllowedPartNo(specAgent, allowedBrands));
    }

    @Override
    public List<ChainLink<Date>> newMfgDateChain() {
        return Arrays.asList(new DateNotNull(), new InPast());
    }
}
