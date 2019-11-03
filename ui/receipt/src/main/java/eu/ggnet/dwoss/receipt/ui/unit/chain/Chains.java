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

import eu.ggnet.dwoss.core.common.values.tradename.TradeName;
import eu.ggnet.dwoss.receipt.ee.UnitSupporter;
import eu.ggnet.dwoss.receipt.ui.unit.ValidationStatus;
import eu.ggnet.dwoss.spec.ee.SpecAgent;

/**
 *
 * @author oliver.guenther
 */
public abstract class Chains {

    protected Chains() {
    }

    public static Chains getInstance(TradeName manufacturer) {
        switch (manufacturer) {
            case ACER:
                return new AcerChains();
            case APPLE:
                return new AppleChains();
            case HP:
                return new HpChains();
            case LENOVO:
                return new LenovoChains();
            default:
                return new AutoChains();
        }
    }

    /**
     * Executes a chain.
     * <p/>
     * @param <T>   the type of the chain
     * @param chain the chain to execute
     * @param value the value to start execution on
     * @return the first invalid result or the last result, all with merged optionals.
     */
    public static <T> ChainLink.Result<T> execute(List<ChainLink<T>> chain, T value) {
        Objects.requireNonNull(chain, "Chain must not be null");
        if ( chain.isEmpty() ) throw new IllegalStateException("The Chain is empty, not allowed");
        ChainLink.Result<T> result = new ChainLink.Result<>(value, ValidationStatus.ERROR, "Validation Chain is empty");
        ChainLink.Optional optional = new ChainLink.Optional(null, null);
        for (ChainLink<T> link : chain) {
            result = link.execute(result.value);
            optional = optional.merge(result.optional);
            if ( !result.isValid() ) return result.withOptional(optional);
        }
        return result.withOptional(optional);
    }

    /**
     * Returns the new RefurbhisId Validation an Modification Chain.
     * <p/>
     * @param contractor    the contractor.
     * @param unitSupporter the unit supporter
     * @param isEdit        if true, this chain is in edit mode.
     * @return the new RefurbhisId Validation an Modification Chain.
     */
    public abstract List<ChainLink<String>> newRefubishIdChain(TradeName contractor, UnitSupporter unitSupporter, boolean isEdit);

    /**
     * Returns the new SerialChain.
     * <p/>
     * @param unitSupporter   the unitSupporter
     * @param editRefurbishId the refurbishId of an edited unit, or null for creation.
     * @return the new SerialChain.
     */
    public abstract List<ChainLink<String>> newSerialChain(UnitSupporter unitSupporter, String editRefurbishId);

    public abstract List<ChainLink<String>> newPartNoChain(SpecAgent specAgent, Set<TradeName> allowedBrands);

    public abstract List<ChainLink<Date>> newMfgDateChain();
}
