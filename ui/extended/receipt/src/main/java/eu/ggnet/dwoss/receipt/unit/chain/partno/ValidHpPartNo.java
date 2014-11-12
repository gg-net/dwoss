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
package eu.ggnet.dwoss.receipt.unit.chain.partno;

import eu.ggnet.dwoss.receipt.unit.ValidationStatus;
import eu.ggnet.dwoss.receipt.unit.chain.ChainLink;

import static eu.ggnet.dwoss.rules.TradeName.HP;

/**
 *
 * @author bastian.venz
 */
public class ValidHpPartNo implements ChainLink<String> {

    @Override
    public ChainLink.Result<String> execute(String value) {
        String violations = HP.getPartNoSupport().violationMessages(value);
        if ( violations == null ) return new ChainLink.Result<>(value);
        return new ChainLink.Result<>(value, ValidationStatus.ERROR, violations);
    }

}
