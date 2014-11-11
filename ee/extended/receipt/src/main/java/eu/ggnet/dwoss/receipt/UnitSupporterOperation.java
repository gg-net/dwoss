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
package eu.ggnet.dwoss.receipt;

import javax.ejb.Stateless;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.*;

import eu.ggnet.dwoss.mandator.api.value.Mandator;
import eu.ggnet.dwoss.redtape.api.LegacyBridge;
import eu.ggnet.dwoss.stock.assist.Stocks;
import eu.ggnet.dwoss.stock.eao.StockUnitEao;
import eu.ggnet.dwoss.stock.entity.StockUnit;
import eu.ggnet.dwoss.uniqueunit.assist.UniqueUnits;
import eu.ggnet.dwoss.uniqueunit.eao.UniqueUnitEao;
import eu.ggnet.dwoss.uniqueunit.entity.UniqueUnit;

/**
 * UnitSupport.
 * <p/>
 * @author oliver.guenther
 */
@Stateless
public class UnitSupporterOperation implements UnitSupporter {

    private final static Logger L = LoggerFactory.getLogger(UnitSupporterOperation.class);

    @Inject
    @UniqueUnits
    private EntityManager uuEm;

    @Inject
    @Stocks
    private EntityManager stockEm;

    @Inject
    private Mandator mandator;

    @Inject
    private Instance<LegacyBridge> bridgeInstance;

    /**
     * Returns true if supplied refurbishId is available, meaning not jet in the database.
     *
     * @param refurbishId the refubishedId
     * @return true if available.
     */
    @Override
    public boolean isRefurbishIdAvailable(String refurbishId) {
        UniqueUnit uniqueUnit = new UniqueUnitEao(uuEm).findByIdentifier(UniqueUnit.Identifier.REFURBISHED_ID, refurbishId);
        if ( uniqueUnit != null ) return false;
        if ( bridgeInstance.isUnsatisfied() ) return true;
        LegacyBridge bridge = bridgeInstance.get();
        L.info("Using LegacyBridge ({})", bridge.name());
        return bridge.isUnitIdentifierAvailable(refurbishId);
    }

    @Override
    public boolean isSerialAvailable(String serial) {
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        StockUnitEao stockUnitEao = new StockUnitEao(stockEm);
        UniqueUnit uu = uniqueUnitEao.findByIdentifier(UniqueUnit.Identifier.SERIAL, serial);
        if ( uu != null ) {
            StockUnit stockUnit = stockUnitEao.findByUniqueUnitId(uu.getId());
            if ( stockUnit != null ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String findRefurbishIdBySerial(String serial) {
        UniqueUnitEao uniqueUnitEao = new UniqueUnitEao(uuEm);
        UniqueUnit uu = uniqueUnitEao.findByIdentifier(UniqueUnit.Identifier.SERIAL, serial);
        if ( uu != null ) return uu.getIdentifier(UniqueUnit.Identifier.REFURBISHED_ID);
        return null;
    }
}
